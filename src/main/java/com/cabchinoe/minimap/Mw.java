package com.cabchinoe.minimap;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.cabchinoe.common.Render;
import com.cabchinoe.minimap.forge.MwConfig;
import com.cabchinoe.minimap.forge.MwForge;
import com.cabchinoe.minimap.forge.MwKeyHandler;
import com.cabchinoe.minimap.gui.MwGui;
import com.cabchinoe.minimap.map.*;
//import com.cabchinoe.minimap.overlay.OverlaySlime;
import com.cabchinoe.minimap.region.BlockColours;
import com.cabchinoe.minimap.region.RegionManager;
import com.cabchinoe.minimap.tasks.CloseRegionManagerTask;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/*

data transfers
---------------
chunk image (16x16 int[]) -> texture (512x512 GL texture)	| every chunk update
region file png -> texture (512x512 GL texture)				| on region load (slow, disk access)
texture (512x512 GL texture) -> region file png				| on region unload (slow, disk access)
chunk (Chunk object) -> anvil save file						| on chunk unload, separate thread handled by minecraft

background thread
------------------
performs all data transfers except Chunk->Anvil, which is handled by ThreadedFileIOBase in minecraft.
regions created in main thread when necessary, but filled from the background thread.

initialization
--------------
init()
  Called once only.
	- registers event and key handlers
	- loads configuration
	- inits marker manager
	- inits commands
	- inits chunkQueue

onClientLoggedIn()
  Called upon entry to each world.
	- inits executor
	- inits overlay
	- inits anvil save handler
	- inits regionMap

onConnectionClosed()
  Called on every exit from world.
	- closes chunkLoader
	- closes regionMap
	- closes overlay
	- saves markermanager
	- saves config
	- closes executor
	- flush chunkQueue

Every hook and event handler should be enclosed in an 'if (this.ready)' statement to ensure that all
components are initialised.
One exception is the fillChunk handler which adds chunks to the chunkQueue so that they can be processed
after initialization. This is so that no chunks are skipped if the chunks are loaded before the player is
logged in.
	
*/
public class Mw {
	
	public static Minecraft mc = Minecraft.getMinecraft();

	// server information
	public static String worldName = "default";

	// configuration files (global and world specific)
	public MwConfig config;
	public MwConfig worldConfig = null;
	
	// directories
	public static File worldDir = null;
	public static File saveDir = new File(new File(mc.mcDataDir, "saves"), "minimap");
	public File imageDir = null;
	
	// configuration options
	public boolean linearTextureScalingEnabled = true;
	public int coordsMode = 1;
	public boolean showDimension = false;
	public boolean undergroundMode = false;
	public boolean teleportEnabled = false;
	public String teleportCommand = "tp";
	public int defaultTeleportHeight = 80;
	public int maxZoom = 4;
	public int minZoom = -4;
	public boolean useSavedBlockColours = false;
	public int maxChunkSaveDistSq = 128 * 128;
	public boolean mapPixelSnapEnabled = true;
	public int textureSize = 4096;
	public int utextureSize = 2048;
	public int configTextureSize = 4096;
	public int maxDeathMarkers = 3;
	public int chunksPerTick = 5;
	public String saveDirOverride = "";
	public boolean regionFileOutputEnabledSP = true;
	public boolean regionFileOutputEnabledMP = true;
	public int backgroundTextureMode = 0;
//	public boolean newMarkerDialog = true;
	public static boolean reloadColours = true;

	//public boolean lightingEnabled = false;
	
	// flags and counters
	private boolean onPlayerDeathAlreadyFired = false;
	public boolean ready = false;
	public boolean multiplayer = false;
	public int tickCounter = 0;
	
	// list of available dimensions
	public List<Integer> dimensionList = new ArrayList<Integer>();
	
	// player position and heading
	public double playerX = 0.0;
	public double playerZ = 0.0;
	public double playerY = 0.0;
	public int playerXInt = 0;
	public int playerYInt = 0;
	public int playerZInt = 0;
	public double playerHeading = 0.0;
	public int playerDimension = 0;
	public double mapRotationDegrees = 0.0;
	
	// constants
	public final static String catWorld = "world";
	public final static String catMarkers = "markers";
	public final static String catOptions = "options";
	public final static String worldDirConfigName = "com.cabchinoe.minimap.cfg";
	public final static String blockColourSaveFileName = "minimapBlockColours.txt";
	public final static String blockColourOverridesFileName = "minimapBlockColourOverrides.txt";
	
	// instances of components
	public MapTexture mapTexture = null;
	public UndergroundTexture undergroundMapTexture = null;
	public BackgroundExecutor executor = null;
	public MiniMap miniMap = null;
	public MarkerManager markerManager = null;
	public BlockColours blockColours = null;
	public RegionManager regionManager = null;
	public ChunkManager chunkManager = null;
	public Trail playerTrail = null;
	public TeamManager clientTM = new TeamManager();

	public static Mw instance;
	private boolean closing = false;
	public Mw(MwConfig config) {
		// client only initialization
		// oops, no idea why I was using a ModLoader method to get the Minecraft instance before
		this.mc = Minecraft.getMinecraft();
		
		// load config
		this.config = config;
		
		// create base save directory

		this.ready = false;
		
		RegionManager.logger = MwForge.logger;
		
		instance = this;
	}

	public String getWorldName() {
		if (!this.multiplayer) {
			// cannot use this.mc.theWorld.getWorldInfo().getWorldName() as it
			// is set statically to "MpServer".
			IntegratedServer server = this.mc.getIntegratedServer();
			this.worldName = (server != null) ? server.getFolderName() : "sp_world";
		}
		
		// strip invalid characters from the server name so that it
		// can't be something malicious like '..\..\..\windows\'
		this.worldName = MwUtil.mungeString(this.worldName);
		
		// if something went wrong make sure the name is not blank
		// (causes crash on start up due to empty configuration section)
		if (this.worldName.equals("")) {
			this.worldName = "default";
		}
		return this.worldName;
	}
	
	public void loadConfig() {
		this.config.load();
		this.linearTextureScalingEnabled = this.config.getOrSetBoolean(catOptions, "linearTextureScaling", this.linearTextureScalingEnabled);
		this.useSavedBlockColours = this.config.getOrSetBoolean(catOptions, "useSavedBlockColours", this.useSavedBlockColours);
		this.teleportEnabled = this.config.getOrSetBoolean(catOptions, "teleportEnabled", this.teleportEnabled);
		this.teleportCommand = this.config.get(catOptions, "teleportCommand", this.teleportCommand).getString();
		this.coordsMode = this.config.getOrSetInt(catOptions, "coordsMode", this.coordsMode, 0, 2);
		this.maxChunkSaveDistSq = this.config.getOrSetInt(catOptions, "maxChunkSaveDistSq", this.maxChunkSaveDistSq, 1, 256 * 256);
		this.mapPixelSnapEnabled = this.config.getOrSetBoolean(catOptions, "mapPixelSnapEnabled", this.mapPixelSnapEnabled);
		this.maxDeathMarkers = this.config.getOrSetInt(catOptions, "maxDeathMarkers", this.maxDeathMarkers, 0, 1000);
		this.chunksPerTick = this.config.getOrSetInt(catOptions, "chunksPerTick", this.chunksPerTick, 1, 500);
		this.saveDirOverride = this.config.get(catOptions, "saveDirOverride", this.saveDirOverride).getString();
		this.undergroundMode = this.config.getOrSetBoolean(catOptions, "undergroundMode", this.undergroundMode);
		this.regionFileOutputEnabledSP = this.config.getOrSetBoolean(catOptions, "regionFileOutputEnabledSP", this.regionFileOutputEnabledSP);
		this.regionFileOutputEnabledMP = this.config.getOrSetBoolean(catOptions, "regionFileOutputEnabledMP", this.regionFileOutputEnabledMP);
		this.backgroundTextureMode = this.config.getOrSetInt(catOptions, "backgroundTextureMode", this.backgroundTextureMode, 0, 1);
		this.showDimension = this.config.getOrSetBoolean(catOptions, "showDimension", this.showDimension);
		//this.lightingEnabled = this.config.getOrSetBoolean(catOptions, "lightingEnabled", this.lightingEnabled);
//		this.newMarkerDialog = this.config.getOrSetBoolean(catOptions, "newMarkerDialog", this.newMarkerDialog);
		this.clientTM.visible = this.config.getOrSetBoolean(catOptions, "TeammateVisible", this.clientTM.visible);
		this.maxZoom = this.config.getOrSetInt(catOptions, "zoomOutLevels", this.maxZoom, 0, this.maxZoom);
		this.minZoom = -this.config.getOrSetInt(catOptions, "zoomInLevels", -this.minZoom, 0, -this.minZoom);
		
		this.configTextureSize = this.config.getOrSetInt(catOptions, "textureSize", this.configTextureSize, 2048, 8192);
		this.setTextureSize();
	}
	
	public void loadWorldConfig() {
		// load world specific config file
		File worldConfigFile = new File(this.worldDir, worldDirConfigName);
		this.worldConfig = new MwConfig(worldConfigFile);
		this.worldConfig.load();
		
		this.dimensionList.clear();
		this.worldConfig.getIntList(catWorld, "dimensionList", this.dimensionList);
		this.addDimension(0);
		this.cleanDimensionList();
	}
	
	public void saveConfig() {
		this.config.setBoolean(catOptions, "linearTextureScaling", this.linearTextureScalingEnabled);
		this.config.setBoolean(catOptions, "useSavedBlockColours", this.useSavedBlockColours);
		this.config.setInt(catOptions, "textureSize", this.configTextureSize);
		this.config.setInt(catOptions, "coordsMode", this.coordsMode);
		this.config.setInt(catOptions, "maxChunkSaveDistSq", this.maxChunkSaveDistSq);
		this.config.setBoolean(catOptions, "mapPixelSnapEnabled", this.mapPixelSnapEnabled);
		this.config.setInt(catOptions, "maxDeathMarkers", this.maxDeathMarkers);
		this.config.setInt(catOptions, "chunksPerTick", this.chunksPerTick);
		this.config.setBoolean(catOptions, "undergroundMode", this.undergroundMode);
		this.config.setInt(catOptions, "backgroundTextureMode", this.backgroundTextureMode);
		this.config.setBoolean(catOptions, "showDimension", this.showDimension);
		this.config.setBoolean(catOptions, "TeammateVisible", this.clientTM.visible);

		this.config.save();	
	}

	public String enabledesc(boolean tf){
		return tf? I18n.format("minimap.guislot.booleandesc.on"):I18n.format("minimap.guislot.booleandesc.off");
	}

	public void saveWorldConfig() {
		this.worldConfig.setIntList(catWorld, "dimensionList", this.dimensionList);
		this.worldConfig.save();
	}
	
	public void setTextureSize() {
		if (this.configTextureSize != this.textureSize) {
			int maxTextureSize = Render.getMaxTextureSize();
			int textureSize = 1024;
			while ((textureSize <= maxTextureSize) && (textureSize <= this.configTextureSize)) {
				textureSize *= 2;
			}
			textureSize /= 2;
			
			MwUtil.log("GL reported max texture size = %d", maxTextureSize);
			MwUtil.log("texture size from config = %d", this.configTextureSize);
			MwUtil.log("setting map texture size to = %d", textureSize);
			
			this.textureSize = textureSize;
			if (this.ready) {
				// if we are already up and running need to close and reinitialize the map texture and
				// region manager.
				this.reloadMapTexture();
			}
		}
	}
	
	// update the saved player position and orientation
	// called every tick
	public void updatePlayer() {
		// get player pos
		this.playerX =  this.mc.player.posX;
		this.playerY =  this.mc.player.posY;
		this.playerZ =  this.mc.player.posZ;
		this.playerXInt = (int) Math.floor(this.playerX);
		this.playerYInt = (int) Math.floor(this.playerY);
		this.playerZInt = (int) Math.floor(this.playerZ);
		
		// rotationYaw of 0 points due north, we want it to point due east instead
		// so add pi/2 radians (90 degrees)
		this.playerHeading = Math.toRadians(this.mc.player.rotationYaw) + (Math.PI / 2.0D);
		this.mapRotationDegrees = (-this.mc.player.rotationYaw + 180)%360.0;
		
		// set by onWorldLoad
		//this.playerDimension = this.mc.theWorld.provider.dimensionId;
	}
	
	public void addDimension(int dimension) {
		int i = this.dimensionList.indexOf(dimension);
		if (i < 0) {
			this.dimensionList.add(dimension);
		}
	}
	
	public void cleanDimensionList() {
		List<Integer> dimensionListCopy = new ArrayList<Integer>(this.dimensionList);
		this.dimensionList.clear();
		for (int dimension : dimensionListCopy) {
			this.addDimension(dimension);
		}
	}


	public void loadBlockColourOverrides(BlockColours bc) {
		File f = new File(this.saveDir, blockColourOverridesFileName);
		if (f.isFile()) {
			MwUtil.logInfo("loading block colour overrides file %s", f);
			bc.loadFromFile(f);
		} else {
			MwUtil.logInfo("recreating block colour overrides file %s", f);
			BlockColours.writeOverridesFile(f);
			if (f.isFile()) {
				bc.loadFromFile(f);
			} else {
				MwUtil.logError("could not load block colour overrides from file %s", f);
			}
		}
	}
	
	public void saveBlockColours(BlockColours bc) {
		File f = new File(this.saveDir, blockColourSaveFileName);
		MwUtil.logInfo("saving block colours to '%s'", f);
		bc.saveToFile(f);
	}
	
	public void reloadBlockColours() {
		BlockColours bc = new BlockColours();
		File f = new File(this.saveDir, blockColourSaveFileName);
		if (this.useSavedBlockColours && f.isFile() && bc.CheckFileVersion(f)) {
			// load block colours from file
			MwUtil.logInfo("loading block colours from %s", f);
			bc.loadFromFile(f);
			this.loadBlockColourOverrides(bc);
		} else {
			// generate block colours from current texture pack
			MwUtil.logInfo("generating block colours");
			// block type overrides need to be loaded before the block colours are generated
			BlockColourGen.genBlockColours(bc);
			// load overrides again to override block and biome colours
			this.loadBlockColourOverrides(bc);
			this.saveBlockColours(bc);
		}
		this.blockColours = bc;
	}
	
	public void reloadMapTexture() {
		this.executor.addTask(new CloseRegionManagerTask(this.regionManager));
		this.executor.close();
		MapTexture oldMapTexture = this.mapTexture;
		MapTexture newMapTexture = new MapTexture(this.textureSize, this.linearTextureScalingEnabled);
		this.mapTexture = newMapTexture;
		if (oldMapTexture != null) {
			oldMapTexture.close();
		}
		this.executor = new BackgroundExecutor();
		this.regionManager = new RegionManager(this.worldDir, this.imageDir, this.blockColours, this.minZoom, this.maxZoom);
		
		UndergroundTexture oldTexture = this.undergroundMapTexture;
		UndergroundTexture newTexture = new UndergroundTexture(this, Math.min(this.textureSize,this.utextureSize), this.linearTextureScalingEnabled);
		this.undergroundMapTexture = newTexture;
		if (oldTexture != null) {
			this.undergroundMapTexture.close();
		}
	}
	
	public void setCoordsMode(int mode) {
		this.coordsMode = Math.min(Math.max(0, mode), 2);
	}
	
	public int toggleCoords() {
		this.setCoordsMode((this.coordsMode + 1) % 2);
		return this.coordsMode;
	}
	
	public void toggleUndergroundMode() {
		this.undergroundMode = !this.undergroundMode;
		this.miniMap.view.setUndergroundMode(this.undergroundMode);
	}
	

	////////////////////////////////
	// Initialization and Cleanup
	////////////////////////////////

	public void load() {
		
		if (this.ready || this.closing) {
			return;
		}
		
		if ((this.mc.world == null) || (this.mc.player == null)) {
			MwUtil.log("minimap world or player is null, cannot load yet");
			return;
		}
		
		MwUtil.log("minimap loading...");
		
		this.multiplayer = !this.mc.isIntegratedServerRunning();
		
		this.loadConfig();
		
		this.worldName = this.getWorldName();
		if (this.multiplayer) {
			this.worldDir = new File(new File(saveDir, "minimap_mp_worlds"), this.worldName);
		} else {
			this.worldDir = new File(saveDir, "minimap_sp_worlds");
		}


		// get world and image directories
		File saveDir = this.saveDir;
		if (this.saveDirOverride.length() > 0) {
			File d = new File(this.saveDirOverride);
			if (d.isDirectory()) {
				saveDir = d;
			} else {
				MwUtil.log("error: no such directory %s", this.saveDirOverride);
			}
		}
		

		this.loadWorldConfig();
		
		// create directories
		this.imageDir = new File(this.worldDir, "images");
		if (!this.imageDir.exists()) {
			this.imageDir.mkdirs();
		}
		if (!this.imageDir.isDirectory()) {
			MwUtil.log("minimap: ERROR: could not create images directory '%s'", this.imageDir.getPath());
		}
		
		this.tickCounter = 0;
		this.onPlayerDeathAlreadyFired = false;
		
		// marker manager only depends on the config being loaded
		this.markerManager = new MarkerManager();
		this.markerManager.load(this.worldConfig, catMarkers);
		this.clientTM.load(this.worldConfig,"colors");
		this.playerTrail = new Trail(this, "player");
		
		// executor does not depend on anything
		this.executor = new BackgroundExecutor();
		
		// mapTexture depends on config being loaded
		this.mapTexture = new MapTexture(this.textureSize, this.linearTextureScalingEnabled);
		this.undergroundMapTexture = new UndergroundTexture(this, Math.min(this.textureSize,this.utextureSize), this.linearTextureScalingEnabled);
		this.reloadBlockColours();
		// region manager depends on config, mapTexture, and block colours
		this.regionManager = new RegionManager(this.worldDir, this.imageDir, this.blockColours, this.minZoom, this.maxZoom);
		// overlay manager depends on mapTexture
		this.miniMap = new MiniMap(this);
		this.miniMap.view.setDimension(this.mc.player.dimension);
		
		this.chunkManager = new ChunkManager(this);

		this.clientTM.send_visible();

		this.ready = true;
		//if (!zoomLevelsExist) {
			//printBoth("recreating zoom levels");
			//this.regionManager.recreateAllZoomLevels();
		//}
		
		MwUtil.log("minimap load: Done");
	}
	
	public void close() {
		
		MwUtil.log("minimap closing...");
		
		if (this.ready) {
			this.ready = false;
			this.closing = true;
			this.chunkManager.close();
			this.chunkManager = null;
			
			// close all loaded regions, saving modified images.
			// this will create extra tasks that need to be completed.
			this.executor.addTask(new CloseRegionManagerTask(this.regionManager));
			this.regionManager = null;
			
			MwUtil.log("waiting for %d tasks to finish...", this.executor.tasksRemaining());
			if (this.executor.close()) {
				MwUtil.log("error: timeout waiting for tasks to finish");
			}

			this.playerTrail.close();
			
			this.markerManager.save(this.worldConfig, catMarkers);
			this.markerManager.clear();
			
			// close overlay
			this.miniMap.close();
			this.miniMap = null;
			
			this.undergroundMapTexture.close();
			this.mapTexture.close();

			this.tickCounter = 0;
			MwForge.TM.removeTeammate(this.mc.player.getUniqueID().toString());
			this.clientTM.save(this.worldConfig, "colors");

			this.saveWorldConfig();
			this.saveConfig();
//            OverlaySlime.reset(); //Reset the state so the seed will be asked again when we log in
        }
		MwUtil.log("close done");
		this.closing = false;
	}
	
	////////////////////////////////
	// Event handlers
	////////////////////////////////
	
	public void onWorldLoad(World world) {
		//MwUtil.log("onWorldLoad: %s, name %s, dimension %d",
		//		world,
		//		world.getWorldInfo().getWorldName(),
		//		world.provider.dimensionId);
		
		this.playerDimension = world.provider.getDimension();
		if (this.ready) {
			this.addDimension(this.playerDimension);
			this.miniMap.view.setDimension(this.playerDimension);
		}
	}
	
	public void onWorldUnload(World world) {
		if (this.ready){
			// run the cleanup code when Mw is loaded and the player becomes null.
			// a bit hacky, but simpler than checking if the connection has closed.
			this.close();
		}
	}
	
	public void onTick() {

		this.load();
		if (this.ready && (this.mc.player != null)) {
			
			this.updatePlayer();
			
			if (this.undergroundMode && ((this.tickCounter % 30) == 0)) {
				this.undergroundMapTexture.update();
			}
			
			// check if the game over screen is being displayed and if so 
			// (thanks to Chrixian for this method of checking when the player is dead)
			if (this.mc.currentScreen instanceof GuiGameOver) {
				if (!this.onPlayerDeathAlreadyFired) {
					this.onPlayerDeath();
					this.onPlayerDeathAlreadyFired = true;
				}
			}
			else if(!(this.mc.currentScreen instanceof GuiScreen)){
				// if the player is not dead
				this.onPlayerDeathAlreadyFired = false;
				// if in game (no gui screen) center the com.cabchinoe.minimap on the player and render it.
				this.miniMap.view.setViewCentreScaled(this.playerX, this.playerZ, this.playerDimension);
				this.miniMap.drawCurrentMap();
			}

			
			// process background tasks
			int maxTasks = 50;
			while (!this.executor.processTaskQueue() && (maxTasks > 0)) {
				maxTasks--;
			}
			
			this.chunkManager.onTick();
			
			// update GL texture of mapTexture if updated
			this.mapTexture.processTextureUpdates();
			
			// let the renderEngine know we have changed the bound texture.
	    	//this.mc.renderEngine.resetBoundTexture();
			
	    	//if (this.tickCounter % 100 == 0) {
	    	//	MwUtil.log("tick %d", this.tickCounter);
	    	//}
	    	this.playerTrail.onTick();
	    	
			this.tickCounter++;
		}
	}
	
	// add chunk to the set of loaded chunks
	public void onChunkLoad(Chunk chunk) {
		this.load();
		if ((chunk != null) && (chunk.getWorld() instanceof net.minecraft.client.multiplayer.WorldClient)) {
			if (this.ready) {
				this.chunkManager.addChunk(chunk);
			} else {
				MwUtil.logInfo("missed chunk (%d, %d)", chunk.x, chunk.z);
			}
		}
	}
	
	// remove chunk from the set of loaded chunks.
	// convert to mwchunk and write chunk to region file if in multiplayer.
	public void onChunkUnload(Chunk chunk) {
		if (this.ready && (chunk != null) && (chunk.getWorld() instanceof net.minecraft.client.multiplayer.WorldClient)) {
			this.chunkManager.removeChunk(chunk);
		}
	}
	
	// from onTick when mc.currentScreen is an instance of GuiGameOver
	// it's the only option to detect death client side
	public void onPlayerDeath() {
		if (this.ready && (this.maxDeathMarkers > 0)) {
			this.updatePlayer();
			int deleteCount = this.markerManager.countMarkersInGroup("playerDeaths") - this.maxDeathMarkers + 1;
			for (int i = 0; i < deleteCount; i++) {
				// delete the first marker found in the group "playerDeaths".
				// as new markers are only ever appended to the marker list this will delete the
				// earliest death marker added.
				this.markerManager.delMarker(null, "playerDeaths");
			}
			this.markerManager.addMarker(MwUtil.getCurrentDateString(), "playerDeaths", this.playerXInt, this.playerYInt, this.playerZInt, this.playerDimension, 0xffff0000);
			this.markerManager.setVisibleGroupName("all");
			this.markerManager.update();
		}
	}

	public void onKeyDown(KeyBinding kb) {
		// make sure not in GUI element (e.g. chat box)
		if ((this.mc.currentScreen == null) && (this.ready)) {
			//Mw.log("client tick: %s key pressed", kb.keyDescription);

			if (kb == MwKeyHandler.keyMapScale) {
				// map size toggle
				this.miniMap.smallMapMode.toggleHeightPercent();
			}
			else if (kb == MwKeyHandler.keyMapGui) {
				// open map gui
				this.mc.displayGuiScreen(new MwGui(this));
			
			}
			else if (kb == MwKeyHandler.keyZoomIn) {
				// zoom in
				this.miniMap.view.adjustZoomLevel(-1);
			} else if (kb == MwKeyHandler.keyZoomOut) {
				// zoom out
				this.miniMap.view.adjustZoomLevel(1);
			}else if(kb == MwKeyHandler.keySwitchZoom){
				int currentZoomLevel = this.miniMap.view.getZoomLevel();
				this.miniMap.view.setZoomLevel(currentZoomLevel -2 < this.minZoom? this.maxZoom:currentZoomLevel-2);
			}
			else if (kb == MwKeyHandler.keyUndergroundMode) {
				this.toggleUndergroundMode();
			}
		}
	}
}

package mapwriter;

import mapwriter.util.BlockColours;
import mapwriter.config.Config;
import mapwriter.config.ConfigurationHandler;
import mapwriter.config.WorldConfig;
import mapwriter.forge.MapWriterForge;
import mapwriter.forge.MwKeyHandler;
import mapwriter.gui.GuiFullScreenMap;
import mapwriter.gui.GuiMarkerDialog;
import mapwriter.gui.GuiMarkerDialogNew;
import mapwriter.map.*;
import mapwriter.region.RegionManager;
import mapwriter.tasks.CloseRegionManagerTask;
import mapwriter.util.Reference;
import mapwriter.util.Render;
import mapwriter.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;

import java.io.File;

public class MapWriter {
    private static MapWriter instance;

    public static MapWriter getInstance() {

        if (MapWriter.instance == null) {
            synchronized (WorldConfig.class) {
                if (MapWriter.instance == null) {
                    MapWriter.instance = new MapWriter();
                }
            }
        }

        return MapWriter.instance;
    }

    public Minecraft mc = null;

    private File saveDir;

    public File worldDir = null;
    public File imageDir = null;

    // flags and counters
    public boolean ready = false;

    public int tickCounter = 0;
    public int textureSize = 2048;
    // player position and heading
    public double playerX = 0.0;
    public double playerZ = 0.0;
    public double playerY = 0.0;
    public int playerXInt = 0;
    public int playerYInt = 0;
    public int playerZInt = 0;
    public String playerBiome = "";
    public double playerHeading = 0.0;

    public DimensionType playerDimension = DimensionType.OVERWORLD;
    public float mapRotationDegrees = 0f;
    // instances of components
    public MapTexture mapTexture = null;
    public UndergroundTexture undergroundMapTexture = null;
    public BackgroundExecutor executor = null;
    public MiniMap miniMap = null;
    public MarkerManager markerManager = null;
    public BlockColours blockColours = null;
    public RegionManager regionManager = null;

    public ChunkManager chunkManager = null;

    private MapWriter() {
        // client only initialization
        this.mc = Minecraft.getMinecraft();

        // create base save directory
        this.saveDir = new File(this.mc.mcDataDir, "saves");

        this.ready = false;

        RegionManager.logger = MapWriterForge.LOGGER;

        ConfigurationHandler.loadConfig();
    }

    public void close() {

        MapWriterForge.LOGGER.info("MapWriter.close: closing...");

        if (this.ready) {
            this.ready = false;

            this.chunkManager.close();
            this.chunkManager = null;

            // close all loaded regions, saving modified images.
            // this will create extra tasks that need to be completed.
            this.executor.addTask(new CloseRegionManagerTask(this.regionManager));
            this.regionManager = null;

            MapWriterForge.LOGGER.info("waiting for {} tasks to finish...", this.executor.tasksRemaining());
            if (this.executor.close()) {
                MapWriterForge.LOGGER.info("error: timeout waiting for tasks to finish");
            }
            MapWriterForge.LOGGER.info("done");

            this.markerManager.save(WorldConfig.getInstance().worldConfiguration, Reference.CAT_MARKERS);
            this.markerManager.clear();

            // close overlay
            this.miniMap.close();
            this.miniMap = null;

            this.undergroundMapTexture.close();
            this.mapTexture.close();

            WorldConfig.getInstance().saveWorldConfig();

            this.tickCounter = 0;
        }
    }

    public void load() {
        if (this.ready) {
            return;
        }

        if (this.mc.world == null || this.mc.player == null) {
            MapWriterForge.LOGGER.info("MapWriter.load: world or player is null, cannot load yet");
            return;
        }

        MapWriterForge.LOGGER.info("MapWriter.load: loading...");

        // get world and image directories
        if (Config.saveDirOverride.length() > 0) {
            final File d = new File(Config.saveDirOverride);
            if (d.isDirectory()) {
                this.saveDir = d;
            } else {
                MapWriterForge.LOGGER.info("error: no such directory {}", Config.saveDirOverride);
            }
        }

        if (!this.mc.isSingleplayer()) {
            this.worldDir = new File(new File(this.saveDir, "mapwriter_mp_worlds"), Utils.getWorldName());
        } else {
            this.saveDir = DimensionManager.getCurrentSaveRootDirectory();
            this.worldDir = new File(this.saveDir, "mapwriter");
        }

        // create directories
        this.imageDir = new File(this.worldDir, "images");
        if (!this.imageDir.exists()) {
            this.imageDir.mkdirs();
        }
        if (!this.imageDir.isDirectory()) {
            MapWriterForge.LOGGER.info("Mapwriter: ERROR: could not create images directory '{}'", this.imageDir.getPath());
        }

        this.tickCounter = 0;

        // marker manager only depends on the config being loaded
        this.markerManager = new MarkerManager();
        this.markerManager.load(WorldConfig.getInstance().worldConfiguration, Reference.CAT_MARKERS);

        // executor does not depend on anything
        this.executor = new BackgroundExecutor();

        // mapTexture depends on config being loaded
        this.mapTexture = new MapTexture(this.textureSize, Config.linearTextureScaling);
        this.undergroundMapTexture = new UndergroundTexture(this, this.textureSize, Config.linearTextureScaling);
        // region manager depends on config, mapTexture, and block colours
        this.regionManager = new RegionManager(this.worldDir, this.imageDir, this.blockColours, Config.zoomInLevels, Config.zoomOutLevels);
        // overlay manager depends on mapTexture
        this.miniMap = new MiniMap(this);
        this.miniMap.view.setDimension(this.mc.player.world.provider.getDimensionType());

        this.chunkManager = new ChunkManager(this);

        this.ready = true;
    }

    // add chunk to the set of loaded chunks
    public void onChunkLoad(Chunk chunk) {
        this.load();
        if (chunk != null && chunk.getWorld() instanceof net.minecraft.client.multiplayer.WorldClient) {
            if (this.ready) {
                this.chunkManager.addChunk(chunk);
            } else {
                MapWriterForge.LOGGER.info("missed chunk ({}, {})", chunk.x, chunk.z);
            }
        }
    }

    // remove chunk from the set of loaded chunks.
    // convert to mwchunk and write chunk to region file if in multiplayer.
    public void onChunkUnload(Chunk chunk) {
        if (this.ready && chunk != null && chunk.getWorld() instanceof net.minecraft.client.multiplayer.WorldClient) {
            this.chunkManager.removeChunk(chunk);
        }
    }

    public void onKeyDown(KeyBinding kb) {

        // make sure not in GUI element (e.g. chat box)
        if (this.mc.currentScreen == null && this.ready) {

            if (kb == MwKeyHandler.keyMapMode) {
                // map mode toggle
                this.miniMap.toggleMap();
            } else if (kb == MwKeyHandler.keyMapGui) {
                // open map gui
                this.mc.displayGuiScreen(new GuiFullScreenMap(this));

            } else if (kb == MwKeyHandler.keyNewMarker) {
                // open new marker dialog
                String group = this.markerManager.getVisibleGroupName();
                if (group.equals("none")) {
                    group = "group";
                }
                if (Config.newMarkerDialog) {
                    this.mc.displayGuiScreen(new GuiMarkerDialogNew(null, this.markerManager, "", group, this.playerXInt, this.playerYInt, this.playerZInt, this.playerDimension));
                } else {
                    this.mc.displayGuiScreen(new GuiMarkerDialog(null, this.markerManager, "", group, this.playerXInt, this.playerYInt, this.playerZInt, this.playerDimension));
                }
            } else if (kb == MwKeyHandler.keyNextGroup) {
                // toggle marker mode
                this.markerManager.nextGroup();
                this.markerManager.update();
                this.mc.player.sendMessage(new TextComponentTranslation("mw.msg.groupselected", this.markerManager.getVisibleGroupName()));

            } else if (kb == MwKeyHandler.keyTeleport) {
                // set or remove marker
                final Marker marker = this.markerManager.getNearestMarkerInDirection(this.playerXInt, this.playerZInt, this.playerHeading);
                if (marker != null) {
                    this.teleportToMarker(marker);
                }
            } else if (kb == MwKeyHandler.keyZoomIn) {
                // zoom in
                this.miniMap.view.adjustZoomLevel(-1);
            } else if (kb == MwKeyHandler.keyZoomOut) {
                // zoom out
                this.miniMap.view.adjustZoomLevel(1);
            } else if (kb == MwKeyHandler.keyUndergroundMode) {
                MapWriter.toggleUndergroundMode();
            }
        }
    }

    // from onTick when mc.currentScreen is an instance of GuiGameOver
    // it's the only option to detect death client side
    public void onPlayerDeath() {

        if (this.ready && Config.maxDeathMarkers > 0) {
            this.updatePlayer();
            final int deleteCount = this.markerManager.countMarkersInGroup("playerDeaths") - Config.maxDeathMarkers + 1;
            for (int i = 0; i < deleteCount; i++) {
                // delete the first marker found in the group "playerDeaths".
                // as new markers are only ever appended to the marker list this
                // will delete the
                // earliest death marker added.
                this.markerManager.delMarker(null, "playerDeaths");
            }

            this.markerManager.addMarker(Utils.getCurrentDateString(), "playerDeaths", this.playerXInt, this.playerYInt, this.playerZInt, this.playerDimension, 0xffff0000);
            this.markerManager.setVisibleGroupName("playerDeaths");
            this.markerManager.update();
        }
    }

    public void onTick() {

        this.load();
        if (this.ready && this.mc.player != null) {
            this.setTextureSize();

            this.updatePlayer();

            // check every tick for a change in underground mode.
            // this makes it posible to change to underground mode in the config
            // screen.
            this.miniMap.view.setUndergroundMode(Config.undergroundMode);

            if (Config.undergroundMode && this.tickCounter % 30 == 0) {
                this.undergroundMapTexture.update();
            }

            if (!(this.mc.currentScreen instanceof GuiFullScreenMap)) {
                // if in game (no gui screen) center the minimap on the player
                // and render it.
                this.miniMap.view.setViewCentreScaled(this.playerX, this.playerZ, this.playerDimension);
                this.miniMap.draw();
            }

            // process background tasks
            int maxTasks = 50;
            while (!this.executor.processTaskQueue() && maxTasks > 0) {
                maxTasks--;
            }

            this.chunkManager.onTick();

            // update GL texture of mapTexture if updated
            this.mapTexture.processTextureUpdates();

            this.tickCounter++;
        }
    }

    public void reloadBlockColours() {

        final BlockColours bc = new BlockColours();
        bc.loadColourData();
        // TODO overrides? -dh
        // TODO save? -dh
        this.blockColours = bc;
    }

    public void reloadMapTexture() {

        this.executor.addTask(new CloseRegionManagerTask(this.regionManager));
        this.executor.close();
        final MapTexture oldMapTexture = this.mapTexture;
        final MapTexture newMapTexture = new MapTexture(this.textureSize, Config.linearTextureScaling);
        this.mapTexture = newMapTexture;
        if (oldMapTexture != null) {
            oldMapTexture.close();
        }
        this.executor = new BackgroundExecutor();
        this.regionManager = new RegionManager(this.worldDir, this.imageDir, this.blockColours, Config.zoomInLevels, Config.zoomOutLevels);

        final UndergroundTexture oldTexture = this.undergroundMapTexture;
        final UndergroundTexture newTexture = new UndergroundTexture(this, this.textureSize, Config.linearTextureScaling);
        this.undergroundMapTexture = newTexture;
        if (oldTexture != null) {
            this.undergroundMapTexture.close();
        }
    }

    public void setTextureSize() {

        if (Config.configTextureSize != this.textureSize) {
            final int maxTextureSize = Render.getMaxTextureSize();
            int newSize = 1024;
            while (newSize <= maxTextureSize && newSize <= Config.configTextureSize) {
                newSize *= 2;
            }
            newSize /= 2;

            MapWriterForge.LOGGER.info("GL reported max texture size = {}", maxTextureSize);
            MapWriterForge.LOGGER.info("texture size from config = {}", Config.configTextureSize);
            MapWriterForge.LOGGER.info("setting map texture size to = {}", newSize);

            this.textureSize = newSize;
            if (this.ready) {
                // if we are already up and running need to close and
                // reinitialize the map texture and
                // region manager.
                this.reloadMapTexture();
            }
        }
    }

    // //////////////////////////////
    // Initialization and Cleanup
    // //////////////////////////////

    // cheap and lazy way to teleport...
    public void teleportTo(int x, int y, int z) {
        if (Config.teleportEnabled) {
            this.mc.player.sendChatMessage(String.format("/%s %d %d %d", Config.teleportCommand, x, y, z));
        } else {
            Utils.printBoth(I18n.format("mw.msg.tpdisabled"));
        }
    }

    public void teleportToMapPos(MapView mapView, int x, int y, int z) {
        if (!Config.teleportCommand.equals("warp")) {
            final double scale = mapView.getDimensionScaling(this.playerDimension);
            this.teleportTo((int) (x / scale), y, (int) (z / scale));
        } else {
            Utils.printBoth(I18n.format("mw.msg.warp.error"));
        }
    }

    // //////////////////////////////
    // Event handlers
    // //////////////////////////////

    public void teleportToMarker(Marker marker) {
        if (Config.teleportCommand.equals("warp")) {
            this.warpTo(marker.name);
        } else if (marker.dimension == this.playerDimension) {
            this.teleportTo(marker.x, marker.y, marker.z);
        } else {
            Utils.printBoth(I18n.format("mw.msg.tp.dimError"));
        }
    }

    public void toggleMarkerMode() {
        this.markerManager.nextGroup();
        this.markerManager.update();
        this.mc.player.sendMessage(new TextComponentTranslation("mw.msg.groupselected", this.markerManager.getVisibleGroupName()));
    }

    public static void toggleUndergroundMode() {
        Config.undergroundMode = !Config.undergroundMode;
        // save the new value of underground mode.
        ConfigurationHandler.configuration.get(Reference.CAT_OPTIONS, "undergroundMode", Config.undergroundModeDef).set(Config.undergroundMode);
    }

    // update the saved player position and orientation
    // called every tick
    public void updatePlayer() {
        // get player pos
        this.playerX = this.mc.player.posX;
        this.playerY = this.mc.player.posY;
        this.playerZ = this.mc.player.posZ;
        this.playerXInt = (int) Math.floor(this.playerX);
        this.playerYInt = (int) Math.floor(this.playerY);
        this.playerZInt = (int) Math.floor(this.playerZ);

        if (this.mc.world != null && !this.mc.world.getChunkFromBlockCoords(new BlockPos(this.playerX, 0, this.playerZ)).isEmpty()) {

            this.playerBiome = this.mc.world.getBiomeForCoordsBody(new BlockPos(this.playerX, 0, this.playerZ)).getBiomeName();
        }

        // rotationYaw of 0 points due north, we want it to point due east
        // instead
        // so add pi/2 radians (90 degrees)
        this.playerHeading = Math.toRadians(this.mc.player.rotationYaw) + Math.PI / 2.0D;
        this.mapRotationDegrees = -this.mc.player.rotationYaw + 180;

        // set by onWorldLoad
        this.playerDimension = this.mc.world.provider.getDimensionType();
        if (this.miniMap.view.getDimension() != this.playerDimension) {
            WorldConfig.getInstance().addDimension(this.playerDimension);
            this.miniMap.view.setDimension(this.playerDimension);
        }
    }

    public void warpTo(String name) {
        if (Config.teleportEnabled) {
            this.mc.player.sendChatMessage(String.format("/warp %s", name));
        } else {
            Utils.printBoth(I18n.format("mw.msg.tpdisabled"));
        }
    }
}

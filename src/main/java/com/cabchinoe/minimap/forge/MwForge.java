package com.cabchinoe.minimap.forge;

import java.io.File;

import com.cabchinoe.minimap.item.itemTPbook;
import com.google.gson.JsonObject;
import com.cabchinoe.minimap.MwUtil;
import com.cabchinoe.minimap.forge.server.MinimapMessage;
import com.cabchinoe.minimap.map.TeamManager;
import com.cabchinoe.minimap.map.TeammateData;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cabchinoe.minimap.Mw;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid="minimap", name="minimap", version="2.1.12", acceptableRemoteVersions = "*")
public class MwForge {
	
	@Instance("minimap")
	public static MwForge instance;
	private MwConfig config;
	
	@SidedProxy(clientSide="com.cabchinoe.minimap.forge.ClientProxy", serverSide="com.cabchinoe.minimap.forge.CommonProxy")
	public static CommonProxy proxy;
	
	public static Logger logger = LogManager.getLogger("minimap");

	public static TeamManager TM = new TeamManager();

	public static final SimpleNetworkWrapper simpleNetworkWrapperInstance = NetworkRegistry.INSTANCE.newSimpleChannel("minimap");
	public static itemTPbook tpbook = new itemTPbook();
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
//        FMLCommonHandler.instance().bus().register(this);
        MinecraftForge.EVENT_BUS.register(this);
        //MwUtil.log(Minecraft.getMinecraft().toString());
//        proxy.preInit(new File(Mw.saveDir,"minimap.cfg"));

		simpleNetworkWrapperInstance.registerMessage(com.cabchinoe.minimap.forge.server.MinimapMessageHandler.class, MinimapMessage.class, 123, Side.SERVER);
		simpleNetworkWrapperInstance.registerMessage(com.cabchinoe.minimap.forge.client.MinimapMessageHandler.class, MinimapMessage.class, 123, Side.CLIENT);

		tpbook = new itemTPbook();
		tpbook.setUnlocalizedName("minimap."+tpbook.itemName);
		tpbook.setCreativeTab(CreativeTabs.TOOLS).setRegistryName("minimap",tpbook.itemName);
		tpbook.setMaxStackSize(1);
//		GameRegistry.registerItem(tpbook,tpbook.itemName,"minimap");
		GameRegistry.findRegistry(Item.class).register(tpbook);
		ModelLoader.setCustomModelResourceLocation(tpbook, 0, new ModelResourceLocation( tpbook.getRegistryName(), "inventory"));
	}

	@EventHandler
	public void load(FMLInitializationEvent event) {
		proxy.load();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit();
	}
	
    @SubscribeEvent
    public void renderMap(RenderGameOverlayEvent.Post event){
        if(event.getType() == RenderGameOverlayEvent.ElementType.ALL){
            Mw.instance.onTick();
        }
    }

    @SubscribeEvent
	@SideOnly(Side.CLIENT)
    public void onConnected(FMLNetworkEvent.ClientConnectedToServerEvent event){
    	if (!event.isLocal()) {
    		//InetSocketAddress address = (InetSocketAddress) event.manager.getSocketAddress();
			Mw.worldName = Minecraft.getMinecraft().getCurrentServerData().serverName;
			initMinimap(new File(Mw.saveDir,"minimap.cfg"));
			//MwUtil.log("WWWWWWWWWWWWWWWWW  %s",Minecraft.getMinecraft().currentServerData.getNBTCompound().getString("name"));
    	}
    }

	private int serverticks = 0;
    @SubscribeEvent
	public void serverTick(TickEvent.ServerTickEvent e){
    	serverticks ++;
    	if(serverticks % 100 ==0){
			JsonObject sendData = TM.get_broadcast_data();
			MwUtil.send_to_All(sendData);
    		serverticks = 0;
		}
	}

	@SubscribeEvent
	public void eventPlayerUpdate(LivingEvent.LivingUpdateEvent e){
    	if(!e.getEntity().world.isRemote && Mw.instance.tickCounter%50==0) {
			if (e.getEntity() instanceof EntityPlayerMP) {
				EntityPlayerMP player = (EntityPlayerMP)e.getEntity();
				if(TM.isVisible(player.getUniqueID().toString())) {
					TM.updateTeammate(player.getUniqueID().toString(), new TeammateData(
						player.getUniqueID().toString(), player.getEntityId(), player.getDisplayNameString(),
						player.posX, player.posY, player.posZ, Math.toRadians(player.rotationYaw) + (Math.PI / 2.0D), player.dimension
					));
				}
			}
		}
	}

	@SubscribeEvent
	public void eventPlayerLeave(PlayerEvent.PlayerLoggedOutEvent e){
		if(!e.player.world.isRemote) {
			if (e.player instanceof EntityPlayerMP) {
				TM.removeTeammate(e.player.getUniqueID().toString());
			}
		}
	}

	@EventHandler
	@SideOnly(Side.CLIENT)
	public void IntergratedServerStart(FMLServerAboutToStartEvent event){
		if(Minecraft.getMinecraft().isIntegratedServerRunning()){
			Mw.saveDir = new File(new File(new File(Mw.mc.mcDataDir, "saves"),Mw.mc.getIntegratedServer().getFolderName()),"minimap");
			MwUtil.log(Mw.saveDir.toString());
			initMinimap(new File(Mw.saveDir,"minimap.cfg"));
		}
	}

	public void initMinimap(File configFile){
		this.config = new MwConfig(configFile);
		Mw mw = new Mw(this.config);
		MinecraftForge.EVENT_BUS.register(new com.cabchinoe.minimap.forge.EventHandler(mw));
	}
}

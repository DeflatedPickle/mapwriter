package com.cabchinoe.minimap.forge;

import java.io.File;

import com.google.gson.JsonObject;
import com.cabchinoe.minimap.MwUtil;
import com.cabchinoe.minimap.forge.server.MinimapMessage;
import com.cabchinoe.minimap.item.itemTPbook;
import com.cabchinoe.minimap.map.TeamManager;
import com.cabchinoe.minimap.map.TeammateData;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cabchinoe.minimap.Mw;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;

@Mod(modid="minimap", name="minimap", version="2.1.7.10", acceptableRemoteVersions = "*")
public class MwForge {
	
	@Instance("minimap")
	public static MwForge instance;
	private MwConfig config;
	
	@SidedProxy(clientSide="com.cabchinoe.minimap.forge.ClientProxy", serverSide="com.cabchinoe.minimap.forge.CommonProxy")
	public static CommonProxy proxy;
	public static Logger logger = LogManager.getLogger("minimap");

	public static TeamManager TM = new TeamManager();

	public static final SimpleNetworkWrapper simpleNetworkWrapperInstance = NetworkRegistry.INSTANCE.newSimpleChannel("minimap");
	public static itemTPbook tpbook;
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(this);
        MinecraftForge.EVENT_BUS.register(this);
        //MwUtil.log(Minecraft.getMinecraft().toString());
//        proxy.preInit(new File(Mw.saveDir,"minimap.cfg"));

		simpleNetworkWrapperInstance.registerMessage(com.cabchinoe.minimap.forge.server.MinimapMessageHandler.class, MinimapMessage.class, 0, Side.SERVER);
		simpleNetworkWrapperInstance.registerMessage(com.cabchinoe.minimap.forge.client.MinimapMessageHandler.class, MinimapMessage.class, 0, Side.CLIENT);

		tpbook = new itemTPbook();
		tpbook.setUnlocalizedName("minimap."+tpbook.itemName);
		tpbook.setCreativeTab(CreativeTabs.tabTools).setTextureName("minimap:"+tpbook.itemName);
		tpbook.setMaxStackSize(1);
		GameRegistry.registerItem(tpbook,tpbook.itemName,"minimap");

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
        if(event.type == RenderGameOverlayEvent.ElementType.ALL){
            Mw.instance.onTick();
        }
    }


    @SubscribeEvent
    public void onConnected(FMLNetworkEvent.ClientConnectedToServerEvent event){
    	if (!event.isLocal) {
			Mw.worldName = Minecraft.getMinecraft().currentServerData.serverName;
			initMinimap(new File(Mw.saveDir,"minimap.cfg"));
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
    	if(!e.entity.worldObj.isRemote && Mw.instance.tickCounter%50==0) {
			if (e.entity instanceof EntityPlayerMP) {
				EntityPlayerMP player = (EntityPlayerMP)e.entity;
				if(TM.isVisible(player.getUniqueID().toString())) {
					TM.updateTeammate(player.getUniqueID().toString(), new TeammateData(
						player.getUniqueID().toString(), player.getEntityId(), player.getDisplayName(),
						player.posX, player.posY, player.posZ, Math.toRadians(player.rotationYaw) + (Math.PI / 2.0D), player.dimension
					));
				}
			}
		}
	}

	@SubscribeEvent
	public void eventPlayerLeave(PlayerEvent.PlayerLoggedOutEvent e){
		if(!e.player.worldObj.isRemote) {
			if (e.player instanceof EntityPlayerMP) {
				TM.removeTeammate(e.player.getUniqueID().toString());
			}
		}
	}

	@EventHandler
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

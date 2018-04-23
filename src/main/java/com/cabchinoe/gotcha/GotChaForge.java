package com.cabchinoe.gotcha;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;

/**
 * Created by n3212 on 2017/9/19.
 */
@Mod(modid=GotChaForge.ModName, name=GotChaForge.ModName, version=GotChaForge.version, dependencies = "", acceptableRemoteVersions="*")
public class GotChaForge {
    public static final String ModName = "gotcha";
    public static final String version = "1.7.10";
    public static HashMap<String,String> ModMap = new HashMap<String, String>();

    @Mod.Instance(ModName)
    public static GotChaForge instance;
    public static Logger logger = LogManager.getLogger(ModName);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(this);
        MinecraftForge.EVENT_BUS.register(this);
        GotCha g = new GotCha();
    }

    @Mod.EventHandler
    public void otherMods(FMLInitializationEvent e){
        List<ModContainer> list = Loader.instance().getActiveModList();
        for(ModContainer mod : list){
            if(mod instanceof FMLModContainer){
                ModMap.put(mod.getModId(),mod.getName());
                GCUtils.log("++++ %s  %s",mod.getModId(),mod.getName());
            }
        }
    }

    @SubscribeEvent
    public void renderMap(RenderGameOverlayEvent.Post event) {
        if (event.type == RenderGameOverlayEvent.ElementType.ALL) {
            GotCha.instance.OnTick();
        }
    }

}

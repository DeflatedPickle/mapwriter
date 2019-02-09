package mapwriter.forge;

import mapwriter.MapWriter;
import mapwriter.api.MapWriterAPI;
import mapwriter.config.ConfigurationHandler;
import mapwriter.overlay.OverlayGrid;
import mapwriter.util.Reference;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, guiFactory = Reference.MOD_GUIFACTORY_CLASS, clientSideOnly = true, acceptedMinecraftVersions = Reference.ACCEPTED_VERSION)
public class MapWriterForge {
    public static final Logger LOGGER = LogManager.getLogger(Reference.MOD_ID);

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START && MapWriter.getInstance().ready && Minecraft.getMinecraft().player == null) {
            MapWriter.getInstance().close();
        }
    }

    @EventHandler
    @SideOnly(Side.CLIENT)
    public void postInit(FMLPostInitializationEvent event) {
        MapWriterAPI.registerDataProvider("grid", new OverlayGrid());
    }

    @EventHandler
    @SideOnly(Side.CLIENT)
    public void preInit(FMLPreInitializationEvent event) {
        ConfigurationHandler.init(event.getSuggestedConfigurationFile());
        MinecraftForge.EVENT_BUS.register(new ConfigurationHandler());
        MinecraftForge.EVENT_BUS.register(new MapWriterEventHandler(MapWriter.getInstance()));
        MinecraftForge.EVENT_BUS.register(new MwKeyHandler());
    }
}

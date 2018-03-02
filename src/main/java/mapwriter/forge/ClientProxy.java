package mapwriter.forge;

import java.io.File;

import mapwriter.Mw;
import mapwriter.api.MwAPI;
import mapwriter.config.ConfigurationHandler;
import mapwriter.overlay.OverlayGrid;
import mapwriter.overlay.OverlaySlime;
import mapwriter.region.MwChunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;

public class ClientProxy extends CommonProxy {

    @Override
    public void load () {

    }

    @Override
    public void postInit () {

        if (Loader.isModLoaded("CarpentersBlocks")) {
            MwChunk.carpenterdata();
        }
        if (Loader.isModLoaded("ForgeMultipart")) {
            MwChunk.FMPdata();

        }
        MwAPI.registerDataProvider("Slime", new OverlaySlime());
        MwAPI.registerDataProvider("Grid", new OverlayGrid());
    }

    @Override
    public void preInit (File configFile) {

        ConfigurationHandler.init(configFile);
        MinecraftForge.EVENT_BUS.register(new ConfigurationHandler());
        final EventHandler eventHandler = new EventHandler(Mw.getInstance());
        MinecraftForge.EVENT_BUS.register(eventHandler);

        final MwKeyHandler keyEventHandler = new MwKeyHandler();
        MinecraftForge.EVENT_BUS.register(keyEventHandler);
    }
}

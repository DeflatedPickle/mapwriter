package com.cabchinoe.minimap.forge;

//import com.cabchinoe.minimap.overlay.OverlaySlime;
import com.cabchinoe.minimap.region.MwChunk;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;

public class ClientProxy extends CommonProxy {


	public void preInit() {

	}

	public void load() {



		Object eventhandler = new MwKeyHandler();
		FMLCommonHandler.instance().bus().register(eventhandler);
		MinecraftForge.EVENT_BUS.register(eventhandler);

		// temporary workaround for user defined key bindings not being loaded
		// at game start. see https://github.com/MinecraftForge/FML/issues/378
		// for more info.
		Minecraft.getMinecraft().gameSettings.loadOptions();
	}

	public void postInit() {
		if (Loader.isModLoaded("CarpentersBlocks")) {
			MwChunk.carpenterdata();
		}
		if (Loader.isModLoaded("ForgeMultipart")) {
			MwChunk.FMPdata();
		}
//		MwAPI.registerDataProvider("Slime", new OverlaySlime());
//		MwAPI.registerDataProvider("Grid", new OverlayGrid());
		// MwAPI.registerDataProvider("Checker", new OverlayChecker());
		// MwAPI.setCurrentDataProvider("Slime");
	}
}

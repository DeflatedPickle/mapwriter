package com.cabchinoe.minimap.forge;

//import com.cabchinoe.minimap.overlay.OverlaySlime;
import com.cabchinoe.minimap.region.MwChunk;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;

public class ClientProxy extends CommonProxy {


	public void preInit() {

	}

	public void load() {

		MinecraftForge.EVENT_BUS.register(new MwKeyHandler());

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

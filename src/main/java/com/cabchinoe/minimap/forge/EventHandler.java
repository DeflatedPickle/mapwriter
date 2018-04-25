package com.cabchinoe.minimap.forge;

import com.cabchinoe.minimap.Mw;
//import com.cabchinoe.minimap.overlay.OverlaySlime;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHandler {
	
	Mw mw;
	
	public EventHandler(Mw mw) {
		this.mw = mw;
	}
	
	@SubscribeEvent
	public void eventChunkLoad(ChunkEvent.Load event){
		if(event.getWorld().isRemote){
			this.mw.onChunkLoad(event.getChunk());
		}
	}
	
	@SubscribeEvent
	public void eventChunkUnload(ChunkEvent.Unload event){
		if(event.getWorld().isRemote){
			this.mw.onChunkUnload(event.getChunk());
		}
	}
	
	@SubscribeEvent
	public void eventWorldLoad(WorldEvent.Load event){
		if(event.getWorld().isRemote){
			this.mw.onWorldLoad(event.getWorld());
		}
	}

    @SubscribeEvent
    public void eventWorldUnload(WorldEvent.Unload event){
        if(event.getWorld().isRemote){
            this.mw.onWorldUnload(event.getWorld());
        }
    }



	@SubscribeEvent
	public void onTextureStitchEventPost(TextureStitchEvent.Post event) {
		if (!this.mw.reloadColours){
			this.mw.reloadBlockColours();
		}
	}
	@SubscribeEvent
	public void onGuiOpenEvent(GuiOpenEvent event) {
		if (event.getGui() instanceof GuiMainMenu && this.mw.reloadColours) {
			this.mw.reloadBlockColours();
			this.mw.reloadColours = false;
		}
	}
}

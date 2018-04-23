package com.cabchinoe.minimap.forge;

import java.util.ArrayList;

import com.cabchinoe.minimap.Mw;
import modwarriors.notenoughkeys.api.Api;
import modwarriors.notenoughkeys.api.KeyBindingPressedEvent;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.Optional;

public class MwKeyHandler {
	
	public static KeyBinding keyMapGui = new KeyBinding("key.mw_open_gui", Keyboard.KEY_M, "minimap");
//	public static KeyBinding keyNewMarker = new KeyBinding("key.mw_new_marker", Keyboard.KEY_INSERT, "minimap");
//	public static KeyBinding keyMapMode = new KeyBinding("key.mw_next_map_mode", Keyboard.KEY_N, "minimap");
//	public static KeyBinding keyNextGroup = new KeyBinding("key.mw_next_marker_group", Keyboard.KEY_COMMA, "minimap");
	public static KeyBinding keyTeleport = new KeyBinding("key.mw_teleport", Keyboard.KEY_PERIOD, "minimap");
	public static KeyBinding keyZoomIn = new KeyBinding("key.mw_zoom_in", Keyboard.KEY_PRIOR, "minimap");
	public static KeyBinding keyZoomOut = new KeyBinding("key.mw_zoom_out", Keyboard.KEY_NEXT, "minimap");
	public static KeyBinding keyUndergroundMode = new KeyBinding("key.mw_underground_mode", Keyboard.KEY_U, "minimap");
	//public static KeyBinding keyQuickLargeMap = new KeyBinding("key.mw_quick_large_map", Keyboard.KEY_NONE);
	public static KeyBinding keySwitchZoom = new KeyBinding("key.mw_switch_zoom", Keyboard.KEY_Z, "minimap");
	public static KeyBinding keyMapScale = new KeyBinding("key.mw_map_scale", Keyboard.KEY_N, "minimap");
	public final KeyBinding[] keys = {
		keyMapGui,
//		keyNewMarker,
		keyMapScale,
//		keyNextGroup,
		keyTeleport,
		keyZoomIn,
		keyZoomOut,
		keyUndergroundMode,
		keySwitchZoom
	};
	
	public MwKeyHandler()
	{
		ArrayList<String> listKeyDescs = new ArrayList<String>();
		// Register bindings
		for (KeyBinding key : this.keys)
		{
			if (key != null)
			{
				ClientRegistry.registerKeyBinding(key);
			}
			listKeyDescs.add(key.getKeyDescription());
			}
		
		if (Loader.isModLoaded("notenoughkeys"))
		{
			Api.registerMod("minimap", listKeyDescs.toArray(new String[0]));
		}
	}
	
	@SubscribeEvent
	public void keyEvent(InputEvent.KeyInputEvent event)
	{
		if (!Loader.isModLoaded("notenoughkeys"))
		{
			this.checkKeys();
		}
	}
	
	@Optional.Method(modid = "notenoughkeys")
	@SubscribeEvent
	public void keyEventSpecial(KeyBindingPressedEvent event) 
	{
		if (event.isKeyBindingPressed)
		{
			Mw.instance.onKeyDown(event.keyBinding);
		}
	}
	
	private void checkKeys() 
	{
		for (KeyBinding key : keys) 
		{
			if (key != null && key.isPressed()) 
			{
				Mw.instance.onKeyDown(key);
			}
		}
	}
}

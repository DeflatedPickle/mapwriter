package com.cabchinoe.minimap.gui;

import com.cabchinoe.minimap.Mw;
import com.cabchinoe.minimap.map.MapView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

public class MwGuiOptionSlot extends GuiSlot {
	
	private GuiScreen parentScreen;
	private Minecraft mc;
	private Mw mw;

	private int mouseX = 0;
	private int mouseY = 0;
	
	private int miniMapPositionIndex = 0;
	private static final String[] miniMapPositionStringArray = {
//		I18n.format("minimap.guislot.miniMapPosition.unchanged"),
		I18n.format("minimap.guislot.miniMapPosition.topRight"),
		I18n.format("minimap.guislot.miniMapPosition.topLeft"),
		I18n.format("minimap.guislot.miniMapPosition.bottomRight"),
		I18n.format("minimap.guislot.miniMapPosition.bottomLeft")
	};
	private static final String[] coordsModeStringArray = {
		I18n.format("minimap.guislot.drawCoords.disabled"),
		I18n.format("minimap.guislot.drawCoords.able")
	};


//	private static final String[] backgroundModeStringArray = {
//		I18n.format("minimap.guislot.backgroundMode.none"),
//		I18n.format("minimap.guislot.backgroundMode.static"),
//		I18n.format("minimap.guislot.backgroundMode.panning")
//	};
	
	private GuiButton[] buttons = new GuiButton[9];
	
    static final ResourceLocation WIDGET_TEXTURE_LOC = new ResourceLocation( "textures/gui/widgets.png" );
	
	public void updateButtonLabel(int i) {
		switch(i) {
		case 0:
			this.buttons[i].displayString = I18n.format("minimap.guislot.drawCoords", coordsModeStringArray[this.mw.coordsMode]);
			break;
//		case 1:
//			this.buttons[i].displayString = I18n.format("minimap.guislot.circularMode", this.mw.miniMap.smallMapMode.circular);
//			break;
//		case 2:
//			this.buttons[i].displayString = I18n.format("minimap.guislot.textureSize", this.mw.configTextureSize);
//			break;
//		case 3:
//			this.buttons[i].displayString = I18n.format("minimap.guislot.textureScaling", (this.mw.linearTextureScalingEnabled ? I18n.format("minimap.guislot.textureScaling.linear") : I18n.format("minimap.guislot.textureScaling.nearest")));
//			break;
		case 1:
			this.buttons[i].displayString = I18n.format("minimap.guislot.trailMarkers", this.mw.playerTrail.enableddesc);
			break;
//		case 5:
//			this.buttons[i].displayString = I18n.format("minimap.guislot.mapColours", (this.mw.useSavedBlockColours ? I18n.format("minimap.guislot.mapColours.frozen") : I18n.format("minimap.guislot.mapColours.auto")));
//			break;
		case 2:
			this.buttons[i].displayString = I18n.format("minimap.guislot.maxDrawDistance", Math.round(Math.sqrt(this.mw.maxChunkSaveDistSq)));
			break;
		case 3:
			this.buttons[i].displayString = I18n.format("minimap.guislot.miniMapSize", this.mw.miniMap.smallMapMode.heightPercent);
			break;
		case 4:
			this.buttons[i].displayString = I18n.format("minimap.guislot.miniMapPosition", miniMapPositionStringArray[this.miniMapPositionIndex]);
			break;
//		case 9:
//			this.buttons[i].displayString = I18n.format("minimap.guislot.mapPixelSnapping", (this.mw.mapPixelSnapEnabled ? I18n.format("minimap.guislot.mapPixelSnapping.enabled") : I18n.format("minimap.guislot.mapPixelSnapping.disabled")));
//			break;
		case 5:
			this.buttons[i].displayString = I18n.format("minimap.guislot.maxDeathMarkers", this.mw.maxDeathMarkers);
			break;
//		case 11:
//			this.buttons[i].displayString = I18n.format("minimap.guislot.backgroundMode", backgroundModeStringArray[this.mw.backgroundTextureMode]);
//			break;
		//case 11:
		//	this.buttons[i].displayString = "Map Lighting: " + (this.mw.lightingEnabled ? "enabled" : "disabled");
		//	break;	
//		case 11:
//			this.buttons[i].displayString = I18n.format("minimap.guislot.oldNewMarkerDialog", (this.mw.newMarkerDialog ? I18n.format("minimap.guislot.oldNewMarkerDialog.new") : I18n.format("minimap.guislot.oldNewMarkerDialog.old")));
//			break;
		case 6:
			this.buttons[i].displayString = I18n.format("minimap.guislot.showDimension", this.mw.enabledesc(this.mw.showDimension));
			break;
		case 7:
			this.buttons[i].displayString = I18n.format("minimap.guislot.lockmapdirection", this.mw.enabledesc(!this.mw.miniMap.smallMapMode.rotate));
			break;
		case 8:
			this.buttons[i].displayString = I18n.format("minimap.guislot.sharepos", this.mw.enabledesc(this.mw.clientTM.visible));
			break;
		default:
			break;
		}
	}
	private MapView mv;
	public MwGuiOptionSlot(GuiScreen parentScreen, Minecraft mc, Mw mw, MapView mv) {
		// GuiSlot(minecraft, width, height, top, bottom, slotHeight)
		super(mc, parentScreen.width, parentScreen.height, 16, parentScreen.height - 32, 25);
		//this.parentScreen = parentScreen;
		this.mw = mw;
		this.mc = mc;
		this.mv = mv;
		this.parentScreen = parentScreen;
		for (int i = 0; i < this.buttons.length; i++) {
			this.buttons[i] = new GuiButton(300 + i, 0, 0, "");
			this.updateButtonLabel(i);
		}
	}
	
	protected boolean keyTyped(char c, int k) {
		if (k == Keyboard.KEY_ESCAPE)
		{
			return true;
		}
		return false;
	}
	
	@Override
	protected int getSize() {
		// number of slots
		return this.buttons.length;
	}

    @Override
	protected void elementClicked(int i, boolean doubleClicked, int x, int y) {
		switch(i) {
		case 0:
	        // toggle coords
			this.mw.toggleCoords();
			break;
//		case 1:
//			// toggle circular
//			this.mw.miniMap.toggleRotating();
//			break;
//		case 2:
//			// toggle texture size
//			this.mw.configTextureSize *= 2;
//			if (this.mw.configTextureSize > 4096) {
//				this.mw.configTextureSize = 2048;
//			}
//			break;
//		case 3:
//			// linear scaling
//			this.mw.linearTextureScalingEnabled = !this.mw.linearTextureScalingEnabled;
//			this.mw.mapTexture.setLinearScaling(this.mw.linearTextureScalingEnabled);
//			//this.mw.undergroundMapTexture.setLinearScaling(this.mw.linearTextureScalingEnabled);
//			break;
		case 1:
			// player trail
			this.mw.playerTrail.toggle_enable();
			break;
//		case 5:
//			// map colours
//			this.mw.useSavedBlockColours = !this.mw.useSavedBlockColours;
//			// reload block colours before saving in case player changed
//			// texture packs before pressing button.
//			this.mw.reloadBlockColours();
//			break;
		case 2:
			// toggle max chunk save dist
			int d = Math.round((float) Math.sqrt(this.mw.maxChunkSaveDistSq));
			d += 32;
			if (d > 256) {
				d = 64;
			}
			this.mw.maxChunkSaveDistSq = d * d;
			break;
		case 3:
			this.mw.miniMap.smallMapMode.toggleHeightPercent();
			break;
		case 4:
			this.miniMapPositionIndex++;
			if (this.miniMapPositionIndex >= miniMapPositionStringArray.length) {
				this.miniMapPositionIndex = 0;
			}
			switch (this.miniMapPositionIndex) {
			case 0:
				// top right position
				this.mw.miniMap.smallMapMode.setMargins(10, -1, -1, 10);
				break;
			case 1:
				// top left position
				this.mw.miniMap.smallMapMode.setMargins(10, -1, 10, -1);
				break;
			case 2:
				// bottom right position
				this.mw.miniMap.smallMapMode.setMargins(-1, 40, -1, 10);
				break;
			case 3:
				// bottom left position
				this.mw.miniMap.smallMapMode.setMargins(-1, 40, 10, -1);
				break;
			default:
				break;
			}
//		case 9:
//			// map scroll pixel snapping
//			this.mw.mapPixelSnapEnabled = !this.mw.mapPixelSnapEnabled;
//			break;
		case 5:
			// max death markers
			this.mw.maxDeathMarkers++;
			if (this.mw.maxDeathMarkers > 10) {
				this.mw.maxDeathMarkers = 0;
			}
			break;
//		case 11:
//			// background texture mode
//			this.mw.backgroundTextureMode = (this.mw.backgroundTextureMode + 1) % 3;
//			break;
		//case 11:
		//	// lighting
		//	this.mw.lightingEnabled = !this.mw.lightingEnabled;
		//	break;
//		case 12:
//			this.mw.newMarkerDialog = !this.mw.newMarkerDialog;
//			break;
		case 6:
			this.mw.showDimension = !this.mw.showDimension;
			if(!this.mw.showDimension){
				this.mv.setDimensionAndAdjustZoom(this.mw.playerDimension);
				this.mv.setViewCentreScaled(this.mw.playerX, this.mw.playerZ, this.mw.playerDimension);
			}
			break;
		case 7:
			this.mw.miniMap.smallMapMode.toggleRotating();
			break;
		case 8:
			this.mw.clientTM.toggle_visible();
			break;
		default:
			break;
		}
		this.updateButtonLabel(i);
	}

	@Override
    public void drawScreen(int mouseX, int mouseY, float f) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        super.drawScreen(mouseX, mouseY, f);
    }
	
	@Override
	protected boolean isSelected(int i) {
		return false;
	}

	@Override
	protected void drawBackground() {
	}

    @Override
    protected void drawSlot(int i, int x, int y, int i4, Tessellator tessellator, int i5, int i6){
        GuiButton button = buttons[i];
        button.xPosition = x;
        button.yPosition = y;
        button.drawButton(this.mc, this.mouseX, this.mouseY);
    }
}

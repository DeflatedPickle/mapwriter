package com.cabchinoe.minimap.gui;

import com.cabchinoe.minimap.Mw;
import com.cabchinoe.minimap.map.MapView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class MwGuiOptionSlot extends GuiSlot {
	
	private GuiScreen parentScreen;
	private Minecraft mc;
	private Mw mw;
	
	private int mouseX = 0;
	private int mouseY = 0;
	
	private int miniMapPositionIndex = 1;
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
//        super.handleMouseInput();
        super.drawScreen(mouseX, mouseY, f);
    }
	
	@Override
	protected boolean isSelected(int i) {
		return false;
	}

	@Override
	protected void drawBackground() {
        if (Mouse.isButtonDown(0) && this.getEnabled())
        {
            if (this.initialClickY == -1)
            {
                boolean flag1 = true;

                if (this.mouseY >= this.top && this.mouseY <= this.bottom)
                {
                    int j2 = (this.width - this.getListWidth()) / 2;
                    int k2 = (this.width + this.getListWidth()) / 2;
                    int l2 = this.mouseY - this.top - this.headerPadding + (int)this.amountScrolled - 4;
                    int i1 = l2 / this.slotHeight;

                    if (i1 < this.getSize() && this.mouseX >= j2 && this.mouseX <= k2 && i1 >= 0 && l2 >= 0)
                    {
                        boolean flag = i1 == this.selectedElement && Minecraft.getSystemTime() - this.lastClicked < 250L;
                        this.elementClicked(i1, flag, this.mouseX, this.mouseY);
                        this.selectedElement = i1;
                        this.lastClicked = Minecraft.getSystemTime();
                    }
                    else if (this.mouseX >= j2 && this.mouseX <= k2 && l2 < 0)
                    {
                        this.clickedHeader(this.mouseX - j2, this.mouseY - this.top + (int)this.amountScrolled - 4);
                        flag1 = false;
                    }

                    int i3 = this.getScrollBarX();
                    int j1 = i3 + 6;

                    if (this.mouseX >= i3 && this.mouseX <= j1)
                    {
                        this.scrollMultiplier = -1.0F;
                        int k1 = this.getMaxScroll();

                        if (k1 < 1)
                        {
                            k1 = 1;
                        }

                        int l1 = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / (float)this.getContentHeight());
                        l1 = MathHelper.clamp(l1, 32, this.bottom - this.top - 8);
                        this.scrollMultiplier /= (float)(this.bottom - this.top - l1) / (float)k1;
                    }
                    else
                    {
                        this.scrollMultiplier = 1.0F;
                    }

                    if (flag1)
                    {
                        this.initialClickY = this.mouseY;
                    }
                    else
                    {
                        this.initialClickY = -2;
                    }
                }
                else
                {
                    this.initialClickY = -2;
                }
            }
            else if (this.initialClickY >= 0)
            {
                this.amountScrolled -= (float)(this.mouseY - this.initialClickY) * this.scrollMultiplier;
                this.initialClickY = this.mouseY;
            }
        }
        else
        {
            this.initialClickY = -1;
        }

        int i2 = Mouse.getEventDWheel();

        if (i2 != 0)
        {
            if (i2 > 0)
            {
                i2 = -1;
            }
            else if (i2 < 0)
            {
                i2 = 1;
            }

            this.amountScrolled += (float)(i2 * this.slotHeight / 2);
        }

	}

	@Override
	protected void drawSlot(int p_192637_1_, int p_192637_2_, int p_192637_3_, int p_192637_4_, int p_192637_5_, int p_192637_6_, float p_192637_7_) {
		GuiButton button = buttons[p_192637_1_];
		button.x = p_192637_2_;
		button.y = p_192637_3_;
		button.drawButton(this.mc, this.mouseX, this.mouseY,p_192637_7_);
	}

}

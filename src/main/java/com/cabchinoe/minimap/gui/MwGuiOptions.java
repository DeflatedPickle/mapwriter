package com.cabchinoe.minimap.gui;


import com.cabchinoe.minimap.Mw;
import com.cabchinoe.minimap.map.MapView;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

public class MwGuiOptions extends GuiScreen {
	
	private final Mw mw;
	private final GuiScreen parentScreen;
	private MwGuiOptionSlot optionSlot = null;
	private MapView mv;
	private GuiButton donebtn;
	public MwGuiOptions(GuiScreen parentScreen, Mw mw, MapView mv) {
		this.mw = mw;
		this.mv = mv;
		this.parentScreen = parentScreen;
	}
	
    @SuppressWarnings("unchecked")
	public void initGui() {
    	this.optionSlot = new MwGuiOptionSlot(this, this.mc, this.mw,this.mv);
        this.optionSlot.registerScrollButtons(7, 8);
        donebtn = new GuiButton(200, (this.width / 2) - 50, this.height - 28, 100, 20, I18n.format("gui.done"));
        this.buttonList.add(donebtn);
    }
    
    protected void actionPerformed(GuiButton button) {
		if (button.id == 200) {
			// done
			// reconfigure texture size
			this.mw.setTextureSize();
			this.mc.displayGuiScreen(this.parentScreen);
		}
	}

    public void drawScreen(int mouseX, int mouseY, float f) {
        this.drawDefaultBackground();
        this.optionSlot.drawScreen(mouseX, mouseY, f);
        this.drawCenteredString(this.fontRendererObj, I18n.format("minimap.guioption.mwOptions"), this.width / 2, 10, 0xffffff);
        super.drawScreen(mouseX, mouseY, f);
    }

    protected void mouseClicked(int x, int y, int button) {
        super.mouseClicked(x, y, button);
    }

    protected void keyTyped(char c, int k) {
        if (this.optionSlot.keyTyped(c, k)) {
            switch (k) {
                case Keyboard.KEY_ESCAPE:
                    this.actionPerformed(donebtn);
                    break;
                default:
                    super.keyTyped(c, k);
                    break;
            }
        }
    }
}

package com.cabchinoe.minimap.gui;

import com.cabchinoe.minimap.api.MwAPI;
import com.cabchinoe.minimap.map.TeamManager;
import com.cabchinoe.minimap.map.TeammateData;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;


@SideOnly(Side.CLIENT)
public class TeammateDialog extends GuiScreen
{
	private final GuiScreen parentScreen;
	private String title = "minimap.gui.teammate.title";
	private String name = "minimap.gui.teammate.name";
	private String colorString = "minimap.gui.teammate.color";
	ScrollableColorSelector ScrollableColorSelectorColor = null;
	boolean backToGameOnSubmit = false;
	static final int dialogWidthPercent = 40;
	static final int elementVSpacing = 20;
	private final TeamManager teamManager;
	private TeammateData editingTeammate;
	private int colour = 0;

	private GuiButton doneButton;
	private int offset = 0;


	public TeammateDialog(GuiScreen parentScreen,
						  TeamManager teamManager, TeammateData editingTeammate) {
		this.teamManager = teamManager;
		this.editingTeammate = editingTeammate;
		this.colour = teamManager.getColor(editingTeammate.getId());
		this.parentScreen = parentScreen;
	}

	public void initGui() {
		parentScreen.setWorldAndResolution(mc,width,height);
		offset =0;
		int labelsWidth = this.fontRenderer.getStringWidth("GroupXXXXXX");
		int width = this.width * dialogWidthPercent / 100 - labelsWidth;
		int x = (this.width - width) / 2 + labelsWidth;
		int y = (this.height - elementVSpacing * 7) / 2;

		this.ScrollableColorSelectorColor = new ScrollableColorSelector(205,x, y
				+ this.elementVSpacing * offset++, width, I18n.format(this.colorString), this);
		this.ScrollableColorSelectorColor.setColor(this.colour);
		this.ScrollableColorSelectorColor.setFocused(true);
		this.ScrollableColorSelectorColor.setDrawArrows(true);
		doneButton = new GuiButton(206,width+2*labelsWidth,y
				+ this.elementVSpacing * (offset+2),60,20,"OK");
	}

	public boolean submit() {
		boolean inputCorrect = true;
		if (this.ScrollableColorSelectorColor.validateColorData())
		{
			this.colour = this.ScrollableColorSelectorColor.getColor();
		}
		else
		{
			inputCorrect = false;
		}
		if (inputCorrect) {
			if (this.editingTeammate != null) {
				this.teamManager.setColor(this.editingTeammate.getId(),this.colour);
			}

		}
		return inputCorrect;
	}


	public void drawScreen(int mouseX, int mouseY, float f) {
		if (this.parentScreen != null) {
			this.parentScreen.drawScreen(mouseX, mouseY, f);
		} else {
			this.drawDefaultBackground();
		}
		int w = this.width * this.dialogWidthPercent / 100;
		int y = (this.height - this.elementVSpacing * 6) / 2 + 2;
		drawRect(
				(this.width - w) / 2,
				(this.height - this.elementVSpacing * 9) / 2 - 4,
				(this.width - w) / 2 + w,
				(this.height - this.elementVSpacing * 8) / 2
						+ this.elementVSpacing * (offset +4),
				0x80000000);
		this.drawCenteredString(
				this.fontRenderer,
				I18n.format(this.title),
				(this.width) / 2,
				(this.height - this.elementVSpacing * 8) / 2
						- this.elementVSpacing / 4,
				0xffffff);
		this.ScrollableColorSelectorColor.draw();
		doneButton.drawButton(mc,mouseX,mouseY,f);
		super.drawScreen(mouseX, mouseY, f);
	}

	// override GuiScreen's handleMouseInput to process
	// the scroll wheel.

	@Override
	public void handleMouseInput()throws IOException {
		if (MwAPI.getCurrentDataProvider() != null)
			return;
		int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
		int y = this.height - Mouse.getEventY() * this.height
				/ this.mc.displayHeight - 1;
		int direction = Mouse.getEventDWheel();
		if (direction != 0) {
			this.mouseDWheelScrolled(x, y, direction);
		}
		super.handleMouseInput();
	}

	public void mouseDWheelScrolled(int x, int y, int direction) {
		this.ScrollableColorSelectorColor.mouseDWheelScrolled(x, y, direction);
	}

	protected void mouseClicked(int x, int y, int button) throws IOException {
		super.mouseClicked(x, y, button);
		this.ScrollableColorSelectorColor.mouseClicked(x,y,button);
		if(doneButton.mousePressed(mc,x,y)){
			if (this.submit()) {
				if (!this.backToGameOnSubmit) {
					this.mc.displayGuiScreen(this.parentScreen);
				} else {
					this.mc.displayGuiScreen(null);
				}
			}
		}
	}

	protected void keyTyped(char c, int key) {
		switch (key) {
		case Keyboard.KEY_ESCAPE:
			this.mc.displayGuiScreen(this.parentScreen);
			break;
		case Keyboard.KEY_RETURN:
			// when enter pressed, submit current input
			if (this.submit()) {
				if (!this.backToGameOnSubmit) {
					this.mc.displayGuiScreen(this.parentScreen);
				} else {
					this.mc.displayGuiScreen(null);
				}
			}
			break;
		case Keyboard.KEY_TAB:
			ScrollableField thisField = null;
			ScrollableField prevField = null;
			ScrollableField nextField = null;

			if (this.ScrollableColorSelectorColor.isFocused())
			{
				thisField = this.ScrollableColorSelectorColor.thisField();
				nextField = this.ScrollableColorSelectorColor.nextField(this.ScrollableColorSelectorColor);
				prevField = this.ScrollableColorSelectorColor.prevField(this.ScrollableColorSelectorColor);
			}

			thisField.setFocused(false);
			if (thisField instanceof ScrollableTextBox)
			{
				((ScrollableTextBox) thisField).textField.setCursorPositionEnd();
			}
			if (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54))
			{
				prevField.setFocused(true);
			}
			else
			{
				nextField.setFocused(true);
			}

			break;
		default:
			if(this.ScrollableColorSelectorColor.isFocused()){
				this.ScrollableColorSelectorColor.KeyTyped(c,key);
			}
			break;
		}
	}
}
package com.cabchinoe.minimap.gui;

import java.io.IOException;

import com.cabchinoe.minimap.MwUtil;
import com.cabchinoe.minimap.api.MwAPI;
import com.cabchinoe.minimap.map.Marker;
import com.cabchinoe.minimap.map.MarkerManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;


@SideOnly(Side.CLIENT)
public class MwGuiMarkerDialog extends GuiScreen
{
	private final GuiScreen parentScreen;
	String title = "";
	String titleNew = "mw.gui.mwguimarkerdialognew.title.new";
	String titleEdit = "mw.gui.mwguimarkerdialognew.title.edit";
	private String editMarkerName = "mw.gui.mwguimarkerdialognew.editMarkerName";
	private String editMarkerGroup = "mw.gui.mwguimarkerdialognew.editMarkerGroup";
	private String editMarkerX = "mw.gui.mwguimarkerdialognew.editMarkerX";
	private String editMarkerY = "mw.gui.mwguimarkerdialognew.editMarkerY";
	private String editMarkerZ = "mw.gui.mwguimarkerdialognew.editMarkerZ";
	private String editMarkerColor = "mw.gui.mwguimarkerdialognew.editMarkerColor";
	ScrollableTextBox scrollableTextBoxName = null;
//	ScrollableTextBox scrollableTextBoxGroup = null;
	ScrollableNumericTextBox scrollableNumericTextBoxX = null;
	ScrollableNumericTextBox scrollableNumericTextBoxY = null;
	ScrollableNumericTextBox scrollableNumericTextBoxZ = null;
	ScrollableColorSelector ScrollableColorSelectorColor = null;
	boolean backToGameOnSubmit = false;
	static final int dialogWidthPercent = 40;
	final int elementVSpacing = 20;
	private final MarkerManager markerManager;
	private Marker editingMarker;
	private String markerName = "";
	private String markerGroup = "";
	private int markerX = 0;
	private int markerY = 80;
	private int markerZ = 0;
	private int dimension = 0;
	private int colour = 0;

	private GuiButton doneButton;
	private int offset = 0;

	public MwGuiMarkerDialog(GuiScreen parentScreen,
			MarkerManager markerManager, String markerName, String markerGroup,
			int x, int y, int z, int dimension) {
		this.markerManager = markerManager;
		this.markerName = markerName;
		this.markerGroup = markerGroup;
		this.markerX = x;
		this.markerY = y;
		this.markerZ = z;
		this.editingMarker = null;
		this.dimension = dimension;
		this.colour = MwUtil.getCurrentColour();
		this.parentScreen = parentScreen;
		this.title = this.titleNew;
	}

	public MwGuiMarkerDialog(GuiScreen parentScreen,
			MarkerManager markerManager, Marker editingMarker) {
		this.markerManager = markerManager;
		this.editingMarker = editingMarker;
		this.markerName = editingMarker.name;
		this.markerGroup = editingMarker.groupName;
		this.markerX = editingMarker.x;
		this.markerY = editingMarker.y;
		this.markerZ = editingMarker.z;
		this.dimension = editingMarker.dimension;
		this.colour = editingMarker.colour;
		this.parentScreen = parentScreen;
		this.title = this.titleEdit;
	}

	public boolean submit() {
		boolean inputCorrect = true;
		if (scrollableTextBoxName.validateTextFieldData())
			this.markerName = scrollableTextBoxName.textField.getText();
		else
			inputCorrect = false;
//		if (scrollableTextBoxGroup.validateTextFieldData())
//			this.markerGroup = scrollableTextBoxGroup.textField.getText();
//		else
//			inputCorrect = false;
		if (scrollableNumericTextBoxX.validateTextFieldData())
			this.markerX = scrollableNumericTextBoxX.getTextFieldIntValue();
		else
			inputCorrect = false;
		if (scrollableNumericTextBoxY.validateTextFieldData())
			this.markerY = scrollableNumericTextBoxY.getTextFieldIntValue();
		else
			inputCorrect = false;
		if (scrollableNumericTextBoxZ.validateTextFieldData())
			this.markerZ = scrollableNumericTextBoxZ.getTextFieldIntValue();
		else
			inputCorrect = false;
		if (this.ScrollableColorSelectorColor.validateColorData())
		{
			this.colour = this.ScrollableColorSelectorColor.getColor();
		}
		else
		{
			inputCorrect = false;
		}
		if (inputCorrect) {
			if (this.editingMarker != null) {
//				colour = this.editingMarker.colour;
				this.markerManager.delMarker(this.editingMarker);
				this.editingMarker = null;
			}
			this.editingMarker = this.markerManager.addMarker(this.markerName, this.markerGroup,
					this.markerX, this.markerY, this.markerZ, this.dimension,
					colour);
			this.markerManager.selectedMarker = this.editingMarker;
//			this.markerManager.setVisibleGroupName(this.markerGroup);
			this.markerManager.update();
		}
		return inputCorrect;
	}

	public void initGui() {
		parentScreen.setWorldAndResolution(mc,width,height);
		offset =0;
		int labelsWidth = this.fontRenderer.getStringWidth("Group");
		int width = this.width * dialogWidthPercent / 100 - labelsWidth;
		int x = (this.width - width) / 2 + labelsWidth;
		int y = (this.height - elementVSpacing * 7) / 2;
		this.scrollableTextBoxName = new ScrollableTextBox(201,x, y
				+ this.elementVSpacing * offset++, width, I18n.format(this.editMarkerName),this);
		this.scrollableTextBoxName.textField.setText(this.markerName);
//		this.scrollableTextBoxGroup = new ScrollableTextBox(x, y
//				+ this.elementVSpacing, width, I18n.format(this.editMarkerGroup),
//				this.markerManager.groupList);
//		this.scrollableTextBoxGroup.init();
//		this.scrollableTextBoxGroup.textField.setText(this.markerGroup);
//		this.scrollableTextBoxGroup.setDrawArrows(true);
		this.scrollableNumericTextBoxX = new ScrollableNumericTextBox(202, x, y
				+ this.elementVSpacing * offset++, width, I18n.format(this.editMarkerX), this);
		this.scrollableNumericTextBoxX.textField.setText("" + this.markerX);
		this.scrollableNumericTextBoxX.setDrawArrows(true);
		this.scrollableNumericTextBoxY = new ScrollableNumericTextBox(203, x, y
				+ this.elementVSpacing * offset++, width, I18n.format(this.editMarkerY), this);
		this.scrollableNumericTextBoxY.textField.setText("" + this.markerY);
		this.scrollableNumericTextBoxY.setDrawArrows(true);
		this.scrollableNumericTextBoxZ = new ScrollableNumericTextBox(204, x, y
				+ this.elementVSpacing * offset++, width, I18n.format(this.editMarkerZ), this);
		this.scrollableNumericTextBoxZ.textField.setText("" + this.markerZ);
		this.scrollableNumericTextBoxZ.setDrawArrows(true);
		if(!showCood())
			offset = 0;
		this.ScrollableColorSelectorColor = new ScrollableColorSelector(205,x, y
				+ this.elementVSpacing * offset++, width, I18n.format(this.editMarkerColor), this);
		this.ScrollableColorSelectorColor.setColor(this.colour);
		this.ScrollableColorSelectorColor.setDrawArrows(true);
		if(showCood()){
			this.scrollableTextBoxName.textField.setFocused(true);
		}else {
			this.ScrollableColorSelectorColor.setFocused(true);
		}
		doneButton = new GuiButton(206,width+2*labelsWidth,y
				+ this.elementVSpacing * (offset+2),60,20,"OK");
	}

	private boolean showCood(){
		if(editingMarker !=  null  && editingMarker.groupName.equals("playerDeaths"))
			return false;
		return true;
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
		if(showCood()) {
			this.scrollableTextBoxName.draw();
	//		this.scrollableTextBoxGroup.draw();
			this.scrollableNumericTextBoxX.draw();
			this.scrollableNumericTextBoxY.draw();
			this.scrollableNumericTextBoxZ.draw();
		}
		this.ScrollableColorSelectorColor.draw();
		doneButton.drawButton(mc,mouseX,mouseY,f);
		super.drawScreen(mouseX, mouseY, f);
	}

	// override GuiScreen's handleMouseInput to process
	// the scroll wheel.

	@Override
	public void handleMouseInput() throws IOException {
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
		if(showCood()) {
			this.scrollableTextBoxName.mouseDWheelScrolled(x, y, direction);
//			this.scrollableTextBoxGroup.mouseDWheelScrolled(x, y, direction);
			this.scrollableNumericTextBoxX.mouseDWheelScrolled(x, y, direction);
			this.scrollableNumericTextBoxY.mouseDWheelScrolled(x, y, direction);
			this.scrollableNumericTextBoxZ.mouseDWheelScrolled(x, y, direction);
		}
		this.ScrollableColorSelectorColor.mouseDWheelScrolled(x, y, direction);
	}

	protected void mouseClicked(int x, int y, int button) throws IOException {
		super.mouseClicked(x, y, button);
		if(showCood()) {
			this.scrollableTextBoxName.mouseClicked(x, y, button);
//			this.scrollableTextBoxGroup.mouseClicked(x, y, button);
			this.scrollableNumericTextBoxX.mouseClicked(x, y, button);
			this.scrollableNumericTextBoxY.mouseClicked(x, y, button);
			this.scrollableNumericTextBoxZ.mouseClicked(x, y, button);
		}
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

			if (this.scrollableTextBoxName.isFocused())
			{
				thisField = this.scrollableTextBoxName;
				prevField = this.ScrollableColorSelectorColor;
				nextField = this.scrollableNumericTextBoxX;
			}
//			else if (this.scrollableTextBoxGroup.textField.isFocused())
//			{
//				thistextField = scrollableTextBoxGroup.textField;
//				prevtextField = scrollableTextBoxName.textField;
//				nexttextField = scrollableNumericTextBoxX.textField;
//			}
			else if (this.scrollableNumericTextBoxX.textField.isFocused())
			{
				thisField = scrollableNumericTextBoxX;
				prevField = scrollableTextBoxName;
				nextField = scrollableNumericTextBoxY;
			}
			else if (this.scrollableNumericTextBoxY.textField.isFocused())
			{
				thisField = scrollableNumericTextBoxY;
				prevField = scrollableNumericTextBoxX;
				nextField = scrollableNumericTextBoxZ;
			}
			else if (this.scrollableNumericTextBoxZ.textField.isFocused())
			{
				thisField = scrollableNumericTextBoxZ;
				prevField = scrollableNumericTextBoxY;
				nextField = ScrollableColorSelectorColor;
			}
			else if (this.ScrollableColorSelectorColor.isFocused())
			{
				thisField = this.ScrollableColorSelectorColor.thisField();
				if(showCood()) {
					nextField = this.ScrollableColorSelectorColor.nextField(this.scrollableTextBoxName);
					prevField = this.ScrollableColorSelectorColor.prevField(this.scrollableNumericTextBoxZ);
				}else {
					nextField = this.ScrollableColorSelectorColor.nextField(this.ScrollableColorSelectorColor);
					prevField = this.ScrollableColorSelectorColor.prevField(this.ScrollableColorSelectorColor);
				}
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
			if (this.scrollableTextBoxName.textField.isFocused())
				this.scrollableTextBoxName.textField.textboxKeyTyped(c, key);
//			else if (this.scrollableTextBoxGroup.textField.isFocused())
//				this.scrollableTextBoxGroup.textField.textboxKeyTyped(c, key);
			else if (this.scrollableNumericTextBoxX.textField.isFocused())
				this.scrollableNumericTextBoxX.validateTextboxKeyTyped(c, key);
			else if (this.scrollableNumericTextBoxY.textField.isFocused())
				this.scrollableNumericTextBoxY.validateTextboxKeyTyped(c, key);
			else if (this.scrollableNumericTextBoxZ.textField.isFocused())
				this.scrollableNumericTextBoxZ.validateTextboxKeyTyped(c, key);
			else if(this.ScrollableColorSelectorColor.isFocused()){
				this.ScrollableColorSelectorColor.KeyTyped(c,key);
			}
			break;
		}
	}
}
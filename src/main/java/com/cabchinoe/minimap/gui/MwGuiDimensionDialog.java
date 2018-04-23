package com.cabchinoe.minimap.gui;

import com.cabchinoe.minimap.Mw;
import com.cabchinoe.minimap.map.MapView;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class MwGuiDimensionDialog extends GuiScreen {

	final Mw mw;
	final MapView mapView;
	final int dimension;
	static final int dialogWidthPercent = 40;
	static final int elementVSpacing = 20;
	ScrollableNumericTextBox scrollableNumericTextBoxD = null;
	private GuiButton doneButton;
	private GuiScreen parentScreen;
	boolean backToGameOnSubmit = false;
	boolean showError = false;
	private List<String> DimensionList = new ArrayList<String>(3);

	public MwGuiDimensionDialog(GuiScreen parentScreen, Mw mw, MapView mapView, int dimension) {
//        super(parentScreen, , I18n.format("mw.gui.mwguidimensiondialog.error"));
		this.mw = mw;
		this.mapView = mapView;
		this.dimension = dimension;
		this.parentScreen = parentScreen;
		this.DimensionList =DimensionList;
		this.DimensionList.add("-1");
		this.DimensionList.add("0");
		this.DimensionList.add("1");

	}

	public void initGui(){
		parentScreen.setWorldAndResolution(mc,width,height);
		int labelsWidth = this.fontRendererObj.getStringWidth("Group");
		int width = this.width * dialogWidthPercent / 100 - labelsWidth;
		int x = (this.width - width) / 2 + labelsWidth;
		int y = (this.height - elementVSpacing * 5) / 2;
		scrollableNumericTextBoxD = new ScrollableNumericTextBox(199,x, y, width, "",this,this.DimensionList);
		scrollableNumericTextBoxD.textField.setText(String.valueOf(this.dimension));
		scrollableNumericTextBoxD.setDrawArrows(true);
		doneButton = new GuiButton(5,width+2*labelsWidth,y
				+ this.elementVSpacing * 2,60,20,"OK");
	}

	public void drawScreen(int mouseX, int mouseY, float f) {
		if (this.parentScreen != null) {
			this.parentScreen.drawScreen(mouseX, mouseY, f);
		} else {
			this.drawDefaultBackground();
		}
		int w = this.width * this.dialogWidthPercent / 100;
		int y = (this.height - this.elementVSpacing * 5) / 2 + 2;
		drawRect(
				(this.width - w) / 2,
				(this.height - this.elementVSpacing * 7) / 2 - 4,
				(this.width - w) / 2 + w,
				(this.height - this.elementVSpacing * 7) / 2
						+ this.elementVSpacing * (4+(this.showError?1:0)),
				0x80000000);
		this.drawCenteredString(
				this.fontRendererObj,
				I18n.format("mw.gui.mwguidimensiondialog.title"),
				(this.width) / 2,
				(this.height - this.elementVSpacing * 6) / 2
						- this.elementVSpacing / 4,
				0xffffff);
		this.drawCenteredString(
				this.fontRendererObj,
				I18n.format("mw.gui.mwguidimensiondialog.note"),
				this.width / 2,
				y+ this.elementVSpacing * 1,
				0xffffff);

		if (this.showError) {
			this.drawCenteredString(
					this.fontRendererObj,
					I18n.format("mw.gui.mwguidimensiondialog.error"),
					this.width / 2,
					y+ this.elementVSpacing * 3,
					0xffffff);
		}
		scrollableNumericTextBoxD.draw();
		doneButton.drawButton(mc,mouseX,mouseY);
		super.drawScreen(mouseX, mouseY, f);
	}


	@Override
	public void handleMouseInput() {
		int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
		int y = this.height - Mouse.getEventY() * this.height
				/ this.mc.displayHeight - 1;
		int direction = Mouse.getEventDWheel();
		if (direction != 0) {
			this.scrollableNumericTextBoxD.mouseDWheelScrolled(x, y, direction);
		}
		super.handleMouseInput();
	}

	protected void mouseClicked(int x, int y, int button) {
		super.mouseClicked(x, y, button);
		this.scrollableNumericTextBoxD.mouseClicked(x, y, button);
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

	public boolean submit() {
		boolean done = false;
		if (this.scrollableNumericTextBoxD.validateTextFieldData()) {
			int dimension = this.dimension;
			try {
				dimension = this.scrollableNumericTextBoxD.getTextFieldIntValue();
				if(dimension>=-1 && dimension <=1){
					this.showError = false;
					this.mapView.setDimensionAndAdjustZoom(dimension);
//    				this.mw.miniMap.view.setDimension(dimension);
					this.mw.addDimension(dimension);
					done = true;
				}else{
					this.showError = true;
				}
			}
			catch (NumberFormatException e) {
				this.showError = true;
			}
		}
		return done;
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
			default:
				if (this.scrollableNumericTextBoxD.textField.isFocused())
					this.scrollableNumericTextBoxD.textField.textboxKeyTyped(c, key);
				break;
		}
	}

}

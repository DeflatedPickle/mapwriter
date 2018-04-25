package com.cabchinoe.minimap.gui;

import com.cabchinoe.minimap.MwUtil;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import java.util.ArrayList;

public class ScrollableColorSelector extends ScrollableField
{
	private String editRed = "mw.gui.ScrollableColorSelector.Red";
	private String editGreen = "mw.gui.ScrollableColorSelector.Green";
	private String editBlue = "mw.gui.ScrollableColorSelector.Blue";

	private ScrollableNumericTextBox ScrollableNumericTextBoxColourRed;
	private ScrollableNumericTextBox ScrollableNumericTextBoxColourGreen;
	private ScrollableNumericTextBox ScrollableNumericTextBoxColourBlue;

	private int colour = 0;

	private int colourFieldX = 0;
	private int colourFieldY = 0;
	private int colourFieldW = 0;
	private int colourFieldH = 0;
	private int elementVSpacing = 20;

	private ArrayList<String> colorlist = new ArrayList<String>(256);


	public ScrollableColorSelector(int id, int x, int y, int width, String label, GuiScreen screen)
	{
		super(id, x, y, width, label, screen);
		this.init();
	}
	public ScrollableColorSelector(int id, int x, int y, int width, String label, GuiScreen screen,int elementVSpacing)
	{
		super(id, x, y, width, label, screen);
		this.elementVSpacing = elementVSpacing;
		this.init();
	}

	public void init() {
		this.arrowsY = this.y + this.elementVSpacing;
		int textboxWidth = 16;
		int x1 = this.x + this.arrowsWidth + this.fontRendererObj.getStringWidth(I18n.format(this.editGreen)) + 4;
		int w = (this.arrowsWidth * 2) + this.fontRendererObj.getStringWidth("99999999") + textboxWidth;

		for(int c =0; c<256;++c){
			colorlist.add(Integer.toString(c));
		}

		this.ScrollableNumericTextBoxColourRed = new ScrollableNumericTextBox(210,x1, this.y, w, I18n.format(this.editRed),this.screen,colorlist);
		this.ScrollableNumericTextBoxColourRed.setDrawArrows(true);
		this.ScrollableNumericTextBoxColourRed.setMaxValue(255);
		this.ScrollableNumericTextBoxColourRed.setMinValue(0);

		this.ScrollableNumericTextBoxColourGreen = new ScrollableNumericTextBox(211,x1, this.y + elementVSpacing, w, I18n.format(this.editGreen), this.screen,colorlist);
		this.ScrollableNumericTextBoxColourGreen.setDrawArrows(true);
		this.ScrollableNumericTextBoxColourGreen.setMaxValue(255);
		this.ScrollableNumericTextBoxColourGreen.setMinValue(0);

		this.ScrollableNumericTextBoxColourBlue = new ScrollableNumericTextBox(212,x1, this.y + (elementVSpacing * 2), w, I18n.format(this.editBlue), this.screen,colorlist);
		this.ScrollableNumericTextBoxColourBlue.setDrawArrows(true);
		this.ScrollableNumericTextBoxColourBlue.setMaxValue(255);
		this.ScrollableNumericTextBoxColourBlue.setMinValue(0);

		this.colourFieldX = x1 + w - this.elementVSpacing;
		this.colourFieldY = this.y + 10;
		this.colourFieldW = this.width - w - (ScrollableField.arrowsWidth * 3) - this.fontRendererObj.getStringWidth(I18n.format(this.editGreen)) - 8;
		this.colourFieldH = this.elementVSpacing * 3/2;
	}

	@Override
	public void nextElement()
	{
		this.setColor(MwUtil.getNextColour());
	}

	@Override
	public void previousElement()
	{
		this.setColor(MwUtil.getPrevColour());
	}

	public void setFocused(Boolean focus)
	{
		this.ScrollableNumericTextBoxColourRed.textField.setFocused(focus);
	}

	public boolean isFocused()
	{
		if (this.ScrollableNumericTextBoxColourRed.textField.isFocused() || this.ScrollableNumericTextBoxColourGreen.textField.isFocused() || this.ScrollableNumericTextBoxColourBlue.textField.isFocused())
		{
			return true;
		}
		return false;
	}

	public boolean validateColorData()
	{
		return ((this.ScrollableNumericTextBoxColourRed.textField.getText().length() > 0) && (this.ScrollableNumericTextBoxColourGreen.textField.getText().length() > 0) && (this.ScrollableNumericTextBoxColourBlue.textField.getText().length() > 0));
	}

	public int getColor()
	{
		return this.colour;
	}

	public void setColor(int colour)
	{
		this.colour = colour;

		int red = (colour >> 16) & 0xff;
		int green = (colour >> 8) & 0xff;
		int blue = (colour) & 0xff;
		this.ScrollableNumericTextBoxColourRed.setText(red);
		this.ScrollableNumericTextBoxColourGreen.setText(green);
		this.ScrollableNumericTextBoxColourBlue.setText(blue);
	}

	private void UpdateColour()
	{
		int colour = 0xff << 24;
		colour += this.ScrollableNumericTextBoxColourRed.getTextFieldIntValue() << 16;
		colour += this.ScrollableNumericTextBoxColourGreen.getTextFieldIntValue() << 8;
		colour += this.ScrollableNumericTextBoxColourBlue.getTextFieldIntValue();
		this.colour = colour;
	}

	@Override
	public void mouseClicked(int x, int y, int button)
	{
		super.mouseClicked(x, y, button);
		this.ScrollableNumericTextBoxColourRed.mouseClicked(x, y, button);
		this.ScrollableNumericTextBoxColourGreen.mouseClicked(x, y, button);
		this.ScrollableNumericTextBoxColourBlue.mouseClicked(x, y, button);
	}

	public void mouseDWheelScrolled(int x, int y, int direction)
	{
		this.ScrollableNumericTextBoxColourRed.mouseDWheelScrolled(x, y, direction);
		this.ScrollableNumericTextBoxColourGreen.mouseDWheelScrolled(x, y, direction);
		this.ScrollableNumericTextBoxColourBlue.mouseDWheelScrolled(x, y, direction);

		if (this.posWithinColourField(x, y))
		{
			this.colourFieldScroll(-direction);
		}
	}

	public boolean posWithinColourField(int x, int y)
	{
		return (x >= this.colourFieldX) && (y >= this.colourFieldY) && (x <= (this.colourFieldW + this.colourFieldX)) && (y <= (this.colourFieldH + this.colourFieldY));
	}

	public void colourFieldScroll(int direction)
	{
		if (direction > 0)
		{
			this.nextElement();
		}
		else if (direction < 0)
		{
			this.previousElement();
		}
	}

	public void KeyTyped(char c, int key)
	{
		this.ScrollableNumericTextBoxColourRed.validateTextboxKeyTyped(c, key);
		this.ScrollableNumericTextBoxColourGreen.validateTextboxKeyTyped(c, key);
		this.ScrollableNumericTextBoxColourBlue.validateTextboxKeyTyped(c, key);
	}

	@Override
	public void draw()
	{
		super.draw();
		this.ScrollableNumericTextBoxColourRed.draw();
		this.ScrollableNumericTextBoxColourGreen.draw();
		this.ScrollableNumericTextBoxColourBlue.draw();

		this.UpdateColour();

		drawRect(this.colourFieldX - 1, this.colourFieldY - 1, this.colourFieldX + this.colourFieldW + 1, this.colourFieldY + this.colourFieldH + 1, 0xff000000);
		drawRect(this.colourFieldX, this.colourFieldY, this.colourFieldX + this.colourFieldW, this.colourFieldY + this.colourFieldH, this.colour);
	}

	public ScrollableField thisField()
	{
		if (this.ScrollableNumericTextBoxColourRed.textField.isFocused())
		{
			return this.ScrollableNumericTextBoxColourRed;
		}
		if (this.ScrollableNumericTextBoxColourGreen.textField.isFocused())
		{
			return this.ScrollableNumericTextBoxColourGreen;
		}
		if (this.ScrollableNumericTextBoxColourBlue.textField.isFocused())
		{
			return this.ScrollableNumericTextBoxColourBlue;
		}
		return this.ScrollableNumericTextBoxColourRed;
	}

	public ScrollableField nextField(ScrollableField field)
	{
		if (this.ScrollableNumericTextBoxColourRed.textField.isFocused())
		{
			return this.ScrollableNumericTextBoxColourGreen;
		}
		if (this.ScrollableNumericTextBoxColourGreen.textField.isFocused())
		{
			return this.ScrollableNumericTextBoxColourBlue;
		}
		return field;
	}

	public ScrollableField prevField(ScrollableField field)
	{
		if (this.ScrollableNumericTextBoxColourGreen.textField.isFocused())
		{
			return this.ScrollableNumericTextBoxColourRed;
		}
		if (this.ScrollableNumericTextBoxColourBlue.textField.isFocused())
		{
			return this.ScrollableNumericTextBoxColourGreen;
		}
		return field;
	}
}

package com.cabchinoe.minimap.gui;

import com.cabchinoe.common.Render;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

public abstract class ScrollableField extends GuiScreen {
	public int x;
	public int y;
	public int width;
	public int textFieldHeight = 12;
	public int labelX;
	public int labelY;
	public int labelWidth;
	public int labelHeight;
	public String label;

	private boolean drawArrows = false;
	private int leftArrowX;
	private int rightArrowX;
	public int arrowsY;
	public int textFieldX;
	public int textFieldY;
	public int textFieldWidth;
	public static int arrowsWidth = 7;
	private int arrowsHeight = 12;

	public final FontRenderer fontRendererObj;
	public final int id;
	public GuiScreen screen;
	private ResourceLocation leftArrowTexture = new ResourceLocation(
			"minimap", "textures/map/arrow_text_left.png");
	private ResourceLocation rightArrowTexture = new ResourceLocation(
			"minimap", "textures/map/arrow_text_right.png");

	public ScrollableField(int id, int x, int y, int width, String label, GuiScreen screen) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.id = id;
		this.screen = screen;
		this.fontRendererObj = screen.mc.fontRenderer;
		this.label = label;
		this.textFieldX = this.x + this.arrowsWidth;
		this.textFieldY = this.y;
		this.textFieldWidth = this.width - this.arrowsWidth * 2 - 25;
		this.leftArrowX = this.x - 1;
		this.rightArrowX = this.textFieldX + this.textFieldWidth + 1;
		this.arrowsY = this.y;

		this.labelWidth = fontRendererObj.getStringWidth(this.label);
		this.labelHeight = this.fontRendererObj.FONT_HEIGHT;
		this.labelX = this.x - this.labelWidth - 4;
		this.labelY = this.y + this.labelHeight / 2 - 2;
	}

	public void draw() {
		TextureManager renderEngine = Minecraft.getMinecraft().renderEngine;
		// Render.drawRectBorder(labelX, y, width + this.labelWidth + 4,
		// this.arrowsHeight, 2);

		// draw the description label
		this.drawString(this.fontRendererObj, this.label, this.labelX, this.labelY, 0xffffff);

		if (this.drawArrows) {
			renderEngine.bindTexture(leftArrowTexture);
			Render.drawTexturedRect(this.leftArrowX, this.arrowsY, ScrollableField.arrowsWidth, this.arrowsHeight, 0.0, 0.0, 1.0, 1.0);
			renderEngine.bindTexture(rightArrowTexture);
			Render.drawTexturedRect(this.rightArrowX, this.arrowsY, ScrollableField.arrowsWidth, this.arrowsHeight, 0.0, 0.0, 1.0, 1.0);
		}
	}

	public void setDrawArrows(boolean value) {
		this.drawArrows = value;
	}

	public void mouseClicked(int x, int y, int button) {
		int direction = this.posWithinArrows(x, y);
		if (direction == 1) {
			this.nextElement();
		} else if (direction == -1) {
			this.previousElement();
		}
	}

	/**
	 * @return Returns clicked arrow: 1 for right and -1 for left
	 */
	public int posWithinArrows(int x, int y) {
		if ((x >= this.leftArrowX) && (y >= this.arrowsY) && (x <= (ScrollableField.arrowsWidth + this.leftArrowX)) && (y <= (this.arrowsHeight + this.arrowsY))) {
			return -1;
		} else if ((x >= this.rightArrowX) && (y >= this.arrowsY) && (x <= (ScrollableField.arrowsWidth + this.rightArrowX)) && (y <= (this.arrowsHeight + this.arrowsY))) {
			return 1;
		} else {
			return 0;
		}
	}

	public abstract void nextElement();

	public abstract void previousElement();

	public abstract void setFocused(Boolean focus);

	public abstract Boolean isFocused();
}
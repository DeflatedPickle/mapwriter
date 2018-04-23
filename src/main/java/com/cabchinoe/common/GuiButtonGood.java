package com.cabchinoe.common;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

/**
 * Created by n3212 on 2017/7/10.
 */
public class GuiButtonGood extends GuiButton {

    public final static int LINE_HEIGHT = 11;

    private int hoverTime = 0;
    private long prevSystemTime = 0;
    public  int TOOLTIP_DELAY = 800;

    private String tooltip = null;
    private String[] tooltipLines = null;
    private int tooltipWidth = -1;
    private boolean drawBackground = true;

    private ResourceLocation resourceButtonCustom = null;

    private ResourceLocation resourceButtonCustomPressed =  null;

    private double TexturePositionX = this.xPosition;
    private double TexturePositionY = this.yPosition;
    private double TextureWidth = this.width;
    private double TextureHeight = this.height;


    public GuiButtonGood(int id, int x, int y,int w,int h, String displayString, String tooltip, String pkgname1,String resource1, String pkgname2,String resource2) {
        super(id, x, y, w, h, displayString);
        this.tooltip = tooltip;
        this.setTooltip(this.tooltip);
        if(pkgname1!=null && resource1 !=null) {
            this.resourceButtonCustom = new ResourceLocation(pkgname1, resource1);
        }
        if(pkgname2!=null && resource2 !=null) {
            this.resourceButtonCustomPressed = new ResourceLocation(pkgname2, resource2);
        }
    }

    public GuiButtonGood(int id, int x, int y,int w,int h, String displayString, String tooltip) {
        super(id, x, y, w, h, displayString);
        this.tooltip = tooltip;
        this.setTooltip(this.tooltip);
    }

    public GuiButtonGood(int id, int x, int y, int w, int h, String displayString, String tooltip, boolean drawBackground) {
        super(id, x, y, w, h, displayString);
        this.drawBackground = drawBackground;
        this.tooltip = tooltip;
        this.setTooltip(this.tooltip);
    }

    public void setTexturePosition(double x,double y, double w,double h){
        this.TexturePositionX = x;
        this.TexturePositionY = y;
        this.TextureHeight = h;
        this.TextureWidth = w;
    }

    @Override
    public void drawButton(Minecraft minecraft, int i, int j) {
        if(this.drawBackground) {
            super.drawButton(minecraft, i, j);
        } else {
            this.drawString(minecraft.fontRenderer, this.displayString, this.xPosition,this.yPosition + (this.height - 8) / 2, 0x999999);
        }
        if(resourceButtonCustomPressed !=null || resourceButtonCustom!=null) {
            if (resourceButtonCustom != null) {
                minecraft.renderEngine.bindTexture(resourceButtonCustom);
            }
            if (resourceButtonCustomPressed != null && isMouseOverButton(i, j)) {
                minecraft.renderEngine.bindTexture(resourceButtonCustomPressed);
            }
            Render.drawTexturedRect(
                    TexturePositionX, TexturePositionY, TextureWidth, TextureHeight,
                    0, 0, 1, 1
            );
        }
        if(tooltipLines != null) {
            // Compute hover time
            if(isMouseOverButton(i, j)) {
                long systemTime = System.currentTimeMillis();
                if(prevSystemTime != 0) {
                    hoverTime += systemTime - prevSystemTime;
                }
                prevSystemTime = systemTime;
            } else {
                hoverTime = 0;
                prevSystemTime = 0;
            }

            // Draw tooltip if hover time is long enough
            if(hoverTime > TOOLTIP_DELAY && tooltipLines != null) {

                FontRenderer fontRenderer = minecraft.fontRenderer;

                // Compute tooltip params
                int x = i + 12, y = j - LINE_HEIGHT * tooltipLines.length;
                if(tooltipWidth == -1) {
                    for(String line : tooltipLines) {
                        tooltipWidth = Math.max(fontRenderer.getStringWidth(line), tooltipWidth);
                    }
                }
                if(x + tooltipWidth > minecraft.currentScreen.width) {
                    x = minecraft.currentScreen.width - tooltipWidth;
                }

                // Draw background
                drawGradientRect(x - 3, y - 3, x + tooltipWidth + 3, y + LINE_HEIGHT * tooltipLines.length, 0xc0000000,
                        0xc0000000);

                // Draw lines
                int lineCount = 0;
                for(String line : tooltipLines) {
                    int j1 = y + (lineCount++) * LINE_HEIGHT;
                    int k = -1;
                    fontRenderer.drawStringWithShadow(line, x, j1, k);
                }
            }
        }
    }

    protected boolean isMouseOverButton(int i, int j) {
        return i >= xPosition && j >= yPosition && i < (xPosition + width) && j < (yPosition + height);
    }

    public void setTooltip(String tooltip) {
        tooltip = tooltip.trim().replace("\\n", "\n");
        this.tooltip = tooltip;
        if(this.tooltip!=null && !this.tooltip.equals(""))
            this.tooltipLines = tooltip.split("\n");
    }

    public String getTooltip() {
        return tooltip;
    }

    protected int getTextColor(int i, int j) {

        int textColor = 0xffe0e0e0;
        if(!enabled) {
            textColor = 0xffa0a0a0;
        } else if(isMouseOverButton(i, j)) {
            textColor = 0xffffffa0;
        }
        return textColor;

    }
}

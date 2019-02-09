package mapwriter.gui;

import mapwriter.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderHelper;

public class GuiLabel {
    private enum Side {
        left,
        right,
        top,
        bottom,
        none
    }

    static int spacingX = 4;
    static int spacingY = 2;
    int x = 0, y = 0, w = 1, h = 12;
    private final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
    private boolean background;
    private boolean allowFlip;
    private int parentWidth;
    private int parentHeight;
    private String str1;
    private String str2;
    private String[] s1;
    private String[] s2;
    private GuiLabel label;

    private Side side = Side.none;

    public GuiLabel(String[] s1, String[] s2, int x, int y, boolean Background, boolean allowFlip, int parentWidth, int parentHeight) {
        this.background = Background;
        this.allowFlip = allowFlip;

        this.parentWidth = parentWidth;
        this.parentHeight = parentHeight;

        this.setCoords(x, y);
        this.setText(s1, s2);
    }

    public void draw() {
        this.updateCoords();
        if (this.str1 != null) {
            RenderHelper.disableStandardItemLighting();
            if (this.background) {
                Gui.drawRect(this.x - GuiLabel.spacingX, this.y - GuiLabel.spacingY, this.x + this.w + GuiLabel.spacingX, this.h + this.y + GuiLabel.spacingY, 0x80000000);
            }

            this.fontRenderer.drawSplitString(this.str1, this.x, this.y, this.w, 0xffffff);

            if (this.str2 != null) {
                this.fontRenderer.drawSplitString(this.str2, this.x + 65, this.y, this.w, 0xffffff);
            }
            RenderHelper.enableStandardItemLighting();
        }
    }

    public void drawToAboveOf(GuiLabel label) {
        this.label = label;
        this.side = Side.top;
    }

    public void drawToBelowOf(GuiLabel label) {
        this.label = label;
        this.side = Side.bottom;
    }

    public void drawToLeftOf(GuiLabel label) {
        this.label = label;
        this.side = Side.left;
    }

    public void drawToRightOf(GuiLabel label) {
        this.label = label;
        this.side = Side.right;
    }

    public boolean getAllowFlip() {

        return this.background;
    }

    public boolean getDrawBackground() {

        return this.background;
    }

    public int getparentHeight() {

        return this.parentHeight;
    }

    public int getparentWidth() {

        return this.parentWidth;
    }

    public boolean posWithin(int x, int y) {

        return x >= this.x + GuiLabel.spacingX && y >= this.y + GuiLabel.spacingY && x <= this.x + this.w + GuiLabel.spacingX && y <= this.y + this.h + GuiLabel.spacingY;
    }

    public void setAllowFlip(boolean enable) {
        this.allowFlip = enable;
    }

    public void setCoords(int x, int y) {
        if (this.allowFlip) {
            if (x + this.w + GuiLabel.spacingX > this.parentWidth) {
                this.x = x - this.w - GuiLabel.spacingX - 5;
            } else {
                this.x = x;
            }
            if (y + this.h + GuiLabel.spacingY > this.parentHeight) {
                this.y = y - this.h - GuiLabel.spacingY;
            } else {
                this.y = y;
            }
        } else {
            this.x = x;
            this.y = y;
        }
    }

    public void setDrawBackground(boolean enable) {
        this.background = enable;
    }

    public void setParentWidthAndHeight(int width, int height) {

        this.parentWidth = width;
        this.parentHeight = height;

        this.updateWidthAndHeight();
    }

    public void setText(String[] s1, String[] s2) {
        this.s1 = s1;
        this.s2 = s2;
        this.updateStrings();
    }

    private void updateCoords() {
        switch (this.side) {
            case left:
                this.setCoords(this.label.x - (this.w + 2 * GuiLabel.spacingX + 2), this.label.y);
                break;
            case right:
                this.setCoords(this.label.x + this.label.w + 2 * GuiLabel.spacingX + 2, this.label.y);
                break;
            case bottom:
                this.setCoords(this.label.x, this.label.y + this.label.h + 2 * GuiLabel.spacingY + 2);
                break;
            case top:
                this.setCoords(this.label.x, this.label.y - (this.h + 2 * GuiLabel.spacingY + 2));
                break;
            default:
                break;
        }
    }

    private void updateStrings() {
        if (this.s1 != null && this.s1.length > 0) {
            this.str1 = Utils.stringArrayToString(this.s1);
        }
        if (this.s2 != null && this.s2.length > 0) {
            this.str2 = Utils.stringArrayToString(this.s2);
        }
        this.updateWidthAndHeight();
    }

    private void updateWidthAndHeight() {
        if (this.s1 != null) {
            final int stringwidth = Utils.getMaxWidth(this.s1, this.s2);
            this.w = stringwidth < this.parentWidth - 20 ? stringwidth : this.parentWidth - 20;
            this.h = this.fontRenderer.getWordWrappedHeight(this.str1, this.parentWidth > 0 ? this.parentWidth : 10);
        }
    }
}

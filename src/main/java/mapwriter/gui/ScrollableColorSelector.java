package mapwriter.gui;

import mapwriter.util.Utils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;

public class ScrollableColorSelector extends ScrollableField {
    private final String editRed = "mw.gui.ScrollableColorSelector.Red";
    private final String editGreen = "mw.gui.ScrollableColorSelector.Green";
    private final String editBlue = "mw.gui.ScrollableColorSelector.Blue";

    private ScrollableNumericTextBox ScrollableNumericTextBoxColourRed;
    private ScrollableNumericTextBox ScrollableNumericTextBoxColourGreen;
    private ScrollableNumericTextBox ScrollableNumericTextBoxColourBlue;

    private int colour = 0;

    private int colourFieldX = 0;
    private int colourFieldY = 0;
    private int colourFieldW = 0;
    private int colourFieldH = 0;

    private final int y;

    public ScrollableColorSelector(int x, int y, int width, String label, FontRenderer fontrendererObj) {

        super(x, y + GuiMarkerDialogNew.elementVSpacing, width, label, fontrendererObj);
        this.y = y;
        this.init();
    }

    public void colourFieldScroll(int direction) {

        if (direction > 0) {
            this.nextElement();
        } else if (direction < 0) {
            this.previousElement();
        }
    }

    @Override
    public void draw() {

        super.draw();
        this.ScrollableNumericTextBoxColourRed.draw();
        this.ScrollableNumericTextBoxColourGreen.draw();
        this.ScrollableNumericTextBoxColourBlue.draw();

        this.UpdateColour();

        Gui.drawRect(this.colourFieldX - 1, this.colourFieldY - 1, this.colourFieldX + this.colourFieldW + 1, this.colourFieldY + this.colourFieldH + 1, 0xff000000);
        Gui.drawRect(this.colourFieldX, this.colourFieldY, this.colourFieldX + this.colourFieldW, this.colourFieldY + this.colourFieldH, this.colour);
    }

    public int getColor() {

        return this.colour;
    }

    @Override
    public Boolean isFocused() {

        if (this.ScrollableNumericTextBoxColourRed.isFocused() || this.ScrollableNumericTextBoxColourGreen.isFocused() || this.ScrollableNumericTextBoxColourBlue.isFocused()) {
            return true;
        }
        return false;
    }

    public void KeyTyped(char c, int key) {

        this.ScrollableNumericTextBoxColourRed.KeyTyped(c, key);
        this.ScrollableNumericTextBoxColourGreen.KeyTyped(c, key);
        this.ScrollableNumericTextBoxColourBlue.KeyTyped(c, key);
    }

    @Override
    public void mouseClicked(int x, int y, int button) {

        super.mouseClicked(x, y, button);
        this.ScrollableNumericTextBoxColourRed.mouseClicked(x, y, button);
        this.ScrollableNumericTextBoxColourGreen.mouseClicked(x, y, button);
        this.ScrollableNumericTextBoxColourBlue.mouseClicked(x, y, button);
    }

    public void mouseDWheelScrolled(int x, int y, int direction) {

        this.ScrollableNumericTextBoxColourRed.mouseDWheelScrolled(x, y, direction);
        this.ScrollableNumericTextBoxColourGreen.mouseDWheelScrolled(x, y, direction);
        this.ScrollableNumericTextBoxColourBlue.mouseDWheelScrolled(x, y, direction);

        if (this.posWithinColourField(x, y)) {
            this.colourFieldScroll(-direction);
        }
    }

    @Override
    public void nextElement() {

        this.setColor(Utils.getNextColour());
    }

    public ScrollableField nextField(ScrollableField field) {

        if (this.ScrollableNumericTextBoxColourRed.isFocused()) {
            return this.ScrollableNumericTextBoxColourGreen;
        } else if (this.ScrollableNumericTextBoxColourGreen.isFocused()) {
            return this.ScrollableNumericTextBoxColourBlue;
        }
        return field;
    }

    public boolean posWithinColourField(int x, int y) {

        return x >= this.colourFieldX && y >= this.colourFieldY && x <= this.colourFieldW + this.colourFieldX && y <= this.colourFieldH + this.colourFieldY;
    }

    public ScrollableField prevField(ScrollableField field) {

        if (this.ScrollableNumericTextBoxColourGreen.isFocused()) {
            return this.ScrollableNumericTextBoxColourRed;
        } else if (this.ScrollableNumericTextBoxColourBlue.isFocused()) {
            return this.ScrollableNumericTextBoxColourGreen;
        }
        return field;
    }

    @Override
    public void previousElement() {

        this.setColor(Utils.getPrevColour());
    }

    public void setColor(int colour) {

        this.colour = colour;

        final int red = colour >> 16 & 0xff;
        final int green = colour >> 8 & 0xff;
        final int blue = colour & 0xff;
        this.ScrollableNumericTextBoxColourRed.setText(red);
        this.ScrollableNumericTextBoxColourGreen.setText(green);
        this.ScrollableNumericTextBoxColourBlue.setText(blue);
    }

    @Override
    public void setFocused(Boolean focus) {

        this.ScrollableNumericTextBoxColourRed.setFocused(focus);
    }

    public ScrollableField thisField() {

        if (this.ScrollableNumericTextBoxColourRed.isFocused()) {
            return this.ScrollableNumericTextBoxColourRed;
        }
        if (this.ScrollableNumericTextBoxColourGreen.isFocused()) {
            return this.ScrollableNumericTextBoxColourGreen;
        }
        if (this.ScrollableNumericTextBoxColourBlue.isFocused()) {
            return this.ScrollableNumericTextBoxColourBlue;
        }
        return this.ScrollableNumericTextBoxColourRed;
    }

    public boolean validateColorData() {

        return this.ScrollableNumericTextBoxColourRed.getText().length() > 0 && this.ScrollableNumericTextBoxColourGreen.getText().length() > 0 && this.ScrollableNumericTextBoxColourBlue.getText().length() > 0;
    }

    private void init() {

        final int textboxWidth = 16;
        final int x1 = this.x + ScrollableField.arrowsWidth + this.fontrendererObj.getStringWidth(I18n.format(this.editGreen)) + 4;
        final int w = ScrollableField.arrowsWidth * 2 + this.fontrendererObj.getStringWidth("999") + textboxWidth;

        this.ScrollableNumericTextBoxColourRed = new ScrollableNumericTextBox(x1, this.y, w, I18n.format(this.editRed), this.fontrendererObj);
        this.ScrollableNumericTextBoxColourRed.setDrawArrows(true);
        this.ScrollableNumericTextBoxColourRed.setMaxValue(255);
        this.ScrollableNumericTextBoxColourRed.setMinValue(0);

        this.ScrollableNumericTextBoxColourGreen = new ScrollableNumericTextBox(x1, this.y + GuiMarkerDialogNew.elementVSpacing, w, I18n.format(this.editGreen), this.fontrendererObj);
        this.ScrollableNumericTextBoxColourGreen.setDrawArrows(true);
        this.ScrollableNumericTextBoxColourGreen.setMaxValue(255);
        this.ScrollableNumericTextBoxColourGreen.setMinValue(0);

        this.ScrollableNumericTextBoxColourBlue = new ScrollableNumericTextBox(x1, this.y + GuiMarkerDialogNew.elementVSpacing * 2, w, I18n.format(this.editBlue), this.fontrendererObj);
        this.ScrollableNumericTextBoxColourBlue.setDrawArrows(true);
        this.ScrollableNumericTextBoxColourBlue.setMaxValue(255);
        this.ScrollableNumericTextBoxColourBlue.setMinValue(0);

        this.colourFieldX = x1 + w + 2;
        this.colourFieldY = this.y + 6;
        this.colourFieldW = this.width - w - ScrollableField.arrowsWidth * 2 - this.fontrendererObj.getStringWidth(I18n.format(this.editGreen)) - 8;
        this.colourFieldH = GuiMarkerDialogNew.elementVSpacing * 2;
    }

    private void UpdateColour() {

        int colour = 0xff << 24;
        colour += this.ScrollableNumericTextBoxColourRed.getTextFieldIntValue() << 16;
        colour += this.ScrollableNumericTextBoxColourGreen.getTextFieldIntValue() << 8;
        colour += this.ScrollableNumericTextBoxColourBlue.getTextFieldIntValue();
        this.colour = colour;
    }
}

package mapwriter.gui;

import mapwriter.util.Utils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;

public class ScrollableColorSelector extends ScrollableField {
    private ScrollableNumericTextBox colorComponentRed;
    private ScrollableNumericTextBox colorComponentGreen;
    private ScrollableNumericTextBox colorComponentBlue;

    private int color = 0;

    private int colorFieldX = 0;
    private int colorFieldY = 0;
    private int colorFieldW = 0;
    private int colorFieldH = 0;

    private final int y;

    public ScrollableColorSelector(int x, int y, int width, String label, FontRenderer fontRenderer) {
        super(x, y + GuiMarkerDialogNew.elementVSpacing, width, label, fontRenderer);
        this.y = y;
        this.init();
    }

    public void colorFieldScroll(int direction) {
        if (direction > 0) {
            this.nextElement();
        } else if (direction < 0) {
            this.previousElement();
        }
    }

    @Override
    public void draw() {
        super.draw();
        this.colorComponentRed.draw();
        this.colorComponentGreen.draw();
        this.colorComponentBlue.draw();
        this.updateColor();

        Gui.drawRect(this.colorFieldX - 1, this.colorFieldY - 1, this.colorFieldX + this.colorFieldW + 1, this.colorFieldY + this.colorFieldH + 1, 0xff000000);
        Gui.drawRect(this.colorFieldX, this.colorFieldY, this.colorFieldX + this.colorFieldW, this.colorFieldY + this.colorFieldH, this.color);
    }

    public int getColor() {

        return this.color;
    }

    @Override
    public Boolean isFocused() {
        return this.colorComponentRed.isFocused() || this.colorComponentGreen.isFocused() || this.colorComponentBlue.isFocused();
    }

    public void keyTyped(char symbol, int key) {
        this.colorComponentRed.keyTyped(symbol, key);
        this.colorComponentGreen.keyTyped(symbol, key);
        this.colorComponentBlue.keyTyped(symbol, key);
    }

    @Override
    public void mouseClicked(int x, int y, int button) {
        super.mouseClicked(x, y, button);
        this.colorComponentRed.mouseClicked(x, y, button);
        this.colorComponentGreen.mouseClicked(x, y, button);
        this.colorComponentBlue.mouseClicked(x, y, button);
    }

    public void mouseDWheelScrolled(int x, int y, int direction) {
        this.colorComponentRed.mouseDWheelScrolled(x, y, direction);
        this.colorComponentGreen.mouseDWheelScrolled(x, y, direction);
        this.colorComponentBlue.mouseDWheelScrolled(x, y, direction);

        if (this.posWithinColorField(x, y)) {
            this.colorFieldScroll(-direction);
        }
    }

    @Override
    public void nextElement() {
        this.setColor(Utils.getNextColor());
    }

    public ScrollableField nextField(ScrollableField field) {
        if (this.colorComponentRed.isFocused()) {
            return this.colorComponentGreen;
        } else if (this.colorComponentGreen.isFocused()) {
            return this.colorComponentBlue;
        }
        return field;
    }

    public boolean posWithinColorField(int x, int y) {
        return x >= this.colorFieldX && y >= this.colorFieldY && x <= this.colorFieldW + this.colorFieldX && y <= this.colorFieldH + this.colorFieldY;
    }

    public ScrollableField prevField(ScrollableField field) {
        if (this.colorComponentGreen.isFocused()) {
            return this.colorComponentRed;
        } else if (this.colorComponentBlue.isFocused()) {
            return this.colorComponentGreen;
        }
        return field;
    }

    @Override
    public void previousElement() {
        this.setColor(Utils.getPrevColor());
    }

    public void setColor(int color) {
        this.color = color;

        final int red = color >> 16 & 0xff;
        final int green = color >> 8 & 0xff;
        final int blue = color & 0xff;
        this.colorComponentRed.setText(red);
        this.colorComponentGreen.setText(green);
        this.colorComponentBlue.setText(blue);
    }

    @Override
    public void setFocused(Boolean focus) {
        this.colorComponentRed.setFocused(focus);
    }

    public ScrollableField thisField() {
        if (this.colorComponentRed.isFocused()) {
            return this.colorComponentRed;
        }
        if (this.colorComponentGreen.isFocused()) {
            return this.colorComponentGreen;
        }
        if (this.colorComponentBlue.isFocused()) {
            return this.colorComponentBlue;
        }
        return this.colorComponentRed;
    }

    public boolean validateColorData() {

        return this.colorComponentRed.getText().length() > 0 && this.colorComponentGreen.getText().length() > 0 && this.colorComponentBlue.getText().length() > 0;
    }

    private void init() {
        final int textboxWidth = 16;
        String editGreen = "mw.gui.ScrollableColorSelector.Green";
        final int x1 = this.x + ScrollableField.arrowsWidth + this.fontrendererObj.getStringWidth(I18n.format(editGreen)) + 4;
        final int w = ScrollableField.arrowsWidth * 2 + this.fontrendererObj.getStringWidth("999") + textboxWidth;

        String editRed = "mw.gui.ScrollableColorSelector.Red";
        this.colorComponentRed = new ScrollableNumericTextBox(x1, this.y, w, I18n.format(editRed), this.fontrendererObj);
        this.colorComponentRed.setDrawArrows(true);
        this.colorComponentRed.setMaxValue(255);
        this.colorComponentRed.setMinValue(0);

        this.colorComponentGreen = new ScrollableNumericTextBox(x1, this.y + GuiMarkerDialogNew.elementVSpacing, w, I18n.format(editGreen), this.fontrendererObj);
        this.colorComponentGreen.setDrawArrows(true);
        this.colorComponentGreen.setMaxValue(255);
        this.colorComponentGreen.setMinValue(0);

        String editBlue = "mw.gui.ScrollableColorSelector.Blue";
        this.colorComponentBlue = new ScrollableNumericTextBox(x1, this.y + GuiMarkerDialogNew.elementVSpacing * 2, w, I18n.format(editBlue), this.fontrendererObj);
        this.colorComponentBlue.setDrawArrows(true);
        this.colorComponentBlue.setMaxValue(255);
        this.colorComponentBlue.setMinValue(0);

        this.colorFieldX = x1 + w + 2;
        this.colorFieldY = this.y + 6;
        this.colorFieldW = this.width - w - ScrollableField.arrowsWidth * 2 - this.fontrendererObj.getStringWidth(I18n.format(editGreen)) - 8;
        this.colorFieldH = GuiMarkerDialogNew.elementVSpacing * 2;
    }

    private void updateColor() {
        int color = 0xff << 24;
        color += this.colorComponentRed.getTextFieldIntValue() << 16;
        color += this.colorComponentGreen.getTextFieldIntValue() << 8;
        color += this.colorComponentBlue.getTextFieldIntValue();
        this.color = color;
    }
}

package mapwriter.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class ScrollableTextBox extends ScrollableField {
    private static int textFieldHeight = 12;
    public int textFieldX;
    public int textFieldY;
    public int textFieldWidth;

    public List<String> scrollableElements;

    protected GuiTextField textField;

    ScrollableTextBox(int x, int y, int width, String label, FontRenderer fontRenderer) {
        super(x, y, width, label, fontRenderer);
        this.init();
    }

    ScrollableTextBox(int x, int y, int width, String label, List<String> scrollableElements, FontRenderer fontRenderer) {
        super(x, y, width, label, fontRenderer);
        this.scrollableElements = scrollableElements;
        this.init();
    }

    @Override
    public void draw() {
        super.draw();
        this.textField.drawTextBox();
        if (!this.validateTextFieldData()) {
            // draw a red rectangle over the textbox to indicate that the text
            // is invallid
            final int x1 = this.textFieldX - 1;
            final int y1 = this.textFieldY - 1;
            final int x2 = this.textFieldX + this.textFieldWidth;
            final int y2 = this.textFieldY + ScrollableTextBox.textFieldHeight;
            final int color = 0xff900000;

            this.drawHorizontalLine(x1, x2, y1, color);
            this.drawHorizontalLine(x1, x2, y2, color);

            this.drawVerticalLine(x1, y1, y2, color);
            this.drawVerticalLine(x2, y1, y2, color);
        }
    }

    public int getCursorPosition() {
        return this.textField.getCursorPosition();
    }

    public String getText() {
        return this.textField.getText();
    }

    @Override
    public Boolean isFocused() {
        return this.textField.isFocused();
    }

    public void keyTyped(char symbol, int key) {
        this.textField.textboxKeyTyped(symbol, key);
    }

    @Override
    public void mouseClicked(int x, int y, int button) {
        super.mouseClicked(x, y, button);
        this.textField.mouseClicked(x, y, button);
    }

    public void mouseDWheelScrolled(int x, int y, int direction) {
        if (this.posWithinTextField(x, y)) {
            this.textFieldScroll(-direction);
        }
    }

    @Override
    public void nextElement() {
        this.textFieldScroll(1);
    }

    public boolean posWithinTextField(int x, int y) {
        return x >= this.textFieldX && y >= this.textFieldY && x <= this.textFieldWidth + this.textFieldX && y <= ScrollableTextBox.textFieldHeight + this.textFieldY;
    }

    @Override
    public void previousElement() {
        this.textFieldScroll(-1);
    }

    public void setCursorPositionEnd() {
        this.textField.setCursorPositionEnd();
    }

    @Override
    public void setFocused(Boolean focus) {

        this.textField.setFocused(focus);
        this.textField.setSelectionPos(0);
    }

    public void setText(String text) {

        this.textField.setText(text);
    }

    public void textFieldScroll(int direction) {

        if (this.scrollableElements != null) {
            int index = this.scrollableElements.indexOf(this.getText().trim());
            if (direction > 0) {
                if (index == -1 || index == this.scrollableElements.size() - 1) {
                    index = 0;
                } else {
                    index++;
                }
            } else if (direction < 0) {
                if (index == -1 || index == 0) {
                    index = this.scrollableElements.size() - 1;
                } else {
                    index--;
                }
            }
            this.textField.setText(this.scrollableElements.get(index));
        }
    }

    public boolean validateTextFieldData() {

        return this.getText().length() > 0;
    }

    private void init() {

        this.textFieldX = this.x + ScrollableField.arrowsWidth + 3;
        this.textFieldY = this.y;
        this.textFieldWidth = this.width - 5 - ScrollableField.arrowsWidth * 2;

        this.textField = new GuiTextField(0, this.fontrendererObj, this.textFieldX, this.textFieldY, this.textFieldWidth, ScrollableTextBox.textFieldHeight);

        this.textField.setMaxStringLength(32);

    }
}

class ScrollableNumericTextBox extends ScrollableTextBox {
    public int maxValue = -1;
    public int minValue = -1;

    public ScrollableNumericTextBox(int x, int y, int width, String label, FontRenderer fontrendererObj) {

        super(x, y, width, label, fontrendererObj);
    }

    public int getTextFieldIntValue() {

        try {
            return Integer.parseInt(this.getText());
        } catch (final NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public void keyTyped(char symbol, int key) {

        if (symbol >= '0' && symbol <= '9' || key == Keyboard.KEY_BACK || key == Keyboard.KEY_LEFT || key == Keyboard.KEY_RIGHT || symbol == '-' && this.getCursorPosition() == 0) {
            if (Character.isDigit(symbol) && this.maxValue > -1 && Integer.parseInt(this.getText() + symbol) > this.maxValue) {
                return;
            }
            super.keyTyped(symbol, key);
        }
    }

    public void setMaxValue(int max) {

        this.maxValue = max;
        this.textField.setMaxStringLength(Integer.toString(max).length());
    }

    public void setMinValue(int min) {

        this.minValue = min;
    }

    public void setText(int num) {

        if (this.maxValue < 0 || num <= this.maxValue || num >= this.minValue) {
            this.setText(Integer.toString(num));
        }
    }

    @Override
    public void textFieldScroll(int direction) {

        int newValue = 0;
        if (this.validateTextFieldData()) {
            newValue = this.getTextFieldIntValue();
            if (direction > 0) {
                if (this.maxValue < 0 || newValue + 1 <= this.maxValue) {
                    newValue += 1;
                }
            } else if (direction < 0) {
                if (this.minValue < 0 || newValue - 1 >= this.minValue) {
                    newValue -= 1;
                }
            }
        }
        this.setText(newValue);
    }
}

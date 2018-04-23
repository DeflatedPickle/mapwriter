package com.cabchinoe.minimap.gui;

import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class ScrollableNumericTextBox extends ScrollableTextBox {
    public int maxValue = -1;
    public int minValue = -1;

    public ScrollableNumericTextBox(int id,int x, int y, int width, String label, GuiScreen screen) {
        super(id,x, y, width, label,screen);
    }

    public ScrollableNumericTextBox(int id,int x, int y, int width, String label, GuiScreen screen, List<String> scrollableElements) {
        super(id,x, y, width, label,screen,scrollableElements);
    }

    @Override
    public void textFieldScroll(int direction) {
        if(this.scrollableElements == null) {
            if (this.validateTextFieldData()) {
                int value = this.getTextFieldIntValue();
                if (direction > 0)
                    this.textField.setText("" + (value + 1));
                else if (direction < 0)
                    this.textField.setText("" + (value - 1));
            }
        }else{
            super.textFieldScroll(direction);
        }
    }

    public int getTextFieldIntValue() {
        int value = 0;
        try{
            value= Integer.parseInt(this.textField.getText());
        }finally {
            return value;
        }

    }

    public void validateTextboxKeyTyped(char c, int key) {
        if ((c >= '0' && c <= '9') || key == Keyboard.KEY_BACK
                || key == Keyboard.KEY_LEFT || key == Keyboard.KEY_RIGHT
                || (c == '-' && (this.textField.getCursorPosition() == 0))){
            if (Character.isDigit(c) && ((this.maxValue > -1) && (Integer.parseInt(this.textField.getText() + c) > this.maxValue)))
            {
                return;
            }
            this.textField.textboxKeyTyped(c, key);
        }

    }
    public void setMaxValue(int max)
    {
        this.maxValue = max;
        this.textField.setMaxStringLength(Integer.toString(max).length());
    }

    public void setMinValue(int min)
    {
        this.minValue = min;
    }

    public void setText(int num)
    {
        if ((this.maxValue < 0) || (num <= this.maxValue) || (num >= this.minValue))
        {
            this.textField.setText(Integer.toString(num));
        }
    }
}

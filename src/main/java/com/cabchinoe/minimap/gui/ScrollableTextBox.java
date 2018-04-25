package com.cabchinoe.minimap.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class ScrollableTextBox extends ScrollableField {
    // private int height;


    public List<String> scrollableElements;
    public GuiTextField textField = null;

    ScrollableTextBox(int id,int x, int y, int width, String label,GuiScreen screen) {
        super(id,x,y,width,label,screen);
        this.init();
    }

    ScrollableTextBox(int id,int x, int y, int width, String label,GuiScreen screen, List<String> scrollableElements) {
        super(id,x,y,width,label,screen);
        this.scrollableElements = scrollableElements;
        this.init();
    }

    public void init() {
        this.textField = new GuiTextField(this.id,
                this.fontRendererObj, this.textFieldX,
                this.textFieldY, this.textFieldWidth, this.textFieldHeight);
        this.textField.setMaxStringLength(32);
    }

    public void draw() {
        super.draw();
        this.textField.drawTextBox();
        if (!this.validateTextFieldData()) {
            drawRect(this.textFieldX - 1, this.textFieldY - 1,
                    this.textFieldX + this.textFieldWidth + 1,
                    this.textFieldY,
                    0xff900000);
            drawRect(this.textFieldX - 1, this.textFieldY - 1,
                    this.textFieldX, this.textFieldY + this.textFieldHeight	+ 1,
                    0xff900000);
            drawRect(this.textFieldX + this.textFieldWidth + 1,
                    this.textFieldY + this.textFieldHeight + 1,
                    this.textFieldX,
                    this.textFieldY + this.textFieldHeight,
                    0xff900000);
            drawRect(this.textFieldX + this.textFieldWidth + 1,
                    this.textFieldY + this.textFieldHeight + 1,
                    this.textFieldX + this.textFieldWidth, this.textFieldY,
                    0xff900000);
        }
    }

    public void mouseClicked(int x, int y, int button) {
        super.mouseClicked(x,y,button);
        this.textField.mouseClicked(x, y, button);
    }


    public void mouseDWheelScrolled(int x, int y, int direction) {
        if (posWithinTextField(x, y))
            textFieldScroll(-direction);
    }

    public boolean validateTextFieldData() {
        return this.textField.getText().length() > 0;
    }

    /**
     *
     * @return Returns clicked arrow: 1 for right and -1 for left
     */

    @Override
    public void nextElement() {
        this.textFieldScroll(1);
    }

    @Override
    public void previousElement() {
        this.textFieldScroll(-1);
    }

    @Override
    public void setFocused(Boolean focus) {
        this.textField.setFocused(focus);
    }

    @Override
    public boolean isFocused() {
        return this.textField.isFocused();
    }


    public boolean posWithinTextField(int x, int y) {
        return (x >= this.textFieldX) && (y >= this.textFieldY)
                && (x <= this.textFieldWidth + this.textFieldX)
                && (y <= this.textFieldHeight + this.textFieldY);
    }

    public void textFieldScroll(int direction) {
        if (this.scrollableElements != null) {
            int index = this.scrollableElements.indexOf(this.textField
                    .getText().trim());
            if (direction > 0) {
                if (index == -1
                        || index == this.scrollableElements.size() - 1)
                    index = 0;
                else
                    index++;
            } else if (direction < 0) {
                if (index == -1 || index == 0)
                    index = this.scrollableElements.size() - 1;
                else
                    index--;
            }
            this.textField.setText(this.scrollableElements.get(index));
        }
    }
}

package mapwriter.gui;

import mapwriter.api.MapWriterAPI;
import mapwriter.map.Marker;
import mapwriter.map.MarkerManager;
import mapwriter.util.Utils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.DimensionType;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;

@net.minecraftforge.fml.relauncher.SideOnly(Side.CLIENT)
public class GuiMarkerDialogNew extends GuiScreen {
    static final int dialogWidthPercent = 40;
    static final int elementVSpacing = 20;
    static final int numberOfElements = 8;
    private final GuiScreen parentScreen;
    String title = "";
    String titleNew = "mw.gui.mwguimarkerdialognew.title.new";
    String titleEdit = "mw.gui.mwguimarkerdialognew.title.edit";
    ScrollableTextBox scrollableTextBoxName = null;
    ScrollableTextBox scrollableTextBoxGroup = null;
    ScrollableNumericTextBox scrollableNumericTextBoxX = null;
    ScrollableNumericTextBox scrollableNumericTextBoxY = null;
    ScrollableNumericTextBox scrollableNumericTextBoxZ = null;
    ScrollableColorSelector ScrollableColorSelectorColor = null;
    boolean backToGameOnSubmit = false;
    private final MarkerManager markerManager;
    private Marker editingMarker;
    private String markerName;
    private String markerGroup;
    private int markerX;
    private int markerY;
    private int markerZ;
    private DimensionType dimension;
    private int color;

    public GuiMarkerDialogNew(GuiScreen parentScreen, MarkerManager markerManager, Marker editingMarker) {
        this.markerManager = markerManager;
        this.editingMarker = editingMarker;
        this.markerName = editingMarker.name;
        this.markerGroup = editingMarker.groupName;
        this.markerX = editingMarker.x;
        this.markerY = editingMarker.y;
        this.markerZ = editingMarker.z;
        this.dimension = editingMarker.dimension;
        this.color = editingMarker.color;
        this.parentScreen = parentScreen;
        this.title = this.titleEdit;
    }

    public GuiMarkerDialogNew(GuiScreen parentScreen, MarkerManager markerManager, String markerName, String markerGroup, int x, int y, int z, DimensionType dimension) {
        this.markerManager = markerManager;
        this.markerName = markerName;
        this.markerGroup = markerGroup;
        this.markerX = x;
        this.markerY = y;
        this.markerZ = z;
        this.dimension = dimension;
        this.color = Utils.getCurrentColor();
        this.editingMarker = null;
        this.parentScreen = parentScreen;
        this.title = this.titleNew;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float f) {
        if (this.parentScreen != null) {
            this.parentScreen.drawScreen(mouseX, mouseY, f);
        } else {
            this.drawDefaultBackground();
        }

        final int w = this.width * GuiMarkerDialogNew.dialogWidthPercent / 100;
        drawRect((this.width - w) / 2, (this.height - GuiMarkerDialogNew.elementVSpacing * (GuiMarkerDialogNew.numberOfElements + 2)) / 2 - 4, (this.width - w) / 2 + w, (this.height - GuiMarkerDialogNew.elementVSpacing * (GuiMarkerDialogNew.numberOfElements + 2)) / 2 + GuiMarkerDialogNew.elementVSpacing * (GuiMarkerDialogNew.numberOfElements + 1), 0x80000000);
        this.drawCenteredString(this.fontRenderer, I18n.format(this.title), this.width / 2, (this.height - GuiMarkerDialogNew.elementVSpacing * (GuiMarkerDialogNew.numberOfElements + 1)) / 2 - GuiMarkerDialogNew.elementVSpacing / 4, 0xffffff);
        this.scrollableTextBoxName.draw();
        this.scrollableTextBoxGroup.draw();
        this.scrollableNumericTextBoxX.draw();
        this.scrollableNumericTextBoxY.draw();
        this.scrollableNumericTextBoxZ.draw();
        this.ScrollableColorSelectorColor.draw();
        super.drawScreen(mouseX, mouseY, f);
    }

    // override GuiScreen's handleMouseInput to process
    // the scroll wheel.
    @Override
    public void handleMouseInput() throws IOException {
        if (MapWriterAPI.getCurrentDataProvider() != null) {
            return;
        }
        final int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
        final int y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        final int direction = Mouse.getEventDWheel();
        if (direction != 0) {
            this.mouseDWheelScrolled(x, y, direction);
        }
        super.handleMouseInput();
    }

    @Override
    public void initGui() {
        final int labelsWidth = this.fontRenderer.getStringWidth("Group");
        final int width = this.width * GuiMarkerDialogNew.dialogWidthPercent / 100 - labelsWidth - 20;
        final int x = (this.width - width + labelsWidth) / 2;
        final int y = (this.height - GuiMarkerDialogNew.elementVSpacing * GuiMarkerDialogNew.numberOfElements) / 2;

        String editMarkerName = "mw.gui.mwguimarkerdialognew.editMarkerName";
        this.scrollableTextBoxName = new ScrollableTextBox(x, y, width, I18n.format(editMarkerName), this.fontRenderer);
        this.scrollableTextBoxName.setFocused(true);
        this.scrollableTextBoxName.setText(this.markerName);

        String editMarkerGroup = "mw.gui.mwguimarkerdialognew.editMarkerGroup";
        this.scrollableTextBoxGroup = new ScrollableTextBox(x, y + GuiMarkerDialogNew.elementVSpacing, width, I18n.format(editMarkerGroup), this.markerManager.groups, this.fontRenderer);
        this.scrollableTextBoxGroup.setText(this.markerGroup);
        this.scrollableTextBoxGroup.setDrawArrows(true);

        String editMarkerX = "mw.gui.mwguimarkerdialognew.editMarkerX";
        this.scrollableNumericTextBoxX = new ScrollableNumericTextBox(x, y + GuiMarkerDialogNew.elementVSpacing * 2, width, I18n.format(editMarkerX), this.fontRenderer);
        this.scrollableNumericTextBoxX.setText("" + this.markerX);
        this.scrollableNumericTextBoxX.setDrawArrows(true);

        String editMarkerY = "mw.gui.mwguimarkerdialognew.editMarkerY";
        this.scrollableNumericTextBoxY = new ScrollableNumericTextBox(x, y + GuiMarkerDialogNew.elementVSpacing * 3, width, I18n.format(editMarkerY), this.fontRenderer);
        this.scrollableNumericTextBoxY.setText("" + this.markerY);
        this.scrollableNumericTextBoxY.setDrawArrows(true);

        String editMarkerZ = "mw.gui.mwguimarkerdialognew.editMarkerZ";
        this.scrollableNumericTextBoxZ = new ScrollableNumericTextBox(x, y + GuiMarkerDialogNew.elementVSpacing * 4, width, I18n.format(editMarkerZ), this.fontRenderer);
        this.scrollableNumericTextBoxZ.setText("" + this.markerZ);
        this.scrollableNumericTextBoxZ.setDrawArrows(true);

        String editMarkerColor = "mw.gui.mwguimarkerdialognew.editMarkerColor";
        this.ScrollableColorSelectorColor = new ScrollableColorSelector(x, y + GuiMarkerDialogNew.elementVSpacing * 5, width, I18n.format(editMarkerColor), this.fontRenderer);
        this.ScrollableColorSelectorColor.setColor(this.color);
        this.ScrollableColorSelectorColor.setDrawArrows(true);
    }

    public void mouseDWheelScrolled(int x, int y, int direction) {
        this.scrollableTextBoxName.mouseDWheelScrolled(x, y, direction);
        this.scrollableTextBoxGroup.mouseDWheelScrolled(x, y, direction);
        this.scrollableNumericTextBoxX.mouseDWheelScrolled(x, y, direction);
        this.scrollableNumericTextBoxY.mouseDWheelScrolled(x, y, direction);
        this.scrollableNumericTextBoxZ.mouseDWheelScrolled(x, y, direction);
        this.ScrollableColorSelectorColor.mouseDWheelScrolled(x, y, direction);
    }

    public boolean submit() {
        boolean inputCorrect = true;

        if (this.scrollableTextBoxName.validateTextFieldData()) {
            this.markerName = this.scrollableTextBoxName.getText();
        } else {
            inputCorrect = false;
        }

        if (this.scrollableTextBoxGroup.validateTextFieldData()) {
            this.markerGroup = this.scrollableTextBoxGroup.getText();
        } else {
            inputCorrect = false;
        }

        if (this.scrollableNumericTextBoxX.validateTextFieldData()) {
            this.markerX = this.scrollableNumericTextBoxX.getTextFieldIntValue();
        } else {
            inputCorrect = false;
        }

        if (this.scrollableNumericTextBoxY.validateTextFieldData()) {
            this.markerY = this.scrollableNumericTextBoxY.getTextFieldIntValue();
        } else {
            inputCorrect = false;
        }

        if (this.scrollableNumericTextBoxZ.validateTextFieldData()) {
            this.markerZ = this.scrollableNumericTextBoxZ.getTextFieldIntValue();
        } else {
            inputCorrect = false;
        }

        if (this.ScrollableColorSelectorColor.validateColorData()) {
            this.color = this.ScrollableColorSelectorColor.getColor();
        } else {
            inputCorrect = false;
        }

        if (inputCorrect) {
            if (this.editingMarker != null) {
                this.markerManager.delMarker(this.editingMarker);
                this.editingMarker = null;
            }
            this.markerManager.addMarker(this.markerName, this.markerGroup, this.markerX, this.markerY, this.markerZ, this.dimension, this.color);
            this.markerManager.setVisibleGroupName(this.markerGroup);
            this.markerManager.update();
        }
        return inputCorrect;
    }

    @Override
    protected void keyTyped(char symbol, int key) {
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
            case Keyboard.KEY_TAB:
                ScrollableField thisField = null;
                ScrollableField prevField = null;
                ScrollableField nextField = null;

                if (this.scrollableTextBoxName.isFocused()) {
                    thisField = this.scrollableTextBoxName;
                    prevField = this.ScrollableColorSelectorColor;
                    nextField = this.scrollableTextBoxGroup;
                } else if (this.scrollableTextBoxGroup.isFocused()) {
                    thisField = this.scrollableTextBoxGroup;
                    prevField = this.scrollableTextBoxName;
                    nextField = this.scrollableNumericTextBoxX;
                } else if (this.scrollableNumericTextBoxX.isFocused()) {
                    thisField = this.scrollableNumericTextBoxX;
                    prevField = this.scrollableTextBoxGroup;
                    nextField = this.scrollableNumericTextBoxY;
                } else if (this.scrollableNumericTextBoxY.isFocused()) {
                    thisField = this.scrollableNumericTextBoxY;
                    prevField = this.scrollableNumericTextBoxX;
                    nextField = this.scrollableNumericTextBoxZ;
                } else if (this.scrollableNumericTextBoxZ.isFocused()) {
                    thisField = this.scrollableNumericTextBoxZ;
                    prevField = this.scrollableNumericTextBoxY;
                    nextField = this.ScrollableColorSelectorColor;
                } else if (this.ScrollableColorSelectorColor.isFocused()) {
                    thisField = this.ScrollableColorSelectorColor.thisField();
                    nextField = this.ScrollableColorSelectorColor.nextField(this.scrollableTextBoxName);
                    prevField = this.ScrollableColorSelectorColor.prevField(this.scrollableNumericTextBoxZ);
                }

                thisField.setFocused(false);

                if (thisField instanceof ScrollableTextBox) {
                    ((ScrollableTextBox) thisField).setCursorPositionEnd();
                }
                if (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54)) {
                    prevField.setFocused(true);
                } else {
                    nextField.setFocused(true);
                }

                break;
            default:
                this.scrollableTextBoxName.keyTyped(symbol, key);
                this.scrollableTextBoxGroup.keyTyped(symbol, key);
                this.scrollableNumericTextBoxX.keyTyped(symbol, key);
                this.scrollableNumericTextBoxY.keyTyped(symbol, key);
                this.scrollableNumericTextBoxZ.keyTyped(symbol, key);
                this.ScrollableColorSelectorColor.keyTyped(symbol, key);
                break;
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int button) throws IOException {
        super.mouseClicked(x, y, button);

        this.scrollableTextBoxName.mouseClicked(x, y, button);
        this.scrollableTextBoxGroup.mouseClicked(x, y, button);
        this.scrollableNumericTextBoxX.mouseClicked(x, y, button);
        this.scrollableNumericTextBoxY.mouseClicked(x, y, button);
        this.scrollableNumericTextBoxZ.mouseClicked(x, y, button);
        this.ScrollableColorSelectorColor.mouseClicked(x, y, button);
    }
}
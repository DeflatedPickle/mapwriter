package mapwriter.gui;

import mapwriter.map.Marker;
import mapwriter.map.MarkerManager;
import mapwriter.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class MwGuiMarkerListOverlay extends MwGuiSlot {
    public static int listWidth = 95;
    public static int ListY = 10;

    private final GuiScreen parentScreen;
    private final MarkerManager markerManager;
    private int height;

    public MwGuiMarkerListOverlay(GuiScreen parentScreen, MarkerManager markerManager) {

        super(Minecraft.getMinecraft(), // mcIn
                MwGuiMarkerListOverlay.listWidth, // width
                parentScreen.height - 20, // height
                MwGuiMarkerListOverlay.ListY, // topIn
                10 + parentScreen.height - 20, // bottomIn
                parentScreen.width - 110 // left
        );

        this.height = parentScreen.height - 20;

        this.parentScreen = parentScreen;
        this.markerManager = markerManager;
    }

    @Override
    public void setDimensions(int widthIn, int heightIn, int topIn, int bottomIn, int left) {

        this.height = this.parentScreen.height - 20;

        super.setDimensions(widthIn, heightIn, topIn, bottomIn, left);
    }

    @Override
    protected void drawBackground() {

    }

    @Override
    protected void drawSlot(int entryID, int x, int y, int slotHeight, int mouseXIn, int mouseYIn) {

        final MwGuiLabel label = new MwGuiLabel(this.getLabelString(entryID), null, x, y, false, false, MwGuiMarkerListOverlay.listWidth, this.height);

        label.draw();
    }

    @Override
    protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY, int mouseButton) {

        this.markerManager.selectedMarker = this.markerManager.visibleMarkers.get(slotIndex);
        if (mouseButton == 1) {
            if (this.parentScreen instanceof MwGui) {
                ((MwGui) this.parentScreen).openMarkerGui(this.markerManager.selectedMarker, mouseX, mouseY);
            }
        }
        if (mouseButton == 0 && isDoubleClick) {
            if (this.parentScreen instanceof MwGui) {
                ((MwGui) this.parentScreen).centerOnSelectedMarker();
            }
        }
    }

    protected String[] getLabelString(int index) {

        final Marker m = this.markerManager.visibleMarkers.get(index);

        final String[] text = new String[2];
        text[0] = m.name;
        text[1] = String.format("(%d, %d, %d)", m.x, m.y, m.z);
        return text;
    }

    @Override
    protected int getSize() {

        return this.markerManager.visibleMarkers.size();
    }

    @Override
    protected int getSlotHeight(int index) {

        final String str = Utils.stringArrayToString(this.getLabelString(index));
        int height = this.mc.fontRenderer.getWordWrappedHeight(str, MwGuiMarkerListOverlay.listWidth - 6);

        height += this.spacingY * 2;

        return height;
    }

    @Override
    protected boolean isSelected(int slotIndex) {

        return this.markerManager.selectedMarker == this.markerManager.visibleMarkers.get(slotIndex);
    }
}
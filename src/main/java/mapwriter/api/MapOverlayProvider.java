package mapwriter.api;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.DimensionType;

import java.util.List;

public interface MapOverlayProvider {
    List<MapChunkOverlay> getChunksOverlay(DimensionType dim, double centerX, double centerZ, double minX, double minZ, double maxX, double maxZ);

    ITextComponent getMouseInfo(int mouseX, int mouseY);
    ITextComponent getStatusInfo(DimensionType dim, int bX, int bY, int bZ);

    void onDimensionChanged(DimensionType dimension, MapView mapview);

    void onDraw(MapView mapview, MapMode mapmode);

    void onMapCenterChanged(double vX, double vZ, MapView mapview);

    void onMiddleClick(DimensionType dim, int bX, int bZ, MapView mapview);

    boolean onMouseInput(MapView mapview, MapMode mapmode);

    void onOverlayActivated(MapView mapview);

    void onOverlayDeactivated(MapView mapview);

    void onZoomChanged(int level, MapView mapview);
}

package mapwriter.api;

import net.minecraft.world.DimensionType;

import java.util.List;

public interface IMwDataProvider {
    List<IMwChunkOverlay> getChunksOverlay(DimensionType dim, double centerX, double centerZ, double minX, double minZ, double maxX, double maxZ);

    // return null if nothing should be drawn on fullscreen map
    ILabelInfo getLabelInfo(int mouseX, int mouseY);

    // Returns what should be added to the status bar by the addon.
    String getStatusString(DimensionType dim, int bX, int bY, int bZ);

    // Callback for dimension change on the map
    void onDimensionChanged(DimensionType dimension, IMapView mapview);

    void onDraw(IMapView mapview, IMapMode mapmode);

    void onMapCenterChanged(double vX, double vZ, IMapView mapview);

    // Call back for middle click.
    void onMiddleClick(DimensionType dim, int bX, int bZ, IMapView mapview);

    boolean onMouseInput(IMapView mapview, IMapMode mapmode);

    void onOverlayActivated(IMapView mapview);

    void onOverlayDeactivated(IMapView mapview);

    void onZoomChanged(int level, IMapView mapview);
}

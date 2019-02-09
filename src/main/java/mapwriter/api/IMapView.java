package mapwriter.api;

import net.minecraft.world.DimensionType;

import java.util.List;

public interface IMapView {
    int adjustZoomLevel(int n);

    DimensionType getDimension();

    double getDimensionScaling(DimensionType playerDimension);

    double getHeight();

    double getMaxX();

    double getMaxZ();

    double getMinX();

    double getMinZ();

    int getPixelsPerBlock();

    int getRegionZoomLevel();

    boolean getUndergroundMode();

    double getWidth();

    double getX();

    double getZ();

    int getZoomLevel();

    boolean isBlockWithinView(double bX, double bZ, boolean circular);

    void nextDimension(List<DimensionType> dimensions, int n);

    void panView(double relX, double relZ);

    void setDimension(DimensionType dimension);

    void setDimensionAndAdjustZoom(DimensionType dimension);

    void setMapWH(IMapMode mapMode);

    void setMapWH(int w, int h);

    void setTextureSize(int n);

    void setUndergroundMode(boolean enabled);

    void setViewCentre(double vX, double vZ);

    void setViewCentreScaled(double vX, double vZ, DimensionType playerDimension);

    int setZoomLevel(int zoomLevel);

    // bX and bZ are the coordinates of the block the zoom is centred on.
    // The relative position of the block in the view will remain the same
    // as before the zoom.
    void zoomToPoint(int newZoomLevel, double bX, double bZ);
}

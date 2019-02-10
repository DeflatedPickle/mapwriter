package mapwriter.api;

import java.awt.*;

public interface MapMode {
    Point.Double blockXZtoScreenXY(MapView mapView, double bX, double bZ);

    Point.Double getClampedScreenXY(MapView mapView, double bX, double bZ);

    MapModeConfig getConfig();

    int getH();

    int getHPixels();

    Point.Double getNewPosPoint(double mouseX, double mouseY);

    int getTextColor();

    int getTextX();

    int getTextY();

    int getW();

    int getWPixels();

    int getX();

    int getXTranslation();

    int getY();

    int getYTranslation();

    boolean posWithin(int mouseX, int mouseY);

    Point screenXYtoBlockXZ(MapView mapView, int sx, int sy);

    void setScreenRes();

    void setScreenRes(int dw, int dh, int sw, int sh, double scaling);

    void updateMargin();
}

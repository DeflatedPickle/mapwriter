package mapwriter.api;

public interface MapModeConfig {
    int getAlphaPercent();

    String getBiomeMode();

    boolean isBordered();

    boolean isCircular();

    String getConfigCategory();

    String getCoordsMode();

    boolean isEnabled();

    String getMapPosCategory();

    int getMarkerSize();

    int getPlayerArrowSize();

    boolean isRotating();

    double getHeightPercent();

    double getWidthPercent();

    double getXPos();

    double getYPos();
}

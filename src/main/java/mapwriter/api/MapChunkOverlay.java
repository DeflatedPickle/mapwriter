package mapwriter.api;

import java.awt.*;

public interface MapChunkOverlay {
    int getBorderColor();

    float getBorderWidth();

    int getColor();

    Point getCoordinates();

    float getFilling();

    byte getBorder();
}

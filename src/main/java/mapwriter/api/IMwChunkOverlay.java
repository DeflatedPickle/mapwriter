package mapwriter.api;

import java.awt.*;

public interface IMwChunkOverlay {
    int getBorderColor();

    float getBorderWidth();

    int getColor();

    Point getCoordinates();

    float getFilling();

    boolean hasBorder();
}

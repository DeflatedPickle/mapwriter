package mapwriter.map;

import mapwriter.region.Region;
import net.minecraft.world.DimensionType;

public class MapViewRequest {
    public final int xMin, xMax, zMin, zMax, zoomLevel;
    public final DimensionType dimension;

    public MapViewRequest(MapView view) {
        this.zoomLevel = view.getRegionZoomLevel();
        final int size = Region.SIZE << this.zoomLevel;
        this.xMin = (int) view.getMinX() & -size;
        this.zMin = (int) view.getMinZ() & -size;
        this.xMax = (int) view.getMaxX() & -size;
        this.zMax = (int) view.getMaxZ() & -size;
        this.dimension = view.getDimension();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MapViewRequest)) {
            return false;
        }

        final MapViewRequest req = (MapViewRequest) o;
        return req.zoomLevel == this.zoomLevel && req.dimension == this.dimension && req.xMin == this.xMin && req.xMax == this.xMax && req.zMin == this.zMin && req.zMax == this.zMax;
    }

    public boolean mostlyEquals(MapViewRequest req) {
        return req != null && req.zoomLevel == this.zoomLevel && req.dimension == this.dimension;
    }
}

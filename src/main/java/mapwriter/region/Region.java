package mapwriter.region;

import net.minecraft.world.DimensionType;

import java.io.File;

/*
 * MwRegion class Represents a 32x32 chunk area (512x512 blocks).
 */
public class Region {
    public static final int SHIFT = 9;

    public static final int SIZE = 1 << SHIFT;
    public static final int MASK = -SIZE;

    public static Long getKey(int x, int z, int zoomLevel, DimensionType dimension) {
        x = x >> Region.SHIFT + zoomLevel & 0xffff;
        z = z >> Region.SHIFT + zoomLevel & 0xffff;
        zoomLevel = zoomLevel & 0xff;
        return (long) (dimension.getId() & 0xff) << 40 | (long) zoomLevel << 32 | (long) z << 16 | x;
    }

    private static File addDimensionDirToPath(File dir, DimensionType dimension) {
        if (dimension != DimensionType.OVERWORLD) {
            dir = new File(dir, "DIM" + dimension.getId());
        }
        return dir;
    }

    public RegionManager regionManager;
    public final int x;
    public final int z;
    public final DimensionType dimension;
    public final int zoomLevel;

    public final Long key;

    public final int size;

    public SurfacePixels surfacePixels;

    public Region(RegionManager regionManager, int x, int z, int zoomLevel, DimensionType dimension) {
        this.regionManager = regionManager;

        this.zoomLevel = Math.min(Math.max(0, zoomLevel), regionManager.maxZoom);
        this.dimension = dimension;
        this.size = Region.SIZE << zoomLevel;
        this.x = x & -this.size;
        this.z = z & -this.size;

        this.key = getKey(this.x, this.z, this.zoomLevel, this.dimension);

        final File surfaceImageFile = this.getImageFile();
        this.surfacePixels = new SurfacePixels(this, surfaceImageFile);
    }

    public void clear() {
        this.surfacePixels.clear();
    }

    public void close() {
        this.surfacePixels.close();
    }

    public boolean equals(int x, int z, int zoomLevel, DimensionType dimension) {
        x &= -this.size;
        z &= -this.size;
        return this.x == x && this.z == z && this.zoomLevel == zoomLevel && this.dimension == dimension;
    }

    @Override
    public boolean equals(Object o) {
        boolean equal = false;
        if (o != null) {
            if (o instanceof Region) {
                final Region region = (Region) o;
                equal = this.equals(region.x, region.z, region.zoomLevel, region.dimension);
            }
        }
        return equal;
    }

    public File getImageFile() {
        final File dimDir = addDimensionDirToPath(this.regionManager.imageDir, this.dimension);
        final File zoomDir = new File(dimDir, "z" + this.zoomLevel);

        zoomDir.mkdirs();

        final String filename = String.format("%d.%d.png", this.x >> Region.SHIFT + this.zoomLevel, this.z >> Region.SHIFT + this.zoomLevel);

        return new File(zoomDir, filename);
    }

    public int getPixelOffset(int x, int z) {
        return ((z >> this.zoomLevel & Region.SIZE - 1) << Region.SHIFT) + (x >> this.zoomLevel & Region.SIZE - 1);
    }

    public int[] getPixels() {
        return this.surfacePixels.getPixels();
    }

    public boolean isAreaWithin(int x, int z, int w, int h, DimensionType dimension) {
        return x >= this.x && z >= this.z && x + w <= this.x + this.size && z + h <= this.z + this.size && dimension == this.dimension;
    }

    @Override
    public String toString() {
        return String.format("(%d,%d) z%d %s", this.x, this.z, this.zoomLevel, this.dimension.getName());
    }

    public void updateChunk(MapWriterChunk chunk) {
        if (this.zoomLevel == 0) {
            this.surfacePixels.updateChunk(chunk);
        }
    }

    // scale an area of pixels by half in this region and write them
    // to the pixels of the next zoom level region.
    // x, z, w, h, in world block coordinates
    // returns the region the scaled pixels were written to, or null
    // on failure.
    public Region updateNextZoomLevel(int x, int z, int w, int h) {
        final int[] srcPixels = this.surfacePixels.getPixels();
        Region dstRegion = null;
        if (srcPixels != null) {
            final int dstZoomLevel = this.zoomLevel + 1;
            if (dstZoomLevel <= this.regionManager.maxZoom) {
                dstRegion = this.regionManager.getRegion(x, z, dstZoomLevel, this.dimension);
                final int dstW = Math.max(1, w >> dstRegion.zoomLevel);
                final int dstH = Math.max(1, h >> dstRegion.zoomLevel);

                // AND srcX and srcZ by -2 (0xfffffffe) to make sure that
                // they are always even. This prevents out of bounds exceptions
                // at higher zoom levels.
                final int srcX = x >> this.zoomLevel & Region.SIZE - 1 & -2;
                final int srcZ = z >> this.zoomLevel & Region.SIZE - 1 & -2;
                final int dstX = x >> dstRegion.zoomLevel & Region.SIZE - 1;
                final int dstZ = z >> dstRegion.zoomLevel & Region.SIZE - 1;

                dstRegion.surfacePixels.updateScaled(srcPixels, srcX, srcZ, dstX, dstZ, dstW, dstH);
            }
        }

        return dstRegion;
    }

    // update this entire region in the next zoom level
    public void updateZoomLevels() {
        this.updateZoomLevels(this.x, this.z, this.size, this.size);
    }

    // update all higher zoom level regions that this region
    // lies within
    public void updateZoomLevels(int x, int z, int w, int h) {
        Region nextRegion = this;
        while (nextRegion != null) {
            nextRegion = nextRegion.updateNextZoomLevel(x, z, w, h);
        }
    }
}

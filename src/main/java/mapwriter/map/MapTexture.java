package mapwriter.map;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import mapwriter.BackgroundExecutor;
import mapwriter.region.Region;
import mapwriter.region.RegionManager;
import mapwriter.tasks.MapUpdateViewTask;
import mapwriter.util.Texture;

public class MapTexture extends Texture {

    private class Rect {
        final int x, y, w, h;

        Rect (int x, int y, int w, int h) {

            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }
    }

    public int textureRegions;

    public int textureSize;
    private MapViewRequest loadedView = null;

    private MapViewRequest requestedView = null;

    // accessed from both render and background thread.
    // make sure all methods using it are synchronized.

    private final Region[] regionArray;

    private final List<Rect> textureUpdateQueue = new ArrayList<>();

    public MapTexture (int textureSize, boolean linearScaling) {

        super(textureSize, textureSize, 0x00000000, GL11.GL_LINEAR, GL11.GL_LINEAR, GL11.GL_REPEAT);

        this.setLinearScaling(linearScaling);

        this.textureRegions = textureSize >> Region.SHIFT;
        this.textureSize = textureSize;
        this.regionArray = new Region[this.textureRegions * this.textureRegions];
    }

    public void addTextureUpdate (int x, int z, int w, int h) {

        synchronized (this.textureUpdateQueue) {
            this.textureUpdateQueue.add(new Rect(x, z, w, h));
        }
    }

    public int getRegionIndex (int x, int z, int zoomLevel) {

        x = x >> Region.SHIFT + zoomLevel & this.textureRegions - 1;
        z = z >> Region.SHIFT + zoomLevel & this.textureRegions - 1;
        return z * this.textureRegions + x;
    }

    public boolean isLoaded (MapViewRequest req) {

        return this.loadedView != null && this.loadedView.mostlyEquals(req);
    }

    public boolean loadRegion (RegionManager regionManager, int x, int z, int zoomLevel, int dimension) {

        boolean loaded = false;
        final int index = this.getRegionIndex(x, z, zoomLevel);
        final Region currentRegion = this.regionArray[index];
        if (currentRegion == null || !currentRegion.equals(x, z, zoomLevel, dimension)) {
            final Region newRegion = regionManager.getRegion(x, z, zoomLevel, dimension);
            this.regionArray[index] = newRegion;
            this.updateTextureFromRegion(newRegion, newRegion.x, newRegion.z, newRegion.size, newRegion.size);
            loaded = true;
        }
        return loaded;
    }

    //
    // methods below this point run in the background thread
    //

    public int loadRegions (RegionManager regionManager, MapViewRequest req) {

        final int size = Region.SIZE << req.zoomLevel;
        int loadedCount = 0;
        for (int z = req.zMin; z <= req.zMax; z += size) {
            for (int x = req.xMin; x <= req.xMax; x += size) {
                if (this.loadRegion(regionManager, x, z, req.zoomLevel, req.dimension)) {
                    loadedCount++;
                }
            }
        }
        return loadedCount;
    }

    public void processTextureUpdates () {

        synchronized (this.textureUpdateQueue) {
            for (final Rect rect : this.textureUpdateQueue) {
                this.updateTextureArea(rect.x, rect.y, rect.w, rect.h);
            }
            this.textureUpdateQueue.clear();
        }
    }

    public void requestView (MapViewRequest req, BackgroundExecutor executor, RegionManager regionManager) {

        if (this.requestedView == null || !this.requestedView.equals(req)) {
            this.requestedView = req;
            executor.addTask(new MapUpdateViewTask(this, regionManager, req));
        }
    }

    public void setLoaded (MapViewRequest req) {

        this.loadedView = req;
    }

    public synchronized void setRGBOpaque (int x, int y, int w, int h, int[] pixels, int offset, int scanSize) {

        // TODO: Remove the need for this function. It would better if the
        // region pixels were stored as normal pixels (without the height in
        // the alpha channel). Then we could just directly copy the pixels
        // to the texture pixelBuf.
        final int bufOffset = y * this.w + x;
        for (int i = 0; i < h; i++) {
            this.setPixelBufPosition(bufOffset + i * this.w);
            final int rowOffset = offset + i * scanSize;
            for (int j = 0; j < w; j++) {
                int colour = pixels[rowOffset + j];
                if (colour != 0) {
                    colour |= 0xff000000;
                }
                this.pixelBufPut(colour);
            }
        }
    }

    public void updateArea (RegionManager regionManager, int x, int z, int w, int h, int dimension) {

        for (final Region region : this.regionArray) {
            if (region != null && region.isAreaWithin(x, z, w, h, dimension)) {
                this.updateTextureFromRegion(region, x, z, w, h);
            }
        }
    }

    public void updateTextureFromRegion (Region region, int x, int z, int w, int h) {

        final int tx = x >> region.zoomLevel & this.w - 1;
        final int ty = z >> region.zoomLevel & this.h - 1;
        int tw = w >> region.zoomLevel;
        int th = h >> region.zoomLevel;

        // make sure we don't write outside texture
        tw = Math.min(tw, this.w - tx);
        th = Math.min(th, this.h - th);

        // MwUtil.log("updateTextureFromRegion: region %s, %d %d %d %d -> %d %d
        // %d %d",
        // region, x, z, w, h, tx, ty, tw, th);

        final int[] pixels = region.getPixels();
        if (pixels != null) {
            this.setRGBOpaque(tx, ty, tw, th, pixels, region.getPixelOffset(x, z), Region.SIZE);
        }
        else {
            this.fillRect(tx, ty, tw, th, 0x00000000);
        }

        this.addTextureUpdate(tx, ty, tw, th);
    }
}

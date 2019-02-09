package mapwriter.overlay;

import mapwriter.api.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.DimensionType;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class OverlayGrid implements MapOverlayProvider {
    public class ChunkOverlay implements MapChunkOverlay {
        Point coord;

        public ChunkOverlay(int x, int z) {
            this.coord = new Point(x, z);
        }

        @Override
        public int getBorderColor() {
            return 0x7f000000;
        }

        @Override
        public float getBorderWidth() {
            return 0.5f;
        }

        @Override
        public int getColor() {
            return 0x00ffffff;
        }

        @Override
        public Point getCoordinates() {
            return this.coord;
        }

        @Override
        public float getFilling() {
            return 1f;
        }

        @Override
        public byte getBorder() {
            return 0b1111;
        }
    }

    @Override
    public List<MapChunkOverlay> getChunksOverlay(DimensionType dim, double centerX, double centerZ, double minX, double minZ, double maxX, double maxZ) {
        final int minChunkX = (MathHelper.ceil(minX) >> 4) - 1;
        final int minChunkZ = (MathHelper.ceil(minZ) >> 4) - 1;
        final int maxChunkX = (MathHelper.ceil(maxX) >> 4) + 1;
        final int maxChunkZ = (MathHelper.ceil(maxZ) >> 4) + 1;
        final int cX = (MathHelper.ceil(centerX) >> 4) + 1;
        final int cZ = (MathHelper.ceil(centerZ) >> 4) + 1;

        final int limitMinX = Math.max(minChunkX, cX - 100);
        final int limitMaxX = Math.min(maxChunkX, cX + 100);
        final int limitMinZ = Math.max(minChunkZ, cZ - 100);
        final int limitMaxZ = Math.min(maxChunkZ, cZ + 100);

        final List<MapChunkOverlay> chunks = new ArrayList<>();
        for (int x = limitMinX; x <= limitMaxX; x++) {
            for (int z = limitMinZ; z <= limitMaxZ; z++) {
                chunks.add(new ChunkOverlay(x, z));
            }
        }

        return chunks;
    }

    @Override
    public ITextComponent getMouseInfo(int mouseX, int mouseY) {
        return null;
    }

    @Override
    public ITextComponent getStatusInfo(DimensionType dim, int bX, int bY, int bZ) {
        return null;
    }

    @Override
    public void onDimensionChanged(DimensionType dimension, MapView mapview) {
    }

    @Override
    public void onDraw(MapView mapview, MapMode mapmode) {
    }

    @Override
    public void onMapCenterChanged(double vX, double vZ, MapView mapview) {
    }

    @Override
    public void onMiddleClick(DimensionType dim, int bX, int bZ, MapView mapview) {
    }

    @Override
    public boolean onMouseInput(MapView mapview, MapMode mapmode) {
        return false;
    }

    @Override
    public void onOverlayActivated(MapView mapview) {
    }

    @Override
    public void onOverlayDeactivated(MapView mapview) {
    }

    @Override
    public void onZoomChanged(int level, MapView mapview) {
    }
}

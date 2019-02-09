package mapwriter.map;

import mapwriter.MapWriter;
import mapwriter.region.ChunkRender;
import mapwriter.region.MapChunk;
import mapwriter.util.Texture;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Arrays;

public class UndergroundTexture extends Texture {

    class RenderChunk implements MapChunk {
        Chunk chunk;

        public RenderChunk(Chunk chunk) {
            this.chunk = chunk;
        }

        @Override
        public Biome getBiome(int x, int y, int z) {
            final int localX = x & 15;
            final int localZ = z & 15;
            int bid = this.chunk.getBiomeArray()[localZ << 4 | localX] & 255;

            if (bid == 255) {
                return Minecraft.getMinecraft().world.getBiomeProvider().getBiome(new BlockPos(x, y, z), Biomes.PLAINS);
            }
            return Biome.getBiome(bid);
        }

        @Override
        public IBlockState getBlockState(int x, int y, int z) {
            return this.chunk.getBlockState(x, y, z);
        }

        @Override
        public int getLightValue(int x, int y, int z) {
            return this.chunk.getLightSubtracted(new BlockPos(x, y, z), 0);
        }

        @Override
        public int getMaxY() {
            return this.chunk.getTopFilledSegment() + 15;
        }
    }

    private final MapWriter mw;
    private int px = 0;
    private int py = 0;
    private int pz = 0;
    private DimensionType dimension = DimensionType.OVERWORLD;
    private int updateX;
    private int updateZ;
    private final byte[][] updateFlags = new byte[9][256];
    private final Point[] loadedChunkArray;
    private final int textureSize;
    private final int textureChunks;

    private final int[] pixels;

    public UndergroundTexture(MapWriter mw, int textureSize, boolean linearScaling) {

        super(textureSize, textureSize, 0x00000000, GL11.GL_NEAREST, GL11.GL_NEAREST, GL11.GL_REPEAT);
        this.setLinearScaling(false);
        this.textureSize = textureSize;
        this.textureChunks = textureSize >> 4;
        this.loadedChunkArray = new Point[this.textureChunks * this.textureChunks];
        this.pixels = new int[textureSize * textureSize];
        Arrays.fill(this.pixels, 0xff000000);
        this.mw = mw;
    }

    public void clear() {

        Arrays.fill(this.pixels, 0xff000000);
        this.updateTexture();
    }

    public void clearChunkPixels(int cx, int cz) {

        final int tx = cx << 4 & this.textureSize - 1;
        final int tz = cz << 4 & this.textureSize - 1;
        for (int j = 0; j < 16; j++) {
            final int offset = (tz + j) * this.textureSize + tx;
            Arrays.fill(this.pixels, offset, offset + 16, 0xff000000);
        }
        this.updateTextureArea(tx, tz, 16, 16);
    }

    public int getLoadedChunkOffset(int cx, int cz) {

        final int cxOffset = cx & this.textureChunks - 1;
        final int czOffset = cz & this.textureChunks - 1;
        return czOffset * this.textureChunks + cxOffset;
    }

    public boolean isChunkInTexture(int cx, int cz) {

        final Point requestedChunk = new Point(cx, cz);
        final int offset = this.getLoadedChunkOffset(cx, cz);
        final Point chunk = this.loadedChunkArray[offset];
        return chunk != null && chunk.equals(requestedChunk);
    }

    public void requestView(MapView view) {

        final int cxMin = (int) view.getMinX() >> 4;
        final int czMin = (int) view.getMinZ() >> 4;
        final int cxMax = (int) view.getMaxX() >> 4;
        final int czMax = (int) view.getMaxZ() >> 4;
        for (int cz = czMin; cz <= czMax; cz++) {
            for (int cx = cxMin; cx <= cxMax; cx++) {
                final Point requestedChunk = new Point(cx, cz);
                final int offset = this.getLoadedChunkOffset(cx, cz);
                final Point currentChunk = this.loadedChunkArray[offset];
                if (currentChunk == null || !currentChunk.equals(requestedChunk)) {
                    this.clearChunkPixels(cx, cz);
                    this.loadedChunkArray[offset] = requestedChunk;
                }
            }
        }
    }

    public void update() {

        this.clearFlags();

        if (this.dimension != this.mw.playerDimension) {
            this.clear();
            this.dimension = this.mw.playerDimension;
        }
        this.px = this.mw.playerXInt;
        this.py = this.mw.playerYInt;
        this.pz = this.mw.playerZInt;

        this.updateX = (this.px >> 4) - 1;
        this.updateZ = (this.pz >> 4) - 1;

        this.processBlock(this.px - (this.updateX << 4), this.py, this.pz - (this.updateZ << 4));

        final int cxMax = this.updateX + 2;
        final int czMax = this.updateZ + 2;
        final WorldClient world = this.mw.mc.world;
        int flagOffset = 0;
        for (int cz = this.updateZ; cz <= czMax; cz++) {
            for (int cx = this.updateX; cx <= cxMax; cx++) {
                if (this.isChunkInTexture(cx, cz)) {
                    final Chunk chunk = world.getChunkFromChunkCoords(cx, cz);
                    final int tx = cx << 4 & this.textureSize - 1;
                    final int tz = cz << 4 & this.textureSize - 1;
                    final int pixelOffset = tz * this.textureSize + tx;
                    final byte[] mask = this.updateFlags[flagOffset];
                    ChunkRender.renderUnderground(this.mw.blockColours, new RenderChunk(chunk), this.pixels, pixelOffset, this.textureSize, this.py, mask);
                }
                flagOffset += 1;
            }
        }

        this.renderToTexture(this.py + 1);
    }

    private void clearFlags() {

        for (final byte[] chunkFlags : this.updateFlags) {
            Arrays.fill(chunkFlags, ChunkRender.FLAG_UNPROCESSED);
        }
    }

    private void processBlock(int xi, int y, int zi) {

        final int x = (this.updateX << 4) + xi;
        final int z = (this.updateZ << 4) + zi;

        final int xDist = this.px - x;
        final int zDist = this.pz - z;

        if (xDist * xDist + zDist * zDist <= 256) {
            if (this.isChunkInTexture(x >> 4, z >> 4)) {
                final int chunkOffset = (zi >> 4) * 3 + (xi >> 4);
                final int columnXi = xi & 0xf;
                final int columnZi = zi & 0xf;
                final int columnOffset = (columnZi << 4) + columnXi;
                final byte columnFlag = this.updateFlags[chunkOffset][columnOffset];

                if (columnFlag == ChunkRender.FLAG_UNPROCESSED) {
                    // if column not yet processed
                    final WorldClient world = this.mw.mc.world;
                    final IBlockState state = world.getBlockState(new BlockPos(x, y, z));
                    final Block block = state.getBlock();
                    if (block == null || !state.isOpaqueCube()) {
                        // if block is not opaque
                        this.updateFlags[chunkOffset][columnOffset] = ChunkRender.FLAG_NON_OPAQUE;
                        this.processBlock(xi + 1, y, zi);
                        this.processBlock(xi - 1, y, zi);
                        this.processBlock(xi, y, zi + 1);
                        this.processBlock(xi, y, zi - 1);
                    } else {
                        // block is opaque
                        this.updateFlags[chunkOffset][columnOffset] = ChunkRender.FLAG_OPAQUE;
                    }
                }
            }
        }
    }

    void renderToTexture(int y) {

        this.setPixelBufPosition(0);
        for (final int colour : this.pixels) {
            final int height = colour >> 24 & 0xff;
            int alpha = y >= height ? 255 - (y - height) * 8 : 0;
            if (alpha < 0) {
                alpha = 0;
            }
            this.pixelBufPut(alpha << 24 & 0xff000000 | colour & 0xffffff);
        }
        this.updateTexture();
    }

}

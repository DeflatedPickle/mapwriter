package mapwriter.region;

import net.minecraft.block.state.IBlockState;
import net.minecraft.world.biome.Biome;

public interface MapChunk {
    Biome getBiome(int x, int y, int z);

    IBlockState getBlockState(int x, int y, int z);

    int getLightValue(int x, int y, int z);

    int getMaxY();
}

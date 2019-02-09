package mapwriter.region;

import mapwriter.forge.MapWriterForge;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MapWriterChunk implements MapChunk {

    public static final int SIZE = 16;

    // load from anvil file
    public static MapWriterChunk read(int x, int z, DimensionType dimension, RegionFileCache regionFileCache) {
        final boolean flag = true;
        byte[] biomeArray = null;
        final ExtendedBlockStorage[] data = new ExtendedBlockStorage[16];
        final Map<BlockPos, TileEntity> teMap = new HashMap<>();

        DataInputStream dis = null;
        final RegionFile regionFile = regionFileCache.getRegionFile(x << 4, z << 4, dimension);
        if (!regionFile.isOpen() && regionFile.exists()) {

            regionFile.open();
        }

        if (regionFile.isOpen()) {
            dis = regionFile.getChunkDataInputStream(x & 31, z & 31);
        }

        if (dis != null) {
            try {

                // chunk NBT structure:
                //
                // COMPOUND ""
                // COMPOUND "level"
                // INT "xPos"
                // INT "zPos"
                // LONG "LastUpdate"
                // BYTE "TerrainPopulated"
                // BYTE_ARRAY "Biomes"
                // INT_ARRAY "HeightMap"
                // LIST(COMPOUND) "Sections"
                // BYTE "Y"
                // BYTE_ARRAY "Blocks"
                // BYTE_ARRAY "Add"
                // BYTE_ARRAY "Data"
                // BYTE_ARRAY "BlockLight"
                // BYTE_ARRAY "SkyLight"
                // END
                // LIST(COMPOUND) "Entities"
                // LIST(COMPOUND) "TileEntities"
                // LIST(COMPOUND) "TileTicks"
                // END
                // END
                final NBTTagCompound nbttagcompound = CompressedStreamTools.read(dis);
                final NBTTagCompound level = nbttagcompound.getCompoundTag("Level");

                final int xNbt = level.getInteger("xPos");
                final int zNbt = level.getInteger("zPos");

                if (xNbt != x || zNbt != z) {
                    MapWriterForge.LOGGER.warn("chunk ({}, {}) has NBT coords ({}, {})", x, z, xNbt, zNbt);
                }

                final NBTTagList sections = level.getTagList("Sections", 10);

                for (int k = 0; k < sections.tagCount(); ++k) {
                    final NBTTagCompound section = sections.getCompoundTagAt(k);
                    final int y = section.getByte("Y");
                    final ExtendedBlockStorage extendedblockstorage = new ExtendedBlockStorage(y << 4, flag);
                    final byte[] abyte = nbttagcompound.getByteArray("Blocks");
                    final NibbleArray nibblearray = new NibbleArray(nbttagcompound.getByteArray("Data"));
                    final NibbleArray nibblearray1 = nbttagcompound.hasKey("Add", 7) ? new NibbleArray(nbttagcompound.getByteArray("Add")) : null;
                    extendedblockstorage.getData().setDataFromNBT(abyte, nibblearray, nibblearray1);
                    extendedblockstorage.setBlockLight(new NibbleArray(nbttagcompound.getByteArray("BlockLight")));

                    if (flag) {
                        extendedblockstorage.setSkyLight(new NibbleArray(nbttagcompound.getByteArray("SkyLight")));
                    }

                    extendedblockstorage.recalculateRefCounts();
                    data[y] = extendedblockstorage;
                }

                biomeArray = level.getByteArray("Biomes");

                final NBTTagList nbttaglist2 = level.getTagList("TileEntities", 10);

                if (nbttaglist2 != null) {
                    for (int i1 = 0; i1 < nbttaglist2.tagCount(); ++i1) {
                        final NBTTagCompound nbttagcompound4 = nbttaglist2.getCompoundTagAt(i1);
                        final TileEntity tileentity = TileEntity.create(null, nbttagcompound4);
                        if (tileentity != null) {
                            teMap.put(tileentity.getPos(), tileentity);
                        }
                    }
                }

            } catch (final IOException e) {
                MapWriterForge.LOGGER.error("{}: could not read chunk ({}, {}) from region file\n", e, x, z);
            } finally {
                try {
                    dis.close();
                } catch (final IOException e) {
                    MapWriterForge.LOGGER.error("MapWriterChunk.read: {} while closing input stream", e);
                }
            }
        }

        return new MapWriterChunk(x, z, dimension, data, biomeArray, teMap);
    }

    public final int x;

    public final int z;

    public final DimensionType dimension;

    public ExtendedBlockStorage[] dataArray;

    public final Map<BlockPos, TileEntity> tileentityMap;

    public final byte[] biomeArray;

    public final int maxY;

    public MapWriterChunk(int x, int z, DimensionType dimension, ExtendedBlockStorage[] data, byte[] biomeArray, Map<BlockPos, TileEntity> teMap) {
        this.x = x;
        this.z = z;
        this.dimension = dimension;
        this.biomeArray = biomeArray;
        this.tileentityMap = teMap;
        this.dataArray = data;
        int maxY = 0;
        for (int y = 0; y < 16; y++) {
            if (data[y] != null) {
                maxY = (y << 4) + 15;
            }
        }
        this.maxY = maxY;
    }

    @Override
    public Biome getBiome(int x, int y, int z) {
        final int localX = x & 15;
        final int localZ = z & 15;
        int bid = this.biomeArray[localZ << 4 | localX] & 255;

        if (bid == 255) {
            return Minecraft.getMinecraft().world.getBiomeProvider().getBiome(new BlockPos(x, y, z), Biomes.PLAINS);
        } else {
            return Biome.getBiome(bid);
        }
    }

    @Override
    public IBlockState getBlockState(int x, int y, int z) {
        final int yi = y >> 4 & 0xf;
        return this.dataArray != null && this.dataArray[yi] != null ? this.dataArray[yi].getData().get(x & 15, y & 15, z & 15) : Blocks.AIR.getDefaultState();
    }

    public Long getCoordIntPair() {
        return ChunkPos.asLong(this.x, this.z);
    }

    @Override
    public int getLightValue(int x, int y, int z) {
        return 15;
    }

    @Override
    public int getMaxY() {
        return this.maxY;
    }

    public boolean isEmpty() {
        return this.maxY <= 0;
    }

    @Override
    public String toString() {
        return String.format("(%d, %d) dim %s", this.x, this.z, this.dimension.getName());
    }

    public synchronized boolean write(RegionFileCache regionFileCache) {
        boolean error = false;
        final RegionFile regionFile = regionFileCache.getRegionFile(this.x << 4, this.z << 4, this.dimension);
        if (!regionFile.isOpen()) {
            error = regionFile.open();
        }
        if (!error) {
            final DataOutputStream dos = regionFile.getChunkDataOutputStream(this.x & 31, this.z & 31);
            if (dos != null) {
                try {

                    CompressedStreamTools.write(this.writeChunkToNBT(), dos);
                } catch (final IOException e) {
                    MapWriterForge.LOGGER.error("{}: could not write chunk ({}, {}) to region file", e, this.x, this.z);
                    error = true;
                } finally {
                    try {
                        dos.close();
                    } catch (final IOException e) {
                        MapWriterForge.LOGGER.error("{} while closing chunk data output stream", e);
                    }
                }
            } else {
                MapWriterForge.LOGGER.error("error: could not get output stream for chunk ({}, {})", this.x, this.z);
            }
        } else {
            MapWriterForge.LOGGER.error("error: could not open region file for chunk ({}, {})", this.x, this.z);
        }

        return error;
    }

    // changed to use the NBTTagCompound that minecraft uses. this makes the
    // local way of saving anvill data the same as Minecraft world data
    private NBTTagCompound writeChunkToNBT() {

        final NBTTagCompound level = new NBTTagCompound();
        final NBTTagCompound compound = new NBTTagCompound();
        level.setTag("Level", compound);

        compound.setInteger("xPos", this.x);
        compound.setInteger("zPos", this.z);
        final ExtendedBlockStorage[] aextendedblockstorage = this.dataArray;
        final NBTTagList nbttaglist = new NBTTagList();

        for (final ExtendedBlockStorage extendedblockstorage : aextendedblockstorage) {
            if (extendedblockstorage != Chunk.NULL_BLOCK_STORAGE) {
                final NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Y", (byte) (extendedblockstorage.getYLocation() >> 4 & 255));
                final byte[] abyte = new byte[4096];
                final NibbleArray nibblearray = new NibbleArray();
                final NibbleArray nibblearray1 = extendedblockstorage.getData().getDataForNBT(abyte, nibblearray);
                nbttagcompound.setByteArray("Blocks", abyte);
                nbttagcompound.setByteArray("Data", nibblearray.getData());

                if (nibblearray1 != null) {
                    nbttagcompound.setByteArray("Add", nibblearray1.getData());
                }

                nbttagcompound.setByteArray("BlockLight", extendedblockstorage.getBlockLight().getData());

                if (extendedblockstorage.getSkyLight() != null && extendedblockstorage.getSkyLight().getData() != null) {
                    nbttagcompound.setByteArray("SkyLight", extendedblockstorage.getSkyLight().getData());
                } else {
                    nbttagcompound.setByteArray("SkyLight", new byte[extendedblockstorage.getBlockLight().getData().length]);
                }

                nbttaglist.appendTag(nbttagcompound);
            }
        }

        compound.setTag("Sections", nbttaglist);
        compound.setByteArray("Biomes", this.biomeArray);

        final NBTTagList nbttaglist2 = new NBTTagList();

        for (final TileEntity tileentity : this.tileentityMap.values()) {
            try {
                final NBTTagCompound nbttagcompound3 = tileentity.writeToNBT(new NBTTagCompound());
                nbttaglist2.appendTag(nbttagcompound3);
            } catch (final Exception e) {
                // we eat this exception becous we are doing something we
                // shouldnt do on client side.
            }
        }

        compound.setTag("TileEntities", nbttaglist2);

        return level;
    }
}

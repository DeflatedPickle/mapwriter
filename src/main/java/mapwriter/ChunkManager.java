package mapwriter;

import mapwriter.config.Config;
import mapwriter.region.MapWriterChunk;
import mapwriter.tasks.SaveChunkTask;
import mapwriter.tasks.UpdateSurfaceChunksTask;
import mapwriter.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ChunkManager {
    private static final int VISIBLE_FLAG = 0x01;
    private static final int VIEWED_FLAG = 0x02;

    // create MapWriterChunk from Minecraft chunk.
    // only MapWriterChunk's should be used in the background thread.
    // make this a full copy of chunk data to prevent possible race conditions
    // <-- done
    public static MapWriterChunk copyToMwChunk(Chunk chunk) {
        Map<BlockPos, TileEntity> tileEntityMap = new HashMap<>(chunk.getTileEntityMap());
        final byte[] biomeArray = Arrays.copyOf(chunk.getBiomeArray(), chunk.getBiomeArray().length);
        final ExtendedBlockStorage[] dataArray = Arrays.copyOf(chunk.getBlockStorageArray(), chunk.getBlockStorageArray().length);

        return new MapWriterChunk(chunk.x, chunk.z, chunk.getWorld().provider.getDimensionType(), dataArray, biomeArray, tileEntityMap);
    }

    public MapWriter mw;
    private boolean closed = false;

    private final CircularHashMap<Chunk, Integer> chunkMap = new CircularHashMap<>();

    public ChunkManager(MapWriter mw) {
        this.mw = mw;
    }

    public synchronized void addChunk(Chunk chunk) {
        if (!this.closed && chunk != null) {
            this.chunkMap.put(chunk, 0);
        }
    }

    public synchronized void close() {
        this.closed = true;
        this.saveChunks();
        this.chunkMap.clear();
    }

    public void onTick() {
        if (!this.closed) {
            if ((this.mw.tickCounter & 0xf) == 0) {
                this.updateUndergroundChunks();
            } else {
                this.updateSurfaceChunks();
            }
        }
    }

    public synchronized void removeChunk(Chunk chunk) {
        if (!this.closed && chunk != null) {
            if (!this.chunkMap.containsKey(chunk)) {
                return; // FIXME: Is this failsafe enough for unloading?
            }
            final int flags = this.chunkMap.get(chunk);
            if ((flags & ChunkManager.VIEWED_FLAG) != 0) {
                this.addSaveChunkTask(chunk);
            }
            this.chunkMap.remove(chunk);
        }
    }

    public synchronized void saveChunks() {
        for (final Map.Entry<Chunk, Integer> entry : this.chunkMap.entrySet()) {
            final int flags = entry.getValue();
            if ((flags & ChunkManager.VIEWED_FLAG) != 0) {
                this.addSaveChunkTask(entry.getKey());
            }
        }
    }

    public void updateSurfaceChunks() {
        final int chunksToUpdate = Math.min(this.chunkMap.size(), Config.chunksPerTick);
        final MapWriterChunk[] chunkArray = new MapWriterChunk[chunksToUpdate];
        for (int i = 0; i < chunksToUpdate; i++) {
            final Map.Entry<Chunk, Integer> entry = this.chunkMap.getNextEntry();
            if (entry != null) {
                // if this chunk is within a certain distance to the player then
                // add it to the viewed set
                final Chunk chunk = entry.getKey();

                int flags = entry.getValue();
                if (Utils.distToChunkSq(this.mw.playerXInt, this.mw.playerZInt, chunk) <= Config.maxChunkSaveDistSq) {
                    flags |= ChunkManager.VISIBLE_FLAG | ChunkManager.VIEWED_FLAG;
                } else {
                    flags &= ~ChunkManager.VISIBLE_FLAG;
                }
                entry.setValue(flags);

                if ((flags & ChunkManager.VISIBLE_FLAG) != 0) {
                    chunkArray[i] = copyToMwChunk(chunk);
                    this.mw.executor.addTask(new UpdateSurfaceChunksTask(this.mw, chunkArray[i]));
                } else {
                    chunkArray[i] = null;
                }
            }
        }

        // this.mw.executor.addTask(new UpdateSurfaceChunksTask(this.mw,
        // chunkArray));
    }

    public void updateUndergroundChunks() {
        final int chunkArrayX = (this.mw.playerXInt >> 4) - 1;
        final int chunkArrayZ = (this.mw.playerZInt >> 4) - 1;
        final MapWriterChunk[] chunkArray = new MapWriterChunk[9];
        for (int z = 0; z < 3; z++) {
            for (int x = 0; x < 3; x++) {
                final Chunk chunk = this.mw.mc.world.getChunkFromChunkCoords(chunkArrayX + x, chunkArrayZ + z);
                if (!chunk.isEmpty()) {
                    chunkArray[z * 3 + x] = copyToMwChunk(chunk);
                }
            }
        }
    }

    private void addSaveChunkTask(Chunk chunk) {
        if (Minecraft.getMinecraft().isSingleplayer() && Config.regionFileOutputEnabledMP || !Minecraft.getMinecraft().isSingleplayer() && Config.regionFileOutputEnabledSP) {
            if (!chunk.isEmpty()) {
                this.mw.executor.addTask(new SaveChunkTask(copyToMwChunk(chunk), this.mw.regionManager));
            }
        }
    }
}
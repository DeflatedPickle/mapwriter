package mapwriter.tasks;

import mapwriter.Mw;
import mapwriter.map.MapTexture;
import mapwriter.region.MwChunk;
import mapwriter.region.RegionManager;
import net.minecraft.util.math.ChunkPos;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class UpdateSurfaceChunksTask extends Task {
    private static Map<Long, UpdateSurfaceChunksTask> chunksUpdating = new HashMap<>();
    private MwChunk chunk;
    private final RegionManager regionManager;
    private final MapTexture mapTexture;
    private final AtomicBoolean running = new AtomicBoolean();

    public UpdateSurfaceChunksTask(Mw mw, MwChunk chunk) {
        this.mapTexture = mw.mapTexture;
        this.regionManager = mw.regionManager;
        this.chunk = chunk;
    }

    @Override
    public boolean CheckForDuplicate() {
        final Long coords = ChunkPos.asLong(this.chunk.x, this.chunk.z);

        if (!UpdateSurfaceChunksTask.chunksUpdating.containsKey(coords)) {
            UpdateSurfaceChunksTask.chunksUpdating.put(coords, this);
            return false;
        } else {
            final UpdateSurfaceChunksTask task2 = UpdateSurfaceChunksTask.chunksUpdating.get(coords);
            if (!task2.running.get()) {
                task2.updateChunkData(this.chunk);
            } else {
                UpdateSurfaceChunksTask.chunksUpdating.put(coords, this);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onComplete() {

        final Long coords = this.chunk.getCoordIntPair();
        UpdateSurfaceChunksTask.chunksUpdating.remove(coords);
        this.running.set(false);
    }

    @Override
    public void run() {

        this.running.set(true);
        if (this.chunk != null) {
            // update the chunk in the region pixels
            this.regionManager.updateChunk(this.chunk);
            // copy updated region pixels to maptexture
            this.mapTexture.updateArea(this.chunk.x << 4, this.chunk.z << 4, MwChunk.SIZE, MwChunk.SIZE, this.chunk.dimension);
        }
    }

    public void updateChunkData(MwChunk chunk) {

        this.chunk = chunk;
    }
}

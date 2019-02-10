package mapwriter.tasks;

import mapwriter.MapWriter;
import mapwriter.map.MapTexture;
import mapwriter.region.MapWriterChunk;
import mapwriter.region.RegionManager;
import net.minecraft.util.math.ChunkPos;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class TaskUpdateSurfaceChunks extends Task {
    private static Map<Long, TaskUpdateSurfaceChunks> chunksUpdating = new HashMap<>();
    private MapWriterChunk chunk;
    private final RegionManager regionManager;
    private final MapTexture mapTexture;
    private final AtomicBoolean running = new AtomicBoolean();

    public TaskUpdateSurfaceChunks(MapWriter mw, MapWriterChunk chunk) {
        this.mapTexture = mw.mapTexture;
        this.regionManager = mw.regionManager;
        this.chunk = chunk;
    }

    @Override
    public boolean checkForDuplicate() {
        final Long coords = ChunkPos.asLong(this.chunk.x, this.chunk.z);

        if (!TaskUpdateSurfaceChunks.chunksUpdating.containsKey(coords)) {
            TaskUpdateSurfaceChunks.chunksUpdating.put(coords, this);
            return false;
        } else {
            final TaskUpdateSurfaceChunks task2 = TaskUpdateSurfaceChunks.chunksUpdating.get(coords);
            if (!task2.running.get()) {
                task2.updateChunkData(this.chunk);
            } else {
                TaskUpdateSurfaceChunks.chunksUpdating.put(coords, this);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onComplete() {
        final Long coords = this.chunk.getCoordIntPair();
        TaskUpdateSurfaceChunks.chunksUpdating.remove(coords);
        this.running.set(false);
    }

    @Override
    public void run() {
        this.running.set(true);
        if (this.chunk != null) {
            // update the chunk in the region pixels
            this.regionManager.updateChunk(this.chunk);
            // copy updated region pixels to maptexture
            this.mapTexture.updateArea(this.chunk.x << 4, this.chunk.z << 4, MapWriterChunk.SIZE, MapWriterChunk.SIZE, this.chunk.dimension);
        }
    }

    public void updateChunkData(MapWriterChunk chunk) {
        this.chunk = chunk;
    }
}

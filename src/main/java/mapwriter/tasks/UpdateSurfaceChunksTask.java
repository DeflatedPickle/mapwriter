package mapwriter.tasks;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import mapwriter.Mw;
import mapwriter.map.MapTexture;
import mapwriter.region.MwChunk;
import mapwriter.region.RegionManager;
import net.minecraft.util.math.ChunkPos;

public class UpdateSurfaceChunksTask extends Task {
    private static Map chunksUpdating = new HashMap<Long, UpdateSurfaceChunksTask>();
    private MwChunk chunk;
    private final RegionManager regionManager;
    private final MapTexture mapTexture;
    private final AtomicBoolean Running = new AtomicBoolean();

    public UpdateSurfaceChunksTask (Mw mw, MwChunk chunk) {

        this.mapTexture = mw.mapTexture;
        this.regionManager = mw.regionManager;
        this.chunk = chunk;
    }

    @Override
    public boolean CheckForDuplicate () {

        final Long coords = ChunkPos.asLong(this.chunk.x, this.chunk.z);

        if (!UpdateSurfaceChunksTask.chunksUpdating.containsKey(coords)) {
            UpdateSurfaceChunksTask.chunksUpdating.put(coords, this);
            return false;
        }
        else {
            final UpdateSurfaceChunksTask task2 = (UpdateSurfaceChunksTask) UpdateSurfaceChunksTask.chunksUpdating.get(coords);
            if (task2.Running.get() == false) {
                task2.UpdateChunkData(this.chunk);
            }
            else {
                UpdateSurfaceChunksTask.chunksUpdating.put(coords, this);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onComplete () {

        final Long coords = this.chunk.getCoordIntPair();
        UpdateSurfaceChunksTask.chunksUpdating.remove(coords);
        this.Running.set(false);
    }

    @Override
    public void run () {

        this.Running.set(true);
        if (this.chunk != null) {
            // update the chunk in the region pixels
            this.regionManager.updateChunk(this.chunk);
            // copy updated region pixels to maptexture
            this.mapTexture.updateArea(this.regionManager, this.chunk.x << 4, this.chunk.z << 4, MwChunk.SIZE, MwChunk.SIZE, this.chunk.dimension);
        }
    }

    public void UpdateChunkData (MwChunk chunk) {

        this.chunk = chunk;
    }
}

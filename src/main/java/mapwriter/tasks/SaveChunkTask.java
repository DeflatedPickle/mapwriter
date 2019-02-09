package mapwriter.tasks;

import mapwriter.region.MwChunk;
import mapwriter.region.RegionManager;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class SaveChunkTask extends Task {
    private static HashMap<Long, SaveChunkTask> chunksUpdating = new HashMap<>();
    private MwChunk chunk;
    private RegionManager regionManager;
    private final AtomicBoolean Running = new AtomicBoolean();

    public SaveChunkTask(MwChunk chunk, RegionManager regionManager) {

        this.chunk = chunk;
        this.regionManager = regionManager;
    }

    @Override
    public boolean CheckForDuplicate() {

        final Long coords = this.chunk.getCoordIntPair();

        if (!SaveChunkTask.chunksUpdating.containsKey(coords)) {
            SaveChunkTask.chunksUpdating.put(coords, this);
            return false;
        } else {
            final SaveChunkTask task2 = SaveChunkTask.chunksUpdating.get(coords);
            if (task2.Running.get() == false) {
                task2.UpdateChunkData(this.chunk, this.regionManager);
            } else {
                SaveChunkTask.chunksUpdating.put(coords, this);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onComplete() {

        final Long coords = this.chunk.getCoordIntPair();
        SaveChunkTask.chunksUpdating.remove(coords);
        this.Running.set(false);
    }

    @Override
    public void run() {

        this.Running.set(true);
        this.chunk.write(this.regionManager.regionFileCache);
    }

    public void UpdateChunkData(MwChunk chunk, RegionManager regionManager) {

        this.chunk = chunk;
        this.regionManager = regionManager;
    }
}

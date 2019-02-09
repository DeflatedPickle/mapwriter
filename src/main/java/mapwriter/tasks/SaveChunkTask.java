package mapwriter.tasks;

import mapwriter.region.MapWriterChunk;
import mapwriter.region.RegionManager;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class SaveChunkTask extends Task {
    private static HashMap<Long, SaveChunkTask> chunksUpdating = new HashMap<>();
    private MapWriterChunk chunk;
    private RegionManager regionManager;
    private final AtomicBoolean running = new AtomicBoolean();

    public SaveChunkTask(MapWriterChunk chunk, RegionManager regionManager) {
        this.chunk = chunk;
        this.regionManager = regionManager;
    }

    @Override
    public boolean checkForDuplicate() {
        final Long coords = this.chunk.getCoordIntPair();

        if (!SaveChunkTask.chunksUpdating.containsKey(coords)) {
            SaveChunkTask.chunksUpdating.put(coords, this);
            return false;
        } else {
            final SaveChunkTask task2 = SaveChunkTask.chunksUpdating.get(coords);
            if (!task2.running.get()) {
                task2.updateChunkData(this.chunk, this.regionManager);
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
        this.running.set(false);
    }

    @Override
    public void run() {
        this.running.set(true);
        this.chunk.write(this.regionManager.regionFileCache);
    }

    public void updateChunkData(MapWriterChunk chunk, RegionManager regionManager) {
        this.chunk = chunk;
        this.regionManager = regionManager;
    }
}

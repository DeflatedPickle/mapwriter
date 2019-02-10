package mapwriter.tasks;

import mapwriter.region.MapWriterChunk;
import mapwriter.region.RegionManager;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class TaskSaveChunk extends Task {
    private static HashMap<Long, TaskSaveChunk> chunksUpdating = new HashMap<>();
    private MapWriterChunk chunk;
    private RegionManager regionManager;
    private final AtomicBoolean running = new AtomicBoolean();

    public TaskSaveChunk(MapWriterChunk chunk, RegionManager regionManager) {
        this.chunk = chunk;
        this.regionManager = regionManager;
    }

    @Override
    public boolean checkForDuplicate() {
        final Long coords = this.chunk.getCoordIntPair();

        if (!TaskSaveChunk.chunksUpdating.containsKey(coords)) {
            TaskSaveChunk.chunksUpdating.put(coords, this);
            return false;
        } else {
            final TaskSaveChunk task2 = TaskSaveChunk.chunksUpdating.get(coords);
            if (!task2.running.get()) {
                task2.updateChunkData(this.chunk, this.regionManager);
            } else {
                TaskSaveChunk.chunksUpdating.put(coords, this);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onComplete() {
        final Long coords = this.chunk.getCoordIntPair();
        TaskSaveChunk.chunksUpdating.remove(coords);
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

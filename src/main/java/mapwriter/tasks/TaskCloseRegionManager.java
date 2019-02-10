package mapwriter.tasks;

import mapwriter.region.RegionManager;

public class TaskCloseRegionManager extends Task {

    private final RegionManager regionManager;

    public TaskCloseRegionManager(RegionManager regionManager) {
        this.regionManager = regionManager;
    }

    @Override
    public boolean checkForDuplicate() {
        return false;
    }

    @Override
    public void onComplete() {}

    @Override
    public void run() {
        this.regionManager.close();
    }
}

package com.cabchinoe.minimap.tasks;

import com.cabchinoe.minimap.region.RegionManager;

public class CloseRegionManagerTask extends Task {

	private final RegionManager regionManager;
	
	public CloseRegionManagerTask(RegionManager regionManager) {
		this.regionManager = regionManager;
	}
	
	@Override
	public void run() {
		this.regionManager.close();
	}
	
	@Override
	public void onComplete() {
	}
}

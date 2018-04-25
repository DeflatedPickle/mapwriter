package com.cabchinoe.minimap.tasks;

import com.cabchinoe.minimap.Mw;
import com.cabchinoe.minimap.MwUtil;
import com.cabchinoe.minimap.region.BlockColours;
import com.cabchinoe.minimap.region.RegionManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RebuildRegionsTask extends Task {
	
	final RegionManager regionManager;
	final BlockColours blockColours;
	final int x, z, w, h, dimension;
	String msg = "";
	
	public RebuildRegionsTask(Mw mw, int x, int z, int w, int h, int dimension) {
		this.regionManager = mw.regionManager;
		this.blockColours = mw.blockColours;
		this.x = x;
		this.z = z;
		this.w = w;
		this.h = h;
		this.dimension = dimension;
	}
	
	@Override
	public void run() {
		this.regionManager.blockColours = blockColours;
		this.regionManager.rebuildRegions(this.x, this.z, this.w, this.h, this.dimension);
	}
	
	@Override
	public void onComplete() {
		MwUtil.printBoth("rebuild task complete");
	}

}

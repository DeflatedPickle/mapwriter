package com.cabchinoe.minimap.map.mapmode;

import com.cabchinoe.minimap.forge.MwConfig;

public class SmallMapMode extends MapMode {
	public SmallMapMode(MwConfig config) {
		super(config, "smallMap");
		
		this.heightPercent = 30;
		this.marginTop = 10;
		this.marginBottom = -1;
		//for 1.12
		this.marginLeft = 10;
		this.marginRight = -1;
		
		this.playerArrowSize = 4;
		this.markerSize = 3;
		
		this.coordsEnabled = true;
		
		this.loadConfig();
	}
}

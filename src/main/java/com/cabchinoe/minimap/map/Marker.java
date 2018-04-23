package com.cabchinoe.minimap.map;

import java.awt.Point;

import com.cabchinoe.common.Render;
import com.cabchinoe.minimap.MwUtil;
import com.cabchinoe.minimap.map.mapmode.MapMode;

public class Marker {
	public final String name;
	public final String groupName;
	public int x;
	public int y;
	public int z;
	public int dimension;
	public int colour;
	
	public Point.Double screenPos = new Point.Double(0, 0);

	
	public Marker(String name, String groupName, int x, int y, int z, int dimension, int colour) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.z = z;
		this.dimension = dimension;
		this.colour = colour;
		this.groupName = groupName;
	}
	
	public String getString() {
		return String.format("%s %s (%d, %d, %d) %d %06x",
				this.name, this.groupName, this.x, this.y, this.z, this.dimension, this.colour & 0xffffff);
	}

	public void colourNext()
	{
		this.colour = MwUtil.getNextColour();
	}

	public void colourPrev()
	{
		this.colour = MwUtil.getPrevColour();
	}
    
    public void draw(MapMode mapMode, MapView mapView, int borderColour) {
		double scale = mapView.getDimensionScaling(this.dimension);
		Point.Double p = mapMode.getClampedScreenXY(mapView, this.x * scale, this.z * scale);
		this.screenPos.setLocation(p.x + mapMode.xTranslation, p.y + mapMode.yTranslation);
		
		// draw a coloured rectangle centered on the calculated (x, y)
		double mSize = mapMode.markerSize;
		double halfMSize = mapMode.markerSize / 2.0;
		if(this.groupName.equals("playerDeaths")){
			Render.setColour(borderColour);
			Render.drawRect(p.x-1,p.y-mSize,1.5,mSize*2);
			Render.drawRect(p.x-mSize,p.y-1,mSize*2,1.5);
			Render.setColour(this.colour);
			Render.drawRect(p.x-1+.4,p.y-mSize+.5,1.5-.6,mSize*2-1);
            Render.drawRect(p.x-mSize+.5,p.y-1+.4,mSize*2-1,1.5-.6);
		}else {
			Render.setColour(borderColour);
			Render.drawRect(p.x - halfMSize, p.y - halfMSize, mSize, mSize);
			Render.setColour(this.colour);
			Render.drawRect(p.x - halfMSize + 0.5, p.y - halfMSize + 0.5, mSize - 1.0, mSize - 1.0);
		}
	}

	// arraylist.contains was producing unexpected results in some situations
	// rather than figure out why i'll just control how two markers are compared
	@Override
	public boolean equals(final Object o) {
		if (this == o) { return true; }
		if (o instanceof Marker) {
			Marker m = (Marker) o;
			return (name == m.name) && (groupName == m.groupName) && (x == m.x) && (y == m.y) && (z == m.z) && (dimension == m.dimension);
		}
		return false;
	}
}
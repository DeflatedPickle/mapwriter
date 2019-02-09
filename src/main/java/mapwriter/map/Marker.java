package mapwriter.map;

import mapwriter.map.mapmode.MapMode;
import mapwriter.util.Render;
import mapwriter.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DimensionType;

import java.awt.*;

public class Marker {
    public final String name;
    public final String groupName;
    public int x, y, z;
    public DimensionType dimension;
    public int colour;

    public Point.Double screenPos = new Point.Double(0, 0);

    public Marker(String name, String groupName, int x, int y, int z, DimensionType dimension, int colour) {

        this.name = Utils.mungeStringForConfig(name);
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimension = dimension;
        this.colour = colour;
        this.groupName = Utils.mungeStringForConfig(groupName);
    }

    public void colourNext() {
        this.colour = Utils.getNextColour();
    }

    public void colourPrev() {
        this.colour = Utils.getPrevColour();
    }

    public void draw(MapMode mapMode, MapView mapView, int borderColour) {
        final double scale = mapView.getDimensionScaling(this.dimension);
        final Point.Double p = mapMode.getClampedScreenXY(mapView, this.x * scale, this.z * scale);
        this.screenPos.setLocation(p.x + mapMode.getXTranslation(), p.y + mapMode.getYTranslation());

        // draw a coloured rectangle centered on the calculated (x, y)
        final double mSize = mapMode.getConfig().markerSize;
        final double halfMSize = mapMode.getConfig().markerSize / 2.0;
        Render.setColour(borderColour);
        Render.drawRect(p.x - halfMSize, p.y - halfMSize, mSize, mSize);
        Render.setColour(this.colour);
        Render.drawRect(p.x - halfMSize + 0.5, p.y - halfMSize + 0.5, mSize - 1.0, mSize - 1.0);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Marker) {
            final Marker m = (Marker) o;
            return this.name.equals(m.name) && this.groupName.equals(m.groupName) && this.x == m.x && this.y == m.y && this.z == m.z && this.dimension == m.dimension;
        }
        return false;
    }

    public float getBlue() {
        return (this.colour & 0xff) / 255f;
    }

    public double getDistanceToMarker(Entity entityIn) {
        final double d0 = this.x - entityIn.posX;
        final double d1 = this.y - entityIn.posY;
        final double d2 = this.z - entityIn.posZ;
        return MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
    }

    public float getGreen() {
        return (this.colour >> 8 & 0xff) / 255f;
    }

    public float getRed() {
        return (this.colour >> 16 & 0xff) / 255f;
    }

    public String getString() {
        return String.format("%s %s (%d, %d, %d) %d %06x", this.name, this.groupName, this.x, this.y, this.z, this.dimension, this.colour & 0xffffff);
    }
}
package com.cabchinoe.minimap.map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.cabchinoe.common.Render;
import com.cabchinoe.minimap.Mw;
import com.cabchinoe.minimap.map.mapmode.FullScreenMapMode;
import com.cabchinoe.minimap.map.mapmode.MapMode;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.awt.Point;

/**
 * Created by n3212 on 2017/9/1.
 */
public class TeammateData {
    private String id;
    public final double x, y, z;
    public final double angle;
    public final String name;
    public final int dim;
    public final int entityID;
    public static transient int color = 0xff00afff;
    public transient Point.Double screenPos = new Point.Double(0, 0);

    public TeammateData(String id,int entityID, String name, double x, double y, double z, double angle,int dim){
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.angle = angle;
        this.dim = dim;
        this.entityID = entityID;
    }


    public JsonObject toJsonObject(){
        Gson gson = new Gson();
        gson.toJsonTree(this);
        return gson.toJsonTree(this).getAsJsonObject();
//        return j;
    }

    public TeammateData (JsonObject jo){
         this(
            jo.get("id").getAsString(),jo.get("entityID").getAsInt(),jo.get("name").getAsString(),
            jo.get("x").getAsDouble(),jo.get("y").getAsDouble(),jo.get("z").getAsDouble(),
            jo.get("angle").getAsDouble(),jo.get("dim").getAsInt()
         );
    }

    public int getColor(){
        return color;
    }

    public String getId(){
        return this.id;
    }
    @SideOnly(Side.CLIENT)
    public void draw(MapMode mapMode, MapView mapView,Mw mw){
        double scale = mapView.getDimensionScaling(this.dim);
        Point.Double p = mapMode.getClampedScreenXY(mapView, this.x * scale, this.z * scale);
        this.screenPos.setLocation(p.x + mapMode.xTranslation, p.y + mapMode.yTranslation);
        double mSize = mapMode.markerSize;
        GL11.glPushMatrix();
        if (mapMode.rotate) {
            GL11.glRotated(mw.mapRotationDegrees, 0.0f, 0.0f, 1.0f);
        }
        if(mapMode instanceof FullScreenMapMode && mw.clientTM.selectedTeammate != null && this.id.equals(mw.clientTM.selectedTeammate)){
            Render.setColour(0xffffffff);
        }else {
            Render.setColour(0xff323232);
        }
        Render.drawArrow(p.x,p.y,this.angle,mSize*2);
        Render.setColour(mw.clientTM.getColor(id));
        Render.drawArrow(p.x,p.y,this.angle,mSize*1.4);
        GL11.glPopMatrix();

        if(mapMode instanceof FullScreenMapMode && this.screenPos.y-7 > 0 ) {
            ScaledResolution scaledResolution = new ScaledResolution(mw.mc);
//            int screenW = mw.mc.displayWidth, screenH = mw.mc.displayHeight;
            int screenW = scaledResolution.getScaledWidth(), screenH = scaledResolution.getScaledHeight();
            if(this.screenPos.x + (float)screenW/100. +1 < screenW && this.screenPos.y +7 < screenH && this.screenPos.x -(float)screenW/100. -2 >0 ) {
//                MwUtil.log("----------------------->>> %s  %s   %s %s",screenPos.x,this.screenPos.y, screenW, screenH);
                String desc = String.format("%s(%d,%d,%d)", this.name, (int)this.x, (int)this.y, (int)this.z);
                int descWidth = mw.mc.fontRenderer.getStringWidth(desc);
                int descX = (int) Math.min(this.screenPos.x + 10, screenW - descWidth - 16) - 1;
                int descY = (int) Math.min(this.screenPos.y, screenH - 14);
                GL11.glPushMatrix();
                Render.drawString(descX - mapMode.xTranslation,descY - mapMode.yTranslation,0xffffffff,desc);
                GL11.glPopMatrix();
            }
        }

    }


}

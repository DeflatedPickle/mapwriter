package com.cabchinoe.minimap.map;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.cabchinoe.minimap.Mw;
import com.cabchinoe.minimap.MwUtil;
import com.cabchinoe.minimap.forge.MwConfig;
import com.cabchinoe.minimap.map.mapmode.MapMode;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by n3212 on 2017/9/1.
 */
public class TeamManager {

    public final static int ArgsRequestTeamData = 1;
    public final static int ArgsRequestInvisible = 2;
    public final static int ArgsRequestVisible = 3;
    public boolean visible = true;
    private HashMap<String,Integer> colorMap;
    public String selectedTeammate = null;

    private HashMap<String,TeammateData> TeamData;
    private ArrayList<String> InvisibleList = new ArrayList<String>();
    public TeamManager(){
        this.TeamData = new HashMap<String, TeammateData>();
        this.colorMap = new HashMap<String, Integer>();
    }

    public synchronized void removeTeammate(String id){
        if(TeamData.containsKey(id)){
            TeamData.remove(id);
        }
    }

    public synchronized void updateTeammate(String id,TeammateData teammateData){
        TeamData.put(id,teammateData);
    }

    public void setColor(String id,int color){
        this.colorMap.put(id,color);
    }

    public int getColor(String id){
        Integer color = this.colorMap.get(id);
        if(color == null){
            try {
                color = this.TeamData.get(id).getColor();
            }catch (Exception e){}
        }
        if(color == null){
            return TeammateData.color;
        }
        return color;
    }

    public synchronized int getSize(){
        return this.TeamData.size();
    }

    public synchronized Collection<TeammateData> getTeamData(){
        return TeamData.values();
    }

    public ArrayList<String> getInvisibleList(){
        return this.InvisibleList;
    }

    public void setInvisibleList(JsonArray ja){
        this.InvisibleList.clear();
        for(int j=0; j<ja.size();j++){
            this.InvisibleList.add(ja.get(j).getAsString());
        }
    }

    public synchronized void setTeamData(JsonArray ja){
        this.TeamData.clear();
        for(int j=0; j<ja.size();j++){
            EntityPlayer player = Mw.instance.mc.thePlayer;
            TeammateData td = new TeammateData(ja.get(j).getAsJsonObject());
            if(td.getId().equals(player.getUniqueID().toString()) || td.entityID == player.getEntityId())
                continue;
            this.TeamData.put(td.getId(),td);
        }
    }

    @SideOnly(Side.CLIENT)
    public synchronized void drawTeammate(MapMode mapMode, MapView mapView,Mw mw) {
        EntityPlayer player = Mw.instance.mc.thePlayer;
        for (TeammateData tmptd: getTeamData()) {
//            MwUtil.log("++++++++++++++ DATA  %s %s",tmptd.name,tmptd.getId());
//            MwUtil.log("++++++++++++++ MY UUID %s %s %s",Mw.instance.mc.thePlayer.getDisplayName(),Mw.instance.mc.thePlayer.getUniqueID().toString(),Mw.instance.mc.thePlayer.getEntityId());
            if (mapView.getDimension() == tmptd.dim && !tmptd.getId().equals(player.getUniqueID().toString()) && tmptd.entityID != player.getEntityId()) {
                tmptd.draw(mapMode, mapView, mw);
            }
        }
    }

    public void toggle_visible(){
        this.visible = !this.visible;
        this.send_visible();
    }

    public void send_visible(){
        MwUtil.send_to_server(this.visible?this.ArgsRequestVisible:this.ArgsRequestInvisible,null);
    }

    public void addInvisibleList(String id){
        if(!this.InvisibleList.contains(id)){
            this.InvisibleList.add(id);
        }
    }

    public void removeInvisibleList(String id){
        if(this.InvisibleList.contains(id)){
            this.InvisibleList.remove(id);
        }
    }

    public boolean isVisible(String id){
        return !this.InvisibleList.contains(id);
    }

    //save colors
    public void save(MwConfig config, String category){
        config.get(category, "color_count", 0).set(this.colorMap.size());
        int i = 0;
        for(String k :this.colorMap.keySet()){
            String key = "color"+i;
            String value = k+":"+this.colorMap.get(k);
            config.get(category, key, "").set(value);
            i++;
        }
    }

    public void load(MwConfig config, String category) {
        if (config.hasCategory(category)) {
            int color_count = config.get(category, "color_count", 0).getInt();
            for (int i = 0; i < color_count; i++) {
                String key = "color" + i;
                String value = config.get(category, key, "").getString();
                String[] valuesp = value.split(":");
                if(valuesp.length == 2){
                    this.colorMap.put(valuesp[0],Integer.parseInt(valuesp[1]));
                }
            }
            //MwUtil.log("++++++++++++++++++++++++  lalalala %s",colorMap.values().toString());
        }
    }

    public JsonObject get_broadcast_data(){
        JsonObject sendData = new JsonObject();
        sendData.addProperty("args", this.ArgsRequestTeamData);
        Collection<TeammateData> TD = this.getTeamData();
        JsonArray teamArray = new JsonArray();
        for(TeammateData td : TD){
            JsonObject jo = td.toJsonObject();
            teamArray.add(jo);
        }
        sendData.add("TeamData",teamArray);
        JsonArray InvisibleListArray = new JsonArray();
        Gson gson = new Gson();
        sendData.add("InvisibleList",gson.toJsonTree(this.getInvisibleList()).getAsJsonArray());
        return sendData;
    }

}


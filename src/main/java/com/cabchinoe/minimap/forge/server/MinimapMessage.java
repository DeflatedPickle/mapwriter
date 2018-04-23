package com.cabchinoe.minimap.forge.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.cabchinoe.minimap.MwUtil;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

import java.io.UnsupportedEncodingException;

public class MinimapMessage implements IMessage {
    // A default constructor is always required
    public MinimapMessage(){}
    private JsonObject JsonObject;
    public MinimapMessage(JsonObject j) {
        this.JsonObject = j;
    }


    @Override
    public void toBytes(ByteBuf buf) {
        // Writes the int into the buf
        try {
            buf.writeBytes(JsonObject.toString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    public String getJsonObjectString(){
        return this.JsonObject.toString();
    }

    public int getArgs(String args){
        return this.JsonObject.get(args).getAsInt();
    }

    public JsonObject getJsonObject(){
        return this.JsonObject;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int byteLength = buf.readableBytes();
        byte[] bytes = new byte[byteLength];
        buf.readBytes(bytes);
        try {
            String s = new String(bytes, "UTF-8");
            JsonParser jp = new JsonParser();
            this.JsonObject = (JsonObject) jp.parse(s);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }catch (Exception e){
            MwUtil.log(e.toString());
        }
    }
}
package com.cabchinoe.minimap.forge.server;


import com.google.gson.JsonObject;
import com.cabchinoe.minimap.MwUtil;
import com.cabchinoe.minimap.forge.MwForge;
import com.cabchinoe.minimap.map.TeamManager;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

// The params of the IMessageHandler are <REQ, REPLY>
// This means that the first param is the packet you are receiving, and the second is the packet you are returning.
// The returned packet can be used as a "response" from a sent packet.
public class MinimapMessageHandler implements IMessageHandler<MinimapMessage, MinimapMessage> {
    // Do note that the default constructor is required, but implicitly defined in this case

    @Override
    public MinimapMessage onMessage(MinimapMessage message, MessageContext ctx) {
        // This is the player the packet was sent to the server from
        MwUtil.log("%s %s",message.getJsonObjectString(),ctx.getServerHandler().playerEntity.getDisplayName());
        JsonObject sendData = new JsonObject();
        sendData.addProperty("args",message.getArgs("args"));
        switch (message.getArgs("args")){
            case TeamManager.ArgsRequestTeamData:
                sendData = MwForge.TM.get_broadcast_data();
                break;
            case TeamManager.ArgsRequestInvisible:
                if(ctx.getServerHandler().playerEntity!=null) {
                    MwForge.instance.TM.addInvisibleList(ctx.getServerHandler().playerEntity.getUniqueID().toString());
                    MwForge.instance.TM.removeTeammate(ctx.getServerHandler().playerEntity.getUniqueID().toString());
                }
                break;
            case TeamManager.ArgsRequestVisible:
                MwForge.instance.TM.removeInvisibleList(ctx.getServerHandler().playerEntity.getUniqueID().toString());
                break;
            case MwUtil.ArgsRequestTP:
                JsonObject data = (JsonObject)message.getJsonObject().get("data");
                ctx.getServerHandler().playerEntity.setPositionAndUpdate(data.get("x").getAsDouble(),data.get("y").getAsDouble(),data.get("z").getAsDouble());
                break;
            default:
                break;

        }


        return new MinimapMessage(sendData);
    }
}
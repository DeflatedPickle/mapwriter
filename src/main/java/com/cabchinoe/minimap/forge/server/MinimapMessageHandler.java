package com.cabchinoe.minimap.forge.server;


import com.cabchinoe.minimap.MwUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.cabchinoe.minimap.forge.MwForge;
import com.cabchinoe.minimap.map.TeamManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

// The params of the IMessageHandler are <REQ, REPLY>
// This means that the first param is the packet you are receiving, and the second is the packet you are returning.
// The returned packet can be used as a "response" from a sent packet.
public class MinimapMessageHandler implements IMessageHandler<MinimapMessage, MinimapMessage> {
    // Do note that the default constructor is required, but implicitly defined in this case

    @Override
    public MinimapMessage onMessage(MinimapMessage message, MessageContext ctx) {
        // This is the player the packet was sent to the server from
        //MwUtil.log("%s %s",message.getJsonObjectString(),ctx.getServerHandler().playerEntity.getDisplayName());
        JsonObject sendData = new JsonObject();
        sendData.addProperty("args",message.getArgs("args"));
        switch (message.getArgs("args")){
            case TeamManager.ArgsRequestTeamData:
                sendData = MwForge.TM.get_broadcast_data();
                break;
            case TeamManager.ArgsRequestInvisible:
                if(ctx.getServerHandler().player!=null) {
                    MwForge.instance.TM.addInvisibleList(ctx.getServerHandler().player.getUniqueID().toString());
                    MwForge.instance.TM.removeTeammate(ctx.getServerHandler().player.getUniqueID().toString());
                }
                break;
            case TeamManager.ArgsRequestVisible:
                MwForge.instance.TM.removeInvisibleList(ctx.getServerHandler().player.getUniqueID().toString());
                break;
            case MwUtil.ArgsRequestTP:
                JsonObject data = (JsonObject)message.getJsonObject().get("data");
                ctx.getServerHandler().player.setPositionAndUpdate(data.get("x").getAsDouble(),data.get("y").getAsDouble(),data.get("z").getAsDouble());
                break;
            default:
                break;

        }


        return new MinimapMessage(sendData);
    }
}
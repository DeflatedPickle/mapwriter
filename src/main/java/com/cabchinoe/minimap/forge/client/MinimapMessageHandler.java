package com.cabchinoe.minimap.forge.client;

import com.cabchinoe.minimap.Mw;
import com.cabchinoe.minimap.forge.server.MinimapMessage;
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
//        MwUtil.log(message.getJsonObjectString());
        switch (message.getArgs("args")){
            case TeamManager.ArgsRequestTeamData:
                Mw.instance.clientTM.setTeamData(message.getJsonObject().get("TeamData").getAsJsonArray());
                Mw.instance.clientTM.setInvisibleList(message.getJsonObject().get("InvisibleList").getAsJsonArray());
                break;
            default:
                break;
        }
        return null;
    }
}
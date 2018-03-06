package mapwriter.forge;

import java.util.ArrayList;

import org.apache.commons.lang3.reflect.FieldUtils;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;

import mapwriter.Mw;
import mapwriter.config.Config;
import mapwriter.overlay.OverlaySlime;
import mapwriter.util.Utils;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreenRealmsProxy;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHandler {
    Mw mw;

    public EventHandler (Mw mw) {

        this.mw = mw;
    }

    @SubscribeEvent
    public void eventChunkLoad (ChunkEvent.Load event) {

        if (event.getWorld().isRemote) {
            this.mw.onChunkLoad(event.getChunk());
        }
    }

    @SubscribeEvent
    public void eventChunkUnload (ChunkEvent.Unload event) {

        if (event.getWorld().isRemote) {
            this.mw.onChunkUnload(event.getChunk());
        }
    }

    @SubscribeEvent
    public void onClientChat (ClientChatReceivedEvent event) {

        if (OverlaySlime.seedFound || !OverlaySlime.seedAsked) {
            return;
        }
        try { // I don't want to crash the game when we derp up in here
            if (event.getMessage() instanceof TextComponentTranslation) {
                final TextComponentTranslation component = (TextComponentTranslation) event.getMessage();
                if (component.getKey().equals("commands.seed.success")) {
                    Long lSeed = (long) 0;
                    if (component.getFormatArgs()[0] instanceof Long) {
                        lSeed = (Long) component.getFormatArgs()[0];
                    }
                    else {
                        lSeed = Long.parseLong((String) component.getFormatArgs()[0]);
                    }
                    OverlaySlime.setSeed(lSeed);
                    event.setCanceled(true); // Don't let the player see this
                    // seed message, They didn't do
                    // /seed, we did
                }
            }
            else if (event.getMessage() instanceof TextComponentString) {
                final TextComponentString component = (TextComponentString) event.getMessage();
                final String msg = component.getUnformattedText();
                if (msg.startsWith("Seed: ")) { // Because bukkit...
                    OverlaySlime.setSeed(Long.parseLong(msg.substring(6)));
                    event.setCanceled(true); // Don't let the player see this
                    // seed message, They didn't do
                    // /seed, we did
                }
            }
        }
        catch (final Exception e) {
            MwForge.logger.error("Something went wrong getting the seed. {}", new Object[] { e.toString() });
        }
    }

    // a bit odd way to reload the blockcolours. if the models are not loaded
    // yet then the uv values and icons will be wrong.
    // this only happens if fml.skipFirstTextureLoad is enabled.
    @SubscribeEvent
    public void onGuiOpenEvent (GuiOpenEvent event) {

        if (event.getGui() instanceof GuiMainMenu && Config.reloadColours) {
            this.mw.reloadBlockColours();
            Config.reloadColours = false;
        }
        else if (event.getGui() instanceof GuiGameOver) {
            this.mw.onPlayerDeath();
        }
        else if (event.getGui() instanceof GuiScreenRealmsProxy) {
            try {
                final RealmsScreen proxy = ((GuiScreenRealmsProxy) event.getGui()).getProxy();
                RealmsMainScreen parrent = null;

                if (proxy instanceof RealmsLongRunningMcoTaskScreen || proxy instanceof RealmsConfigureWorldScreen) {
                    final Object obj = FieldUtils.readField(proxy, "lastScreen", true);
                    if (obj instanceof RealmsMainScreen) {
                        parrent = (RealmsMainScreen) obj;
                    }

                    if (parrent != null) {
                        final long id = (Long) FieldUtils.readField(parrent, "selectedServerId", true);
                        if (id > 0) {
                            final ArrayList list = (ArrayList) FieldUtils.readField(parrent, "realmsServers", true);
                            for (final Object item : list) {
                                final RealmsServer server = (RealmsServer) item;
                                final StringBuilder builder = new StringBuilder();
                                builder.append(server.owner);
                                builder.append("_");
                                builder.append(server.getName());
                                Utils.RealmsWorldName = builder.toString();
                            }
                        }
                    }

                }
            }
            catch (final IllegalAccessException e) {

            }
        }
    }

    @SubscribeEvent
    public void onTextureStitchEventPost (TextureStitchEvent.Post event) {

        if (Config.reloadColours) {
            MwForge.logger.info("Skipping the first generation of blockcolours, models are not loaded yet");
        }
        else {
            this.mw.reloadBlockColours();
        }
    }

    @SubscribeEvent
    public void renderMap (RenderGameOverlayEvent.Post event) {

        if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
            Mw.getInstance().onTick();
        }
    }

    @SubscribeEvent
    public void renderWorldLastEvent (RenderWorldLastEvent event) {

        if (Mw.getInstance().ready) {
            Mw.getInstance().markerManager.drawMarkersWorld(event.getPartialTicks());
        }
    }
}
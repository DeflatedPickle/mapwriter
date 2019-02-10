package mapwriter.util;

import mapwriter.config.Config;
import mapwriter.forge.MapWriterForge;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.DimensionType;
import net.minecraft.world.chunk.Chunk;

import java.io.File;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Utils {
    public static String RealmsWorldName = "";

    private static int[] colors = new int[]{0xff0000, 0x00ff00, 0x0000ff, 0xffff00, 0xff00ff, 0x00ffff, 0xff8000, 0x8000ff};

    // static so that current index is shared between all markers
    public static int colorIndex = 0;

    public static IntBuffer allocateDirectIntBuffer(int size) {
        if (size < 1) {
            final int NewSize = Minecraft.getGLMaximumTextureSize();
            return ByteBuffer.allocateDirect(NewSize * NewSize * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
        }
        return ByteBuffer.allocateDirect(size * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
    }

    public static int distToChunkSq(int x, int z, Chunk chunk) {
        final int dx = (chunk.x << 4) + 8 - x;
        final int dz = (chunk.z << 4) + 8 - z;
        return dx * dx + dz * dz;
    }

    public static int getCurrentColor() {
        return 0xff000000 | Utils.colors[Utils.colorIndex];
    }

    public static String getCurrentDateString() {
        final DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmm");
        return dateFormat.format(new Date());
    }

    public static File getDimensionDir(File worldDir, DimensionType dimension) {
        File dimDir;
        if (dimension != DimensionType.OVERWORLD) {
            dimDir = new File(worldDir, "DIM" + dimension.getId());
        } else {
            dimDir = worldDir;
        }
        return dimDir;
    }

    public static File getFreeFilename(File dir, String baseName, String ext) {
        int i = 0;
        File outputFile;
        if (dir != null) {
            outputFile = new File(dir, baseName + "." + ext);
        } else {
            outputFile = new File(baseName + "." + ext);
        }
        while (outputFile.exists() && i < 1000) {
            if (dir != null) {
                outputFile = new File(dir, baseName + "." + i + "." + ext);
            } else {
                outputFile = new File(baseName + "." + i + "." + ext);
            }
            i++;
        }
        return i < 1000 ? outputFile : null;
    }

    public static int getMaxWidth(String[] arr, String[] arr2) {
        final FontRenderer fontRendererObj = Minecraft.getMinecraft().fontRenderer;
        int Width = 1;
        for (int i = 0; i < arr.length; i++) {
            int w1 = 0;
            int w2 = 0;

            if (i < arr.length) {
                final String s = I18n.format(arr[i]);
                w1 = fontRendererObj.getStringWidth(s);
            }
            if (arr2 != null && i < arr2.length) {
                final String s = I18n.format(arr2[i]);
                w2 = fontRendererObj.getStringWidth(s);
                w2 += 65;
            }
            final int wTot = w1 > w2 ? w1 : w2;
            Width = Width > wTot ? Width : wTot;
        }
        return Width;
    }

    public static int getNextColor() {
        Utils.colorIndex = (Utils.colorIndex + 1) % Utils.getColorsLength();
        return Utils.getCurrentColor();
    }

    public static int getPrevColor() {
        Utils.colorIndex = (Utils.colorIndex + Utils.getColorsLength() - 1) % Utils.getColorsLength();
        return Utils.getCurrentColor();
    }

    public static String getWorldName() {
        String worldName;

        if (Minecraft.getMinecraft().isIntegratedServerRunning()) {
            // cannot use this.mc.theWorld.getWorldInfo().getWorldName() as it
            // is set statically to "MpServer".
            final IntegratedServer server = Minecraft.getMinecraft().getIntegratedServer();
            worldName = server != null ? server.getFolderName() : "sp_world";
        } else if (Minecraft.getMinecraft().isConnectedToRealms()) {
            if (Utils.RealmsWorldName != "") {
                worldName = Utils.RealmsWorldName;
            } else {
                worldName = "Realms";
            }
        } else if (Minecraft.getMinecraft().getCurrentServerData() != null) {
            worldName = Minecraft.getMinecraft().getCurrentServerData().serverIP;
            if (!Config.portNumberInWorldNameEnabled) {
                worldName = worldName.substring(0, worldName.indexOf(":"));
            } else {
                if (!worldName.contains(":")) {// standard port is missing. Adding it
                    worldName += "_25565";
                } else {
                    worldName = worldName.replace(":", "_");
                }
            }
        } else {
            worldName = "default";
        }

        // strip invalid characters from the server name so that it
        // can't be something malicious like '..\..\..\windows\'
        worldName = mungeString(worldName);

        // if something went wrong make sure the name is not blank
        // (causes crash on start up due to empty configuration section)
        if (worldName.isEmpty()) {
            worldName = "default";
        }
        return worldName;
    }

    public static String mungeString(String s) {
        s = s.replace('.', '_');
        s = s.replace('-', '_');
        s = s.replace(' ', '_');
        s = s.replace('/', '_');
        s = s.replace('\\', '_');
        return Reference.PATTERN_INVALID_CHARS.matcher(s).replaceAll("");
    }

    public static String mungeStringForConfig(String s) {
        return Reference.PATTERN_INVALID_CHARS_2.matcher(s).replaceAll("");
    }

    // algorithm from http://graphics.stanford.edu/~seander/bithacks.html (Sean
    // Anderson)
    // works by making sure all bits to the right of the highest set bit are 1,
    // then
    // adding 1 to get the answer.
    public static int nextHighestPowerOf2(int v) {
        // decrement by 1 (to handle cases where v is already a power of two)
        v--;

        // set all bits to the right of the uppermost set bit.
        v |= v >> 1;
        v |= v >> 2;
        v |= v >> 4;
        v |= v >> 8;
        v |= v >> 16;
        // v |= v >> 32; // uncomment for 64 bit input values

        // add 1 to get the power of two result
        return v + 1;
    }

    public static void openWebLink(URI url) {
        try {
            final Class<?> oclass = Class.forName("java.awt.Desktop");
            final Object object = oclass.getMethod("getDesktop", new Class[0]).invoke((Object) null);
            oclass.getMethod("browse", new Class[]{URI.class}).invoke(object, url);
        } catch (final Throwable throwable) {
            MapWriterForge.LOGGER.error("Couldn\'t open link {}", throwable.getStackTrace().toString());
        }
    }

    // send an ingame chat message and console log
    public static void printBoth(String msg) {
        final EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player != null) {
            player.sendMessage(new TextComponentString(msg));
        }
        MapWriterForge.LOGGER.info("{}", msg);
    }

    public static String stringArrayToString(String[] arr) {
        final StringBuilder builder = new StringBuilder();
        for (final String s : arr) {
            builder.append(I18n.format(s));
            builder.append("\n");
        }
        return builder.toString();
    }

    private static int getColorsLength() {
        return Utils.colors.length;
    }
}

package com.cabchinoe.minimap;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.cabchinoe.minimap.forge.MwForge;
import com.cabchinoe.minimap.forge.server.MinimapMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.chunk.Chunk;

public class MwUtil {
	public final static int ArgsRequestTP = 4;
	public final static Pattern patternInvalidChars = Pattern.compile("[^\\p{IsAlphabetic}\\p{Digit}_]");
	
	public static void logInfo(String s, Object...args) {
		MwForge.logger.info(String.format(s, args));
	}
	
	public static void logWarning(String s, Object...args) {
		MwForge.logger.warn(String.format(s, args));
	}
	
	public static void logError(String s, Object...args) {
		MwForge.logger.error(String.format(s, args));
	}
	
	public static void debug(String s, Object...args) {
		MwForge.logger.debug(String.format(s, args));
	}
	
	public static void log(String s, Object...args) {
		logInfo(String.format(s, args));
	}
	
	public static String mungeString(String s) {
		s = s.replace('.', '_');
		s = s.replace('-', '_');
		s = s.replace(' ',  '_');
		s = s.replace('/',  '_');
		s = s.replace('\\',  '_');
		return patternInvalidChars.matcher(s).replaceAll("");
	}
	
	public static File getFreeFilename(File dir, String baseName, String ext) {
		int i = 0;
		File outputFile;
		if (dir != null) {
			outputFile = new File(dir, baseName + "." + ext);
		} else {
			outputFile = new File(baseName + "." + ext);
		}
		while (outputFile.exists() && (i < 1000)) {
			if (dir != null) {
				outputFile = new File(dir, baseName + "." + i + "." + ext);
			} else {
				outputFile = new File(baseName + "." + i + "." + ext);
			}
			i++;
		}
		return (i < 1000) ? outputFile : null;
	}
	
	public static void printBoth(String msg) {
		EntityPlayerSP thePlayer = Minecraft.getMinecraft().player;
		if (thePlayer != null) {
			thePlayer.sendMessage(new TextComponentString(msg));
		}
		MwUtil.log("%s", msg);
	}
	
	public static File getDimensionDir(File worldDir, int dimension) {
		File dimDir;
		if (dimension != 0) {
			dimDir = new File(worldDir, "DIM" + dimension);
		} else {
			dimDir = worldDir;
		}
		return dimDir;
	}
	
	public static IntBuffer allocateDirectIntBuffer(int size) {
		return ByteBuffer.allocateDirect(size * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
	}
	
	// algorithm from http://graphics.stanford.edu/~seander/bithacks.html (Sean Anderson)
	// works by making sure all bits to the right of the highest set bit are 1, then
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
	
	public static String getCurrentDateString() {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmm");
		return dateFormat.format(new Date());
	}
	
	public static int distToChunkSq(int x, int z, Chunk chunk) {
		int dx = (chunk.x << 4) + 8 - x;
		int dz = (chunk.z << 4) + 8 - z;
		return (dx * dx) + (dz * dz);
	}
	private static int[] colours = new int[]
	{
			0xff0000,
			0x00ff00,
			0x0000ff,
			0xffff00,
			0xff00ff,
			0x00ffff,
			0xff8000,
			0x8000ff
	};
	// static so that current index is shared between all markers
	public static int colourIndex = 0;

	private static int getColoursLengt()
	{
		return colours.length;
	}

	public static int getCurrentColour()
	{
		return 0xff000000 | colours[colourIndex];
	}

	public static int getNextColour()
	{
		colourIndex = (MwUtil.colourIndex + 1) % getColoursLengt();
		return getCurrentColour();
	}

	public static int getPrevColour()
	{
		colourIndex = ((colourIndex + getColoursLengt()) - 1) % getColoursLengt();
		return getCurrentColour();
	}

	public static void send_to_server(int args,JsonElement data){
		JsonObject p = new JsonObject();
		p.addProperty("args", args);
		if(data!=null){
			p.add("data",data);
		}
		MwForge.instance.simpleNetworkWrapperInstance.sendToServer(new MinimapMessage(p));
	}

	public static void send_to_All(JsonObject p){
		MwForge.instance.simpleNetworkWrapperInstance.sendToAll(new MinimapMessage(p));
	}
}

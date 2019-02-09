package mapwriter.util;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.registries.IRegistryDelegate;

import java.util.Map;

public class TextureUtils {

    public static Map<String, TextureAtlasSprite> getTextures(TextureMap map) {
        return ReflectionHelper.getPrivateValue(TextureMap.class, map, "field_110574_e", "mapRegisteredSprites");
    }

    public static Map<IRegistryDelegate<Block>, IBlockColor> getBlockColours() {
        return ReflectionHelper.getPrivateValue(BlockColors.class, Minecraft.getMinecraft().getBlockColors(), "blockColorMap");
    }

    public static int getIconMapColour(TextureAtlasSprite icon, Texture terrainTexture) {
        final int iconX = Math.round(terrainTexture.w * Math.min(icon.getMinU(), icon.getMaxU()));
        final int iconY = Math.round(terrainTexture.h * Math.min(icon.getMinV(), icon.getMaxV()));
        final int iconWidth = Math.round(terrainTexture.w * Math.abs(icon.getMaxU() - icon.getMinU()));
        final int iconHeight = Math.round(terrainTexture.h * Math.abs(icon.getMaxV() - icon.getMinV()));

        final int[] pixels = new int[iconWidth * iconHeight];

        terrainTexture.getRGB(iconX, iconY, iconWidth, iconHeight, pixels, 0, iconWidth, icon);
        return Render.getAverageColourOfArray(pixels);
    }
}
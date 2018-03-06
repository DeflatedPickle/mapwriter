package mapwriter;

import java.util.HashMap;
import java.util.Map;

import mapwriter.region.BlockColours;
import mapwriter.util.Logging;
import mapwriter.util.Render;
import mapwriter.util.Texture;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class BlockColourGen {
    
    public static final Map<TextureAtlasSprite, Integer> COLOUR_MAP = new HashMap<>();
    
    public static Map<String, TextureAtlasSprite> getTextures(TextureMap map) {
        
        return ReflectionHelper.getPrivateValue(TextureMap.class, map, "mapRegisteredSprites");
    }
    
    public static void genBlockColours (BlockColours bc) {

        Logging.log("generating block map colours from textures");
        
        final int terrainTextureId = Minecraft.getMinecraft().renderEngine.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).getGlTextureId();
        final Texture terrainTexture = new Texture(terrainTextureId);

        long preTime = System.currentTimeMillis();        
        int textures = 0;
        
        for (final TextureAtlasSprite sprite : getTextures(Minecraft.getMinecraft().getTextureMapBlocks()).values()) {
            
            COLOUR_MAP.put(sprite, getIconMapColour(sprite, terrainTexture));
            textures++;
        }
        
        Logging.log("Processed %d textures. Took %dms", textures, System.currentTimeMillis() - preTime);

        long start = System.currentTimeMillis();
        int blocks = 0;
        int exceptions = 0;
        
        for (final Block block : Block.REGISTRY) {

            for (IBlockState state : block.getBlockState().getValidStates()) {
                
                if (state != null && state.getRenderType() != EnumBlockRenderType.INVISIBLE) {
                    
                    try {
                        
                        final TextureAtlasSprite icon = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
                        
                        if (icon != null) {

                            final int blockColour = COLOUR_MAP.get(icon);                            
                            bc.setColour(block.delegate.name().toString(), String.valueOf(block.getMetaFromState(state)), blockColour);
                            blocks++;
                        }
                    }
                    
                    catch (Exception e) {
                        
                        exceptions++;
                        e.printStackTrace();
                    }
                }
            }
        }

        Logging.log("Processed %d blocks. Had %d exceptions. Took %dms", blocks, exceptions, System.currentTimeMillis() - start);
        System.out.println("Time: " + (System.currentTimeMillis() - start));

        genBiomeColours(bc);
    }

    private static void genBiomeColours (BlockColours bc) {

        for (final Biome biome : Biome.REGISTRY) {
            if (biome != null) {
                final double temp = MathHelper.clamp(biome.getDefaultTemperature(), 0.0F, 1.0F);
                final double rain = MathHelper.clamp(biome.getRainfall(), 0.0F, 1.0F);
                final int grasscolor = ColorizerGrass.getGrassColor(temp, rain);
                final int foliagecolor = ColorizerFoliage.getFoliageColor(temp, rain);
                final int watercolor = biome.getWaterColorMultiplier();

                bc.setBiomeData(biome.getBiomeName(), watercolor & 0xffffff, grasscolor & 0xffffff, foliagecolor & 0xffffff);
            }
        }
    }

    private static int getIconMapColour (TextureAtlasSprite icon, Texture terrainTexture) {

        final int iconX = Math.round(terrainTexture.w * Math.min(icon.getMinU(), icon.getMaxU()));
        final int iconY = Math.round(terrainTexture.h * Math.min(icon.getMinV(), icon.getMaxV()));
        final int iconWidth = Math.round(terrainTexture.w * Math.abs(icon.getMaxU() - icon.getMinU()));
        final int iconHeight = Math.round(terrainTexture.h * Math.abs(icon.getMaxV() - icon.getMinV()));

        final int[] pixels = new int[iconWidth * iconHeight];

        terrainTexture.getRGB(iconX, iconY, iconWidth, iconHeight, pixels, 0, iconWidth, icon);
        return Render.getAverageColourOfArray(pixels);
    }
}

package com.jarhax.map;

import java.util.HashMap;
import java.util.Map;

import mapwriter.forge.MwForge;
import mapwriter.util.Texture;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.registries.IRegistryDelegate;

public class BlockColours {

    private final Map<TextureAtlasSprite, Integer> averageSpriteColours = new HashMap<>();
    private final Map<IBlockState, Integer> stateColours = new HashMap<>();
    private final Map<Biome, BiomeColours> biomeColours = new HashMap<>();
    private Map<IRegistryDelegate<Block>, IBlockColor> blockColorMap;
    
    public int getColorModifier (IBlockState state, World world, BlockPos pos) {

        if (blockColorMap.containsKey(state.getBlock().delegate)) {
            
            return blockColorMap.get(state.getBlock().delegate).colorMultiplier(state, world, pos, 0);
        }
        
        // Default behavior is white, which doesn't change the color.
        return 0xffffff;
    }

    public int getStateColour (IBlockState state) {

        // Invisible blocks should be skipped. (return 0)
        if (state.getRenderType() == EnumBlockRenderType.INVISIBLE) {

            return 0;
        }

        return this.stateColours.containsKey(state) ? this.stateColours.get(state) : 0;
    }

    public void loadColourData () {

        long time = System.currentTimeMillis();
        this.generateColourAverages();
        MwForge.logger.info("Generating colour averages. Took {}ms.", System.currentTimeMillis() - time);

        time = System.currentTimeMillis();
        this.generateBlockStateColours();
        MwForge.logger.info("Generating BlockState colours. Took {}ms.", System.currentTimeMillis() - time);

        time = System.currentTimeMillis();
        this.generateBiomeColours();
        MwForge.logger.info("Generating Biome colours. Took {}ms.", System.currentTimeMillis() - time);
        
        blockColorMap = TextureUtils.getBlockColours();
    }

    private void generateColourAverages () {

        this.averageSpriteColours.clear();
        final int terrainTextureId = Minecraft.getMinecraft().renderEngine.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).getGlTextureId();
        final Texture terrainTexture = new Texture(terrainTextureId);

        for (final TextureAtlasSprite sprite : TextureUtils.getTextures(Minecraft.getMinecraft().getTextureMapBlocks()).values()) {

            this.averageSpriteColours.put(sprite, TextureUtils.getIconMapColour(sprite, terrainTexture));
        }
    }

    private void generateBlockStateColours () {

        this.stateColours.clear();
        for (final Block block : Block.REGISTRY) {

            for (final IBlockState state : block.getBlockState().getValidStates()) {

                if (state != null && state.getRenderType() != EnumBlockRenderType.INVISIBLE) {

                    try {

                        final TextureAtlasSprite icon = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);

                        if (icon != null) {

                            this.stateColours.put(state, this.averageSpriteColours.get(icon));
                        }
                    }

                    catch (final Exception e) {

                        MwForge.logger.trace(e);
                    }
                }
            }
        }
    }

    private void generateBiomeColours () {

        this.biomeColours.clear();
        for (final Biome biome : Biome.REGISTRY) {

            try {

                final double temp = MathHelper.clamp(biome.getDefaultTemperature(), 0.0F, 1.0F);
                final double rain = MathHelper.clamp(biome.getRainfall(), 0.0F, 1.0F);
                final int grassColour = ColorizerGrass.getGrassColor(temp, rain);
                final int foliageColour = ColorizerFoliage.getFoliageColor(temp, rain);
                final int waterColour = biome.getWaterColorMultiplier();
                this.biomeColours.put(biome, new BiomeColours(waterColour, grassColour, foliageColour));
            }

            catch (final Exception e) {

                MwForge.logger.trace(e);
            }
        }
    }
}

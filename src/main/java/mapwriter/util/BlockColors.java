package mapwriter.util;

import mapwriter.forge.MapWriterForge;
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

import java.util.HashMap;
import java.util.Map;

public class BlockColors {
    private final Map<TextureAtlasSprite, Integer> averageSpriteColors = new HashMap<>();
    private final Map<IBlockState, Integer> stateColors = new HashMap<>();
    private final Map<Biome, BiomeColors> biomeColors = new HashMap<>();
    private Map<IRegistryDelegate<Block>, IBlockColor> blockColorMap;

    public int getColorModifier(IBlockState state, World world, BlockPos pos) {
        if (blockColorMap.containsKey(state.getBlock().delegate)) {
            return blockColorMap.get(state.getBlock().delegate).colorMultiplier(state, world, pos, 0);
        }

        // Default behavior is white, which doesn't change the color.
        return 0xffffff;
    }

    public int getStateColor(IBlockState state) {
        // Invisible blocks should be skipped. (return 0)
        if (state.getRenderType() == EnumBlockRenderType.INVISIBLE) {
            return 0;
        }

        return this.stateColors.getOrDefault(state, 0);
    }

    public void loadColorData() {
        long time = System.currentTimeMillis();
        this.generateColorAverages();
        MapWriterForge.LOGGER.info("Generating color averages. Took {}ms.", System.currentTimeMillis() - time);

        time = System.currentTimeMillis();
        this.generateBlockStateColors();
        MapWriterForge.LOGGER.info("Generating BlockState colors. Took {}ms.", System.currentTimeMillis() - time);

        time = System.currentTimeMillis();
        this.generateBiomeColors();
        MapWriterForge.LOGGER.info("Generating Biome colors. Took {}ms.", System.currentTimeMillis() - time);

        blockColorMap = TextureUtils.getBlockColors();
    }

    private void generateColorAverages() {
        this.averageSpriteColors.clear();
        final int terrainTextureId = Minecraft.getMinecraft().renderEngine.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).getGlTextureId();
        final Texture terrainTexture = new Texture(terrainTextureId);

        for (final TextureAtlasSprite sprite : TextureUtils.getTextures(Minecraft.getMinecraft().getTextureMapBlocks()).values()) {
            this.averageSpriteColors.put(sprite, TextureUtils.getIconMapColor(sprite, terrainTexture));
        }
    }

    private void generateBlockStateColors() {
        this.stateColors.clear();
        for (final Block block : Block.REGISTRY) {
            for (final IBlockState state : block.getBlockState().getValidStates()) {
                if (state != null && state.getRenderType() != EnumBlockRenderType.INVISIBLE) {
                    try {
                        TextureAtlasSprite icon = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
                        Integer color = this.averageSpriteColors.get(icon);
                        if (color != null) {
                            this.stateColors.put(state, color);
                        }
                    } catch (final Exception e) {
                        MapWriterForge.LOGGER.trace(e);
                    }
                }
            }
        }
    }

    private void generateBiomeColors() {
        this.biomeColors.clear();
        for (final Biome biome : Biome.REGISTRY) {
            try {
                final double temp = MathHelper.clamp(biome.getDefaultTemperature(), 0.0F, 1.0F);
                final double rain = MathHelper.clamp(biome.getRainfall(), 0.0F, 1.0F);
                final int grassColor = ColorizerGrass.getGrassColor(temp, rain);
                final int foliageColor = ColorizerFoliage.getFoliageColor(temp, rain);
                final int waterColor = biome.getWaterColorMultiplier();
                this.biomeColors.put(biome, new BiomeColors(waterColor, grassColor, foliageColor));
            } catch (final Exception e) {
                MapWriterForge.LOGGER.trace(e);
            }
        }
    }
}

package mapwriter.region;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import mapwriter.forge.MwForge;
import mapwriter.util.Reference;
import mapwriter.util.Render;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;

public class BlockColours {
    public class BiomeData {
        private int waterMultiplier = 0;
        private int grassMultiplier = 0;
        private int foliageMultiplier = 0;
    }

    public class BlockData {
        private int color = 0;
        private BlockType type = BlockType.NORMAL;
    }

    public enum BlockType {
        NORMAL,
        GRASS,
        LEAVES,
        FOLIAGE,
        WATER,
        OPAQUE
    }

    public static final int MAX_META = 16;

    public static final String SECTION_BIOMES = "[biomes]";

    public static final String SECTION_BLOCKS = "[blocks]";

    private final LinkedHashMap<String, BiomeData> biomeMap = new LinkedHashMap<>();

    private final LinkedHashMap<String, BlockData> bcMap = new LinkedHashMap<>();

    public static int getColourFromString (String s) {

        return (int) (Long.parseLong(s, 16) & 0xffffffffL);
    }

    public static void writeOverridesFile (File f) {

        try (Writer fout = new OutputStreamWriter(new FileOutputStream(f))) {
            fout.write(String.format("version: %s%n", Reference.VERSION));

            fout.write("block minecraft:yellow_flower * 60ffff00	# make dandelions more yellow\n" + "block minecraft:red_flower 0 60ff0000		# make poppy more red\n" + "block minecraft:red_flower 1 601c92d6		# make Blue Orchid more red\n" + "block minecraft:red_flower 2 60b865fb		# make Allium more red\n" + "block minecraft:red_flower 3 60e4eaf2		# make Azure Bluet more red\n" + "block minecraft:red_flower 4 60d33a17		# make Red Tulip more red\n" + "block minecraft:red_flower 5 60e17124		# make Orange Tulip more red\n" + "block minecraft:red_flower 6 60ffffff		# make White Tulip more red\n" + "block minecraft:red_flower 7 60eabeea		# make Pink Tulip more red\n" + "block minecraft:red_flower 8 60eae6ad		# make Oxeye Daisy more red\n" + "block minecraft:double_plant 0 60ffff00		# make Sunflower more Yellow-orrange\n" + "block minecraft:double_plant 1 d09f78a4		# make Lilac more pink\n" + "block minecraft:double_plant 4 60ff0000		# make Rose Bush more red\n" + "block minecraft:double_plant 5 d0e3b8f7		# make Peony more red\n" + "blocktype minecraft:grass * grass			# grass block\n" + "blocktype minecraft:flowing_water * water	# flowing water block\n" + "blocktype minecraft:water * water			# still water block\n" + "blocktype minecraft:leaves * leaves    		# leaves block\n" + "blocktype minecraft:leaves2 * leaves    		# leaves block\n" + "blocktype minecraft:leaves 1 opaque    		# pine leaves (not biome colorized)\n" + "blocktype minecraft:leaves 2 opaque    		# birch leaves (not biome colorized)\n" + "blocktype minecraft:tallgrass * grass     	# tall grass block\n" + "blocktype minecraft:vine * foliage  			# vines block\n" + "blocktype biomesoplenty:grass * grass		# BOP grass block\n" + "blocktype biomesoplenty:plant_0 * grass		# BOP plant block\n" + "blocktype biomesoplenty:plant_1 * grass		# BOP plant block\n" + "blocktype biomesoplenty:leaves_0 * leaves	# BOP Leave block\n" + "blocktype biomesoplenty:leaves_1 * leaves	# BOP Leave block\n" + "blocktype biomesoplenty:leaves_2 * leaves	# BOP Leave block\n" + "blocktype biomesoplenty:leaves_3 * leaves	# BOP Leave block\n" + "blocktype biomesoplenty:leaves_4 * leaves	# BOP Leave block\n" + "blocktype biomesoplenty:leaves_5 * leaves	# BOP Leave block\n" + "blocktype biomesoplenty:tree_moss * foliage	# biomes o plenty tree moss\n");
        }
        catch (final IOException e) {
            MwForge.logger.error("saving block overrides: could not write to '{}'", f);

        }
    }

    private static int adjustBlockColourFromType (String blockName, String meta, BlockType type, int blockColour) {

        // for normal blocks multiply the block colour by the render colour.
        // for other blocks the block colour will be multiplied by the biome
        // colour.
        final Block block = Block.getBlockFromName(blockName);

        switch (type) {
            case OPAQUE:
                blockColour |= 0xff000000;
            case NORMAL:
                // fix crash when mods don't implement getRenderColor for all
                // block meta values.
                try {
                    @SuppressWarnings("deprecation")
                    final int renderColour = block.getMapColor(block.getStateFromMeta(Integer.parseInt(meta) & 0xf), null, null).colorValue;
                    if (renderColour != 0xffffff) {
                        blockColour = Render.multiplyColours(blockColour, 0xff000000 | renderColour);
                    }
                }
                catch (final RuntimeException e) {
                    // do nothing
                }
                break;
            case LEAVES:
                // leaves look weird on the map if they are not opaque.
                // they also look too dark if the render colour is applied.
                blockColour |= 0xff000000;
                break;
            case GRASS:
                // the icon returns the dirt texture so hardcode it to the grey
                // undertexture.
                blockColour = 0xff9b9b9b;
            default:
                break;
        }
        return blockColour;
    }

    private static String getBlockTypeAsString (BlockType blockType) {

        String s = "normal";
        switch (blockType) {
            case NORMAL:
                s = "normal";
                break;
            case GRASS:
                s = "grass";
                break;
            case LEAVES:
                s = "leaves";
                break;
            case FOLIAGE:
                s = "foliage";
                break;
            case WATER:
                s = "water";
                break;
            case OPAQUE:
                s = "opaque";
                break;
        }
        return s;
    }

    private static BlockType getBlockTypeFromString (String typeString) {

        BlockType blockType = BlockType.NORMAL;
        if (typeString.equalsIgnoreCase("normal")) {
            blockType = BlockType.NORMAL;
        }
        else if (typeString.equalsIgnoreCase("grass")) {
            blockType = BlockType.GRASS;
        }
        else if (typeString.equalsIgnoreCase("leaves")) {
            blockType = BlockType.LEAVES;
        }
        else if (typeString.equalsIgnoreCase("foliage")) {
            blockType = BlockType.FOLIAGE;
        }
        else if (typeString.equalsIgnoreCase("water")) {
            blockType = BlockType.WATER;
        }
        else if (typeString.equalsIgnoreCase("opaque")) {
            blockType = BlockType.OPAQUE;
        }
        else {
            MwForge.logger.warn("unknown block type '{}'", typeString);
        }
        return blockType;
    }

    private static String getMostOccurringKey (Map<String, Integer> map, String defaultItem) {

        // find the most commonly occurring key in a hash map.
        // only return a key if there is more than 1.
        int maxCount = 1;
        String mostOccurringKey = defaultItem;
        for (final Entry<String, Integer> entry : map.entrySet()) {
            final String key = entry.getKey();
            final int count = entry.getValue();

            if (count > maxCount) {
                maxCount = count;
                mostOccurringKey = key;
            }
        }

        return mostOccurringKey;
    }

    // to use the least number of lines possible find the most commonly
    // occurring
    // item for the different meta values of a block.
    // an 'item' is either a block colour or a block type.
    // the most commonly occurring item is then used as the wildcard entry for
    // the block, and all non matching items added afterwards.
    private static void writeMinimalBlockLines (Writer fout, String lineStart, List<String> items, String defaultItem) throws IOException {

        final Map<String, Integer> frequencyMap = new HashMap<>();

        // first count the number of occurrences of each item.
        for (final String item : items) {
            int count = 0;
            if (frequencyMap.containsKey(item)) {
                count = frequencyMap.get(item);
            }
            frequencyMap.put(item, count + 1);
        }

        // then find the most commonly occurring item.
        final String mostOccurringItem = getMostOccurringKey(frequencyMap, defaultItem);

        // only add a wildcard line if it actually saves lines.
        if (!mostOccurringItem.equals(defaultItem)) {
            fout.write(String.format("%s * %s%n", lineStart, mostOccurringItem));
        }

        // add lines for items that don't match the wildcard line.

        int meta = 0;
        for (final String s : items) {
            if (!s.equals(mostOccurringItem) && !s.equals(defaultItem)) {
                fout.write(String.format("%s %d %s%n", lineStart, meta, s));
            }
            meta++;
        }
    }

    public boolean checkFileVersion (File fn) {

        String lineData = "";

        try (final RandomAccessFile inFile = new RandomAccessFile(fn, "rw")) {

            lineData = inFile.readLine();
        }

        catch (final IOException ex) {
            MwForge.logger.trace(ex);
        }

        return lineData.equals(String.format("version: %s", Reference.VERSION));
    }

    public String combineBlockMeta (String blockName, int meta) {

        return blockName + " " + meta;
    }

    public String combineBlockMeta (String blockName, String meta) {

        return blockName + " " + meta;
    }

    public int getBiomeColour (IBlockState state, int biomeId) {

        String biomeName = "";
        Biome biome = Biome.getBiomeForId(biomeId);

        if (biomeId == 255) {
            biome = Biomes.PLAINS;
        }

        if (biome != null) {
            biomeName = biome.getBiomeName();
        }

        final Block block = state.getBlock();
        final int meta = block.getMetaFromState(state);

        return this.getBiomeColour(block.delegate.name().toString(), meta, biomeName);
    }

    public int getBiomeColour (String blockName, int meta, String biomeName) {

        int colourMultiplier = 0xffffff;

        if (this.bcMap.containsKey(this.combineBlockMeta(blockName, meta))) {
            switch (this.bcMap.get(this.combineBlockMeta(blockName, meta)).type) {
                case GRASS:
                    colourMultiplier = this.getGrassColourMultiplier(biomeName);
                    break;
                case LEAVES:
                case FOLIAGE:
                    colourMultiplier = this.getFoliageColourMultiplier(biomeName);
                    break;
                case WATER:
                    colourMultiplier = this.getWaterColourMultiplier(biomeName);
                    break;
                default:
                    colourMultiplier = 0xffffff;
                    break;
            }
        }
        return colourMultiplier;
    }

    public BlockType getBlockType (int blockAndMeta) {

        final Block block = Block.getBlockById(blockAndMeta >> 4);
        final int meta = blockAndMeta & 0xf;
        return this.getBlockType(block.delegate.name().toString(), meta);
    }

    public BlockType getBlockType (String blockName, int meta) {

        final String blockAndMeta = this.combineBlockMeta(blockName, meta);
        final String BlockAndWildcard = this.combineBlockMeta(blockName, "*");

        BlockData data = new BlockData();

        if (this.bcMap.containsKey(blockAndMeta)) {
            data = this.bcMap.get(blockAndMeta);
        }
        else if (this.bcMap.containsKey(BlockAndWildcard)) {
            data = this.bcMap.get(BlockAndWildcard);
        }
        return data.type;
    }

    public int getColour (IBlockState state) {

        final Block block = state.getBlock();
        final int meta = block.getMetaFromState(state);

        if (block.delegate == null) {
            MwForge.logger.error("Delegate was Null when getting colour, Block in: {}", block.toString());
            return 0;
        }
        else if (block.delegate.name() == null) {
            MwForge.logger.error("Block Name was Null when getting colour, Block in: {}, Delegate: {}", block.toString(), block.delegate.toString());
            return 0;
        }
        return this.getColour(block.delegate.name().toString(), meta);
    }

    public int getColour (String blockName, int meta) {

        final String blockAndMeta = this.combineBlockMeta(blockName, meta);
        final String BlockAndWildcard = this.combineBlockMeta(blockName, "*");

        BlockData data = new BlockData();

        if (this.bcMap.containsKey(blockAndMeta)) {
            data = this.bcMap.get(blockAndMeta);
        }
        else if (this.bcMap.containsKey(BlockAndWildcard)) {
            data = this.bcMap.get(BlockAndWildcard);
        }
        return data.color;
    }

    //
    // Methods for loading block colours from file:
    //

    public void loadFromFile (File f) {

        try (Scanner fin = new Scanner(new FileReader(f))) {

            while (fin.hasNextLine()) {
                // get next line and remove comments (part of line after #)
                final String line = fin.nextLine().split("#")[0].trim();
                if (line.length() > 0) {
                    final String[] lineSplit = line.split(" ");
                    if (lineSplit[0].equals("biome") && lineSplit.length == 5) {
                        this.loadBiomeLine(lineSplit);
                    }
                    else if (lineSplit[0].equals("block") && lineSplit.length == 4) {
                        this.loadBlockLine(lineSplit);
                    }
                    else if (lineSplit[0].equals("blocktype") && lineSplit.length == 4) {
                        this.loadBlockTypeLine(lineSplit);
                    }
                    else {
                        MwForge.logger.warn("invalid map colour line '{}'", line);
                    }
                }
            }
        }
        catch (final IOException e) {
            MwForge.logger.error("loading block colours: no such file '{}'", f);

        }
    }

    // save biome colour multipliers to a file.
    public void saveBiomes (Writer fout) throws IOException {

        fout.write("biome * ffffff ffffff ffffff\n");

        for (final Map.Entry<String, BiomeData> entry : this.biomeMap.entrySet()) {
            final String biomeName = entry.getKey();
            final BiomeData data = entry.getValue();

            // don't add lines that are covered by the default.
            if (data.waterMultiplier != 0xffffff || data.grassMultiplier != 0xffffff || data.foliageMultiplier != 0xffffff) {
                fout.write(String.format("biome %s %06x %06x %06x%n", biomeName, data.waterMultiplier, data.grassMultiplier, data.foliageMultiplier));
            }
        }
    }

    public void saveBlocks (Writer fout) throws IOException {

        fout.write("block * * 00000000\n");

        String lastBlock = "";
        final List<String> colours = new ArrayList<>();

        for (final Map.Entry<String, BlockData> entry : this.bcMap.entrySet()) {
            final String[] blockAndMeta = entry.getKey().split(" ");
            final String block = blockAndMeta[0];

            final String color = String.format("%08x", entry.getValue().color);

            if (!lastBlock.equals(block) && !lastBlock.isEmpty()) {
                final String lineStart = String.format("block %s", lastBlock);
                writeMinimalBlockLines(fout, lineStart, colours, "00000000");

                colours.clear();
            }

            colours.add(color);
            lastBlock = block;
        }
    }

    public void saveBlockTypes (Writer fout) throws IOException {

        fout.write("blocktype * * normal\n");

        String lastBlock = "";
        final List<String> blockTypes = new ArrayList<>();

        for (final Map.Entry<String, BlockData> entry : this.bcMap.entrySet()) {
            final String[] blockAndMeta = entry.getKey().split(" ");
            final String block = blockAndMeta[0];

            final String Type = getBlockTypeAsString(entry.getValue().type);

            if (!lastBlock.equals(block) && !lastBlock.isEmpty()) {
                final String lineStart = String.format("blocktype %s", lastBlock);
                writeMinimalBlockLines(fout, lineStart, blockTypes, getBlockTypeAsString(BlockType.NORMAL));

                blockTypes.clear();
            }

            blockTypes.add(Type);
            lastBlock = block;
        }
    }

    //
    // Methods for saving block colours to file.
    //

    // save block colours and biome colour multipliers to a file.
    public void saveToFile (File f) {

        try (Writer fout = new OutputStreamWriter(new FileOutputStream(f))) {
            fout.write(String.format("version: %s%n", Reference.VERSION));
            this.saveBiomes(fout);
            this.saveBlockTypes(fout);
            this.saveBlocks(fout);

        }
        catch (final IOException e) {
            MwForge.logger.error("saving block colours: could not write to '{}'", f);

        }
    }

    public void setBiomeData (String biomeName, int waterShading, int grassShading, int foliageShading) {

        final BiomeData data = new BiomeData();
        data.foliageMultiplier = foliageShading;
        data.grassMultiplier = grassShading;
        data.waterMultiplier = waterShading;
        this.biomeMap.put(biomeName, data);
    }

    public void setBlockType (String blockName, String meta, BlockType type) {

        final String blockAndMeta = this.combineBlockMeta(blockName, meta);

        if (meta.equals("*")) {
            for (int i = 0; i < 16; i++) {
                this.setBlockType(blockName, String.valueOf(i), type);
            }
            return;
        }

        if (this.bcMap.containsKey(blockAndMeta)) {
            final BlockData data = this.bcMap.get(blockAndMeta);
            data.type = type;
            data.color = adjustBlockColourFromType(blockName, meta, type, data.color);
        }
        else {
            final BlockData data = new BlockData();
            data.type = type;
            this.bcMap.put(blockAndMeta, data);
        }
    }

    public void setColour (String blockName, String meta, int colour) {

        final String blockAndMeta = this.combineBlockMeta(blockName, meta);

        if (meta.equals("*")) {
            for (int i = 0; i < 16; i++) {
                this.setColour(blockName, String.valueOf(i), colour);
            }
        }

        if (this.bcMap.containsKey(blockAndMeta)) {
            final BlockData data = this.bcMap.get(blockAndMeta);
            data.color = colour;
        }
        else {
            final BlockData data = new BlockData();
            data.color = colour;
            this.bcMap.put(blockAndMeta, data);
        }
    }

    private int getFoliageColourMultiplier (String biomeName) {

        final BiomeData data = this.biomeMap.get(biomeName);
        return data != null ? data.foliageMultiplier : 0xffffff;
    }

    private int getGrassColourMultiplier (String biomeName) {

        final BiomeData data = this.biomeMap.get(biomeName);
        return data != null ? data.grassMultiplier : 0xffffff;
    }

    private int getWaterColourMultiplier (String biomeName) {

        final BiomeData data = this.biomeMap.get(biomeName);
        return data != null ? data.waterMultiplier : 0xffffff;
    }

    // read biome colour multiplier values.
    // line format is:
    // biome <biomeId> <waterMultiplier> <grassMultiplier> <foliageMultiplier>
    // accepts "*" wildcard for biome id (meaning for all biomes).
    private void loadBiomeLine (String[] split) {

        try {
            final int waterMultiplier = getColourFromString(split[2]) & 0xffffff;
            final int grassMultiplier = getColourFromString(split[3]) & 0xffffff;
            final int foliageMultiplier = getColourFromString(split[4]) & 0xffffff;
            this.setBiomeData(split[1], waterMultiplier, grassMultiplier, foliageMultiplier);
        }

        catch (final NumberFormatException e) {
            MwForge.logger.warn("invalid biome colour line '{} {} {} {} {}'", split[0], split[1], split[2], split[3], split[4]);
        }
    }

    // read block colour values.
    // line format is:
    // block <blockName> <blockMeta> <colour>
    // the biome id, meta value, and colour code are in hex.
    // accepts "*" wildcard for biome id and meta (meaning for all blocks and/or
    // meta values).
    private void loadBlockLine (String[] split) {

        try {
            // block colour line
            final int colour = getColourFromString(split[3]);
            this.setColour(split[1], split[2], colour);

        }
        catch (final NumberFormatException e) {
            MwForge.logger.error("invalid block colour line '{} {} {} {}'", split[0], split[1], split[2], split[3]);
        }
    }

    private void loadBlockTypeLine (String[] split) {

        try {
            // block type line
            final BlockType type = getBlockTypeFromString(split[3]);
            this.setBlockType(split[1], split[2], type);
        }
        catch (final NumberFormatException e) {
            MwForge.logger.warn("invalid block colour line '{} {} {} {}'", split[0], split[1], split[2], split[3]);
        }
    }
}

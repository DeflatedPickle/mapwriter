package mapwriter.util;

import net.minecraft.util.ResourceLocation;

import java.util.regex.Pattern;

public final class Reference {
    public static final String MOD_ID = "mapwriter";
    public static final String MOD_NAME = "MapWriter";
    public static final String VERSION = "@MOD_VERSION@";
    public static final String MOD_GUIFACTORY_CLASS = "mapwriter.gui.ModGuiFactoryHandler";
    public static final String ACCEPTED_VERSION = "@ACCEPTED_MC_VERSION@";

    public static final String CAT_OPTIONS = "options";
    public static final String CAT_SMALL_MAP_CONFIG = "smallmap";
    public static final String CAT_FULL_MAP_CONFIG = "fullscreenmap";
    public static final String CAT_MAP_POS = "mappos";
    public static final String CAT_WORLD = "world";
    public static final String CAT_MARKERS = "markers";

    public static final Pattern PATTERN_INVALID_CHARS = Pattern.compile("[^\\p{L}\\p{Nd}_]");
    public static final Pattern PATTERN_INVALID_CHARS_2 = Pattern.compile("[^\\p{L}\\p{Nd}_ -]");

    public static final String WORLD_DIR_CONFIG_NAME = "mapwriter.cfg";

    public static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("mapwriter", "textures/map/background.png");
    public static final ResourceLocation COMPASS_TEXTURE = new ResourceLocation("mapwriter", "textures/map/compass.png");
    public static final ResourceLocation ROUND_MAP_TEXTURE = new ResourceLocation("mapwriter", "textures/map/border_round.png");
    public static final ResourceLocation SQUARE_MAP_TEXTURE = new ResourceLocation("mapwriter", "textures/map/border_square.png");
    public static final ResourceLocation PLAYER_ARROW_TEXTURE = new ResourceLocation("mapwriter", "textures/map/arrow_player.png");
    public static final ResourceLocation LEFT_ARROW_TEXTURE = new ResourceLocation("mapwriter", "textures/map/arrow_text_left.png");
    public static final ResourceLocation RIGHT_ARROW_TEXTURE = new ResourceLocation("mapwriter", "textures/map/arrow_text_right.png");
    public static final ResourceLocation DUMMY_MAP_TEXTURE = new ResourceLocation("mapwriter", "textures/map/dummy_map.png");
}

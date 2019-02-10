package mapwriter.config;

import mapwriter.util.Reference;

public class Config {

    public static final String[] BACKGROUND_MODES = {"mw.config.backgroundTextureMode.none", "mw.config.backgroundTextureMode.static", "mw.config.backgroundTextureMode.panning"};

    // configuration options
    public static boolean linearTextureScalingDef = true;
    public static boolean linearTextureScaling = linearTextureScalingDef;
    public static boolean undergroundModeDef = false;
    public static boolean undergroundMode = undergroundModeDef;
    public static boolean teleportEnabledDef = true;
    public static boolean teleportEnabled = teleportEnabledDef;
    public static String teleportCommandDef = "tp";
    public static String teleportCommand = teleportCommandDef;
    public static int defaultTeleportHeightDef = 80;
    public static int defaultTeleportHeight = defaultTeleportHeightDef;
    public static int zoomOutLevelsDef = 5;
    public static int zoomOutLevels = zoomOutLevelsDef;
    public static int zoomInLevelsDef = -5;
    public static int zoomInLevels = zoomInLevelsDef;
    public static boolean useSavedBlockColorsDef = false;
    public static boolean useSavedBlockColors = useSavedBlockColorsDef;
    public static int maxChunkSaveDistSqDef = 128 * 128;
    public static int maxChunkSaveDistSq = maxChunkSaveDistSqDef;
    public static boolean mapPixelSnapEnabledDef = true;
    public static boolean mapPixelSnapEnabled = mapPixelSnapEnabledDef;
    public static int configTextureSizeDef = 2048;
    public static int configTextureSize = configTextureSizeDef;
    public static int maxDeathMarkersDef = 3;
    public static int maxDeathMarkers = maxDeathMarkersDef;
    public static int chunksPerTickDef = 5;
    public static int chunksPerTick = chunksPerTickDef;
    public static boolean portNumberInWorldNameEnabledDef = true;
    public static boolean portNumberInWorldNameEnabled = portNumberInWorldNameEnabledDef;
    public static String saveDirOverrideDef = "";
    public static String saveDirOverride = saveDirOverrideDef;
    public static boolean regionFileOutputEnabledSPDef = true;
    public static boolean regionFileOutputEnabledSP = regionFileOutputEnabledSPDef;
    public static boolean regionFileOutputEnabledMPDef = true;
    public static boolean regionFileOutputEnabledMP = regionFileOutputEnabledMPDef;
    public static String backgroundTextureModeDef = BACKGROUND_MODES[0];
    public static String backgroundTextureMode = backgroundTextureModeDef;
    public static boolean moreRealisticMapDef = false;
    public static boolean moreRealisticMap = moreRealisticMapDef;
    public static boolean newMarkerDialogDef = true;
    public static boolean newMarkerDialog = newMarkerDialogDef;
    public static boolean drawMarkersInWorldDef = false;
    public static boolean drawMarkersInWorld = drawMarkersInWorldDef;
    public static boolean drawMarkersNameInWorldDef = false;
    public static boolean drawMarkersNameInWorld = drawMarkersNameInWorldDef;
    public static boolean drawMarkersDistanceInWorldDef = false;
    public static boolean drawMarkersDistanceInWorld = drawMarkersDistanceInWorldDef;

    // World configuration Options
    public static boolean overlayEnabledDef = true;
    public static boolean overlayEnabled = overlayEnabledDef;
    public static int overlayZoomLevelDef = 0;
    public static int overlayZoomLevel = overlayZoomLevelDef;
    public static int fullScreenZoomLevelDef = 0;
    public static int fullScreenZoomLevel = fullScreenZoomLevelDef;

    public static SmallMapModeConfig smallMap = new SmallMapModeConfig(Reference.CAT_SMALL_MAP_CONFIG);
    public static MapModeConfig fullScreenMap = new MapModeConfig(Reference.CAT_FULL_MAP_CONFIG);

    public static boolean reloadColors = Boolean.parseBoolean(System.getProperty("fml.skipFirstTextureLoad", "true"));
}

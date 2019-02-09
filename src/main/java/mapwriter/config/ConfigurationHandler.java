package mapwriter.config;

import mapwriter.forge.MapWriterForge;
import mapwriter.util.Reference;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;

public class ConfigurationHandler {
    // configuration files (global and world specific)
    public static Configuration configuration;

    public static void init(File configFile) {

        // Create the configuration object from the given configuration file
        if (configuration == null) {
            configuration = new Configuration(configFile);
            setMapModeDefaults();
            loadConfig();
            Config.fullScreenMap.loadConfig();
            Config.smallMap.loadConfig();

            if (configuration.hasChanged()) {
                configuration.save();
            }

            configuration.get(Reference.CAT_OPTIONS, "overlayEnabled", Config.overlayEnabledDef).setShowInGui(false);
            configuration.get(Reference.CAT_OPTIONS, "overlayZoomLevel", Config.zoomInLevelsDef).setShowInGui(false);
        }
    }

    public static void loadConfig() {

        Config.linearTextureScaling = configuration.getBoolean("linearTextureScaling", Reference.CAT_OPTIONS, Config.linearTextureScalingDef, "", "mw.config.linearTextureScaling");
        Config.useSavedBlockColours = configuration.getBoolean("useSavedBlockColours", Reference.CAT_OPTIONS, Config.useSavedBlockColoursDef, "", "mw.config.useSavedBlockColours");
        Config.teleportEnabled = configuration.getBoolean("teleportEnabled", Reference.CAT_OPTIONS, Config.teleportEnabledDef, "", "mw.config.teleportEnabled");
        Config.teleportCommand = configuration.getString("teleportCommand", Reference.CAT_OPTIONS, Config.teleportCommandDef, "", "mw.config.teleportCommand");
        Config.maxChunkSaveDistSq = configuration.getInt("maxChunkSaveDistSq", Reference.CAT_OPTIONS, Config.maxChunkSaveDistSqDef, 1, 256 * 256, "", "mw.config.maxChunkSaveDistSq");
        Config.mapPixelSnapEnabled = configuration.getBoolean("mapPixelSnapEnabled", Reference.CAT_OPTIONS, Config.mapPixelSnapEnabledDef, "", "mw.config.mapPixelSnapEnabled");
        Config.maxDeathMarkers = configuration.getInt("maxDeathMarkers", Reference.CAT_OPTIONS, Config.maxDeathMarkersDef, 0, 1000, "", "mw.config.maxDeathMarkers");
        Config.chunksPerTick = configuration.getInt("chunksPerTick", Reference.CAT_OPTIONS, Config.chunksPerTickDef, 1, 500, "", "mw.config.chunksPerTick");
        Config.saveDirOverride = configuration.getString("saveDirOverride", Reference.CAT_OPTIONS, Config.saveDirOverrideDef, "", "mw.config.saveDirOverride");
        Config.portNumberInWorldNameEnabled = configuration.getBoolean("portNumberInWorldNameEnabled", Reference.CAT_OPTIONS, Config.portNumberInWorldNameEnabledDef, "", "mw.config.portNumberInWorldNameEnabled");
        Config.undergroundMode = configuration.getBoolean("undergroundMode", Reference.CAT_OPTIONS, Config.undergroundModeDef, "", "mw.config.undergroundMode");
        Config.regionFileOutputEnabledSP = configuration.getBoolean("regionFileOutputEnabledSP", Reference.CAT_OPTIONS, Config.regionFileOutputEnabledSPDef, "", "mw.config.regionFileOutputEnabledSP");
        Config.regionFileOutputEnabledMP = configuration.getBoolean("regionFileOutputEnabledMP", Reference.CAT_OPTIONS, Config.regionFileOutputEnabledMPDef, "", "mw.config.regionFileOutputEnabledMP");
        Config.backgroundTextureMode = configuration.getString("backgroundTextureMode", Reference.CAT_OPTIONS, Config.backgroundTextureModeDef, "", Config.BACKGROUND_MODES, "mw.config.backgroundTextureMode");
        Config.zoomOutLevels = configuration.getInt("zoomOutLevels", Reference.CAT_OPTIONS, Config.zoomOutLevelsDef, 1, 256, "", "mw.config.zoomOutLevels");
        Config.zoomInLevels = -configuration.getInt("zoomInLevels", Reference.CAT_OPTIONS, -Config.zoomInLevelsDef, 1, 256, "", "mw.config.zoomInLevels");

        Config.configTextureSize = configuration.getInt("textureSize", Reference.CAT_OPTIONS, Config.configTextureSizeDef, 1024, 4096, "", "mw.config.textureSize");

        Config.overlayEnabled = configuration.getBoolean("overlayEnabled", Reference.CAT_OPTIONS, Config.overlayEnabledDef, "", "mw.config.overlayEnabled");
        Config.overlayZoomLevel = configuration.getInt("overlayZoomLevel", Reference.CAT_OPTIONS, Config.overlayZoomLevelDef, Config.zoomInLevels, Config.zoomOutLevels, "", "mw.config.overlayZoomLevel");

        Config.moreRealisticMap = configuration.getBoolean("moreRealisticMap", Reference.CAT_OPTIONS, Config.moreRealisticMapDef, "", "mw.config.moreRealisticMap");

        Config.newMarkerDialog = configuration.getBoolean("newMarkerDialog", Reference.CAT_OPTIONS, Config.newMarkerDialogDef, "", "mw.config.newMarkerDialog");
        Config.drawMarkersInWorld = configuration.getBoolean("drawMarkersInWorld", Reference.CAT_OPTIONS, Config.drawMarkersInWorldDef, "", "mw.config.drawMarkersInWorld");
        Config.drawMarkersNameInWorld = configuration.getBoolean("drawMarkersNameInWorld", Reference.CAT_OPTIONS, Config.drawMarkersNameInWorldDef, "", "mw.config.drawMarkersNameInWorld");
        Config.drawMarkersDistanceInWorld = configuration.getBoolean("drawMarkersDistanceInWorld", Reference.CAT_OPTIONS, Config.drawMarkersDistanceInWorldDef, "", "mw.config.drawMarkersDistanceInWorld");
    }

    public static void setMapModeDefaults() {
        Config.fullScreenMap.setDefaults();
        Config.smallMap.setDefaults();
    }

    @SubscribeEvent
    public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equalsIgnoreCase(Reference.MOD_ID)) {
            switch (event.getConfigID()) {
                case Reference.CAT_OPTIONS:
                    loadConfig();
                    break;
                case Reference.CAT_FULL_MAP_CONFIG:
                    Config.fullScreenMap.loadConfig();
                    break;
                case Reference.CAT_SMALL_MAP_CONFIG:
                    Config.smallMap.loadConfig();
                    break;
                default:
                    MapWriterForge.LOGGER.error("Unknown config id: {}", event.getConfigID());
                    break;
            }

            if (configuration.hasChanged()) {
                configuration.save();
            }
        }
    }
}
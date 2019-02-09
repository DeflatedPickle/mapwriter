package mapwriter.config;

import mapwriter.gui.ModGuiConfig.ModNumberSliderEntry;
import mapwriter.gui.ModGuiConfigHUD.MapPosConfigEntry;
import mapwriter.util.Reference;
import net.minecraftforge.common.config.Configuration;

public class MapModeConfig implements mapwriter.api.MapModeConfig {
    public static final String[] TEXT_MODE = {
        "mw.config.map.textMode.disabled",
        "mw.config.map.textMode.small",
        "mw.config.map.textMode.large"
    };
    public final String configCategory;
    public final String mapPosCategory;

    public boolean enabledDef = true;
    public boolean enabled = this.enabledDef;
    public boolean rotateDef = false;
    public boolean rotate = this.rotateDef;
    public boolean circularDef = false;
    public boolean circular = this.circularDef;
    public String coordsModeDef = TEXT_MODE[0];
    public String coordsMode = this.coordsModeDef;
    public boolean borderModeDef = false;
    public boolean borderMode = this.borderModeDef;
    public int playerArrowSizeDef = 5;
    public int playerArrowSize = this.playerArrowSizeDef;
    public int markerSizeDef = 5;
    public int markerSize = this.markerSizeDef;
    public int alphaPercentDef = 100;
    public int alphaPercent = this.alphaPercentDef;
    public String biomeModeDef = TEXT_MODE[0];
    public String biomeMode = this.biomeModeDef;
    public double xPosDef = 0;
    public double xPos = this.xPosDef;
    public double yPosDef = 0;
    public double yPos = this.yPosDef;
    public double heightPercentDef = 100;
    public double heightPercent = this.heightPercentDef;
    public double widthPercentDef = 100;
    public double widthPercent = this.widthPercentDef;

    public MapModeConfig(String configCategory) {
        this.configCategory = configCategory;
        this.mapPosCategory = configCategory + Configuration.CATEGORY_SPLITTER + Reference.CAT_MAP_POS;
    }

    @Override
    public int getAlphaPercent() {
        return this.alphaPercent;
    }

    @Override
    public String getBiomeMode() {
        return this.biomeMode;
    }

    @Override
    public boolean isBorderMode() {
        return this.borderMode;
    }

    @Override
    public boolean isCircular() {
        return this.circular;
    }

    @Override
    public String getConfigCategory() {
        return this.configCategory;
    }

    @Override
    public String getCoordsMode() {
        return this.coordsMode;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public double getHeightPercent() {
        return this.heightPercent;
    }

    @Override
    public String getMapPosCategory() {
        return this.mapPosCategory;
    }

    @Override
    public int getMarkerSize() {
        return this.markerSize;
    }

    @Override
    public int getPlayerArrowSize() {
        return this.playerArrowSize;
    }

    @Override
    public boolean isRotate() {
        return this.rotate;
    }

    @Override
    public double getWidthPercent() {
        return this.widthPercent;
    }

    @Override
    public double getXPos() {
        return this.xPos;
    }

    @Override
    public double getYPos() {
        return this.yPos;
    }

    public void loadConfig() {
        // get options from config file
        this.playerArrowSize = ConfigurationHandler.configuration.getInt("playerArrowSize", this.configCategory, this.playerArrowSizeDef, 1, 20, "", "mw.config.map.playerArrowSize");
        this.markerSize = ConfigurationHandler.configuration.getInt("markerSize", this.configCategory, this.markerSizeDef, 1, 20, "", "mw.config.map.markerSize");
        this.alphaPercent = ConfigurationHandler.configuration.getInt("alphaPercent", this.configCategory, this.alphaPercentDef, 0, 100, "", "mw.config.map.alphaPercent");
        this.xPos = ConfigurationHandler.configuration.get(this.mapPosCategory, "xPos", this.xPosDef, " [range: " + 0.0 + " ~ " + 100.0 + ", default: " + this.xPosDef + "]", 0.0, 100.0).setLanguageKey("mw.config.map.xPos").setConfigEntryClass(ModNumberSliderEntry.class).getDouble();
        this.yPos = ConfigurationHandler.configuration.get(this.mapPosCategory, "yPos", this.yPosDef, " [range: " + 0.0 + " ~ " + 100.0 + ", default: " + this.yPosDef + "]", 0.0, 100.0).setLanguageKey("mw.config.map.yPos").setConfigEntryClass(ModNumberSliderEntry.class).getDouble();
        this.heightPercent = ConfigurationHandler.configuration.get(this.mapPosCategory, "heightPercent", this.heightPercentDef, " [range: " + 0.0 + " ~ " + 100.0 + ", default: " + this.heightPercentDef + "]", 0.0, 100.0).setLanguageKey("mw.config.map.heightPercent").setConfigEntryClass(ModNumberSliderEntry.class).getDouble();
        this.widthPercent = ConfigurationHandler.configuration.get(this.mapPosCategory, "widthPercent", this.widthPercentDef, " [range: " + 0.0 + " ~ " + 100.0 + ", default: " + this.widthPercentDef + "]", 0.0, 100.0).setLanguageKey("mw.config.map.widthPercent").setConfigEntryClass(ModNumberSliderEntry.class).getDouble();
    }

    public void setDefaults() {
        ConfigurationHandler.configuration.getCategory(this.mapPosCategory).setLanguageKey("mw.config.map.ctgy.position").setConfigEntryClass(MapPosConfigEntry.class).setShowInGui(false);
    }
}
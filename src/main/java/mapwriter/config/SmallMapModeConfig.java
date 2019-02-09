package mapwriter.config;

import mapwriter.gui.ModGuiConfig.ModBooleanEntry;
import mapwriter.gui.ModGuiConfigHUD.MapPosConfigEntry;

public class SmallMapModeConfig extends MapModeConfig {
    public SmallMapModeConfig(String configCategory) {
        super(configCategory);
    }

    @Override
    public void loadConfig() {
        super.loadConfig();
        this.enabled = ConfigurationHandler.configuration.getBoolean("enabled", this.configCategory, this.enabledDef, "", "mw.config.map.enabled");
        this.rotate = ConfigurationHandler.configuration.getBoolean("rotate", this.configCategory, this.rotateDef, "", "mw.config.map.rotate");
        this.circular = ConfigurationHandler.configuration.getBoolean("circular", this.configCategory, this.circularDef, "", "mw.config.map.circular");
        this.coordsMode = ConfigurationHandler.configuration.getString("coordsMode", this.configCategory, this.coordsModeDef, "", TEXT_MODE, "mw.config.map.coordsMode");
        this.borderMode = ConfigurationHandler.configuration.getBoolean("borderMode", this.configCategory, this.borderModeDef, "", "mw.config.map.borderMode");
        this.biomeMode = ConfigurationHandler.configuration.getString("biomeMode", this.configCategory, this.biomeModeDef, "", TEXT_MODE, "mw.config.map.biomeMode");
        ConfigurationHandler.configuration.getCategory(this.configCategory).remove("Position");
        ConfigurationHandler.configuration.getCategory(this.configCategory).remove("heightPercent");
    }

    @Override
    public void setDefaults() {
        this.rotateDef = true;
        this.circularDef = true;
        this.borderModeDef = true;
        this.coordsModeDef = TEXT_MODE[1];
        this.biomeModeDef = TEXT_MODE[0];
        this.playerArrowSizeDef = 4;
        this.markerSizeDef = 3;
        this.xPosDef = 0;
        this.yPosDef = 0;
        this.heightPercentDef = 30;
        this.widthPercentDef = 30;

        ConfigurationHandler.configuration.get(this.configCategory, "enabled", this.enabled).setRequiresWorldRestart(true);
        ConfigurationHandler.configuration.get(this.configCategory, "rotate", this.rotate).setConfigEntryClass(ModBooleanEntry.class);
        ConfigurationHandler.configuration.getCategory(this.mapPosCategory).setConfigEntryClass(MapPosConfigEntry.class).setLanguageKey("mw.config.map.ctgy.position").setShowInGui(true);
    }
}

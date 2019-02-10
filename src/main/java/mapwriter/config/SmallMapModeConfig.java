package mapwriter.config;

import mapwriter.gui.ModGuiConfig;
import mapwriter.gui.ModGuiConfig.ModBooleanEntry;
import mapwriter.gui.ModGuiConfigHUD.MapPosConfigEntry;
import net.minecraft.item.EnumDyeColor;

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
        this.coordsMode = ConfigurationHandler.configuration.get(this.configCategory, "coordsMode", this.coordsModeDef, "", TEXT_MODES).setLanguageKey("mw.config.map.coordsMode").setConfigEntryClass(ModGuiConfig.ModCycleValueEntry.class).getString();
        this.biomeMode = ConfigurationHandler.configuration.get(this.configCategory, "biomeMode", this.biomeModeDef, "", TEXT_MODES).setLanguageKey("mw.config.map.biomeMode").setConfigEntryClass(ModGuiConfig.ModCycleValueEntry.class).getString();
        this.borderMode = ConfigurationHandler.configuration.getBoolean("borderMode", this.configCategory, this.borderModeDef, "", "mw.config.map.borderMode");
        String[] colors = new String[EnumDyeColor.values().length];
        for (EnumDyeColor color : EnumDyeColor.values()) {
            colors[color.ordinal()] = color.getUnlocalizedName();
        }
        this.borderColor = ConfigurationHandler.configuration.get(this.configCategory, "borderColor", this.borderColorDef, "", colors).setLanguageKey("mw.config.map.borderColor").setConfigEntryClass(ModGuiConfig.ModCycleColorEntry.class).getString();
        ConfigurationHandler.configuration.getCategory(this.configCategory).remove("Position");
        ConfigurationHandler.configuration.getCategory(this.configCategory).remove("heightPercent");
    }

    @Override
    public void setDefaults() {
        this.rotateDef = true;
        this.circularDef = true;
        this.borderModeDef = true;
        this.coordsModeDef = TEXT_MODES[1];
        this.biomeModeDef = TEXT_MODES[0];
        this.playerArrowSizeDef = 6;
        this.markerSizeDef = 4;
        this.xPosDef = 2;
        this.yPosDef = 4;
        this.heightPercentDef = 30;
        this.widthPercentDef = 30;

        ConfigurationHandler.configuration.get(this.configCategory, "enabled", this.enabled).setRequiresWorldRestart(true);
        ConfigurationHandler.configuration.get(this.configCategory, "rotate", this.rotate).setConfigEntryClass(ModBooleanEntry.class);
        ConfigurationHandler.configuration.getCategory(this.mapPosCategory).setConfigEntryClass(MapPosConfigEntry.class).setLanguageKey("mw.config.map.ctgy.position").setShowInGui(true);
    }
}

package mapwriter.gui;

import mapwriter.config.ConfigurationHandler;
import mapwriter.util.Reference;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.*;
import net.minecraftforge.fml.client.config.GuiConfigEntries.*;

import java.util.ArrayList;
import java.util.List;

public class ModGuiConfig extends GuiConfig {
    public static class MapModeConfigEntry extends CategoryEntry {
        public MapModeConfigEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
            super(owningScreen, owningEntryList, configElement);
        }

        @Override
        protected GuiScreen buildChildScreen() {
            final String qualifiedName = this.configElement.getQualifiedName();
            // This GuiConfig object specifies the configID of the object
            // and as
            // such will force-save when it is closed. The parent
            // GuiConfig object's entryList will also be refreshed to
            // reflect
            // the changes.
            return new GuiConfig(this.owningScreen, this.getConfigElement().getChildElements(), this.owningScreen.modID, qualifiedName, this.configElement.requiresWorldRestart() || this.owningScreen.allRequireWorldRestart, this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart, this.owningScreen.title);
        }
    }

    public static class ModBooleanEntry extends ButtonEntry {
        protected final boolean beforeValue;
        protected boolean currentValue;

        public ModBooleanEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
            super(owningScreen, owningEntryList, configElement);
            this.beforeValue = Boolean.valueOf(configElement.get().toString());
            this.currentValue = this.beforeValue;
            this.btnValue.enabled = this.enabled();
            this.updateValueButtonText();
        }

        @Override
        public boolean enabled() {
            for (final IConfigEntry entry : this.owningEntryList.listEntries) {
                if (entry.getName().equals("circular") && entry instanceof BooleanEntry) {
                    return Boolean.valueOf(entry.getCurrentValue().toString());
                }
            }

            return true;
        }

        @Override
        public Boolean getCurrentValue() {
            return this.currentValue;
        }

        @Override
        public Boolean[] getCurrentValues() {
            return new Boolean[]{this.getCurrentValue()};
        }

        @Override
        public boolean isChanged() {
            return this.currentValue != this.beforeValue;
        }

        @Override
        public boolean isDefault() {
            return this.currentValue == Boolean.valueOf(this.configElement.getDefault().toString());
        }

        @Override
        public boolean saveConfigElement() {
            if (this.enabled() && this.isChanged()) {
                this.configElement.set(this.currentValue);
                return this.configElement.requiresMcRestart();
            }
            return false;
        }

        @Override
        public void setToDefault() {
            if (this.enabled()) {
                this.currentValue = Boolean.valueOf(this.configElement.getDefault().toString());
                this.updateValueButtonText();
            }
        }

        @Override
        public void undoChanges() {
            if (this.enabled()) {
                this.currentValue = this.beforeValue;
                this.updateValueButtonText();
            }
        }

        @Override
        public void updateValueButtonText() {
            this.btnValue.displayString = I18n.format(String.valueOf(this.currentValue));
            this.btnValue.packedFGColour = this.currentValue ? GuiUtils.getColorCode('2', true) : GuiUtils.getColorCode('4', true);
        }

        @Override
        public void valueButtonPressed(int slotIndex) {
            if (this.enabled()) {
                this.currentValue = !this.currentValue;
            }
        }
    }

    public static class ModCycleValueEntry extends GuiConfigEntries.ButtonEntry {
        protected final int beforeIndex;
        protected final int defaultIndex;
        protected int currentIndex;

        public ModCycleValueEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
            super(owningScreen, owningEntryList, configElement);
            this.beforeIndex = this.getIndex(configElement.get().toString());
            this.defaultIndex = this.getIndex(configElement.getDefault().toString());
            this.currentIndex = this.beforeIndex;
            this.btnValue.enabled = this.enabled();
            this.updateValueButtonText();
        }

        private int getIndex(String s) {
            for(int i = 0; i < this.configElement.getValidValues().length; ++i) {
                if (this.configElement.getValidValues()[i].equalsIgnoreCase(s)) {
                    return i;
                }
            }

            return 0;
        }

        @Override
        public void updateValueButtonText() {
            this.btnValue.displayString = I18n.format(this.configElement.getLanguageKey() + "." + this.configElement.getValidValues()[this.currentIndex]);
        }

        @Override
        public void valueButtonPressed(int slotIndex) {
            if (this.enabled()) {
                if (++this.currentIndex >= this.configElement.getValidValues().length) {
                    this.currentIndex = 0;
                }

                this.updateValueButtonText();
            }
        }

        @Override
        public boolean isDefault() {
            return this.currentIndex == this.defaultIndex;
        }

        @Override
        public void setToDefault() {
            if (this.enabled()) {
                this.currentIndex = this.defaultIndex;
                this.updateValueButtonText();
            }

        }

        @Override
        public boolean isChanged() {
            return this.currentIndex != this.beforeIndex;
        }

        @Override
        public void undoChanges() {
            if (this.enabled()) {
                this.currentIndex = this.beforeIndex;
                this.updateValueButtonText();
            }
        }

        @Override
        public boolean saveConfigElement() {
            if (this.enabled() && this.isChanged()) {
                this.configElement.set(this.configElement.getValidValues()[this.currentIndex]);
                return this.configElement.requiresMcRestart();
            } else {
                return false;
            }
        }

        @Override
        public String getCurrentValue() {
            return this.configElement.getValidValues()[this.currentIndex];
        }

        @Override
        public String[] getCurrentValues() {
            return new String[]{this.getCurrentValue()};
        }
    }

    public static class ModCycleColorEntry extends ModCycleValueEntry {
        public ModCycleColorEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
            super(owningScreen, owningEntryList, configElement);
        }

        @Override
        public void updateValueButtonText() {
            this.btnValue.displayString = I18n.format("item.fireworksCharge." + this.configElement.getValidValues()[this.currentIndex]);
        }
    }

    public static class ModNumberSliderEntry extends NumberSliderEntry {
        private boolean enabled = true;

        public ModNumberSliderEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
            super(owningScreen, owningEntryList, configElement);
            ((GuiSlider) this.btnValue).precision = 2;
            this.updateValueButtonText();
        }

        @Override
        public boolean enabled() {
            return this.owningScreen.isWorldRunning ? !this.owningScreen.allRequireWorldRestart && !this.configElement.requiresWorldRestart() && this.enabled : this.enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public void setValue(double val) {
            ((GuiSlider) this.btnValue).setValue(val);
            ((GuiSlider) this.btnValue).updateSlider();
        }
    }

    /**
     * Compiles a list of config elements
     */
    private static List<IConfigElement> getConfigElements() {
        // Add categories to config GUI
        final List<IConfigElement> list = new ArrayList<>();
        list.add(new DummyCategoryElement(Reference.CAT_OPTIONS, "mw.configgui.ctgy.general", new ConfigElement(ConfigurationHandler.configuration.getCategory(Reference.CAT_OPTIONS)).getChildElements()));
        list.add(new DummyCategoryElement(Reference.CAT_FULL_MAP_CONFIG, "mw.configgui.ctgy.fullScreenMap", new ConfigElement(ConfigurationHandler.configuration.getCategory(Reference.CAT_FULL_MAP_CONFIG)).getChildElements(), MapModeConfigEntry.class));
        list.add(new DummyCategoryElement(Reference.CAT_SMALL_MAP_CONFIG, "mw.configgui.ctgy.smallMap", new ConfigElement(ConfigurationHandler.configuration.getCategory(Reference.CAT_SMALL_MAP_CONFIG)).getChildElements(), MapModeConfigEntry.class));
        return list;
    }

    public ModGuiConfig(GuiScreen guiScreen) {
        super(guiScreen, getConfigElements(), Reference.MOD_ID, Reference.CAT_OPTIONS, false, false, GuiConfig.getAbridgedConfigPath(ConfigurationHandler.configuration.toString()));
    }
}

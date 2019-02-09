package mapwriter.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

import java.util.Set;

public class ModGuiFactoryHandler implements IModGuiFactory {

    @Override
    public GuiScreen createConfigGui(GuiScreen parentScreen) {

        return new ModGuiConfig(parentScreen);
    }

    @Override
    public boolean hasConfigGui() {

        return true;
    }

    @Override
    public void initialize(Minecraft minecraftInstance) {

    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {

        return null;
    }
}

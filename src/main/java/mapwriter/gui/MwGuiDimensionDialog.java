package mapwriter.gui;

import mapwriter.Mw;
import mapwriter.config.WorldConfig;
import mapwriter.map.MapView;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.DimensionType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MwGuiDimensionDialog extends MwGuiTextDialog {
    final Mw mw;
    final MapView mapView;
    final DimensionType dimension;

    public MwGuiDimensionDialog(GuiScreen parentScreen, Mw mw, MapView mapView, DimensionType dimension) {
        super(parentScreen, I18n.format("mw.gui.mwguidimensiondialog.title") + ":", dimension.getName(), I18n.format("mw.gui.mwguidimensiondialog.error"));
        this.mw = mw;
        this.mapView = mapView;
        this.dimension = dimension;
    }

    @Override
    public boolean submit() {
        boolean done = false;
        String name = this.getInputAsString();
        DimensionType dimension = null;
        for (DimensionType d : DimensionType.values()) {
            if (d.getName().equalsIgnoreCase(name)) {
                dimension = d;
                break;
            }
        }

        if (this.inputValid && dimension != null) {
            this.mapView.setDimensionAndAdjustZoom(dimension);
            this.mw.miniMap.view.setDimension(dimension);
            WorldConfig.getInstance().addDimension(dimension);
            done = true;
        }
        return done;
    }
}

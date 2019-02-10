package mapwriter.tasks;

import mapwriter.util.BlockColors;
import mapwriter.MapWriter;
import mapwriter.region.RegionManager;
import mapwriter.util.Utils;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.DimensionType;

public class RebuildRegionsTask extends Task {

    final RegionManager regionManager;
    final BlockColors blockColors;
    final int x, z, w, h;
    final DimensionType dimension;
    String msg = "";

    public RebuildRegionsTask(MapWriter mw, int x, int z, int w, int h, DimensionType dimension) {
        this.regionManager = mw.regionManager;
        this.blockColors = mw.blockColors;
        this.x = x;
        this.z = z;
        this.w = w;
        this.h = h;
        this.dimension = dimension;
    }

    @Override
    public boolean checkForDuplicate() {
        return false;
    }

    @Override
    public void onComplete() {
        Utils.printBoth(I18n.format("mw.task.rebuildregionstask.chatmsg.rebuild.compleet"));
    }

    @Override
    public void run() {
        this.regionManager.blockColors = this.blockColors;
        this.regionManager.rebuildRegions(this.x, this.z, this.w, this.h, this.dimension);
    }
}

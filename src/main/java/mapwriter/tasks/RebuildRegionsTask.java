package mapwriter.tasks;

import com.jarhax.map.BlockColours;
import mapwriter.Mw;
import mapwriter.region.RegionManager;
import mapwriter.util.Utils;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.DimensionType;

public class RebuildRegionsTask extends Task {

    final RegionManager regionManager;
    final BlockColours blockColours;
    final int x, z, w, h;
    final DimensionType dimension;
    String msg = "";

    public RebuildRegionsTask(Mw mw, int x, int z, int w, int h, DimensionType dimension) {
        this.regionManager = mw.regionManager;
        this.blockColours = mw.blockColours;
        this.x = x;
        this.z = z;
        this.w = w;
        this.h = h;
        this.dimension = dimension;
    }

    @Override
    public boolean CheckForDuplicate() {
        return false;
    }

    @Override
    public void onComplete() {
        Utils.printBoth(I18n.format("mw.task.rebuildregionstask.chatmsg.rebuild.compleet"));
    }

    @Override
    public void run() {
        this.regionManager.blockColours = this.blockColours;
        this.regionManager.rebuildRegions(this.x, this.z, this.w, this.h, this.dimension);
    }
}

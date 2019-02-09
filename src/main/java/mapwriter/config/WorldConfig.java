package mapwriter.config;

import mapwriter.MapWriter;
import mapwriter.util.Reference;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WorldConfig {
    private static WorldConfig instance = null;

    public static WorldConfig getInstance() {
        if (instance == null) {
            synchronized (WorldConfig.class) {
                if (instance == null) {
                    instance = new WorldConfig();
                }
            }
        }

        return instance;
    }

    public Configuration worldConfiguration;

    // list of available dimensions
    public List<DimensionType> dimensions = new ArrayList<>();

    private WorldConfig() {
        // load world specific config file
        final File worldConfigFile = new File(MapWriter.getInstance().worldDir, Reference.WORLD_DIR_CONFIG_NAME);
        this.worldConfiguration = new Configuration(worldConfigFile);

        this.initDimensions();
    }

    public void addDimension(DimensionType dimension) {
        final int i = this.dimensions.indexOf(dimension);
        if (i < 0) {
            this.dimensions.add(dimension);
        }
    }

    public void cleanDimensions() {
        final List<DimensionType> dimensions = new ArrayList<>(this.dimensions);
        this.dimensions.clear();
        for (final DimensionType dimension : dimensions) {
            this.addDimension(dimension);
        }
    }

    // Dimension List
    public void initDimensions() {
        this.dimensions.clear();
        String[] dimensions = new String[this.dimensions.size()];
        for (int i = 0; i < this.dimensions.size(); i++) {
            dimensions[i] = this.dimensions.get(i).getName();
        }
        this.worldConfiguration.get(Reference.CAT_WORLD, "dimensions", dimensions);
        this.addDimension(DimensionType.OVERWORLD);
        this.cleanDimensions();
    }

    public void saveWorldConfig() {
        this.worldConfiguration.save();
    }
}

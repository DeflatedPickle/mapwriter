package mapwriter.region;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

public class RegionFileCache {

    // simple Least Recently Used (LRU) cache implementation
    class LruCache extends LinkedHashMap<String, RegionFile> {
        private static final long serialVersionUID = 1L;
        static final int MAX_REGION_FILES_OPEN = 8;

        public LruCache () {

            // initial capacity, loading factor, true for access time ordering
            super(MAX_REGION_FILES_OPEN * 2, 0.5f, true);
        }

        // called on every put and putAll call, the entry 'entry' is removed
        // if this function returns true.
        @Override
        protected boolean removeEldestEntry (Map.Entry<String, RegionFile> entry) {

            boolean ret = false;
            if (this.size() > MAX_REGION_FILES_OPEN) {
                final RegionFile regionFile = entry.getValue();
                regionFile.close();
                ret = true;
            }
            return ret;
        }
    }

    private final LruCache regionFileCache = new LruCache();
    private final File worldDir;

    public RegionFileCache (File worldDir) {

        this.worldDir = worldDir;
    }

    public void close () {

        for (final RegionFile regionFile : this.regionFileCache.values()) {
            regionFile.close();
        }
        this.regionFileCache.clear();
    }

    public RegionFile getRegionFile (int x, int z, int dimension) {

        final File regionFilePath = this.getRegionFilePath(x, z, dimension);
        final String key = regionFilePath.toString();
        RegionFile regionFile = this.regionFileCache.get(key);
        if (regionFile == null) {
            regionFile = new RegionFile(regionFilePath);
            this.regionFileCache.put(key, regionFile);
        }
        return regionFile;
    }

    public File getRegionFilePath (int x, int z, int dimension) {

        File dir = this.worldDir;
        if (dimension != 0) {
            dir = new File(dir, "DIM" + dimension);
        }
        dir = new File(dir, "region");

        final String filename = String.format("r.%d.%d.mca", x >> Region.SHIFT, z >> Region.SHIFT);

        return new File(dir, filename);
    }

    public boolean regionFileExists (int x, int z, int dimension) {

        final File regionFilePath = this.getRegionFilePath(x, z, dimension);
        return regionFilePath.isFile();
    }
}

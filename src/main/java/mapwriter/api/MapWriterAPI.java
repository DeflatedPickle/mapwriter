package mapwriter.api;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import mapwriter.MapWriter;
import mapwriter.map.MarkerManager;
import net.minecraft.world.DimensionType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class MapWriterAPI {
    private static BiMap<String, MapOverlayProvider> dataProviders = HashBiMap.create();
    private static MapOverlayProvider currentProvider = null;
    private static List<String> providerKeys = new ArrayList<>();

    public static MapOverlayProvider getCurrentDataProvider() {
        return currentProvider;
    }

    public static String getCurrentProviderName() {
        if (currentProvider != null) {
            return getProviderName(currentProvider);
        } else {
            return "none";
        }
    }

    // Returns the data provider based on its name //
    public static MapOverlayProvider getDataProvider(String name) {
        return dataProviders.get(name);
    }

    public static Collection<MapOverlayProvider> getDataProviders() {
        return dataProviders.values();
    }

    // Returns the name based on the data provider //
    public static String getProviderName(MapOverlayProvider provider) {
        return dataProviders.inverse().get(provider);
    }

    public static void registerDataProvider(String name, MapOverlayProvider handler) {
        dataProviders.put(name, handler);
        providerKeys.add(name);
    }

    public static MapOverlayProvider setCurrentDataProvider(MapOverlayProvider provider) {
        currentProvider = provider;
        return currentProvider;
    }

    public static MapOverlayProvider setCurrentDataProvider(String name) {
        currentProvider = dataProviders.get(name);
        return currentProvider;
    }

    public static MapOverlayProvider setNextProvider() {
        if (currentProvider != null) {
            final int index = providerKeys.indexOf(getCurrentProviderName());
            if (index + 1 >= providerKeys.size()) {
                currentProvider = null;
            } else {
                final String nextKey = providerKeys.get(index + 1);
                currentProvider = getDataProvider(nextKey);
            }
        } else if (!providerKeys.isEmpty()) {
            currentProvider = getDataProvider(providerKeys.get(0));
        }
        return currentProvider;
    }

    public static MapOverlayProvider setPrevProvider() {
        if (currentProvider != null) {
            final int index = providerKeys.indexOf(getCurrentProviderName());
            if (index - 1 < 0) {
                currentProvider = null;
            } else {
                final String prevKey = providerKeys.get(index - 1);
                currentProvider = getDataProvider(prevKey);
            }
        } else if (!providerKeys.isEmpty()) {
            currentProvider = getDataProvider(providerKeys.get(providerKeys.size() - 1));
        }
        return currentProvider;
    }

    public static UUID addMarker(String name, String group, int x, int y, int z, DimensionType dimension, int color, boolean save) {
        MapWriter mw = MapWriter.getInstance();
        MarkerManager markerManager = mw.markerManager;
        return markerManager != null ? markerManager.addMarker(name, group, x, y, z, dimension, color, save) : null;
    }

    public static boolean deleteMarker(UUID id, boolean save) {
        MapWriter mw = MapWriter.getInstance();
        MarkerManager markerManager = mw.markerManager;
        return markerManager != null && markerManager.delMarker(m -> m.id.equals(id), Integer.MAX_VALUE, save) > 0;
    }

    public static int deleteMarker(String group, int max, boolean save) {
        MapWriter mw = MapWriter.getInstance();
        MarkerManager markerManager = mw.markerManager;
        return markerManager != null ? markerManager.delMarker(m -> m.group.equals(group), max, save) : 0;
    }
}

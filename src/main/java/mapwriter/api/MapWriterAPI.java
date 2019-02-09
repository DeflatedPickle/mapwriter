package mapwriter.api;

import com.google.common.collect.HashBiMap;

import java.util.ArrayList;
import java.util.Collection;

public class MapWriterAPI {
    private static HashBiMap<String, MapOverlayProvider> dataProviders = HashBiMap.create();
    private static MapOverlayProvider currentProvider = null;
    private static ArrayList<String> providerKeys = new ArrayList<>();

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
}

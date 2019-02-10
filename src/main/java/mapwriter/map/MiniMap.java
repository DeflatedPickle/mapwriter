package mapwriter.map;

import mapwriter.MapWriter;
import mapwriter.config.Config;

public class MiniMap {
    public MapMode smallMapMode;
    public MapView view;
    public MapRenderer smallMap;
    private boolean enabled = Config.overlayEnabled;

    public MiniMap(MapWriter mw) {
        // map view shared between large and small map modes
        this.view = new MapView(mw, false);
        this.view.setZoomLevel(Config.overlayZoomLevel);

        // small map mode
        this.smallMapMode = new MapMode(Config.smallMap);
        this.smallMap = new MapRenderer(mw, this.smallMapMode, this.view);
    }

    public void close() {}

    // draw the map overlay, player arrow, and markers
    public void draw() {
        if (this.enabled) {
            this.smallMap.draw();
        }
    }

    public void toggleMap() {
        this.enabled = ! this.enabled;
        Config.overlayEnabled = this.enabled;
    }
}
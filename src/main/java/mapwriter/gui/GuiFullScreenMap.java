package mapwriter.gui;

import mapwriter.MapWriter;
import mapwriter.api.MapOverlayProvider;
import mapwriter.api.MapWriterAPI;
import mapwriter.config.Config;
import mapwriter.config.WorldConfig;
import mapwriter.forge.MapWriterForge;
import mapwriter.forge.MapWriterKeyHandler;
import mapwriter.map.MapRenderer;
import mapwriter.map.MapView;
import mapwriter.map.Marker;
import mapwriter.map.MapMode;
import mapwriter.tasks.TaskMerge;
import mapwriter.tasks.TaskRebuildRegions;
import mapwriter.util.Reference;
import mapwriter.util.Utils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DimensionType;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;

@SideOnly(Side.CLIENT)
public class GuiFullScreenMap extends GuiScreen {
    private final static double PAN_FACTOR = 0.3;
    private static final int menuY = 5;
    private static final int menuX = 5;

    private final MapWriter mw;
    public MapMode mapMode;

    private final MapView mapView;

    private final MapRenderer map;
    private final String[] helpText1 = new String[]{"mw.gui.mwgui.keys", "", "  Space", "  Delete", "  C", "  Home", "  End", "  N", "  T", "  P", "  R", "  U", "  L", "", "mw.gui.mwgui.helptext.1", "mw.gui.mwgui.helptext.2", "mw.gui.mwgui.helptext.3", "mw.gui.mwgui.helptext.4", "mw.gui.mwgui.helptext.5", "mw.gui.mwgui.helptext.6", "", "mw.gui.mwgui.helptext.7", "mw.gui.mwgui.helptext.8", "mw.gui.mwgui.helptext.9"};
    private final String[] helpText2 = new String[]{"", "", "mw.gui.mwgui.helptext.nextmarkergroup", "mw.gui.mwgui.helptext.deletemarker", "mw.gui.mwgui.helptext.cyclecolor", "mw.gui.mwgui.helptext.centermap", "mw.gui.mwgui.helptext.centermapplayer", "mw.gui.mwgui.helptext.selectnextmarker", "mw.gui.mwgui.helptext.teleport", "mw.gui.mwgui.helptext.savepng", "mw.gui.mwgui.helptext.regenerate", "mw.gui.mwgui.helptext.undergroundmap", "mw.gui.mwgui.helptext.markerlist"};
    private int mouseLeftHeld = 0;
    private int mouseLeftDragStartX = 0;
    private int mouseLeftDragStartY = 0;
    private double viewXStart;
    private double viewZStart;
    private Marker movingMarker = null;
    private int movingMarkerXStart = 0;
    private int movingMarkerZStart = 0;
    private int mouseBlockX = 0;
    private int mouseBlockY = 0;

    private int mouseBlockZ = 0;
    private GuiLabel helpLabel;
    private GuiLabel optionsLabel;
    private GuiLabel dimensionLabel;
    private GuiLabel groupLabel;
    private GuiLabel overlayLabel;

    private final GuiMarkersOverlay markerOverlay;
    private GuiLabel helpTooltipLabel;
    private GuiLabel statusLabel;

    private URI clickedLinkURI;

    public GuiFullScreenMap(MapWriter mw) {
        this.mw = mw;
        this.mapMode = new MapMode(Config.fullScreenMap);
        this.mapView = new MapView(this.mw, true);
        this.map = new MapRenderer(this.mw, this.mapMode, this.mapView);

        this.mapView.setDimension(this.mw.miniMap.view.getDimension());
        this.mapView.setViewCentreScaled(this.mw.playerX, this.mw.playerZ, this.mw.playerDimension);
        this.mapView.setZoomLevel(Config.fullScreenZoomLevel);

        this.initLabels();

        this.markerOverlay = new GuiMarkersOverlay(this, this.mw.markerManager);
    }

    public GuiFullScreenMap(MapWriter mw, DimensionType dim, int x, int z) {
        this(mw);
        this.mapView.setDimension(dim);
        this.mapView.setViewCentreScaled(x, z, dim);
        this.mapView.setZoomLevel(Config.fullScreenZoomLevel);
    }

    public void centerOnSelectedMarker() {
        if (this.mw.markerManager.selectedMarker != null) {
            this.mapView.setViewCentreScaled(this.mw.markerManager.selectedMarker.x, this.mw.markerManager.selectedMarker.z, DimensionType.OVERWORLD);
        }
    }

    @Override
    public void confirmClicked(boolean result, int id) {
        if (id == 31102009) {
            if (result) {
                Utils.openWebLink(this.clickedLinkURI);
            }
            this.clickedLinkURI = null;
            this.mc.displayGuiScreen(this);
        }
    }

    public void deleteSelectedMarker() {
        if (this.mw.markerManager.selectedMarker != null) {
            this.mw.markerManager.delMarker(this.mw.markerManager.selectedMarker, true);
            this.mw.markerManager.update();
            this.mw.markerManager.selectedMarker = null;
        }
    }

    // also called every frame
    @Override
    public void drawScreen(int mouseX, int mouseY, float f) {
        // check every tick for a change in underground mode.
        // this makes it posible to change to underground mode in the config
        // screen.
        this.mapView.setUndergroundMode(Config.undergroundMode);

        this.drawDefaultBackground();
        double xOffset;
        double yOffset;

        if (this.mouseLeftHeld > 2) {
            xOffset = (this.mouseLeftDragStartX - mouseX) * this.mapView.getWidth() / this.mapMode.getW();
            yOffset = (this.mouseLeftDragStartY - mouseY) * this.mapView.getHeight() / this.mapMode.getH();

            if (this.movingMarker != null) {
                final double scale = this.mapView.getDimensionScaling(this.movingMarker.dimension);
                this.movingMarker.x = this.movingMarkerXStart - (int) (xOffset / scale);
                this.movingMarker.z = this.movingMarkerZStart - (int) (yOffset / scale);
            } else {
                this.mapView.setViewCentre(this.viewXStart + xOffset, this.viewZStart + yOffset);
            }
        }

        if (this.mouseLeftHeld > 0) {
            this.mouseLeftHeld++;
        }

        // draw the map
        this.map.draw();

        // get the block the mouse is currently hovering over
        final Point p = this.mapMode.screenXYtoBlockXZ(this.mapView, mouseX, mouseY);
        this.mouseBlockX = p.x;
        this.mouseBlockZ = p.y;
        this.mouseBlockY = this.getHeightAtBlockPos(this.mouseBlockX, this.mouseBlockZ);

        // draw the label near mousepointer
        this.drawMarkerLabel(mouseX, mouseY);

        // draw status message
        this.drawStatus(this.mouseBlockX, this.mouseBlockY, this.mouseBlockZ);

        // draw labels
        this.drawLabel(mouseX, mouseY);

        this.markerOverlay.drawScreen(mouseX, mouseY, f);

        super.drawScreen(mouseX, mouseY, f);
    }

    public void drawStatus(int bX, int bY, int bZ) {
        ITextComponent status;
        if (bY != 0) {
            status = new TextComponentTranslation("mw.gui.mwgui.status.cursor", bX, bY, bZ);
        } else {
            status = new TextComponentTranslation("mw.gui.mwgui.status.cursorNoY", bX, bZ);
        }

        if (this.mc.world != null && !this.mc.world.getChunkFromBlockCoords(new BlockPos(bX, 0, bZ)).isEmpty()) {
            status.appendText(", ");
            status.appendSibling(new TextComponentTranslation("mw.gui.mwgui.status.biome", this.mc.world.getBiomeForCoordsBody(new BlockPos(bX, 0, bZ)).getBiomeName()));
        }

        final MapOverlayProvider provider = MapWriterAPI.getCurrentDataProvider();
        if (provider != null) {
            ITextComponent info = provider.getStatusInfo(this.mapView.getDimension(), bX, bY, bZ);
            if (info != null) {
                status.appendSibling(info);
            }
        }
        final String s = status.getFormattedText().trim();
        final int x = this.width / 2 - 10 - this.fontRenderer.getStringWidth(s) / 2;

        this.statusLabel.setCoords(x, this.height - 21);
        this.statusLabel.setText(new String[]{s}, null);
        this.statusLabel.draw();
    }

    // closes this gui
    public void exitGui() {
        this.mc.displayGuiScreen((GuiScreen) null);
    }

    public int getHeightAtBlockPos(int bX, int bZ) {
        int bY = 0;
        final DimensionType worldDimension = this.mw.mc.world.provider.getDimensionType();
        if (worldDimension == this.mapView.getDimension() && worldDimension != DimensionType.NETHER) {
            bY = this.mw.mc.world.getHeight(new BlockPos(bX, 0, bZ)).getY();
        }
        return bY;
    }

    // get a marker near the specified block pos if it exists.
    // the maxDistance is based on the view width so that you need to click
    // closer
    // to a marker when zoomed in to select it.
    public Marker getMarkerNearScreenPos(int x, int y) {
        Marker nearMarker = null;
        for (final Marker marker : this.mw.markerManager.visibleMarkers) {
            if (marker.screenPos != null && marker.screenPos.distanceSq(x, y) < 6.0) {
                nearMarker = marker;
            }
        }
        return nearMarker;
    }

    @Override
    public void handleMouseInput() throws IOException {
        if (this.markerOverlay.isMouseInField() && this.mouseLeftHeld == 0) {
            this.markerOverlay.handleMouseInput();
        } else if (MapWriterAPI.getCurrentDataProvider() != null && MapWriterAPI.getCurrentDataProvider().onMouseInput(this.mapView, this.mapMode)) {
            return;
        } else {
            final int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
            final int y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
            final int direction = Mouse.getEventDWheel();
            if (direction != 0) {
                this.mouseDWheelScrolled(x, y, direction);
            }
        }
        super.handleMouseInput();
    }

    @Override
    public void initGui() {
        this.helpLabel.setParentWidthAndHeight(this.width, this.height);
        this.optionsLabel.setParentWidthAndHeight(this.width, this.height);
        this.dimensionLabel.setParentWidthAndHeight(this.width, this.height);
        this.groupLabel.setParentWidthAndHeight(this.width, this.height);
        this.overlayLabel.setParentWidthAndHeight(this.width, this.height);

        this.helpTooltipLabel.setParentWidthAndHeight(this.width, this.height);
        this.statusLabel.setParentWidthAndHeight(this.width, this.height);

        this.markerOverlay.setDimensions(GuiMarkersOverlay.listWidth, this.height - 20, GuiMarkersOverlay.ListY, 10 + this.height - 20, this.width - 110);
    }

    public void initLabels() {
        this.helpLabel = new GuiLabel(new String[]{"[" + I18n.format("mw.gui.mwgui.help") + "]"}, null, GuiFullScreenMap.menuX, GuiFullScreenMap.menuY, true, false, this.width, this.height);
        this.optionsLabel = new GuiLabel(new String[]{"[" + I18n.format("mw.gui.mwgui.options") + "]"}, null, 0, 0, true, false, this.width, this.height);
        this.dimensionLabel = new GuiLabel(null, null, 0, 0, true, false, this.width, this.height);
        this.groupLabel = new GuiLabel(null, null, 0, 0, true, false, this.width, this.height);
        this.overlayLabel = new GuiLabel(null, null, 0, 0, true, false, this.width, this.height);
        this.helpTooltipLabel = new GuiLabel(this.helpText1, this.helpText2, 0, 0, true, false, this.width, this.height);

        this.statusLabel = new GuiLabel(null, null, 0, 0, true, false, this.width, this.height);

        this.optionsLabel.drawToRightOf(this.helpLabel);
        this.dimensionLabel.drawToRightOf(this.optionsLabel);
        this.groupLabel.drawToRightOf(this.dimensionLabel);
        this.overlayLabel.drawToRightOf(this.groupLabel);

        this.helpTooltipLabel.drawToBelowOf(this.helpLabel);
    }

    public boolean isPlayerNearScreenPos(int x, int y) {
        final Point.Double p = this.map.getPlayerArrowScreenPos();
        return p.distanceSq(x, y) < 9.0;
    }

    public void mergeMapViewToImage() {
        this.mw.chunkManager.saveChunks();
        this.mw.executor.addTask(new TaskMerge(this.mw.regionManager, (int) this.mapView.getX(), (int) this.mapView.getZ(), (int) this.mapView.getWidth(), (int) this.mapView.getHeight(), this.mapView.getDimension(), this.mw.worldDir, this.mw.worldDir.getName()));
        Utils.printBoth(I18n.format("mw.gui.mwgui.chatmsg.merge", this.mw.worldDir.getAbsolutePath()));
    }

    // zoom on mouse direction wheel scroll
    public void mouseDWheelScrolled(int x, int y, int direction) {
        final Marker marker = this.getMarkerNearScreenPos(x, y);
        if (marker != null && marker == this.mw.markerManager.selectedMarker) {
            if (direction > 0) {
                marker.colorNext();
            } else {
                marker.colorPrev();
            }
        } else if (this.dimensionLabel.posWithin(x, y)) {
            final int n = direction > 0 ? 1 : -1;
            this.mapView.nextDimension(WorldConfig.getInstance().dimensions, n);
        } else if (this.groupLabel.posWithin(x, y)) {
            final int n = direction > 0 ? 1 : -1;
            this.mw.markerManager.nextGroup(n);
            this.mw.markerManager.update();
        } else if (this.overlayLabel.posWithin(x, y)) {
            final int n = direction > 0 ? 1 : -1;
            if (MapWriterAPI.getCurrentDataProvider() != null) {
                MapWriterAPI.getCurrentDataProvider().onOverlayDeactivated(this.mapView);
            }
            if (n == 1) {
                MapWriterAPI.setNextProvider();
            } else {
                MapWriterAPI.setPrevProvider();
            }
            if (MapWriterAPI.getCurrentDataProvider() != null) {
                MapWriterAPI.getCurrentDataProvider().onOverlayActivated(this.mapView);
            }
        } else {
            final int zF = direction > 0 ? -1 : 1;
            this.mapView.zoomToPoint(this.mapView.getZoomLevel() + zF, this.mouseBlockX, this.mouseBlockZ);
            Config.fullScreenZoomLevel = this.mapView.getZoomLevel();
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        this.mw.miniMap.view.setDimension(this.mapView.getDimension());
        Keyboard.enableRepeatEvents(false);
    }

    public void openMarkerGui(Marker m, int mouseX, int mouseY) {
        if (m != null && this.mw.markerManager.selectedMarker == m) {
            // right clicked previously selected marker.
            // edit the marker
            if (Config.newMarkerDialog) {
                this.mc.displayGuiScreen(new GuiMarkerDialogNew(this, this.mw.markerManager, m));
            } else {
                this.mc.displayGuiScreen(new GuiMarkerDialog(this, this.mw.markerManager, m));
            }
        } else if (m == null) {
            // open new marker dialog
            String group = this.mw.markerManager.getVisibleGroupName();
            if (group.equals("none")) {
                group = I18n.format("mw.gui.mwgui.group.2");
            }

            int mx, my, mz;
            if (this.isPlayerNearScreenPos(mouseX, mouseY)) {
                // marker at player's locations
                mx = this.mw.playerXInt;
                my = this.mw.playerYInt;
                mz = this.mw.playerZInt;
            } else {
                // marker at mouse pointer location
                mx = this.mouseBlockX;
                my = this.mouseBlockY > 0 ? this.mouseBlockY : Config.defaultTeleportHeight;
                mz = this.mouseBlockZ;
            }
            if (Config.newMarkerDialog) {
                this.mc.displayGuiScreen(new GuiMarkerDialogNew(this, this.mw.markerManager, "", group, mx, my, mz, this.mapView.getDimension()));
            } else {
                this.mc.displayGuiScreen(new GuiMarkerDialog(this, this.mw.markerManager, "", group, mx, my, mz, this.mapView.getDimension()));
            }
        }
    }

    public void regenerateView() {
        Utils.printBoth(I18n.format("mw.gui.mwgui.chatmsg.regenmap", (int) this.mapView.getWidth(), (int) this.mapView.getHeight(), (int) this.mapView.getMinX(), (int) this.mapView.getMinZ()));
        this.mw.executor.addTask(new TaskRebuildRegions(this.mw, (int) this.mapView.getMinX(), (int) this.mapView.getMinZ(), (int) this.mapView.getWidth(), (int) this.mapView.getHeight(), this.mapView.getDimension()));
    }

    @Override
    public void updateScreen() {}

    private void drawLabel(int mouseX, int mouseY) {
        this.helpLabel.draw();
        this.optionsLabel.draw();
        final String dimString = "[" + I18n.format("mw.gui.mwgui.dimension", this.mapView.getDimension().getName()) + "]";
        this.dimensionLabel.setText(new String[]{dimString}, null);
        this.dimensionLabel.draw();

        final String groupString = "[" + I18n.format("mw.gui.mwgui.group.1", this.mw.markerManager.getVisibleGroupName()) + "]";
        this.groupLabel.setText(new String[]{groupString}, null);
        this.groupLabel.draw();

        final String overlayString = "[" + I18n.format("mw.gui.mwgui.overlay", I18n.format("mw.gui.mwgui.provider." + MapWriterAPI.getCurrentProviderName())) + "]";
        this.overlayLabel.setText(new String[]{overlayString}, null);
        this.overlayLabel.draw();

        // help message on mouse over
        if (this.helpLabel.posWithin(mouseX, mouseY)) {
            this.helpTooltipLabel.draw();
        }
    }

    private void drawMarkerLabel(int mouseX, int mouseY) {
        // draw name of marker under mouse cursor
        final Marker marker = this.getMarkerNearScreenPos(mouseX, mouseY);
        if (marker != null) {
            GuiUtils.drawHoveringText(Arrays.asList(
                marker.name, String.format("(%d, %d, %d)", marker.x, marker.y, marker.z)
            ), mouseX + 8, mouseY, this.width, this.height, -1, this.fontRenderer);
            return;
        }

        // draw name of player under mouse cursor
        if (this.isPlayerNearScreenPos(mouseX, mouseY)) {
            GuiUtils.drawHoveringText(Arrays.asList(
                this.mc.player.getDisplayNameString(), String.format("(%d, %d, %d)", this.mw.playerXInt, this.mw.playerYInt, this.mw.playerZInt)
            ), mouseX + 8, mouseY, this.width, this.height, -1, this.fontRenderer);
            return;
        }

        final MapOverlayProvider provider = MapWriterAPI.getCurrentDataProvider();
        if (provider != null) {
            final ITextComponent info = provider.getMouseInfo(mouseX, mouseY);
            if (info != null) {
                GuiUtils.drawHoveringText(Collections.singletonList(info.getFormattedText()), mouseX + 8, mouseY, this.width, this.height, -1, this.fontRenderer);
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {}

    @Override
    protected void keyTyped(char symbol, int key) {
        switch (key) {
            case Keyboard.KEY_ESCAPE:
                this.exitGui();
                break;
            case Keyboard.KEY_DELETE:
                this.deleteSelectedMarker();
                break;
            case Keyboard.KEY_SPACE:
                // next marker group
                this.mw.markerManager.nextGroup();
                this.mw.markerManager.update();
                break;
            case Keyboard.KEY_C:
                // cycle selected marker color
                if (this.mw.markerManager.selectedMarker != null) {
                    this.mw.markerManager.selectedMarker.colorNext();
                }
                break;
            case Keyboard.KEY_N:
                // select next visible marker
                this.mw.markerManager.selectNextMarker();
                break;
            case Keyboard.KEY_HOME:
                // centre map on player
                this.mapView.setViewCentreScaled(this.mw.playerX, this.mw.playerZ, this.mw.playerDimension);
                break;
            case Keyboard.KEY_END:
                // centre map on selected marker
                this.centerOnSelectedMarker();
                break;
            case Keyboard.KEY_P:
                this.mergeMapViewToImage();
                this.exitGui();
                break;
            case Keyboard.KEY_T:
                if (this.mw.markerManager.selectedMarker != null) {
                    this.mw.teleportToMarker(this.mw.markerManager.selectedMarker);
                    this.exitGui();
                } else {
                    this.mc.displayGuiScreen(new GuiTeleportDialog(this, this.mw, this.mapView, this.mouseBlockX, Config.defaultTeleportHeight, this.mouseBlockZ));
                }
                break;
            case Keyboard.KEY_LEFT:
                this.mapView.panView(-GuiFullScreenMap.PAN_FACTOR, 0);
                break;
            case Keyboard.KEY_RIGHT:
                this.mapView.panView(GuiFullScreenMap.PAN_FACTOR, 0);
                break;
            case Keyboard.KEY_UP:
                this.mapView.panView(0, -GuiFullScreenMap.PAN_FACTOR);
                break;
            case Keyboard.KEY_DOWN:
                this.mapView.panView(0, GuiFullScreenMap.PAN_FACTOR);
                break;
            case Keyboard.KEY_R:
                this.regenerateView();
                this.exitGui();
                break;
            case Keyboard.KEY_L:
                this.markerOverlay.setEnabled(!this.markerOverlay.getEnabled());
                break;
            default:
                if (key == MapWriterKeyHandler.keyMapGui.getKeyCode()) {
                    this.exitGui();
                } else if (key == MapWriterKeyHandler.keyZoomIn.getKeyCode()) {
                    this.mapView.adjustZoomLevel(-1);
                } else if (key == MapWriterKeyHandler.keyZoomOut.getKeyCode()) {
                    this.mapView.adjustZoomLevel(1);
                } else if (key == MapWriterKeyHandler.keyNextGroup.getKeyCode()) {
                    this.mw.markerManager.nextGroup();
                    this.mw.markerManager.update();
                } else if (key == MapWriterKeyHandler.keyUndergroundMode.getKeyCode()) {
                    MapWriter.toggleUndergroundMode();
                }
                break;
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int button) {
        final Marker marker = this.getMarkerNearScreenPos(x, y);
        final Marker prevMarker = this.mw.markerManager.selectedMarker;

        if (this.markerOverlay.isMouseInField() && this.mouseLeftHeld == 0) {
            this.markerOverlay.handleMouseInput();
        } else {
            switch (button) {
                case 0:
                    if (this.dimensionLabel.posWithin(x, y)) {
                        this.mc.displayGuiScreen(new GuiDimensionDialog(this, this.mw, this.mapView, this.mapView.getDimension()));
                    } else if (this.optionsLabel.posWithin(x, y)) {
                        try {
                            final GuiScreen newScreen = ModGuiConfig.class.getConstructor(GuiScreen.class).newInstance(this);
                            this.mc.displayGuiScreen(newScreen);
                        } catch (final Exception e) {
                            MapWriterForge.LOGGER.error("There was a critical issue trying to build the config GUI for {}", Reference.MOD_ID);
                        }
                    } else {
                        this.mouseLeftHeld = 1;
                        this.mouseLeftDragStartX = x;
                        this.mouseLeftDragStartY = y;
                        this.mw.markerManager.selectedMarker = marker;

                        if (marker != null && prevMarker == marker) {
                            // clicked previously selected marker.
                            // start moving the marker.
                            this.movingMarker = marker;
                            this.movingMarkerXStart = marker.x;
                            this.movingMarkerZStart = marker.z;
                        }
                    }

                    break;
                case 1:
                    this.openMarkerGui(marker, x, y);
                    break;
                case 2:
                    final Point blockPoint = this.mapMode.screenXYtoBlockXZ(this.mapView, x, y);

                    final MapOverlayProvider provider = MapWriterAPI.getCurrentDataProvider();
                    if (provider != null) {
                        provider.onMiddleClick(this.mapView.getDimension(), blockPoint.x, blockPoint.y, this.mapView);
                    }
                    break;
            }

            this.viewXStart = this.mapView.getX();
            this.viewZStart = this.mapView.getZ();
        }
    }

    @Override
    protected void mouseReleased(int x, int y, int button) {
        if (button == 0) {
            this.mouseLeftHeld = 0;
            this.movingMarker = null;
        }
    }
}

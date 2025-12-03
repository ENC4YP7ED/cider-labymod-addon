package net.labymod.addons.cider.core.labymod.hudwidgets.elements.widgets;

import net.labymod.addons.cider.core.CiderAddon;
import net.labymod.addons.cider.core.Textures;
import net.labymod.addons.cider.core.api.CiderAPI;
import net.labymod.addons.cider.core.api.CiderTrack;
import net.labymod.addons.cider.core.labymod.hudwidgets.CiderHudWidget;
import net.labymod.addons.cider.core.util.TrackUtil;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.hud.hudwidget.HudWidget;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.gui.lss.property.annotation.AutoWidget;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.activity.Link;
import net.labymod.api.client.gui.screen.widget.Widget;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.DivWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.FlexibleContentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.VerticalListWidget;
import net.labymod.api.client.gui.screen.widget.widgets.renderer.IconWidget;
import org.jetbrains.annotations.Nullable;

/**
 * Main HUD widget for Cider, displaying track information and controls
 */
@AutoWidget
@Link("cider-widget.lss")
public class CiderWidget extends FlexibleContentWidget implements HudWidget.Updatable {
    private static final String PROGRESS_VISIBLE_KEY = "--progress-visible";
    private static final String LARGE_PROGRESS_VISIBLE_KEY = "--large-progress-visible";

    private final CiderHudWidget hudWidget;
    private final CiderAPI ciderAPI;
    private final boolean editorContext;

    private ComponentWidget trackWidget;
    private ComponentWidget artistWidget;
    private IconWidget coverWidget;
    private DivWidget controlsWidget;
    private IconWidget playPauseWidget;
    private ComponentWidget currentTimeWidget;
    private ComponentWidget totalTimeWidget;

    private int lastTickPosition = -1;

    public CiderWidget(CiderHudWidget hudWidget, boolean editorContext) {
        this.hudWidget = hudWidget;
        this.ciderAPI = hudWidget.ciderAPI();
        this.editorContext = editorContext;

        boolean hasTrack = this.ciderAPI.getCurrentTrack() != null;
        this.setVariable(PROGRESS_VISIBLE_KEY, hasTrack);
        this.setVariable(LARGE_PROGRESS_VISIBLE_KEY, hasTrack);

        this.setPressable(() -> {
            if (!this.ciderAPI.isInitialized()) {
                this.artistWidget.setVisible(false);
                this.hudWidget.addon().initializeCider();
            }
        });
    }

    @Override
    public void initialize(Parent parent) {
        super.initialize(parent);
        this.children.clear();

        boolean maximize = this.editorContext ||
            !this.hudWidget.getConfig().minimizeIngame().get();

        if (maximize) {
            this.addId("maximized");
        }

        if (!this.hudWidget.getConfig().showCover().get()) {
            this.addId("no-cover");
        }

        boolean leftAligned = this.hudWidget.anchor().isLeft();
        this.addId(leftAligned ? "left" : "right");

        // Cover artwork
        this.coverWidget = new IconWidget(Icon.texture(Textures.UNKNOWN_COVER));
        this.coverWidget.addId("cover");

        if (!maximize) {
            ProgressBarWidget minimizedProgressBar = new ProgressBarWidget(this.ciderAPI);
            minimizedProgressBar.addId("minimized-bar");
            this.coverWidget.addChild(minimizedProgressBar);
        }

        if (leftAligned) {
            this.addContent(this.coverWidget);
        }

        // Player container
        FlexibleContentWidget player = new FlexibleContentWidget();
        player.addId("player");

        // Text container
        FlexibleContentWidget textContainer = new FlexibleContentWidget();
        textContainer.addId("text-and-control");

        VerticalListWidget text = new VerticalListWidget();
        text.addId("text");

        this.trackWidget = ComponentWidget.empty();
        text.addChild(this.trackWidget);

        this.artistWidget = ComponentWidget.empty();
        text.addChild(this.artistWidget);

        // Media controls
        this.controlsWidget = new DivWidget();
        this.controlsWidget.addId("controls");

        this.playPauseWidget = new IconWidget(this.ciderAPI.isPlaying() ? Textures.SpriteControls.PAUSE : Textures.SpriteControls.PLAY);
        this.playPauseWidget.addId("play");
        this.playPauseWidget.setPressable(() -> {
            boolean success = this.hudWidget.addon().getPlaybackController().togglePlayPause();
            if (!success) {
                // Fallback to system media keys if API doesn't work
                this.hudWidget.addon().getPlaybackController().useSystemMediaKeys();
            }
            this.playPauseWidget.icon().set(this.ciderAPI.isPlaying() ? Textures.SpriteControls.PLAY : Textures.SpriteControls.PAUSE);
        });
        this.controlsWidget.addChild(this.playPauseWidget);

        IconWidget previousTrack = new IconWidget(Textures.SpriteControls.PREVIOUS);
        previousTrack.addId("previous");
        previousTrack.setPressable(() -> {
            this.hudWidget.addon().getPlaybackController().previous();
        });
        this.controlsWidget.addChild(previousTrack);

        IconWidget nextTrack = new IconWidget(Textures.SpriteControls.NEXT);
        nextTrack.addId("next");
        nextTrack.setPressable(() -> {
            this.hudWidget.addon().getPlaybackController().next();
        });
        this.controlsWidget.addChild(nextTrack);

        if (leftAligned) {
            textContainer.addFlexibleContent(text);
            textContainer.addContent(this.controlsWidget);
        } else {
            textContainer.addContent(this.controlsWidget);
            textContainer.addFlexibleContent(text);
        }

        player.addFlexibleContent(textContainer);

        // Progress section
        FlexibleContentWidget progress = new FlexibleContentWidget();
        progress.addId("progress");

        this.currentTimeWidget = ComponentWidget.empty();
        progress.addContent(this.currentTimeWidget);

        ProgressBarWidget progressBar = new ProgressBarWidget(this.ciderAPI);
        progressBar.addId("full-bar");
        progress.addFlexibleContent(progressBar);

        this.totalTimeWidget = ComponentWidget.empty();
        progress.addContent(this.totalTimeWidget);

        player.addContent(progress);
        this.addContent(player);

        if (!leftAligned) {
            this.addContent(this.coverWidget);
        }

        this.updateTrack(this.ciderAPI.getCurrentTrack());
    }

    @Override
    public void tick() {
        super.tick();

        boolean hasTrack = this.ciderAPI.getCurrentTrack() != null;
        this.setVariable(PROGRESS_VISIBLE_KEY, hasTrack);

        if (!this.editorContext) {
            boolean isChatOpen = Laby.references().chatAccessor().isChatOpen();
            if (!this.hudWidget.getConfig().minimizeIngame().get() || isChatOpen) {
                this.addId("maximized");
                this.setVariable(LARGE_PROGRESS_VISIBLE_KEY, hasTrack);
            } else {
                this.removeId("maximized");
                this.setVariable(LARGE_PROGRESS_VISIBLE_KEY, false);
            }
        } else {
            this.setVariable(LARGE_PROGRESS_VISIBLE_KEY, hasTrack);
        }

        // Update current time display
        CiderTrack track = this.ciderAPI.getCurrentTrack();
        if (track != null && this.currentTimeWidget != null) {
            int position = (int) track.getCurrentTime();
            if (this.lastTickPosition < 0 || this.lastTickPosition != position) {
                String positionDisplay = String.format("%d:%02d", position / 60, position % 60);
                this.currentTimeWidget.setComponent(Component.text(positionDisplay));
                this.lastTickPosition = position;
            }
        }
    }

    @Override
    public void update(@Nullable String reason) {
        if (reason == null || reason.equals("connect")) {
            this.reInitialize();
            return;
        }

        if (reason.equals("playback_change") && this.playPauseWidget != null) {
            this.playPauseWidget.icon().set(this.ciderAPI.isPlaying() ? Textures.SpriteControls.PAUSE : Textures.SpriteControls.PLAY);
        }

        if (reason.equals("track_change")) {
            this.updateTrack(this.ciderAPI.getCurrentTrack());
        }

        if (reason.equals("cover_visibility")) {
            boolean showCover = this.hudWidget.getConfig().showCover().get();
            if (showCover) {
                this.removeId("no-cover");
            } else {
                this.addId("no-cover");
            }
        }
    }

    private void updateTrack(CiderTrack track) {
        if (this.trackWidget == null || this.artistWidget == null) {
            return;
        }

        this.trackWidget.setComponent(Component.text(track == null ? "Not playing" : track.getName()));
        this.artistWidget.setComponent(Component.text(track == null ? "Click to retry" : track.getArtistName()));
        this.artistWidget.setVisible(true);

        if (track == null || track.getDuration() <= 0) {
            if (this.controlsWidget != null) {
                this.controlsWidget.setVisible(false);
            }
            return;
        }

        if (this.controlsWidget != null) {
            this.controlsWidget.setVisible(true);
        }

        int length = (int) track.getDuration();
        String totalTimeDisplay = String.format("%d:%02d", length / 60, length % 60);
        this.totalTimeWidget.setComponent(Component.text(totalTimeDisplay));

        // Update cover artwork
        Icon icon = TrackUtil.createIcon(track);
        this.coverWidget.icon().set(icon);
    }
}

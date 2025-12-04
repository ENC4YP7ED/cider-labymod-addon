package net.labymod.addons.cider.core.labymod.hudwidgets;

import net.labymod.addons.cider.core.api.CiderAPI;
import net.labymod.addons.cider.core.api.CiderTrack;
import net.labymod.addons.cider.core.events.CiderConnectEvent;
import net.labymod.addons.cider.core.events.CiderDisconnectEvent;
import net.labymod.addons.cider.core.events.CiderPlaybackChangedEvent;
import net.labymod.addons.cider.core.events.CiderTrackChangedEvent;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.event.Subscribe;

/**
 * Simple text-based HUD widget displaying current Cider/Apple Music track information
 * Uses the TextHudWidget pattern with TextLine components
 */
public class CiderTextHudWidget extends TextHudWidget<TextHudWidgetConfig> {
    private TextLine trackLine;
    private TextLine artistLine;
    private final Icon hudWidgetIcon;
    private final CiderAPI ciderAPI;

    public CiderTextHudWidget(String id, Icon icon, CiderAPI ciderAPI) {
        super(id);
        this.hudWidgetIcon = icon;
        this.ciderAPI = ciderAPI;
    }

    @Override
    public void load(TextHudWidgetConfig config) {
        super.load(config);
        this.trackLine = super.createLine("Track", "Loading...");
        this.artistLine = super.createLine("Artist", "Loading...");
        this.setIcon(this.hudWidgetIcon);
        this.updateTrack();
    }

    @Override
    public boolean isVisibleInGame() {
        return this.ciderAPI.isInitialized() &&
               this.ciderAPI.isPlaying() &&
               this.ciderAPI.getCurrentTrack() != null;
    }

    @Subscribe
    public void onCiderConnect(CiderConnectEvent event) {
        this.updateTrack();
    }

    @Subscribe
    public void onCiderDisconnect(CiderDisconnectEvent event) {
        this.updateTrack();
    }

    @Subscribe
    public void onCiderTrackChanged(CiderTrackChangedEvent event) {
        this.updateTrack();
    }

    @Subscribe
    public void onCiderPlaybackChanged(CiderPlaybackChangedEvent event) {
        this.updateTrack();
    }

    private void updateTrack() {
        if (this.trackLine == null || this.artistLine == null) {
            return;
        }

        if (this.ciderAPI.isPlaying() && this.ciderAPI.getCurrentTrack() != null) {
            CiderTrack track = this.ciderAPI.getCurrentTrack();
            this.trackLine.updateAndFlush(track.getName());
            this.artistLine.updateAndFlush(track.getArtist());
        } else {
            this.trackLine.updateAndFlush("Not playing");
            this.artistLine.updateAndFlush("Not playing");
        }
    }
}

package net.labymod.addons.cider.core.ui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.labymod.addons.cider.core.CiderAddon;
import net.labymod.addons.cider.core.api.CiderTrack;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.activity.Activity;
import net.labymod.api.client.gui.screen.activity.AutoActivity;
import net.labymod.api.client.gui.screen.activity.Link;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.DivWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.FlexibleContentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.ScrollWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.VerticalListWidget;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for displaying track history
 */
@AutoActivity
@Link("track-history.lss")
public class TrackHistoryActivity extends Activity {

    private final CiderAddon addon;
    private final List<CiderTrack> trackHistory;
    private final VerticalListWidget<TrackHistoryWidget> trackList;

    public TrackHistoryActivity(CiderAddon addon) {
        this.addon = addon;
        this.trackHistory = new ArrayList<>();  // Will be populated from addon's history
        this.trackList = new VerticalListWidget<>()
            .addId("track-history-list");
    }

    @Override
    public void initialize(Parent parent) {
        super.initialize(parent);

        FlexibleContentWidget container = new FlexibleContentWidget()
            .addId("history-container");

        // Title
        ComponentWidget titleWidget = ComponentWidget.text(
            Component.text("Track History", NamedTextColor.WHITE)
        ).addId("history-title");

        // Info text if no history
        if (trackHistory.isEmpty()) {
            ComponentWidget infoWidget = ComponentWidget.text(
                Component.text("No track history available yet", NamedTextColor.GRAY)
            ).addId("no-history-info");

            container.addContent(titleWidget);
            container.addContent(infoWidget);
        } else {
            // Populate list with track history
            for (CiderTrack track : trackHistory) {
                this.trackList.addChild(new TrackHistoryWidget(track));
            }

            ScrollWidget scrollWidget = new ScrollWidget(this.trackList);

            container.addContent(titleWidget);
            container.addFlexibleContent(scrollWidget);
        }

        this.document().addChild(container);
    }

    /**
     * Widget for displaying a single track in the history list
     */
    @AutoActivity
    public static class TrackHistoryWidget extends DivWidget {

        private final CiderTrack track;

        public TrackHistoryWidget(CiderTrack track) {
            this.track = track;
        }

        @Override
        public void initialize(Parent parent) {
            super.initialize(parent);
            this.addId("track-history-item");

            // Track name
            ComponentWidget trackName = ComponentWidget.text(
                Component.text(track.getName(), NamedTextColor.WHITE)
            ).addId("track-name");

            // Artist and album info
            Component artistAlbum = Component.empty()
                .append(Component.text(track.getArtist(), NamedTextColor.GRAY))
                .append(Component.text(" • ", NamedTextColor.DARK_GRAY))
                .append(Component.text(track.getAlbum(), NamedTextColor.GRAY));

            ComponentWidget artistInfo = ComponentWidget.component(artistAlbum)
                .addId("artist-info");

            this.addChild(trackName);
            this.addChild(artistInfo);
        }

        public CiderTrack getTrack() {
            return this.track;
        }
    }
}

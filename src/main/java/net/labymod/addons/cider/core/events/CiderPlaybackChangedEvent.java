package net.labymod.addons.cider.core.events;

import net.labymod.api.event.Event;

/**
 * Event fired when playback state changes
 */
public class CiderPlaybackChangedEvent implements Event {

    private final boolean isPlaying;

    public CiderPlaybackChangedEvent(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    public boolean isPlaying() {
        return isPlaying;
    }
}

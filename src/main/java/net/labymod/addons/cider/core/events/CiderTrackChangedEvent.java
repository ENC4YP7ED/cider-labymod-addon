package net.labymod.addons.cider.core.events;

import net.labymod.addons.cider.core.api.CiderTrack;
import net.labymod.api.event.Event;

/**
 * Event fired when the current track changes
 */
public class CiderTrackChangedEvent implements Event {

    private final CiderTrack oldTrack;
    private final CiderTrack newTrack;

    public CiderTrackChangedEvent(CiderTrack oldTrack, CiderTrack newTrack) {
        this.oldTrack = oldTrack;
        this.newTrack = newTrack;
    }

    public CiderTrack getOldTrack() {
        return oldTrack;
    }

    public CiderTrack getNewTrack() {
        return newTrack;
    }
}

package net.labymod.addons.cider.core.events;

import net.labymod.api.event.Event;

/**
 * Event fired when playback position changes
 */
public class CiderPositionChangedEvent implements Event {

    private final double currentTime;
    private final double duration;

    public CiderPositionChangedEvent(double currentTime, double duration) {
        this.currentTime = currentTime;
        this.duration = duration;
    }

    public double getCurrentTime() {
        return currentTime;
    }

    public double getDuration() {
        return duration;
    }

    public double getProgress() {
        return duration > 0 ? currentTime / duration : 0;
    }
}

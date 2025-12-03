package net.labymod.addons.cider.core.api;

/**
 * Listener interface for Cider API events
 */
public interface CiderListener {

    /**
     * Called when the current track changes
     */
    void onTrackChanged(CiderTrack oldTrack, CiderTrack newTrack);

    /**
     * Called when playback state changes
     */
    void onPlaybackChanged(boolean isPlaying);

    /**
     * Called when playback position updates
     */
    void onPositionChanged(double currentTime, double duration);

    /**
     * Called when Cider disconnects
     */
    void onDisconnect();
}

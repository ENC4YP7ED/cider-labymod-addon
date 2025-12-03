package net.labymod.addons.cider.core.sharing;

import java.util.UUID;

/**
 * Represents a track shared by a player
 */
public class SharedTrack {
    private final UUID playerUuid;
    private final String playerName;
    private final String trackName;
    private final String artistName;
    private final String albumName;
    private final long timestamp;

    public SharedTrack(UUID playerUuid, String playerName, String trackName, String artistName, String albumName) {
        this.playerUuid = playerUuid;
        this.playerName = playerName;
        this.trackName = trackName;
        this.artistName = artistName;
        this.albumName = albumName;
        this.timestamp = System.currentTimeMillis();
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getTrackName() {
        return trackName;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - timestamp > 30000; // 30 seconds
    }

    @Override
    public String toString() {
        return String.format("%s - %s", artistName, trackName);
    }
}

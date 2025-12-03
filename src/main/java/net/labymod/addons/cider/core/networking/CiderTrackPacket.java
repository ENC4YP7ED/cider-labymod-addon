package net.labymod.addons.cider.core.networking;

import net.labymod.api.client.network.ClientPacket;
import net.labymod.api.util.io.web.request.Request;
import java.util.UUID;

/**
 * Packet for sharing track information between players
 * Uses LabyMod's networking protocol to broadcast track data
 */
public class CiderTrackPacket implements ClientPacket {

    private UUID playerUuid;
    private String playerName;
    private String trackName;
    private String artistName;
    private String albumName;
    private long timestamp;

    /**
     * Empty constructor required for packet deserialization
     */
    public CiderTrackPacket() {
    }

    public CiderTrackPacket(UUID playerUuid, String playerName, String trackName, String artistName, String albumName) {
        this.playerUuid = playerUuid;
        this.playerName = playerName;
        this.trackName = trackName;
        this.artistName = artistName;
        this.albumName = albumName;
        this.timestamp = System.currentTimeMillis();
    }

    @Override
    public void write(Request.Buffer buffer) {
        buffer.writeUniqueId(playerUuid);
        buffer.writeString(playerName);
        buffer.writeString(trackName);
        buffer.writeString(artistName);
        buffer.writeString(albumName);
        buffer.writeLong(timestamp);
    }

    @Override
    public void read(Request.Buffer buffer) {
        this.playerUuid = buffer.readUniqueId();
        this.playerName = buffer.readString();
        this.trackName = buffer.readString();
        this.artistName = buffer.readString();
        this.albumName = buffer.readString();
        this.timestamp = buffer.readLong();
    }

    // Getters
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
}

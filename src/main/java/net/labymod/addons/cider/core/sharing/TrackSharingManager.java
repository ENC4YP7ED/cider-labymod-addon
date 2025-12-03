package net.labymod.addons.cider.core.sharing;

import net.labymod.addons.cider.core.CiderAddon;
import net.labymod.addons.cider.core.api.CiderTrack;
import net.labymod.api.Laby;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages track sharing between players
 */
public class TrackSharingManager {
    private final CiderAddon addon;
    private final Map<UUID, SharedTrack> sharedTracks;
    private boolean sharingEnabled;

    public TrackSharingManager(CiderAddon addon) {
        this.addon = addon;
        this.sharedTracks = new ConcurrentHashMap<>();
        this.sharingEnabled = false;
    }

    /**
     * Enable or disable track sharing
     */
    public void setSharingEnabled(boolean enabled) {
        this.sharingEnabled = enabled;
        if (!enabled) {
            sharedTracks.clear();
        }
    }

    public boolean isSharingEnabled() {
        return sharingEnabled;
    }

    /**
     * Share current track with other players
     * Note: This requires LabyMod's networking API to actually broadcast
     */
    public void shareCurrentTrack() {
        if (!sharingEnabled) {
            return;
        }

        CiderTrack track = addon.getCiderAPI().getCurrentTrack();
        if (track == null) {
            return;
        }

        try {
            UUID playerUuid = Laby.labyAPI().minecraft().getClientPlayer().getUuid();
            String playerName = Laby.labyAPI().minecraft().getClientPlayer().getName();

            SharedTrack sharedTrack = new SharedTrack(
                playerUuid,
                playerName,
                track.getName(),
                track.getArtistName(),
                track.getAlbumName()
            );

            sharedTracks.put(playerUuid, sharedTrack);

            // TODO: Broadcast to other players using LabyMod's networking API
            // This would typically use LabyMod's server connection or protocol
            // Example:
            // addon.labyAPI().networkingAPI().sendPacket(new TrackSharePacket(sharedTrack));

        } catch (Exception e) {
            addon.logger().error("Failed to share track", e);
        }
    }

    /**
     * Receive a shared track from another player
     */
    public void receiveSharedTrack(SharedTrack track) {
        if (!sharingEnabled) {
            return;
        }

        sharedTracks.put(track.getPlayerUuid(), track);
    }

    /**
     * Get shared track for a specific player
     */
    public SharedTrack getSharedTrack(UUID playerUuid) {
        SharedTrack track = sharedTracks.get(playerUuid);
        if (track != null && track.isExpired()) {
            sharedTracks.remove(playerUuid);
            return null;
        }
        return track;
    }

    /**
     * Get all currently shared tracks
     */
    public Map<UUID, SharedTrack> getAllSharedTracks() {
        // Remove expired tracks
        sharedTracks.entrySet().removeIf(entry -> entry.getValue().isExpired());
        return new ConcurrentHashMap<>(sharedTracks);
    }

    /**
     * Clear all shared tracks
     */
    public void clear() {
        sharedTracks.clear();
    }
}

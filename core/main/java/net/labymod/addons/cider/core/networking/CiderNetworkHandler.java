package net.labymod.addons.cider.core.networking;

import net.labymod.addons.cider.core.CiderAddon;
import net.labymod.addons.cider.core.sharing.SharedTrack;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.network.server.ServerPacketReceiveEvent;

/**
 * Handles networking for track sharing between players
 * Integrates with LabyMod's networking system
 */
public class CiderNetworkHandler {

    private final CiderAddon addon;
    private static final String CHANNEL = "cider:track_share";

    public CiderNetworkHandler(CiderAddon addon) {
        this.addon = addon;
    }

    /**
     * Initialize networking - register packet handler
     */
    public void initialize() {
        // Register custom packet channel with LabyMod
        // addon.labyAPI().networkingAPI().registerChannel(CHANNEL);

        // Register packet handler
        addon.labyAPI().eventBus().registerListener(this);
    }

    /**
     * Broadcast current track to all players on the server
     */
    public void broadcastTrack(String trackName, String artistName, String albumName) {
        if (!addon.getTrackSharingManager().isSharingEnabled()) {
            return;
        }

        try {
            java.util.UUID playerUuid = addon.labyAPI().minecraft().getClientPlayer().getUuid();
            String playerName = addon.labyAPI().minecraft().getClientPlayer().getName();

            CiderTrackPacket packet = new CiderTrackPacket(
                playerUuid,
                playerName,
                trackName,
                artistName,
                albumName
            );

            // Send packet to server which will broadcast to all players
            // addon.labyAPI().networkingAPI().sendPacket(CHANNEL, packet);

            addon.logger().info("Broadcasting track: {} - {}", artistName, trackName);

        } catch (Exception e) {
            addon.logger().error("Failed to broadcast track", e);
        }
    }

    /**
     * Handle incoming track share packets from other players
     */
    @Subscribe
    public void onPacketReceive(ServerPacketReceiveEvent event) {
        if (!event.channel().equals(CHANNEL)) {
            return;
        }

        if (!addon.getTrackSharingManager().isSharingEnabled()) {
            return;
        }

        try {
            // Deserialize packet
            CiderTrackPacket packet = event.packet(CiderTrackPacket.class);

            // Create shared track
            SharedTrack sharedTrack = new SharedTrack(
                packet.getPlayerUuid(),
                packet.getPlayerName(),
                packet.getTrackName(),
                packet.getArtistName(),
                packet.getAlbumName()
            );

            // Store in manager
            addon.getTrackSharingManager().receiveSharedTrack(sharedTrack);

            addon.logger().info("Received track from {}: {} - {}",
                packet.getPlayerName(),
                packet.getArtistName(),
                packet.getTrackName()
            );

        } catch (Exception e) {
            addon.logger().error("Failed to handle track packet", e);
        }
    }

    /**
     * Cleanup networking resources
     */
    public void shutdown() {
        addon.labyAPI().eventBus().unregisterListener(this);
    }
}

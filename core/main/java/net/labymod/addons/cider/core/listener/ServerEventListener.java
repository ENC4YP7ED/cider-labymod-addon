package net.labymod.addons.cider.core.listener;

import net.labymod.addons.cider.core.CiderAddon;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.network.server.ServerDisconnectEvent;
import net.labymod.api.event.client.network.server.ServerJoinEvent;

/**
 * Handles server connection/disconnection events for cleanup and reinitializ

ation
 */
public class ServerEventListener {
    private final CiderAddon addon;

    public ServerEventListener(CiderAddon addon) {
        this.addon = addon;
    }

    @Subscribe
    public void onServerJoin(ServerJoinEvent event) {
        // Reinitialize Cider connection when joining a server
        if (addon.configuration().enabled().get()) {
            addon.initializeCider();
        }
    }

    @Subscribe
    public void onServerDisconnect(ServerDisconnectEvent event) {
        // Clean up shared track data when disconnecting
        addon.getTrackSharingManager().clear();
    }
}

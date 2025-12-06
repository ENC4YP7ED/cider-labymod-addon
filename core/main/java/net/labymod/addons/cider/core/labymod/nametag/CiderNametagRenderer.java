package net.labymod.addons.cider.core.labymod.nametag;

import net.labymod.addons.cider.core.CiderAddon;
import net.labymod.addons.cider.core.sharing.SharedTrack;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.entity.player.Player;
import net.labymod.api.client.render.matrix.Stack;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.render.NameTagRenderEvent;

/**
 * Renders shared track information above player name tags
 */
public class CiderNametagRenderer {
    private final CiderAddon addon;

    public CiderNametagRenderer(CiderAddon addon) {
        this.addon = addon;
    }

    @Subscribe
    public void onNametagRender(NameTagRenderEvent event) {
        if (!addon.getTrackSharingManager().isSharingEnabled()) {
            return;
        }

        Player player = event.entity();
        if (player == null) {
            return;
        }

        SharedTrack sharedTrack = addon.getTrackSharingManager().getSharedTrack(player.getUuid());
        if (sharedTrack == null) {
            return;
        }

        Stack stack = event.stack();

        // Create component for track info
        Component trackInfo = Component.text()
            .append(Component.text(net.labymod.api.util.I18n.translate("cider.nametag.prefix"), NamedTextColor.RED))
            .append(Component.text(sharedTrack.getArtistName() + net.labymod.api.util.I18n.translate("cider.nametag.separator") + sharedTrack.getTrackName(), NamedTextColor.GRAY))
            .build();

        // Add to nametag (this would be rendered above the player's name)
        // Note: Actual implementation depends on LabyMod's nametag API
        // event.addComponent(trackInfo);

        // For now, we'll just store it and let the HUD handle display
        // The actual nametag rendering would require LabyMod's specific API methods
    }

    /**
     * Get display component for a shared track
     */
    public Component getTrackComponent(SharedTrack track) {
        return Component.text()
            .append(Component.text(net.labymod.api.util.I18n.translate("cider.nametag.prefix"), NamedTextColor.RED))
            .append(Component.text(track.getArtistName() + net.labymod.api.util.I18n.translate("cider.nametag.separator"), NamedTextColor.GRAY))
            .append(Component.text(track.getTrackName(), NamedTextColor.WHITE))
            .build();
    }
}

package net.labymod.addons.cider.core.interaction;

import net.kyori.adventure.text.Component;
import net.labymod.addons.cider.core.CiderAddon;
import net.labymod.addons.cider.core.api.CiderTrack;
import net.labymod.api.Laby;
import net.labymod.api.client.entity.player.Player;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.gui.navigation.elements.BulletPoint;
import net.labymod.api.client.resources.ResourceLocation;

/**
 * BulletPoint for sharing currently playing track with other players
 */
public class ShareTrackBulletPoint implements BulletPoint {

    private final CiderAddon addon;

    public ShareTrackBulletPoint(CiderAddon addon) {
        this.addon = addon;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("cider.interaction.shareTrack");
    }

    @Override
    public Icon getIcon() {
        return Icon.texture(
            ResourceLocation.create("cider", "themes/vanilla/textures/settings/hud/cider32.png")
        ).resolution(32, 32);
    }

    @Override
    public void execute(Player player) {
        CiderTrack currentTrack = this.addon.getCiderAPI().getCurrentTrack();
        if (currentTrack == null) {
            return;
        }

        Laby.labyAPI().minecraft().executeNextTick(() -> {
            // Open chat with pre-filled message
            String message = String.format("/msg %s I'm listening to: %s by %s",
                player.getName(),
                currentTrack.getName(),
                currentTrack.getArtist()
            );
            Laby.labyAPI().minecraft().openChat(message);
        });
    }

    @Override
    public boolean isVisible(Player player) {
        if (!this.addon.configuration().enabled().get()) {
            return false;
        }

        if (!this.addon.configuration().enableTrackSharing().get()) {
            return false;
        }

        // Only show if we're currently playing something
        return this.addon.getCiderAPI().isInitialized()
            && this.addon.getCiderAPI().getCurrentTrack() != null
            && this.addon.getCiderAPI().isPlaying();
    }
}

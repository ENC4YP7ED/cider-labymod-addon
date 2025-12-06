package net.labymod.addons.cider.core.mixins;

import net.labymod.addons.cider.core.CiderAddon;
import net.labymod.addons.cider.core.api.CiderTrack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to display now playing information on the title screen
 */
@Mixin(TitleScreen.class)
public class MixinTitleScreen {

    /**
     * Render now playing info on title screen
     */
    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        CiderAddon addon = CiderAddon.get();
        if (addon != null && addon.configuration().showOnTitleScreen().get()) {
            CiderTrack track = addon.getCiderAPI().getCurrentTrack();
            if (track != null) {
                String nowPlaying = "♫ Now Playing: " + track.getName() + " - " + track.getArtist();
                int width = graphics.guiWidth();
                int x = width - 5;
                int y = 5;

                // Render text in top-right corner
                graphics.drawString(
                    ((TitleScreen)(Object)this).minecraft.font,
                    Component.literal(nowPlaying),
                    x - ((TitleScreen)(Object)this).minecraft.font.width(nowPlaying),
                    y,
                    0xFFFFFF,
                    true
                );
            }
        }
    }
}

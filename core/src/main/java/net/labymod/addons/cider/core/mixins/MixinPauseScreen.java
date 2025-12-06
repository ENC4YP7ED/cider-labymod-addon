package net.labymod.addons.cider.core.mixins;

import net.labymod.addons.cider.core.CiderAddon;
import net.labymod.addons.cider.core.api.CiderPlaybackController;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to add music controls to the pause screen
 */
@Mixin(PauseScreen.class)
public abstract class MixinPauseScreen extends Screen {

    protected MixinPauseScreen(Component title) {
        super(title);
    }

    /**
     * Add Cider music control buttons to pause screen
     */
    @Inject(method = "createPauseMenu", at = @At("RETURN"))
    private void onCreatePauseMenu(CallbackInfo ci) {
        CiderAddon addon = CiderAddon.get();
        if (addon != null && addon.configuration().showPauseScreenControls().get()) {
            CiderPlaybackController controller = addon.getPlaybackController();

            // Add play/pause button
            this.addRenderableWidget(
                Button.builder(
                    Component.literal("⏯ Music"),
                    button -> controller.togglePlayPause()
                )
                .bounds(this.width / 2 - 102, this.height / 4 + 120 + 12, 98, 20)
                .build()
            );

            // Add next track button
            this.addRenderableWidget(
                Button.builder(
                    Component.literal("⏭"),
                    button -> controller.next()
                )
                .bounds(this.width / 2 + 2, this.height / 4 + 120 + 12, 48, 20)
                .build()
            );

            // Add previous track button
            this.addRenderableWidget(
                Button.builder(
                    Component.literal("⏮"),
                    button -> controller.previous()
                )
                .bounds(this.width / 2 + 52, this.height / 4 + 120 + 12, 48, 20)
                .build()
            );
        }
    }
}

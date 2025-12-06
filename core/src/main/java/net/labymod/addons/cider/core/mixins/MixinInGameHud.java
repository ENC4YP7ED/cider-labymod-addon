package net.labymod.addons.cider.core.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import net.labymod.addons.cider.core.CiderAddon;
import net.labymod.addons.cider.core.api.CiderTrack;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to integrate Cider with the in-game HUD rendering
 */
@Mixin(Gui.class)
public class MixinInGameHud {

    /**
     * Inject into HUD rendering to add Cider integration hooks
     */
    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(GuiGraphics graphics, float tickDelta, CallbackInfo ci) {
        CiderAddon addon = CiderAddon.get();
        if (addon != null && addon.configuration().enabled().get()) {
            // Allow for custom rendering integration
            // This provides a hook point for future enhancements
            CiderTrack track = addon.getCiderAPI().getCurrentTrack();
            if (track != null && addon.configuration().showActionBar().get()) {
                // Action bar integration could be added here
            }
        }
    }
}

package net.labymod.addons.cider.core.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import net.labymod.addons.cider.core.CiderAddon;
import net.labymod.addons.cider.core.api.CiderTrack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to add visual effects to player when listening to music
 */
@Mixin(PlayerRenderer.class)
public class MixinPlayerRenderer {

    /**
     * Inject into player rendering to add music note particles or effects
     */
    @Inject(method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("TAIL"))
    private void onRender(AbstractClientPlayer player, float entityYaw, float partialTicks,
                         PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        CiderAddon addon = CiderAddon.get();
        if (addon != null && addon.configuration().showMusicNotes().get()) {
            CiderTrack track = addon.getCiderAPI().getCurrentTrack();
            if (track != null && addon.getCiderAPI().isPlaying()) {
                // Check if this is the local player
                if (player.isLocalPlayer()) {
                    // Hook for rendering music notes or other visual effects
                    // Implementation would spawn particles or render custom effects
                }
            }
        }
    }
}

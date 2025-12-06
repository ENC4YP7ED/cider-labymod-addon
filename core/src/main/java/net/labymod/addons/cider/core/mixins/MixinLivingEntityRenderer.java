package net.labymod.addons.cider.core.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import net.labymod.addons.cider.core.CiderAddon;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin for optimizing entity rendering performance
 * Similar to performance optimizations in Flux addon
 */
@Mixin(LivingEntityRenderer.class)
public class MixinLivingEntityRenderer {

    /**
     * Optimize entity rendering when music visualization is enabled
     */
    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"))
    private void onRenderEntity(LivingEntity entity, float entityYaw, float partialTicks,
                                PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        CiderAddon addon = CiderAddon.get();
        if (addon != null && addon.configuration().optimizeRendering().get()) {
            // Hook for performance optimizations when rendering with music effects
            // This could include culling optimizations or render batching
        }
    }
}

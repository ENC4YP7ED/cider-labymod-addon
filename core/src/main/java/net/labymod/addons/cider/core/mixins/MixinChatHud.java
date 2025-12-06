package net.labymod.addons.cider.core.mixins;

import net.labymod.addons.cider.core.CiderAddon;
import net.labymod.addons.cider.core.api.CiderTrack;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to enhance chat functionality with Cider track notifications
 */
@Mixin(ChatComponent.class)
public class MixinChatHud {

    /**
     * Inject into chat message rendering to potentially add track info
     */
    @Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;)V", at = @At("HEAD"), cancellable = true)
    private void onAddMessage(Component message, CallbackInfo ci) {
        // Allow addons to intercept chat messages for custom formatting
        CiderAddon addon = CiderAddon.get();
        if (addon != null && addon.configuration().showTrackInChat().get()) {
            CiderTrack currentTrack = addon.getCiderAPI().getCurrentTrack();
            if (currentTrack != null && message.getString().contains("!nowplaying")) {
                // Custom handling for track display commands
                ci.cancel();
            }
        }
    }
}

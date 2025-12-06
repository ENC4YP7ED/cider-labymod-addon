package net.labymod.addons.cider.v1_20_6.mixins;

import org.spongepowered.asm.mixin.Mixin;

/**
 * Example mixin for Minecraft 1.20.6
 * Demonstrates version-specific mixin structure for LabyMod addons
 */
@Mixin(targets = "net.minecraft.client.gui.GuiGraphics", remap = false)
public class MixinGuiGraphics {
    // Example mixin class - hooks can be added here for custom rendering
    // This serves as a template for version-specific mixins
}

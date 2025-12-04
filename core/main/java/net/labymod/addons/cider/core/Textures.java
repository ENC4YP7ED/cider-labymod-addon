package net.labymod.addons.cider.core;

import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.resources.texture.ThemeTextureLocation;

/**
 * Texture resources for Cider addon
 */
public class Textures {
    // Cover artwork
    public static final ThemeTextureLocation UNKNOWN_COVER =
        ThemeTextureLocation.of("cider:unknown_cover", 32, 32);

    // HUD Widget icons
    public static class HudWidget {
        public static final Icon CIDER_32 = Icon.texture(
            net.labymod.api.client.resources.ResourceLocation.create(
                "cider", "themes/vanilla/textures/settings/hud/cider32.png"
            )
        ).resolution(32, 32);

        public static final Icon CIDER_64 = Icon.texture(
            net.labymod.api.client.resources.ResourceLocation.create(
                "cider", "themes/vanilla/textures/settings/hud/cider64.png"
            )
        ).resolution(64, 64);
    }

    // Playback control sprites
    public static class SpriteControls {
        public static final ThemeTextureLocation TEXTURE =
            ThemeTextureLocation.of("cider:controls", 20, 20);
        public static final Icon PAUSE = Icon.sprite(TEXTURE, 0, 0, 10);
        public static final Icon PLAY = Icon.sprite(TEXTURE, 1, 0, 10);
        public static final Icon NEXT = Icon.sprite(TEXTURE, 0, 1, 10);
        public static final Icon PREVIOUS = Icon.sprite(TEXTURE, 1, 1, 10);
    }

    // Configuration settings icons
    public static class Settings {
        public static final ThemeTextureLocation TEXTURE =
            ThemeTextureLocation.of("cider:settings_icons", 16, 16);
        public static final Icon GENERAL = Icon.sprite(TEXTURE, 0, 0, 8);
        public static final Icon DISPLAY = Icon.sprite(TEXTURE, 1, 0, 8);
        public static final Icon SHARING = Icon.sprite(TEXTURE, 2, 0, 8);
        public static final Icon API = Icon.sprite(TEXTURE, 3, 0, 8);
    }
}

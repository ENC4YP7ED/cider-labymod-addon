package net.labymod.addons.cider.core;

import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.resources.texture.ThemeTextureLocation;

/**
 * Texture resources for Cider addon
 */
public class Textures {
    public static final ThemeTextureLocation UNKNOWN_COVER =
        ThemeTextureLocation.of("cider:unknown_cover", 32, 32);

    public static class SpriteControls {
        public static final ThemeTextureLocation TEXTURE =
            ThemeTextureLocation.of("cider:controls", 20, 20);
        public static final Icon PAUSE = Icon.sprite(TEXTURE, 0, 0, 10);
        public static final Icon PLAY = Icon.sprite(TEXTURE, 1, 0, 10);
        public static final Icon NEXT = Icon.sprite(TEXTURE, 0, 1, 10);
        public static final Icon PREVIOUS = Icon.sprite(TEXTURE, 1, 1, 10);
    }
}

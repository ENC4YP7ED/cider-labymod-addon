package net.labymod.addons.cider.core.labymod.hudwidgets;

import net.labymod.addons.cider.core.CiderAddon;
import net.labymod.addons.cider.core.api.CiderAPI;
import net.labymod.addons.cider.core.api.CiderTrack;
import net.labymod.api.client.gui.hud.HudWidget;
import net.labymod.api.client.gui.hud.binding.category.HudWidgetCategory;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.FlexibleContentWidget;
import net.labymod.api.client.render.matrix.Stack;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;

/**
 * HUD Widget displaying current Cider/Apple Music track
 */
public class CiderHudWidget extends HudWidget {

    private final CiderAddon addon;
    private final CiderAPI ciderAPI;

    public CiderHudWidget(String id, Icon icon, CiderAddon addon, CiderAPI ciderAPI) {
        super(id, icon);
        this.addon = addon;
        this.ciderAPI = ciderAPI;
    }

    @Override
    public void load(HudWidgetCategory category) {
        super.load(category);
    }

    @Override
    public void render(Stack stack, float partialTicks) {
        CiderTrack track = ciderAPI.getCurrentTrack();

        if (track == null || !ciderAPI.isPlaying()) {
            // Don't render if no track is playing
            return;
        }

        // Build display text
        String text = String.format("♫ %s - %s",
            track.getArtistName(),
            track.getName()
        );

        // Render the text
        renderText(stack, text);
    }

    /**
     * Simple text rendering
     */
    private void renderText(Stack stack, String text) {
        Component component = Component.text(text, NamedTextColor.WHITE);

        // Create a simple text widget
        FlexibleContentWidget widget = new FlexibleContentWidget();
        ComponentWidget textWidget = ComponentWidget.component(component);
        widget.addContent(textWidget);

        // Render it
        widget.render(stack, 0, 0, 0, 0, partialTicks);
    }
}

package net.labymod.addons.cider.core.labymod.hudwidgets.elements.widgets;

import net.labymod.addons.cider.core.api.CiderAPI;
import net.labymod.addons.cider.core.api.CiderTrack;
import net.labymod.api.client.gui.lss.property.LssProperty;
import net.labymod.api.client.gui.lss.property.annotation.AutoWidget;
import net.labymod.api.client.gui.screen.ScreenContext;
import net.labymod.api.client.gui.screen.widget.SimpleWidget;
import net.labymod.api.client.gui.screen.widget.attributes.bounds.Bounds;
import net.labymod.api.util.bounds.Rectangle;
import net.labymod.api.util.math.MathHelper;

/**
 * Progress bar widget for Cider playback
 */
@AutoWidget
public class ProgressBarWidget extends SimpleWidget {
    private final CiderAPI ciderAPI;
    private final LssProperty<Integer> foregroundColor = new LssProperty<>(0x00FF00);

    public ProgressBarWidget(CiderAPI api) {
        this.ciderAPI = api;
    }

    @Override
    public void renderWidget(ScreenContext context) {
        super.renderWidget(context);

        CiderTrack track = ciderAPI.getCurrentTrack();
        if (track != null && ciderAPI.isPlaying()) {
            double currentTime = track.getCurrentTime();
            double duration = track.getDuration();

            if (duration > 0) {
                float progress = (float) (currentTime / duration);
                progress = MathHelper.clamp(progress, 0.0f, 1.0f);

                Bounds bounds = this.bounds();
                context.canvas().submitRect(
                    Rectangle.relative(
                        bounds.getLeft(),
                        bounds.getTop(),
                        bounds.getWidth() * progress,
                        bounds.getHeight()
                    ),
                    this.foregroundColor.get()
                );
            }
        }
    }

    public LssProperty<Integer> foregroundColor() {
        return this.foregroundColor;
    }
}

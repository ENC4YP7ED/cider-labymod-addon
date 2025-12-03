package net.labymod.addons.cider.core.labymod.hudwidgets;

import net.labymod.addons.cider.core.CiderAddon;
import net.labymod.addons.cider.core.api.CiderAPI;
import net.labymod.addons.cider.core.events.*;
import net.labymod.addons.cider.core.labymod.hudwidgets.elements.widgets.CiderWidget;
import net.labymod.api.client.gui.hud.hudwidget.HudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.widget.WidgetHudWidget;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.gui.screen.widget.widgets.hud.HudWidgetWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.event.Subscribe;
import net.labymod.api.util.ThreadSafe;
import net.labymod.api.util.bounds.area.RectangleAreaPosition;

/**
 * HUD Widget displaying current Cider/Apple Music track
 */
public class CiderHudWidget extends WidgetHudWidget<CiderHudWidget.CiderHudWidgetConfig> {
    public static final String TRACK_CHANGE_REASON = "track_change";
    public static final String PLAYBACK_CHANGE_REASON = "playback_change";
    public static final String COVER_VISIBILITY_REASON = "cover_visibility";
    public static final String CONNECT_REASON = "connect";

    private final CiderAddon addon;
    private final CiderAPI ciderAPI;
    private final Icon hudWidgetIcon;

    public CiderHudWidget(String id, Icon icon, CiderAddon addon, CiderAPI ciderAPI) {
        super(id, CiderHudWidgetConfig.class);
        this.addon = addon;
        this.ciderAPI = ciderAPI;
        this.hudWidgetIcon = icon;
    }

    @Override
    public void initializePreConfigured(CiderHudWidgetConfig config) {
        super.initializePreConfigured(config);
        config.setEnabled(true);
        config.setAreaIdentifier(RectangleAreaPosition.TOP_RIGHT);
        config.setX(-2.0f);
        config.setY(2.0f);
        config.setParentToTailOfChainIn(RectangleAreaPosition.TOP_RIGHT);
    }

    @Override
    public void load(CiderHudWidgetConfig config) {
        super.load(config);
        this.setIcon(this.hudWidgetIcon);
        config.showCover().addChangeListener((property, oldValue, newValue) ->
            ThreadSafe.executeOnRenderThread(() -> this.requestUpdate(COVER_VISIBILITY_REASON))
        );
    }

    @Override
    public void initialize(HudWidgetWidget widget) {
        super.initialize(widget);
        boolean editorContext = widget.accessor().isEditor();
        CiderWidget ciderWidget = new CiderWidget(this, editorContext);
        widget.addChild(ciderWidget);
        widget.addId("cider");
    }

    @Override
    public boolean isVisibleInGame() {
        return this.ciderAPI.isInitialized() && this.ciderAPI.getCurrentTrack() != null;
    }

    @Subscribe
    public void onCiderConnect(CiderConnectEvent event) {
        ThreadSafe.executeOnRenderThread(() -> {
            if (!this.isEnabled()) {
                return;
            }
            this.requestUpdate(CONNECT_REASON);
        });
    }

    @Subscribe
    public void onCiderDisconnect(CiderDisconnectEvent event) {
        ThreadSafe.executeOnRenderThread(() -> {
            if (!this.isEnabled()) {
                return;
            }
            this.requestUpdate(CONNECT_REASON);
        });
    }

    @Subscribe
    public void onCiderTrackChanged(CiderTrackChangedEvent event) {
        ThreadSafe.executeOnRenderThread(() -> {
            if (!this.isEnabled()) {
                return;
            }
            this.requestUpdate(TRACK_CHANGE_REASON);
        });
    }

    @Subscribe
    public void onCiderPlaybackChanged(CiderPlaybackChangedEvent event) {
        ThreadSafe.executeOnRenderThread(() -> {
            if (!this.isEnabled()) {
                return;
            }
            this.requestUpdate(PLAYBACK_CHANGE_REASON);
        });
    }

    public CiderAPI ciderAPI() {
        return this.ciderAPI;
    }

    public CiderAddon addon() {
        return this.addon;
    }

    public static class CiderHudWidgetConfig extends HudWidgetConfig {
        @SwitchSetting
        private final ConfigProperty<Boolean> showCover = ConfigProperty.create(true);

        @SwitchSetting
        private final ConfigProperty<Boolean> minimizeIngame = ConfigProperty.create(true);

        public ConfigProperty<Boolean> showCover() {
            return this.showCover;
        }

        public ConfigProperty<Boolean> minimizeIngame() {
            return this.minimizeIngame;
        }
    }
}

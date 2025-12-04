package net.labymod.addons.cider.core;

import net.labymod.addons.cider.core.api.CiderAPI;
import net.labymod.addons.cider.core.api.CiderAPIFactory;
import net.labymod.addons.cider.core.api.CiderListener;
import net.labymod.addons.cider.core.api.CiderPlaybackController;
import net.labymod.addons.cider.core.api.CiderTrack;
import net.labymod.addons.cider.core.events.*;
import net.labymod.addons.cider.core.interaction.ShareTrackBulletPoint;
import net.labymod.addons.cider.core.labymod.hudwidgets.CiderHudWidget;
import net.labymod.addons.cider.core.labymod.hudwidgets.CiderTextHudWidget;
import net.labymod.addons.cider.core.networking.CiderNetworkHandler;
import net.labymod.addons.cider.core.sharing.TrackSharingManager;
import net.labymod.api.Laby;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.client.gui.hud.HudWidgetRegistry;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.models.addon.annotation.AddonMain;
import net.labymod.api.revision.SimpleRevision;
import net.labymod.api.util.version.SemanticVersion;

/**
 * Main addon class for Cider integration
 */
@AddonMain
public class CiderAddon extends LabyAddon<CiderConfiguration> {

    private static CiderAddon instance;
    private final CiderAPI ciderAPI;
    private final CiderPlaybackController playbackController;
    private final TrackSharingManager trackSharingManager;
    private final CiderNetworkHandler networkHandler;
    private final Icon hudIcon;

    public CiderAddon() {
        instance = this;
        this.ciderAPI = CiderAPIFactory.create();
        this.playbackController = new CiderPlaybackController(null);
        this.trackSharingManager = new TrackSharingManager(this);
        this.networkHandler = new CiderNetworkHandler(this);
        this.hudIcon = Textures.HudWidget.CIDER_32;
    }

    @Override
    protected void preConfigurationLoad() {
        // Register config migration revisions
        Laby.references().revisionRegistry().register(
            new SimpleRevision("cider", new SemanticVersion("1.0.0"), "2024-12-04")
        );
    }

    @Override
    protected void enable() {
        this.registerSettingCategory();

        // Register event listener
        this.ciderAPI.registerListener(new CiderListener() {
            @Override
            public void onTrackChanged(CiderTrack oldTrack, CiderTrack newTrack) {
                labyAPI().eventBus().fire(new CiderTrackChangedEvent(oldTrack, newTrack));
            }

            @Override
            public void onPlaybackChanged(boolean isPlaying) {
                labyAPI().eventBus().fire(new CiderPlaybackChangedEvent(isPlaying));
            }

            @Override
            public void onPositionChanged(double currentTime, double duration) {
                labyAPI().eventBus().fire(new CiderPositionChangedEvent(currentTime, duration));
            }

            @Override
            public void onDisconnect() {
                labyAPI().eventBus().fire(new CiderDisconnectEvent());
            }
        });

        // Initialize Cider API
        initializeCider();

        // Initialize networking
        networkHandler.initialize();

        // Register HUD widgets
        HudWidgetRegistry registry = this.labyAPI().hudWidgetRegistry();
        registry.register(new CiderHudWidget("cider", this.hudIcon, this, this.ciderAPI));
        registry.register(new CiderTextHudWidget("cider-text", this.hudIcon, this.ciderAPI));

        // Register interaction menu bullet points
        this.labyAPI().interactionMenuRegistry().register(new ShareTrackBulletPoint(this));
    }

    @Override
    protected void disable() {
        if (ciderAPI.isInitialized()) {
            ciderAPI.stop();
        }
        networkHandler.shutdown();
    }

    /**
     * Initialize Cider API
     */
    public void initializeCider() {
        if (ciderAPI.isInitialized()) {
            ciderAPI.stop();
        }

        if (!configuration().enabled().get()) {
            return;
        }

        // Update API settings from configuration
        String apiUrl = configuration().apiUrl().get();
        boolean requireToken = configuration().requireApiToken().get();
        String token = requireToken ? configuration().appToken().get() : null;

        ciderAPI.updateSettings(apiUrl, token, requireToken);
        playbackController.updateSettings(apiUrl, token, requireToken);
        ciderAPI.initialize();
        labyAPI().eventBus().fire(new CiderConnectEvent());
    }

    /**
     * Disconnect from Cider
     */
    public void disconnect() {
        if (ciderAPI.isInitialized()) {
            ciderAPI.stop();
        }
    }

    @Override
    protected Class<CiderConfiguration> configurationClass() {
        return CiderConfiguration.class;
    }

    public CiderAPI getCiderAPI() {
        return ciderAPI;
    }

    public CiderPlaybackController getPlaybackController() {
        return playbackController;
    }

    public TrackSharingManager getTrackSharingManager() {
        return trackSharingManager;
    }

    public CiderNetworkHandler getNetworkHandler() {
        return networkHandler;
    }

    public static CiderAddon get() {
        return instance;
    }
}

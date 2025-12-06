package net.labymod.addons.cider.core;

import net.labymod.addons.cider.core.ui.TrackHistoryActivity;
import net.labymod.api.addon.AddonConfig;
import net.labymod.api.client.gui.screen.activity.Activity;
import net.labymod.api.client.gui.screen.widget.widgets.activity.settings.ActivitySetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget.TextFieldSetting;
import net.labymod.api.configuration.loader.annotation.ConfigName;
import net.labymod.api.configuration.loader.annotation.ShowSettingIf;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.annotation.SettingSection;
import net.labymod.api.configuration.loader.annotation.IntroducedIn;
import net.labymod.api.configuration.loader.annotation.MethodOrder;

/**
 * Configuration for the Cider addon
 */
@ConfigName("settings")
public class CiderConfiguration extends AddonConfig {

    // General Settings
    @SwitchSetting
    @ConfigName("enabled")
    private final ConfigProperty<Boolean> enabled = new ConfigProperty<Boolean>(true).addChangeListener((property, prevValue, newValue) -> {
        CiderAddon addon = CiderAddon.get();
        if (addon == null) {
            return;
        }
        if (newValue) {
            addon.initializeCider();
        } else {
            addon.disconnect();
        }
    });

    @TextFieldSetting
    @ConfigName("apiUrl")
    private final ConfigProperty<String> apiUrl = new ConfigProperty<>("http://localhost:10767");

    @SwitchSetting
    @ConfigName("requireApiToken")
    private final ConfigProperty<Boolean> requireApiToken = new ConfigProperty<>(false);

    @TextFieldSetting
    @ConfigName("appToken")
    @ShowSettingIf(value = "requireApiToken", equals = "true")
    private final ConfigProperty<String> appToken = new ConfigProperty<>("");

    // Display Settings
    @SettingSection("display")
    @SwitchSetting
    @ConfigName("showArtwork")
    private final ConfigProperty<Boolean> showArtwork = new ConfigProperty<>(true);

    @SwitchSetting
    @ConfigName("showProgressBar")
    private final ConfigProperty<Boolean> showProgressBar = new ConfigProperty<>(true);

    @SwitchSetting
    @ConfigName("showControls")
    private final ConfigProperty<Boolean> showControls = new ConfigProperty<>(true);

    @SwitchSetting
    @ConfigName("showOnTitleScreen")
    private final ConfigProperty<Boolean> showOnTitleScreen = new ConfigProperty<>(true);

    @SwitchSetting
    @ConfigName("showActionBar")
    private final ConfigProperty<Boolean> showActionBar = new ConfigProperty<>(false);

    // Integration Settings
    @SettingSection("integration")
    @SwitchSetting
    @ConfigName("showTrackInChat")
    private final ConfigProperty<Boolean> showTrackInChat = new ConfigProperty<>(true);

    @SwitchSetting
    @ConfigName("showPauseScreenControls")
    private final ConfigProperty<Boolean> showPauseScreenControls = new ConfigProperty<>(true);

    @SwitchSetting
    @ConfigName("showMusicNotes")
    private final ConfigProperty<Boolean> showMusicNotes = new ConfigProperty<>(false);

    // Performance Settings
    @SettingSection("performance")
    @SwitchSetting
    @ConfigName("optimizeRendering")
    private final ConfigProperty<Boolean> optimizeRendering = new ConfigProperty<>(true);

    // Sharing Settings
    @SettingSection("sharing")
    @SwitchSetting
    @ConfigName("enableTrackSharing")
    private final ConfigProperty<Boolean> enableTrackSharing = new ConfigProperty<>(false);

    @MethodOrder(after = "enableTrackSharing")
    @ActivitySetting
    @ConfigName("trackHistory")
    @IntroducedIn(namespace = "cider", value = "1.0.0")
    public Activity trackHistory() {
        return new TrackHistoryActivity(CiderAddon.get());
    }

    public ConfigProperty<Boolean> enabled() {
        return enabled;
    }

    public ConfigProperty<String> apiUrl() {
        return apiUrl;
    }

    public ConfigProperty<Boolean> requireApiToken() {
        return requireApiToken;
    }

    public ConfigProperty<String> appToken() {
        return appToken;
    }

    public ConfigProperty<Boolean> showArtwork() {
        return showArtwork;
    }

    public ConfigProperty<Boolean> showProgressBar() {
        return showProgressBar;
    }

    public ConfigProperty<Boolean> enableTrackSharing() {
        return enableTrackSharing;
    }

    public ConfigProperty<Boolean> showControls() {
        return showControls;
    }

    public ConfigProperty<Boolean> showOnTitleScreen() {
        return showOnTitleScreen;
    }

    public ConfigProperty<Boolean> showActionBar() {
        return showActionBar;
    }

    public ConfigProperty<Boolean> showTrackInChat() {
        return showTrackInChat;
    }

    public ConfigProperty<Boolean> showPauseScreenControls() {
        return showPauseScreenControls;
    }

    public ConfigProperty<Boolean> showMusicNotes() {
        return showMusicNotes;
    }

    public ConfigProperty<Boolean> optimizeRendering() {
        return optimizeRendering;
    }

    @Override
    public int getConfigVersion() {
        return 2;
    }
}

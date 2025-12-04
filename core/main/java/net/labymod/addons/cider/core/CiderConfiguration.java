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
    private final ConfigProperty<String> apiUrl = new ConfigProperty<>("http://localhost:10767");

    @SwitchSetting
    private final ConfigProperty<Boolean> requireApiToken = new ConfigProperty<>(false);

    @TextFieldSetting
    @ShowSettingIf(value = "requireApiToken", equals = "true")
    private final ConfigProperty<String> appToken = new ConfigProperty<>("");

    // Display Settings
    @SettingSection("display")
    @SwitchSetting
    private final ConfigProperty<Boolean> showArtwork = new ConfigProperty<>(true);

    @SwitchSetting
    private final ConfigProperty<Boolean> showProgressBar = new ConfigProperty<>(true);

    @SwitchSetting
    private final ConfigProperty<Boolean> showControls = new ConfigProperty<>(true);

    // Sharing Settings
    @SettingSection("sharing")
    @SwitchSetting
    private final ConfigProperty<Boolean> enableTrackSharing = new ConfigProperty<>(false);

    @MethodOrder(after = "enableTrackSharing")
    @ActivitySetting
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

    @Override
    public int getConfigVersion() {
        return 1;
    }
}

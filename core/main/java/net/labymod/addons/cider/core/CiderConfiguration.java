package net.labymod.addons.cider.core;

import net.labymod.api.addon.AddonConfig;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget.TextFieldSetting;
import net.labymod.api.configuration.loader.annotation.ConfigName;
import net.labymod.api.configuration.loader.annotation.ShowSettingIf;
import net.labymod.api.configuration.loader.property.ConfigProperty;

/**
 * Configuration for the Cider addon
 */
@ConfigName("cider")
public class CiderConfiguration extends AddonConfig {

    @SwitchSetting
    private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

    @TextFieldSetting
    private final ConfigProperty<String> apiUrl = new ConfigProperty<>("http://localhost:10767");

    @SwitchSetting
    private final ConfigProperty<Boolean> requireApiToken = new ConfigProperty<>(false);

    @TextFieldSetting
    @ShowSettingIf(value = "requireApiToken", equals = "true")
    private final ConfigProperty<String> appToken = new ConfigProperty<>("");

    @SwitchSetting
    private final ConfigProperty<Boolean> showArtwork = new ConfigProperty<>(true);

    @SwitchSetting
    private final ConfigProperty<Boolean> showProgressBar = new ConfigProperty<>(true);

    @SwitchSetting
    private final ConfigProperty<Boolean> enableTrackSharing = new ConfigProperty<>(false);

    @SwitchSetting
    private final ConfigProperty<Boolean> showControls = new ConfigProperty<>(true);

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
}

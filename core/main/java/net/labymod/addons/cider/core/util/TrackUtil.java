package net.labymod.addons.cider.core.util;

import net.labymod.addons.cider.core.Textures;
import net.labymod.addons.cider.core.api.CiderTrack;
import net.labymod.api.Laby;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.resources.CompletableResourceLocation;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.client.resources.texture.TextureDetails;
import net.labymod.api.client.resources.texture.TextureRepository;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Utility for creating track icons from artwork URLs
 */
public class TrackUtil {
    private static final Cache<Icon> ICON_CACHE = new Cache<>(1800000L, icon -> {
        ResourceLocation resourceLocation = icon.getResourceLocation();
        if (Objects.equals(resourceLocation, Textures.UNKNOWN_COVER.resource())) {
            return;
        }
        Laby.references().textureRepository().queueTextureRelease(resourceLocation);
    });

    public static synchronized Icon createIcon(CiderTrack track) {
        if (track == null || track.getArtworkUrl() == null || track.getArtworkUrl().isEmpty()) {
            return Icon.texture(Textures.UNKNOWN_COVER);
        }

        String trackId = track.getId();
        Icon cachedIcon = ICON_CACHE.get(trackId);
        if (cachedIcon != null) {
            return cachedIcon;
        }

        CompletableResourceLocation completable = new CompletableResourceLocation(Textures.UNKNOWN_COVER);
        ResourceLocation resourceLocation = getResourceLocationForTrackId(trackId);

        registerTrackImage(track.getArtworkUrl(), resourceLocation, res -> {
            completable.executeCompletableListeners(res);
        });

        Icon icon = Icon.completable(completable);
        ICON_CACHE.push(trackId, icon);
        return icon;
    }

    private static ResourceLocation getResourceLocationForTrackId(String trackId) {
        return Laby.references().resources().resourceLocationFactory()
            .create("cider", "track/" + trackId.toLowerCase(Locale.ENGLISH).replaceAll("[^a-z0-9-]", "_"));
    }

    private static synchronized void registerTrackImage(String artworkUrl, ResourceLocation resourceLocation, Consumer<ResourceLocation> callback) {
        if (artworkUrl == null || artworkUrl.isEmpty()) {
            return;
        }

        TextureDetails details = TextureDetails.builder(resourceLocation)
            .withUrl(artworkUrl)
            .withFinishHandler(texture -> callback.accept(resourceLocation))
            .build();

        TextureRepository textureRepository = Laby.references().textureRepository();
        textureRepository.getOrRegisterTexture(details);
    }
}

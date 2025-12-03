package net.labymod.addons.cider.core.api;

/**
 * Represents a track from Cider/Apple Music
 */
public class CiderTrack {

    private final String name;
    private final String artistName;
    private final String albumName;
    private final String artworkUrl;
    private final double duration;
    private final double currentTime;
    private final String genre;

    public CiderTrack(String name, String artistName, String albumName,
                     String artworkUrl, double duration, double currentTime, String genre) {
        this.name = name;
        this.artistName = artistName;
        this.albumName = albumName;
        this.artworkUrl = artworkUrl;
        this.duration = duration;
        this.currentTime = currentTime;
        this.genre = genre;
    }

    /**
     * Get unique identifier for this track
     */
    public String getId() {
        return name + "|" + artistName;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public String getArtworkUrl() {
        return artworkUrl;
    }

    public double getDuration() {
        return duration;
    }

    public double getCurrentTime() {
        return currentTime;
    }

    public String getGenre() {
        return genre;
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%s)", artistName, name, albumName);
    }
}

package net.labymod.addons.cider.core.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Main API class for interacting with Cider RPC
 */
public class CiderAPI {

    private static final String DEFAULT_API_URL = "http://localhost:10767";
    private static final int POLL_INTERVAL_MS = 1000;

    private String apiUrl;
    private String appToken;
    private boolean requireToken;
    private final Gson gson;
    private final List<CiderListener> listeners;
    private ScheduledExecutorService executor;

    private CiderTrack currentTrack;
    private boolean isPlaying;
    private boolean isInitialized;
    private String lastTrackId;

    public CiderAPI(String apiUrl, String appToken) {
        this.apiUrl = apiUrl != null ? apiUrl : DEFAULT_API_URL;
        this.appToken = appToken;
        this.requireToken = false;
        this.gson = new Gson();
        this.listeners = new ArrayList<>();
        this.isInitialized = false;
    }

    /**
     * Update API settings (URL, token, and whether token is required)
     */
    public void updateSettings(String apiUrl, String appToken, boolean requireToken) {
        this.apiUrl = apiUrl != null ? apiUrl : DEFAULT_API_URL;
        this.appToken = appToken;
        this.requireToken = requireToken;
    }

    /**
     * Initialize the API and start polling
     */
    public void initialize() {
        if (isInitialized) {
            return;
        }

        isInitialized = true;
        executor = Executors.newSingleThreadScheduledExecutor();

        // Start polling for track changes
        executor.scheduleAtFixedRate(this::poll, 0, POLL_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }

    /**
     * Stop the API and cleanup resources
     */
    public void stop() {
        if (!isInitialized) {
            return;
        }

        isInitialized = false;
        if (executor != null) {
            executor.shutdown();
            try {
                executor.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }

        notifyDisconnect();
    }

    /**
     * Poll for current track information
     */
    private void poll() {
        try {
            // Check if Cider is active
            if (!isActive()) {
                handleDisconnect();
                return;
            }

            // Get current playing state
            boolean wasPlaying = isPlaying;
            isPlaying = getIsPlaying();

            // Get current track
            CiderTrack track = getNowPlaying();

            if (track == null) {
                handleDisconnect();
                return;
            }

            // Check for track change
            String trackId = track.getId();
            if (!trackId.equals(lastTrackId)) {
                CiderTrack oldTrack = currentTrack;
                currentTrack = track;
                lastTrackId = trackId;
                notifyTrackChanged(oldTrack, currentTrack);
            } else {
                currentTrack = track;
            }

            // Check for playback state change
            if (wasPlaying != isPlaying) {
                notifyPlaybackChanged(isPlaying);
            }

            // Notify position update
            notifyPositionChanged(track.getCurrentTime(), track.getDuration());

        } catch (Exception e) {
            System.err.println("Error polling Cider API: " + e.getMessage());
        }
    }

    /**
     * Check if Cider RPC is active
     */
    private boolean isActive() {
        try {
            URL url = new URL(apiUrl + "/api/v1/playback/active");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Only send token if required and available
            if (requireToken && appToken != null && !appToken.isEmpty()) {
                conn.setRequestProperty("apptoken", appToken);
            }

            conn.setConnectTimeout(2000);
            conn.setReadTimeout(2000);

            int responseCode = conn.getResponseCode();
            conn.disconnect();

            return responseCode == 204 || responseCode == 200;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get current playing state
     */
    private boolean getIsPlaying() {
        try {
            URL url = new URL(apiUrl + "/api/v1/playback/is-playing");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Only send token if required and available
            if (requireToken && appToken != null && !appToken.isEmpty()) {
                conn.setRequestProperty("apptoken", appToken);
            }

            conn.setConnectTimeout(2000);
            conn.setReadTimeout(2000);

            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String response = reader.readLine();
                reader.close();
                conn.disconnect();

                return Boolean.parseBoolean(response);
            }

            conn.disconnect();
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get currently playing track information
     */
    private CiderTrack getNowPlaying() {
        try {
            URL url = new URL(apiUrl + "/api/v1/playback/now-playing");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Only send token if required and available
            if (requireToken && appToken != null && !appToken.isEmpty()) {
                conn.setRequestProperty("apptoken", appToken);
            }

            conn.setConnectTimeout(2000);
            conn.setReadTimeout(2000);

            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                conn.disconnect();

                JsonObject json = gson.fromJson(response.toString(), JsonObject.class);
                JsonObject info = json.getAsJsonObject("info");
                JsonObject artwork = info.has("artwork") ? info.getAsJsonObject("artwork") : null;

                return new CiderTrack(
                    info.get("name").getAsString(),
                    info.get("artistName").getAsString(),
                    info.get("albumName").getAsString(),
                    artwork != null ? artwork.get("url").getAsString() : null,
                    info.get("durationInMillis").getAsLong() / 1000.0,
                    info.has("currentPlaybackTime") ? info.get("currentPlaybackTime").getAsDouble() : 0.0,
                    info.has("genreNames") && info.getAsJsonArray("genreNames").size() > 0
                        ? info.getAsJsonArray("genreNames").get(0).getAsString()
                        : "Unknown"
                );
            }

            conn.disconnect();
            return null;
        } catch (Exception e) {
            System.err.println("Error getting now playing: " + e.getMessage());
            return null;
        }
    }

    /**
     * Handle disconnect event
     */
    private void handleDisconnect() {
        if (currentTrack != null) {
            currentTrack = null;
            lastTrackId = null;
            isPlaying = false;
            notifyDisconnect();
        }
    }

    /**
     * Register a listener for Cider events
     */
    public void registerListener(CiderListener listener) {
        listeners.add(listener);
    }

    /**
     * Unregister a listener
     */
    public void unregisterListener(CiderListener listener) {
        listeners.remove(listener);
    }

    // Notification methods
    private void notifyTrackChanged(CiderTrack oldTrack, CiderTrack newTrack) {
        for (CiderListener listener : listeners) {
            listener.onTrackChanged(oldTrack, newTrack);
        }
    }

    private void notifyPlaybackChanged(boolean playing) {
        for (CiderListener listener : listeners) {
            listener.onPlaybackChanged(playing);
        }
    }

    private void notifyPositionChanged(double currentTime, double duration) {
        for (CiderListener listener : listeners) {
            listener.onPositionChanged(currentTime, duration);
        }
    }

    private void notifyDisconnect() {
        for (CiderListener listener : listeners) {
            listener.onDisconnect();
        }
    }

    // Getters
    public CiderTrack getCurrentTrack() {
        return currentTrack;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public boolean isInitialized() {
        return isInitialized;
    }
}

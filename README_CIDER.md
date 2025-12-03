# Cider LabyMod Addon

A LabyMod addon that integrates Cider (Apple Music client) with Minecraft, displaying your currently playing tracks in-game.

## Features

- **Real-time Track Display**: Shows currently playing track from Cider/Apple Music
- **HUD Widget**: Customizable on-screen display of track information
- **Auto-reconnect**: Automatically reconnects to Cider when it becomes available
- **State Management**: Tracks playing, paused, and stopped states
- **Event System**: Fires events for track changes, playback changes, and position updates

## How It Works

This addon communicates with Cider's RPC (Rich Presence Client) API running on `localhost:10767` to fetch real-time playback information.

### Architecture

The addon consists of several key components:

1. **CiderAPI** (`CiderAPI.java`): Main API client that polls Cider's RPC endpoints
   - Polls every 1 second for track changes
   - Manages connection state
   - Fires events to listeners

2. **CiderTrack** (`CiderTrack.java`): Data model representing a track
   - Contains track metadata (title, artist, album, artwork, etc.)
   - Provides unique track identifiers

3. **CiderListener** (`CiderListener.java`): Interface for event callbacks
   - `onTrackChanged()` - Fired when track changes
   - `onPlaybackChanged()` - Fired when play/pause state changes
   - `onPositionChanged()` - Fired for playback position updates
   - `onDisconnect()` - Fired when Cider disconnects

4. **CiderAddon** (`CiderAddon.java`): Main addon entry point
   - Registers with LabyMod
   - Initializes API and widgets
   - Manages addon lifecycle

5. **CiderHudWidget** (`CiderHudWidget.java`): HUD display component
   - Renders track information on screen
   - Configurable visibility and position

### Events

The addon fires LabyMod events that can be listened to:

- `CiderConnectEvent` - Cider connection established
- `CiderDisconnectEvent` - Cider disconnected
- `CiderTrackChangedEvent` - Track changed
- `CiderPlaybackChangedEvent` - Playback state changed (play/pause)
- `CiderPositionChangedEvent` - Playback position updated

## Configuration

The addon provides several configuration options:

- **Enabled**: Toggle addon on/off
- **API URL**: Cider RPC endpoint (default: `http://localhost:10767`)
- **App Token**: Optional authentication token for Cider API
- **Show Artwork**: Display album artwork
- **Show Progress Bar**: Display playback progress bar

## Building

### Prerequisites

- Java 17 or higher
- Gradle 7.0 or higher

### Build Commands

```bash
# Build the addon
./gradlew build

# The compiled JAR will be in build/libs/
```

## Installation

1. Build the addon or download the pre-compiled JAR
2. Place the JAR file in your LabyMod addons folder:
   - Windows: `%APPDATA%\.minecraft\labymod-neo\addons\`
   - Linux: `~/.minecraft/labymod-neo/addons/`
   - macOS: `~/Library/Application Support/minecraft/labymod-neo/addons/`
3. Start Minecraft with LabyMod
4. Make sure Cider is running with RPC enabled
5. The addon should automatically connect

## Cider RPC API

The addon uses the following Cider RPC endpoints:

- `GET /api/v1/playback/active` - Check if Cider is running (returns 204)
- `GET /api/v1/playback/is-playing` - Get playback state (boolean)
- `GET /api/v1/playback/now-playing` - Get current track information

### Response Format

The `/api/v1/playback/now-playing` endpoint returns:

```json
{
  "info": {
    "name": "Track Title",
    "artistName": "Artist Name",
    "albumName": "Album Name",
    "artwork": {
      "url": "https://..."
    },
    "durationInMillis": 240000,
    "currentPlaybackTime": 60.5,
    "genreNames": ["Pop"]
  }
}
```

## Comparison with Spotify Addon

This addon was created by adapting the LabyMod Spotify addon for Cider:

| Feature | Spotify Addon | Cider Addon |
|---------|--------------|-------------|
| Music Service | Spotify | Apple Music (via Cider) |
| API Type | Native Spotify API | HTTP RPC API |
| Authentication | OAuth | App Token (optional) |
| Polling | Via java-spotify-api | Custom HTTP client |
| Track Sharing | Yes | Not yet implemented |
| Nametag Display | Yes | Not yet implemented |

## Troubleshooting

### Addon doesn't connect to Cider

1. Make sure Cider is running
2. Check that RPC is enabled in Cider settings
3. Verify Cider is listening on port 10767
4. Check addon configuration for correct API URL

### No track information displayed

1. Make sure a track is actually playing in Cider
2. Check addon is enabled in LabyMod settings
3. Verify HUD widget is enabled and visible
4. Check Minecraft logs for errors

## Development

### Project Structure

```
src/main/java/net/labymod/addons/cider/core/
├── CiderAddon.java              # Main addon class
├── CiderConfiguration.java       # Configuration
├── api/
│   ├── CiderAPI.java            # API client
│   ├── CiderAPIFactory.java     # Factory
│   ├── CiderListener.java       # Event listener interface
│   └── CiderTrack.java          # Track model
├── events/
│   ├── CiderConnectEvent.java
│   ├── CiderDisconnectEvent.java
│   ├── CiderPlaybackChangedEvent.java
│   ├── CiderPositionChangedEvent.java
│   └── CiderTrackChangedEvent.java
└── labymod/
    └── hudwidgets/
        └── CiderHudWidget.java  # HUD widget
```

### Future Enhancements

- Track sharing between players
- Nametag display for shared tracks
- Progress bar widget
- Album artwork display
- Lyrics integration
- Queue management
- Playback controls (play/pause/skip)

## Credits

- Based on the LabyMod Spotify addon structure
- Uses Cider RPC API
- Built for LabyMod 4 (Neo)

## License

This addon is provided as-is for educational and personal use.

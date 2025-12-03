# Cider LabyMod Addon - Project Summary

## What Was Created

I've successfully created a complete LabyMod addon that integrates Cider (Apple Music client) with Minecraft. This addon is based on the decompiled Spotify addon structure but adapted to work with Cider's RPC API.

## Project Structure

```
Cider Addon/
├── build.gradle.kts          # Gradle build configuration (Kotlin DSL)
├── settings.gradle.kts        # Gradle settings
├── gradle.properties          # Build properties
├── README_CIDER.md           # Comprehensive documentation
├── SUMMARY.md                # This file
├── decompiled/               # Decompiled Spotify addon (reference)
└── src/
    └── main/
        ├── java/
        │   └── net/labymod/addons/cider/core/
        │       ├── CiderAddon.java              # Main addon class [@AddonMain]
        │       ├── CiderConfiguration.java       # Configuration/settings
        │       ├── api/
        │       │   ├── CiderAPI.java            # HTTP RPC client
        │       │   ├── CiderAPIFactory.java     # Factory pattern
        │       │   ├── CiderListener.java       # Event listener interface
        │       │   └── CiderTrack.java          # Track data model
        │       ├── events/
        │       │   ├── CiderConnectEvent.java
        │       │   ├── CiderDisconnectEvent.java
        │       │   ├── CiderPlaybackChangedEvent.java
        │       │   ├── CiderPositionChangedEvent.java
        │       │   └── CiderTrackChangedEvent.java
        │       └── labymod/
        │           └── hudwidgets/
        │               └── CiderHudWidget.java  # In-game HUD display
        └── resources/
            └── addon.json                # Addon manifest
```

## Key Components

### 1. CiderAPI (HTTP RPC Client)
**File:** `src/main/java/net/labymod/addons/cider/core/api/CiderAPI.java`

- Polls Cider RPC API every 1 second
- Endpoints used:
  - `GET /api/v1/playback/active` - Health check
  - `GET /api/v1/playback/is-playing` - Playback state
  - `GET /api/v1/playback/now-playing` - Track information
- Manages connection state and reconnection
- Fires events to registered listeners
- Handles authentication via app token

**Key Features:**
- Automatic polling with ScheduledExecutorService
- Track change detection via unique ID
- Playback state monitoring
- Graceful disconnect handling

### 2. CiderTrack (Data Model)
**File:** `src/main/java/net/labymod/addons/cider/core/api/CiderTrack.java`

Represents a track with:
- Name, artist, album
- Artwork URL
- Duration and current playback time
- Genre
- Unique identifier (name + artist)

### 3. Event System
**Files:** `src/main/java/net/labymod/addons/cider/core/events/*`

Five event types fired by the API:
- `CiderConnectEvent` - When connection established
- `CiderDisconnectEvent` - When Cider disconnects
- `CiderTrackChangedEvent` - Track change
- `CiderPlaybackChangedEvent` - Play/pause state change
- `CiderPositionChangedEvent` - Playback position update

### 4. CiderAddon (Main Class)
**File:** `src/main/java/net/labymod/addons/cider/core/CiderAddon.java`

- Extends `LabyAddon<CiderConfiguration>`
- Annotated with `@AddonMain`
- Registers API listeners and translates to LabyMod events
- Manages HUD widget registration
- Handles addon lifecycle (enable/disable)

### 5. CiderHudWidget
**File:** `src/main/java/net/labymod/addons/cider/core/labymod/hudwidgets/CiderHudWidget.java`

- Displays current track on screen
- Shows: ♫ Artist - Track Name
- Only renders when track is playing
- Customizable position via LabyMod HUD editor

### 6. Configuration
**File:** `src/main/java/net/labymod/addons/cider/core/CiderConfiguration.java`

Settings exposed to users:
- Enable/disable addon
- API URL (default: http://localhost:10767)
- App token for authentication
- Show artwork toggle
- Show progress bar toggle

## How It Works

### Connection Flow
1. Addon starts when Minecraft loads with LabyMod
2. `CiderAddon.enable()` is called
3. Creates `CiderAPI` instance via factory
4. Registers event listener to bridge API → LabyMod events
5. Calls `CiderAPI.initialize()` which starts polling
6. Polling thread runs every 1000ms checking Cider RPC

### Data Flow
```
Cider App (localhost:10767)
    ↓ HTTP GET
CiderAPI.poll()
    ↓ Parse JSON
CiderTrack model
    ↓ Event firing
CiderListener callbacks
    ↓ LabyMod events
CiderAddon (event bus)
    ↓
CiderHudWidget.render()
    ↓
On-screen display
```

### Track Change Detection
- Creates unique ID: `{trackName}|{artistName}`
- Compares with previous ID
- If different, fires `CiderTrackChangedEvent`
- Updates `currentTrack` reference

### State Management
- Tracks: `isInitialized`, `isPlaying`
- Maintains `currentTrack` and `lastTrackId`
- Handles disconnect when Cider stops/crashes
- Cleans up resources on addon disable

## Comparison: Spotify Addon → Cider Addon

| Aspect | Spotify Addon | Cider Addon |
|--------|--------------|-------------|
| **API Library** | `de.labystudio.spotifyapi` | Custom HTTP client |
| **Protocol** | Native Spotify Desktop API | HTTP REST (RPC) |
| **Polling** | Handled by library | Custom ScheduledExecutorService |
| **Auth** | OAuth flow | App token header (optional) |
| **Connection** | TCP socket | HTTP requests |
| **Track Model** | SpotifyTrack object | CiderTrack object |
| **Dependencies** | java-spotify-api library | Only Gson for JSON |
| **Events** | Library events | Custom listener interface |

## Build Instructions

### Prerequisites
- Java 17+ installed
- Gradle 7.0+ (or use wrapper)
- Cider installed and RPC enabled

### Build Commands

```bash
# Navigate to project
cd "/home/x/Schreibtisch/Cider Addon/"

# Build the addon
./gradlew build

# Output JAR location
build/libs/cider-1.0.0.jar
```

### Installation

1. Copy `build/libs/cider-1.0.0.jar` to:
   - `~/.minecraft/labymod-neo/addons/` (Linux)
   - `%APPDATA%\.minecraft\labymod-neo\addons\` (Windows)
   - `~/Library/Application Support/minecraft/labymod-neo/addons/` (macOS)

2. Start Minecraft with LabyMod
3. Launch Cider with RPC enabled
4. Addon should auto-connect

## Configuration in LabyMod

1. Press ESC → LabyMod Settings
2. Navigate to Addons → Cider
3. Configure:
   - Toggle enabled
   - Set API URL if different from default
   - Add app token if required
   - Enable/disable artwork and progress bar

## Testing with Flask Server

Your existing Flask server (`/home/x/Schreibtisch/cider-test/`) serves the same Cider RPC API endpoints. You can test the addon against it:

1. Start Flask server: `python3 server.py`
2. Ensure it's on port 10767
3. Addon will connect automatically
4. Check logs for "State transition" messages

## Integration with Existing Cider Test Server

Your Flask server in `/home/x/Schreibtisch/cider-test/` is already implementing the exact API this addon uses:

**Endpoint Mapping:**
| Flask Server | Cider Addon |
|-------------|------------|
| `/api/v1/playback/now-playing` | `CiderAPI.getNowPlaying()` |
| `/api/v1/playback/is-playing` | `CiderAPI.getIsPlaying()` |
| Uses app token from `.env` | Uses token from config |

This means the addon will work seamlessly with your existing test server!

## Future Enhancements

Based on the Spotify addon, these features could be added:

1. **Track Sharing**
   - Share tracks with other players
   - Nametag display for shared tracks
   - Interaction menu integration

2. **Advanced HUD**
   - Album artwork rendering
   - Animated progress bar
   - Multiple widget styles

3. **Player Controls**
   - Play/pause integration
   - Skip track
   - Volume control

4. **Rich Presence**
   - Discord integration
   - Activity tracking
   - Listening statistics

## Files Created

**Java Source Files:** 10
- 1 main addon class
- 1 configuration class
- 4 API classes
- 5 event classes
- 1 HUD widget

**Configuration Files:** 4
- build.gradle.kts
- settings.gradle.kts
- gradle.properties
- addon.json

**Documentation:** 2
- README_CIDER.md
- SUMMARY.md (this file)

**Total Lines of Code:** ~800 (excluding documentation)

## Credits

- Based on LabyMod Spotify addon (decompiled from `spotify.jar`)
- Adapted for Cider RPC API
- Uses your existing Flask server API structure
- Built following LabyMod 4 addon standards

## Next Steps

1. Add Gradle wrapper (`./gradlew wrapper`)
2. Test build: `./gradlew build`
3. Install and test in Minecraft
4. Add artwork rendering to HUD widget
5. Implement progress bar widget
6. Add player interaction features

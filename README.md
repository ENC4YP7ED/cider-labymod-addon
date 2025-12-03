# Cider LabyMod Addon

> Display your Apple Music now playing from Cider in Minecraft

A LabyMod 4 addon that integrates [Cider](https://cider.sh) (Apple Music client) with Minecraft, showing your currently playing tracks in-game via HUD widgets.

![LabyMod](https://img.shields.io/badge/LabyMod-4-blue)
![Java](https://img.shields.io/badge/Java-17%2B-orange)
![Minecraft](https://img.shields.io/badge/Minecraft-1.20.1-green)

## Features

- 🎵 **Real-time Track Display** - Shows currently playing track from Cider/Apple Music
- 🎮 **Advanced HUD Widget** - Customizable on-screen display with album artwork
- 📊 **Animated Progress Bar** - Real-time playback position with time display
- 🎨 **Album Artwork** - Dynamic cover art loading with caching
- 🎛️ **Playback Controls** - UI buttons for play/pause/skip (note: requires system media keys)
- 👥 **Track Sharing** - Share your currently playing track with other players
- 🏷️ **Nametag Display** - See what other players are listening to above their names
- 🔄 **Auto-reconnect** - Automatically reconnects when Cider becomes available
- 🎯 **State Management** - Tracks playing, paused, and stopped states
- 🔐 **Optional Authentication** - Support for secured Cider RPC instances
- ⚡ **Event System** - Fires events for track changes, playback changes, and position updates
- 📐 **Flexible Layout** - Minimized/maximized states, left/right alignment

## Installation

### For Users

1. Download the latest `.jar` file from [Releases](../../releases)
2. Place it in your LabyMod addons folder:
   - **Windows:** `%APPDATA%\.minecraft\labymod-neo\addons\`
   - **Linux:** `~/.minecraft/labymod-neo/addons/`
   - **macOS:** `~/Library/Application Support/minecraft/labymod-neo/addons/`
3. Start Minecraft with LabyMod
4. Make sure Cider is running with RPC enabled (default port: 10767)

### For Developers

```bash
# Clone the repository
git clone https://github.com/YOUR_USERNAME/cider-labymod-addon.git
cd cider-labymod-addon

# Build the addon
./gradlew build

# The compiled JAR will be in build/libs/
```

## Configuration

Access addon settings in: **LabyMod Settings → Addons → Cider**

| Setting | Description | Default |
|---------|-------------|---------|
| **Enabled** | Toggle addon on/off | ✅ On |
| **API URL** | Cider RPC endpoint | `http://localhost:10767` |
| **Require API Token** | Enable authentication | ❌ Off |
| **App Token** | Authentication token (only visible when required) | Empty |
| **Show Artwork** | Display album artwork | ✅ On |
| **Show Progress Bar** | Display playback progress | ✅ On |
| **Enable Track Sharing** | Share your track with other players | ❌ Off |
| **Show Controls** | Display playback control buttons | ✅ On |

### Authentication

The addon supports both secured and unsecured Cider RPC instances:

- **Unsecured** (default): Leave "Require API Token" OFF
- **Secured**: Enable "Require API Token" and enter your token

## How It Works

The addon communicates with Cider's RPC API:

```
Cider App (localhost:10767)
    ↓ HTTP GET (every 1s)
CiderAPI.poll()
    ↓ Parse JSON
CiderTrack model
    ↓ Events
LabyMod EventBus
    ↓ Render
HUD Widget → On-screen display
```

### API Endpoints Used

- `GET /api/v1/playback/active` - Health check
- `GET /api/v1/playback/is-playing` - Playback state
- `GET /api/v1/playback/now-playing` - Track information

## Building from Source

### Prerequisites

- Java 17 or higher
- Gradle 7.0+ (or use the wrapper)
- Git

### Build Steps

```bash
# Clone the repository
git clone https://github.com/YOUR_USERNAME/cider-labymod-addon.git
cd cider-labymod-addon

# Build using Gradle wrapper
./gradlew build

# Output: build/libs/cider-1.0.0.jar
```

### Development

```bash
# Run tests
./gradlew test

# Generate sources
./gradlew generateSources

# Run in development
./gradlew runClient
```

## Project Structure

```
cider-labymod-addon/
├── src/main/java/net/labymod/addons/cider/core/
│   ├── CiderAddon.java              # Main addon entry point
│   ├── CiderConfiguration.java       # Settings/configuration
│   ├── api/
│   │   ├── CiderAPI.java            # HTTP RPC client
│   │   ├── CiderAPIFactory.java     # Factory pattern
│   │   ├── CiderListener.java       # Event listener interface
│   │   └── CiderTrack.java          # Track data model
│   ├── events/                      # Event classes
│   │   ├── CiderConnectEvent.java
│   │   ├── CiderDisconnectEvent.java
│   │   ├── CiderPlaybackChangedEvent.java
│   │   ├── CiderPositionChangedEvent.java
│   │   └── CiderTrackChangedEvent.java
│   └── labymod/hudwidgets/
│       └── CiderHudWidget.java      # In-game HUD display
├── src/main/resources/
│   └── addon.json                   # Addon manifest
├── build.gradle.kts                 # Build configuration
├── settings.gradle.kts              # Gradle settings
└── README.md                        # This file
```

## Compatibility

- **LabyMod:** 4.0+
- **Minecraft:** 1.20.1+ (Neo)
- **Java:** 17+
- **Cider:** v1.x, v2.x

## Troubleshooting

### Addon doesn't connect

1. ✅ Verify Cider is running
2. ✅ Check RPC is enabled in Cider settings
3. ✅ Confirm Cider is on port 10767
4. ✅ Check API URL in addon settings
5. ✅ Review Minecraft logs for errors

### No track displayed

1. ✅ Play a track in Cider
2. ✅ Verify addon is enabled in LabyMod
3. ✅ Check HUD widget is visible
4. ✅ Ensure track is actually playing (not paused)

### Authentication errors

1. ✅ Verify "Require API Token" matches your Cider setup
2. ✅ Check token is correctly entered
3. ✅ Confirm Cider RPC has authentication enabled

## Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Style

- Follow Java conventions
- Use meaningful variable names
- Add JavaDoc comments for public methods
- Keep methods focused and small

## Comparison with Spotify Addon

| Feature | Spotify Addon | Cider Addon |
|---------|--------------|-------------|
| Music Service | Spotify | Apple Music (via Cider) |
| API Type | Native Desktop API | HTTP RPC |
| Auth | OAuth | Optional token |
| Track Display | ✅ | ✅ |
| HUD Widget | ✅ | ✅ |
| Track Sharing | ✅ | ⏳ Planned |
| Nametag Display | ✅ | ⏳ Planned |

## Roadmap

- [x] Album artwork rendering in HUD
- [x] Animated progress bar widget
- [x] Track sharing between players (infrastructure ready)
- [x] Nametag display for shared tracks (infrastructure ready)
- [x] Playback controls UI (play/pause/skip)
- [ ] Full networking integration for track sharing
- [ ] Discord Rich Presence integration
- [ ] Lyrics display
- [ ] Playback control API (requires Cider RPC enhancement)

## Credits

- **Based on:** LabyMod Spotify addon
- **API:** [Cider RPC Documentation](https://v2.cider.sh/docs/other/rpc)
- **Built for:** LabyMod 4 (Neo)

## License

This project is provided as-is for educational and personal use.

## Links

- [Cider Official Website](https://cider.sh)
- [LabyMod](https://labymod.net)
- [LabyMod Addon Template](https://github.com/LabyMod/addon-template)

## Support

If you encounter issues or have questions:

1. Check the [Troubleshooting](#troubleshooting) section
2. Search [existing issues](../../issues)
3. Create a [new issue](../../issues/new) with:
   - Minecraft version
   - LabyMod version
   - Cider version
   - Error logs (if applicable)

---

Made with ❤️ for the Cider and Minecraft community

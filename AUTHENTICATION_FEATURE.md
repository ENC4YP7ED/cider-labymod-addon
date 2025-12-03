# Authentication Feature - Conditional Token Support

## Overview

The Cider addon now supports **conditional authentication** - the app token is only sent when explicitly required, allowing the addon to work with both secured and unsecured Cider RPC instances.

## Changes Made

### 1. Configuration UI (`CiderConfiguration.java`)

Added a new toggle setting **"Require API Token"** that controls whether authentication is needed:

```java
@SwitchSetting
private final ConfigProperty<Boolean> requireApiToken = new ConfigProperty<>(false);

@TextFieldSetting
@ShowSettingIf(value = "requireApiToken", equals = "true")
private final ConfigProperty<String> appToken = new ConfigProperty<>("");
```

**Key Features:**
- Default: `false` (no authentication required)
- Token field only appears when toggle is enabled
- Uses `@ShowSettingIf` annotation for conditional visibility

### 2. API Client (`CiderAPI.java`)

Updated to conditionally send the authentication token:

**Added:**
- `requireToken` boolean field
- `updateSettings()` method to update API configuration at runtime

**Modified HTTP Requests:**
All API calls now check `requireToken` before adding the header:

```java
// Only send token if required and available
if (requireToken && appToken != null && !appToken.isEmpty()) {
    conn.setRequestProperty("apptoken", appToken);
}
```

This applies to:
- `isActive()` - Health check endpoint
- `getIsPlaying()` - Playback state endpoint
- `getNowPlaying()` - Track information endpoint

### 3. Addon Integration (`CiderAddon.java`)

Modified initialization to pass settings to the API:

```java
String apiUrl = configuration().apiUrl().get();
boolean requireToken = configuration().requireApiToken().get();
String token = requireToken ? configuration().appToken().get() : null;

ciderAPI.updateSettings(apiUrl, token, requireToken);
```

## User Experience

### In LabyMod Settings:

1. **Navigate to:** LabyMod → Addons → Cider

2. **See settings:**
   ```
   [✓] Enabled

   API URL: [http://localhost:10767]

   [ ] Require API Token          <-- NEW TOGGLE

   [Hidden by default]
   App Token: [____________]      <-- Only shows when toggle is ON

   [✓] Show Artwork
   [✓] Show Progress Bar
   ```

3. **When to enable "Require API Token":**
   - Your Cider instance requires authentication
   - Your Flask test server has token validation enabled
   - You're using a secured RPC endpoint

4. **When to leave it disabled:**
   - Using default Cider without authentication
   - Local development without security
   - Public/unsecured RPC instance

## Behavior

### Token NOT Required (Default)
```
User: [Sets Require API Token: OFF]
Addon: Makes requests WITHOUT apptoken header
Result: Works with unsecured Cider/API
```

### Token Required
```
User: [Sets Require API Token: ON]
User: [Enters token: "abc123"]
Addon: Makes requests WITH apptoken: abc123 header
Result: Authenticates against secured Cider/API
```

### Token Required but Empty
```
User: [Sets Require API Token: ON]
User: [Leaves token field empty]
Addon: Makes requests WITHOUT apptoken header
Result: Will fail if server requires auth (401/403)
```

## Technical Details

### HTTP Request Flow

**Without Token:**
```http
GET /api/v1/playback/now-playing HTTP/1.1
Host: localhost:10767
```

**With Token:**
```http
GET /api/v1/playback/now-playing HTTP/1.1
Host: localhost:10767
apptoken: your-token-here
```

### Configuration File

Settings are stored in LabyMod's config:

```json
{
  "enabled": true,
  "apiUrl": "http://localhost:10767",
  "requireApiToken": false,
  "appToken": "",
  "showArtwork": true,
  "showProgressBar": true
}
```

## Compatibility

### With Your Flask Server (`/home/x/Schreibtisch/cider-test/`)

The Flask server checks for the `apptoken` header:

```python
headers = {'apptoken': CIDER_TOKEN}
```

**Behavior:**
- If Flask server has `CIDER_TOKEN` set in `.env` → Enable "Require API Token" in addon
- If Flask server doesn't check tokens → Leave toggle OFF

### With Official Cider

Most Cider installations don't require authentication by default:
- **Cider v1:** No authentication
- **Cider v2:** Optional authentication (can be enabled in settings)

## Testing

### Test Case 1: No Authentication
1. Set "Require API Token" to OFF
2. Connect to unsecured Cider instance
3. ✅ Should connect and display tracks

### Test Case 2: With Authentication
1. Set "Require API Token" to ON
2. Enter valid token in "App Token" field
3. Connect to secured Cider instance
4. ✅ Should authenticate and display tracks

### Test Case 3: Wrong Token
1. Set "Require API Token" to ON
2. Enter invalid token
3. ✅ Should fail to connect (expected behavior)

### Test Case 4: Missing Token
1. Set "Require API Token" to ON
2. Leave token field empty
3. ✅ Should fail if server requires auth

## Security Considerations

1. **Token Storage**: Tokens are stored in LabyMod's config files (plaintext)
2. **Transmission**: Tokens sent via HTTP header (not encrypted unless using HTTPS)
3. **Scope**: Token only used for local Cider RPC API calls
4. **Best Practice**: Only enable authentication if actually required

## Advantages of This Approach

✅ **Backward Compatible**: Works with existing unsecured setups (default OFF)
✅ **User-Friendly**: Clear UI with conditional field visibility
✅ **Flexible**: Supports both authenticated and unauthenticated APIs
✅ **Dynamic**: Settings can be changed without restarting Minecraft
✅ **Clean Code**: Single boolean controls all authentication logic

## Files Modified

1. `CiderConfiguration.java` - Added requireApiToken toggle and conditional visibility
2. `CiderAPI.java` - Added conditional token sending in HTTP requests
3. `CiderAddon.java` - Updated initialization to pass settings to API

## Migration from Old Config

If users had the old config with `appToken` always present:
- Old behavior: Token was always sent if not empty
- New behavior: Token only sent if `requireApiToken` is true
- **Action Required**: Users must enable the toggle to continue using tokens

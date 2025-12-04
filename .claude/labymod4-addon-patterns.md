# LabyMod 4 Addon Development Patterns

> Comprehensive analysis of LabyMod 4 addon code structures from RappyTV, RappyLabyAddons, and Global-Tags repositories.

## Table of Contents

1. [Project Structure](#1-project-structure-patterns)
2. [Main Addon Class](#2-main-addon-class-patterns)
3. [Configuration](#3-configuration-patterns)
4. [HUD Widgets](#4-hud-widget-patterns)
5. [Activity & UI](#5-activity-and-ui-patterns)
6. [Event Listeners](#6-event-listener-patterns)
7. [Commands](#7-command-patterns)
8. [Interaction Menu](#8-interaction-menu-bulletpoint-patterns)
9. [API Module](#9-api-module-patterns)
10. [Best Practices](#10-best-practices)

---

## 1. Project Structure Patterns

### Standard Module Layout

All modern LabyMod 4 addons follow a consistent three-module structure:

```
addon-root/
├── api/                    # Public API module (ReferenceType.INTERFACE)
├── core/                   # Core implementation module (ReferenceType.DEFAULT)
├── game-runner/           # Version-specific implementations (optional)
├── build.gradle.kts       # Root build configuration
├── settings.gradle.kts    # Module definitions
└── gradle.properties      # Build properties
```

### Build Configuration

**settings.gradle.kts** (Standard pattern):
```kotlin
rootProject.name = "addon-name"

pluginManagement {
    val labyGradlePluginVersion = "0.5.9"
    buildscript {
        repositories {
            maven("https://dist.labymod.net/api/v1/maven/release/")
            maven("https://maven.neoforged.net/releases/")
            maven("https://maven.fabricmc.net/")
            gradlePluginPortal()
            mavenCentral()
        }
        dependencies {
            classpath("net.labymod.gradle", "common", labyGradlePluginVersion)
        }
    }
}

plugins.apply("net.labymod.labygradle.settings")
include(":api")
include(":core")
```

**Root build.gradle.kts**:
```kotlin
plugins {
    id("net.labymod.labygradle")
    id("net.labymod.labygradle.addon")
}

val versions = providers.gradleProperty("net.labymod.minecraft-versions").get().split(";")

group = "com.yourname"
version = providers.environmentVariable("VERSION").getOrElse("1.0.0")

labyMod {
    defaultPackageName = "com.yourname.addonname"

    minecraft {
        registerVersion(versions.toTypedArray()) {
            runs {
                getByName("client") {
                    // devLogin = true
                }
            }
        }
    }

    addonInfo {
        namespace = "addonname"
        displayName = "Addon Name"
        author = "Author Name"
        description = "Description"
        minecraftVersion = "*"
        version = rootProject.version.toString()
    }
}

subprojects {
    plugins.apply("net.labymod.labygradle")
    plugins.apply("net.labymod.labygradle.addon")
    group = rootProject.group
    version = rootProject.version
}
```

**API module build.gradle.kts**:
```kotlin
import net.labymod.labygradle.common.extension.LabyModAnnotationProcessorExtension.ReferenceType

dependencies {
    labyProcessor()
    labyApi("api")
}

labyModAnnotationProcessor {
    referenceType = ReferenceType.INTERFACE
}
```

**Core module build.gradle.kts**:
```kotlin
import net.labymod.labygradle.common.extension.LabyModAnnotationProcessorExtension.ReferenceType

dependencies {
    labyProcessor()
    api(project(":api"))
}

labyModAnnotationProcessor {
    referenceType = ReferenceType.DEFAULT
}
```

**gradle.properties**:
```properties
org.gradle.jvmargs=-Xmx4096m
net.labymod.minecraft-versions=1.20.1;1.20.2;1.20.4;1.20.6;1.21
```

### Resource Structure

```
core/src/main/resources/assets/{namespace}/
├── i18n/
│   ├── en_us.json
│   ├── de_de.json
│   └── ...
├── textures/
│   ├── icon.png           # Addon icon
│   └── ...
└── themes/
    ├── vanilla/
    │   ├── lss/           # LabyMod Style Sheets
    │   └── textures/
    │       └── settings.png  # Sprite sheet for settings icons
    └── fancy/
        ├── lss/
        └── textures/
```

---

## 2. Main Addon Class Patterns

### Basic Pattern

```java
@AddonMain
public class ExampleAddon extends LabyAddon<ExampleConfiguration> {

  @Override
  protected void enable() {
    this.registerSettingCategory();

    this.registerListener(new ExampleGameTickListener(this));
    this.registerCommand(new ExamplePingCommand());

    this.logger().info("Enabled the Addon");
  }

  @Override
  protected Class<ExampleConfiguration> configurationClass() {
    return ExampleConfiguration.class;
  }
}
```

### Advanced Pattern with Singleton

```java
@AddonMain
public class CustomAddon extends LabyAddon<CustomConfiguration> {

    private static CustomAddon instance;
    private CustomAPI api;

    @Override
    protected void enable() {
        instance = this;

        this.registerSettingCategory();

        // Register custom HUD widget category
        labyAPI().hudWidgetRegistry().categoryRegistry().register(
            new CustomHudWidgetCategory()
        );

        // Initialize API
        this.api = new CustomAPI(this);
    }

    public static CustomAddon get() {
        return instance;
    }

    @Override
    protected Class<? extends CustomConfiguration> configurationClass() {
        return CustomConfiguration.class;
    }
}
```

### Pattern with Pre-Configuration Load and Revision Registry

```java
@AddonMain
public class VersionedAddon extends LabyAddon<VersionedConfig> {

    private static VersionedAddon instance;

    @Override
    protected void preConfigurationLoad() {
        // Register config migration revisions
        Laby.references().revisionRegistry().register(
            new SimpleRevision("namespace", new SemanticVersion("1.3.4"), "2024-01-26")
        );
        this.registerListener(new ConfigMigrationListener());
    }

    @Override
    protected void enable() {
        instance = this;
        this.registerSettingCategory();

        // Register recurring tasks
        Task.builder(() -> {
            // Periodic task code
        }).repeat(2, TimeUnit.MINUTES).build().execute();
    }

    public static VersionedAddon getInstance() {
        return instance;
    }

    @Override
    protected Class<? extends VersionedConfig> configurationClass() {
        return VersionedConfig.class;
    }
}
```

---

## 3. Configuration Patterns

### Basic Configuration

```java
@ConfigName("settings")
public class ExampleConfiguration extends AddonConfig {

  @SwitchSetting
  private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

  @Override
  public ConfigProperty<Boolean> enabled() {
    return this.enabled;
  }
}
```

### Configuration with Sprite Texture

```java
@ConfigName("settings")
@SpriteTexture("settings")
public class SpriteConfig extends AddonConfig {

    @SwitchSetting
    @SpriteSlot(size = 32)
    private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

    @SliderSetting(min = 0, max = 10)
    @SpriteSlot(x = 1)
    private final ConfigProperty<Integer> value = new ConfigProperty<>(5);

    @Override
    public ConfigProperty<Boolean> enabled() {
        return enabled;
    }
}
```

### Configuration with Sections and Activity

```java
@ConfigName("settings")
@SpriteTexture("settings")
public class AdvancedConfig extends AddonConfig {

    @SpriteSlot(size = 32)
    @SwitchSetting
    private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

    @SettingSection("display")
    @SpriteSlot(x = 1)
    @SwitchSetting
    private final ConfigProperty<Boolean> showArtwork = new ConfigProperty<>(true);

    @MethodOrder(after = "enabled")
    @SpriteSlot(size = 32, x = 2)
    @ActivitySetting
    public Activity openSettings() {
        return new SettingsActivity();
    }

    @Override
    public ConfigProperty<Boolean> enabled() {
        return this.enabled;
    }
}
```

### Configuration with Change Listeners

```java
@ConfigName("settings")
public class ReactiveConfig extends AddonConfig {

    public ReactiveConfig() {
        // Add change listener in constructor
        this.enabled.addChangeListener((property, prevValue, newValue) -> {
            ExampleAddon addon = ExampleAddon.get();
            if (addon == null) return;

            if (newValue) {
                addon.initialize();
            } else {
                addon.disconnect();
            }
        });
    }

    @SwitchSetting
    private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

    @Override
    public ConfigProperty<Boolean> enabled() {
        return this.enabled;
    }
}
```

### Sub-Config Pattern

```java
public class SubConfig extends Config {

    public SubConfig() {
        ChangeListener<Property<Boolean>, Boolean> listener =
            (property, oldValue, newValue) -> {
                // React to changes
            };

        this.option1.addChangeListener(listener);
        this.option2.addChangeListener(listener);
    }

    @ShowSettingInParent
    private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

    @SwitchSetting
    @SpriteSlot(x = 2)
    private final ConfigProperty<Boolean> option1 = new ConfigProperty<>(true);

    @SwitchSetting
    @SpriteSlot(x = 3)
    private final ConfigProperty<Boolean> option2 = new ConfigProperty<>(true);
}
```

### Configuration with Version Control

```java
@ConfigName("settings")
@SpriteTexture("settings")
public class VersionedConfig extends AddonConfig {

    @SwitchSetting
    @SpriteSlot(size = 32)
    private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

    @IntroducedIn(namespace = "myaddon", value = "1.4.0")
    @SpriteSlot(x = 1)
    @SwitchSetting
    private final ConfigProperty<Boolean> newFeature = new ConfigProperty<>(true);

    @Override
    public int getConfigVersion() {
        return 2;
    }

    @Override
    public ConfigProperty<Boolean> enabled() {
        return this.enabled;
    }
}
```

---

## 4. HUD Widget Patterns

### Basic TextHudWidget

```java
public class SimpleTextWidget extends TextHudWidget<TextHudWidgetConfig> {

    private TextLine trackLine;
    private TextLine artistLine;

    public SimpleTextWidget(String id, Icon icon) {
        super(id);
        this.setIcon(icon);
    }

    @Override
    public void load(TextHudWidgetConfig config) {
        super.load(config);
        this.trackLine = super.createLine("Track", "Loading...");
        this.artistLine = super.createLine("Artist", "Loading...");
    }

    @Override
    public boolean isVisibleInGame() {
        return true; // Add your visibility logic
    }

    private void updateTrack(String track, String artist) {
        if (this.trackLine != null && this.artistLine != null) {
            this.trackLine.updateAndFlush(track);
            this.artistLine.updateAndFlush(artist);
        }
    }
}
```

### TextHudWidget with Events

```java
public class EventDrivenWidget extends TextHudWidget<TextHudWidgetConfig> {

    private TextLine line;
    private final CustomAPI api;

    public EventDrivenWidget(String id, Icon icon, CustomAPI api) {
        super(id);
        this.api = api;
        this.setIcon(icon);
    }

    @Override
    public void load(TextHudWidgetConfig config) {
        super.load(config);
        this.line = this.createLine("Data", "Loading...");
    }

    @Subscribe
    public void onDataChanged(DataChangedEvent event) {
        if (this.line != null) {
            this.line.updateAndFlush(event.getData());
        }
    }

    @Override
    public boolean isVisibleInGame() {
        return this.api.isConnected();
    }
}
```

### TextHudWidget with Periodic Updates

```java
public class PeriodicWidget extends TextHudWidget<WidgetConfig> {

    private final Task updateTask;
    private TextLine line;
    private String lastValue = "N/A";

    public PeriodicWidget(String id, Icon icon) {
        super(id, WidgetConfig.class);
        this.setIcon(icon);

        // Setup periodic update task
        this.updateTask = Task.builder(() -> {
            this.lastValue = this.fetchData();
            if (this.line != null) {
                Laby.labyAPI().minecraft().executeOnRenderThread(
                    () -> this.line.updateAndFlush(this.lastValue)
                );
            }
        }).repeat(1, TimeUnit.MINUTES).build();
        this.updateTask.execute();
    }

    @Override
    public void load(WidgetConfig config) {
        super.load(config);
        this.line = this.createLine("Value", this.lastValue);
    }

    @Override
    public boolean isVisibleInGame() {
        return !this.lastValue.equals("N/A");
    }

    private String fetchData() {
        // Fetch data from API/service
        return "Data";
    }

    public static class WidgetConfig extends TextHudWidgetConfig {
        @SwitchSetting
        private final ConfigProperty<Boolean> autoRefresh = new ConfigProperty<>(true);
    }
}
```

### TextHudWidget with Custom Config

```java
public class ConfigurableWidget extends TextHudWidget<ConfigurableWidget.CustomConfig> {

    private TextLine line;

    public ConfigurableWidget(String id, Icon icon) {
        super(id, CustomConfig.class);
        this.setIcon(icon);
    }

    @Override
    public void load(CustomConfig config) {
        super.load(config);
        this.line = this.createLine("Label", "Value");

        // React to config changes
        config.showDetails().addChangeListener((property, oldValue, newValue) -> {
            this.updateDisplay();
        });
    }

    @Override
    public void onTick(boolean isEditorContext) {
        if (isEditorContext) {
            this.line.updateAndFlush("Preview");
        }
    }

    private void updateDisplay() {
        // Update based on config
    }

    public static class CustomConfig extends TextHudWidgetConfig {
        @SwitchSetting
        private final ConfigProperty<Boolean> showDetails = new ConfigProperty<>(true);

        @ColorPickerSetting
        private final ConfigProperty<Color> textColor = new ConfigProperty<>(NamedTextColor.WHITE.color());

        @ButtonSetting
        public void reset() {
            this.showDetails.set(true);
            this.textColor.set(NamedTextColor.WHITE.color());
        }

        public ConfigProperty<Boolean> showDetails() {
            return this.showDetails;
        }

        public ConfigProperty<Color> textColor() {
            return this.textColor;
        }
    }
}
```

### HudWidgetCategory Pattern

```java
public class CustomWidgetCategory extends HudWidgetCategory {

    public CustomWidgetCategory() {
        super("custom_category");
    }
}
```

**Registering with Category**:
```java
@Override
protected void enable() {
    HudWidgetCategory category = new CustomWidgetCategory();
    this.labyAPI().hudWidgetRegistry().categoryRegistry().register(category);

    CustomWidget widget = new CustomWidget("custom_widget", icon);
    widget.bindCategory(category);
    this.labyAPI().hudWidgetRegistry().register(widget);
}
```

---

## 5. Activity and UI Patterns

### Basic Activity

```java
@AutoActivity
@Links("settings.lss")
public class SettingsActivity extends Activity {

    @Override
    public void initialize(Parent parent) {
        super.initialize(parent);

        FlexibleContentWidget container = new FlexibleContentWidget()
            .addId("container");

        ComponentWidget title = ComponentWidget.i18n("myaddon.activity.title")
            .addId("title");

        ButtonWidget saveButton = ButtonWidget.i18n("labymod.ui.button.save", () -> {
            // Save logic
            Laby.labyAPI().minecraft().minecraftWindow().displayPreviousScreen();
        });

        container.addContent(title);
        container.addContent(saveButton);

        this.document().addChild(container);
    }
}
```

### Activity with List Widget

```java
@AutoActivity
@Links({"list.lss", "items.lss"})
public class ListActivity extends Activity {

    private final VerticalListWidget<ItemWidget> itemList;
    private final List<Item> items;

    public ListActivity(List<Item> items) {
        this.items = items;
        this.itemList = new VerticalListWidget<>()
            .addId("item-list");

        this.itemList.setSelectCallback(itemWidget -> {
            // Handle selection
        });

        this.itemList.setDoubleClickCallback(itemWidget -> {
            // Handle double-click
        });
    }

    @Override
    public void initialize(Parent parent) {
        super.initialize(parent);

        // Populate list
        this.items.forEach(item ->
            this.itemList.addChild(new ItemWidget(item))
        );

        ScrollWidget scrollWidget = new ScrollWidget(this.itemList);

        FlexibleContentWidget container = new FlexibleContentWidget()
            .addId("container");
        container.addFlexibleContent(scrollWidget);

        this.document().addChild(container);
    }
}
```

### Custom Widget

```java
@AutoWidget
public class ItemWidget extends SimpleWidget {

    private final Item item;

    public ItemWidget(Item item) {
        this.item = item;
    }

    @Override
    public void initialize(Parent parent) {
        super.initialize(parent);

        IconWidget iconWidget = new IconWidget(this.item.getIcon())
            .addId("item-icon");

        ComponentWidget nameWidget = ComponentWidget.text(this.item.getName())
            .addId("item-name");

        ComponentWidget descWidget = ComponentWidget.text(this.item.getDescription())
            .addId("item-description");

        this.addChild(iconWidget);
        this.addChild(nameWidget);
        this.addChild(descWidget);
    }

    public Item getItem() {
        return this.item;
    }
}
```

### LSS Styling

**container.lss**:
```css
.container {
  width: 100%;
  height: 100%;
  left: 0;
  top: 0;

  .title {
    left: 50%;
    alignment-x: center;
    top: 10;
  }

  Scroll {
    width: 90%;
    height: 80%;
    top: 30;
    left: 50%;
    alignment-x: center;

    .item-list {
      left: 0;
      top: 0;
      height: fit-content;
      space-between-entries: 2;
      selectable: true;
    }

    Scrollbar {
      left: 0;
      top: 0;
      width: 5;
      height: 100%;
      margin-left: 5;
    }
  }
}

ItemWidget {
  width: 100%;
  height: 24;
  padding: 1;

  .item-icon {
    left: 3;
    top: 2;
    width: 16;
    height: width;
  }

  .item-name {
    left: 22;
    top: 2;
  }

  .item-description {
    left: 22;
    top: 13;
  }

  &:selected {
    padding: 0;
    border: 1 gray;
    background-color: black;
  }
}
```

---

## 6. Event Listener Patterns

### Basic Event Listener

```java
public class GameTickListener {

  private final ExampleAddon addon;

  public GameTickListener(ExampleAddon addon) {
    this.addon = addon;
  }

  @Subscribe
  public void onGameTick(GameTickEvent event) {
    if (event.phase() != Phase.PRE) {
      return;
    }

    // Tick logic here
  }
}
```

### Multi-Event Listener

```java
public class MultiEventListener {

    private final ExampleAddon addon;

    public MultiEventListener(ExampleAddon addon) {
        this.addon = addon;
    }

    @Subscribe
    public void onWorldEnter(WorldEnterEvent event) {
        // World enter logic
    }

    @Subscribe
    public void onServerDisconnect(ServerDisconnectEvent event) {
        // Disconnect logic
    }

    @Subscribe
    public void onChatReceive(ChatReceiveEvent event) {
        if (!this.addon.configuration().enabled().get()) {
            return;
        }

        String message = event.chatMessage().getPlainText();
        // Process message
    }
}
```

### Advanced Listener with Tracking

```java
public class ItemTrackingListener {

    private final ExampleAddon addon;
    private final List<ItemStack> trackedItems = new ArrayList<>();

    public ItemTrackingListener(ExampleAddon addon) {
        this.addon = addon;
    }

    @Subscribe
    public void onTick(GameTickEvent event) {
        ClientPlayer player = Laby.labyAPI().minecraft().getClientPlayer();
        if (player == null) return;

        ItemStack itemStack = player.getMainHandItemStack();
        if (itemStack == null || itemStack.isAir()) return;

        this.checkItem(itemStack);
    }

    private void checkItem(ItemStack itemStack) {
        if (!this.trackedItems.contains(itemStack)) {
            this.trackedItems.add(itemStack);
            // Process new item
        }
    }
}
```

---

## 7. Command Patterns

### Basic Command

```java
public class ExampleCommand extends Command {

  public ExampleCommand() {
    super("example", "ex");
  }

  @Override
  public boolean execute(String prefix, String[] arguments) {
    this.displayMessage(Component.text("Hello!", NamedTextColor.GREEN));
    return true;
  }
}
```

### Command with Sub-Commands

```java
public class MainCommand extends Command {

  public MainCommand() {
    super("main");
    this.withSubCommand(new InfoSubCommand());
    this.withSubCommand(new ResetSubCommand());
  }

  @Override
  public boolean execute(String prefix, String[] arguments) {
    if (arguments.length == 0) {
      this.displayMessage(Component.text("Usage: /main <info|reset>"));
      return false;
    }
    return false; // Let sub-commands handle
  }
}

class InfoSubCommand extends SubCommand {
  @Override
  public boolean execute(String prefix, String[] arguments) {
    // Info logic
    return true;
  }
}
```

---

## 8. Interaction Menu (BulletPoint) Patterns

### Simple BulletPoint

```java
public class SimpleBulletPoint implements BulletPoint {

    @Override
    public Component getTitle() {
        return Component.translatable("myaddon.interaction.title");
    }

    @Override
    public Icon getIcon() {
        return Icon.texture(ResourceLocation.create("myaddon", "textures/icon.png"));
    }

    @Override
    public void execute(Player player) {
        Laby.labyAPI().minecraft().executeNextTick(
            () -> {
                // Execute action
            }
        );
    }

    @Override
    public boolean isVisible(Player player) {
        return true; // Add visibility logic
    }
}
```

### BulletPoint with Chat Command

```java
public class ChatCommandBulletPoint implements BulletPoint {

    private static final String COMMAND = "/pay %s ";

    @Override
    public Component getTitle() {
        return Component.translatable("myaddon.interaction.pay");
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public void execute(Player player) {
        Laby.labyAPI().minecraft().executeNextTick(
            () -> Laby.labyAPI().minecraft().openChat(
                String.format(COMMAND, player.getName())
            )
        );
    }

    @Override
    public boolean isVisible(Player player) {
        return true;
    }
}
```

### BulletPoint with Activity

```java
public class ActivityBulletPoint implements BulletPoint {

    private final ExampleAddon addon;

    public ActivityBulletPoint(ExampleAddon addon) {
        this.addon = addon;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("myaddon.interaction.manage");
    }

    @Override
    public Icon getIcon() {
        return Icon.texture(ResourceLocation.create("myaddon", "textures/icon.png"));
    }

    @Override
    public void execute(Player player) {
        Laby.labyAPI().minecraft().executeNextTick(() ->
            Laby.labyAPI().minecraft().minecraftWindow().displayScreen(
                new ManageActivity(player)
            )
        );
    }

    @Override
    public boolean isVisible(Player player) {
        return this.addon.configuration().enabled().get();
    }
}
```

---

## 9. API Module Patterns

### Enum with Icon Support

```java
public enum ItemType {
    SWORD(0, 0),
    PICKAXE(1, 0),
    AXE(2, 0);

    private static final ResourceLocation ICON_SPRITE =
        ResourceLocation.create("myaddon", "textures/items.png");

    private final Icon icon;

    ItemType(int x, int y) {
        this.icon = Icon.sprite32(ICON_SPRITE, x, y);
    }

    public Icon getIcon() {
        return this.icon;
    }

    public static ItemType fromItemStack(@Nullable ItemStack itemStack) {
        if (itemStack == null) return null;

        String path = itemStack.getIdentifier().getPath();
        if (path.endsWith("_sword")) return SWORD;
        if (path.endsWith("_pickaxe")) return PICKAXE;
        if (path.endsWith("_axe")) return AXE;

        return null;
    }
}
```

### Data Class Pattern

```java
public class CustomData {

    private final String id;
    private final String name;
    private final int value;
    private final long timestamp;

    public CustomData(String id, String name, int value) {
        this(id, name, value, System.currentTimeMillis());
    }

    public CustomData(String id, String name, int value, long timestamp) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.timestamp = timestamp;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getValue() {
        return this.value;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public boolean isExpired(long maxAge) {
        return System.currentTimeMillis() - this.timestamp > maxAge;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CustomData)) return false;
        CustomData other = (CustomData) obj;
        return this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
}
```

---

## 10. Best Practices

### Project Structure

1. **Always use api/core/game-runner structure** with proper `ReferenceType` settings
2. **Keep API module clean** - only interfaces and data classes
3. **Use proper version ranges** in `gradle.properties`
4. **Organize resources properly** - use themes/vanilla and themes/fancy

### Code Organization

1. **Singleton Pattern**: Use static instance getter for accessing the addon
2. **Null Safety**: Always check for null, especially with Minecraft objects
3. **Thread Safety**: Use `executeOnRenderThread()` for UI updates
4. **Resource Cleanup**: Properly dispose resources in `disable()`

### Configuration

1. **Use @ConfigName and @SpriteTexture** for proper UI rendering
2. **Add change listeners in constructor** for reactive updates
3. **Use @IntroducedIn** for new features to support config migration
4. **Implement getConfigVersion()** for versioned configs
5. **Use @SettingSection** to group related settings

### HUD Widgets

1. **Extend TextHudWidget** for simple text displays
2. **Bind to categories** for better organization
3. **Use TextLine for dynamic content** - call `updateAndFlush()`
4. **Check isVisibleInGame()** for proper visibility control
5. **Use @Subscribe** for event-driven updates

### Activities & UI

1. **Use @AutoActivity and @Links** for LSS styling
2. **Implement proper initialization** in `initialize(Parent)`
3. **Use executeNextTick()** when opening screens
4. **Handle keyboard shortcuts** in `keyPressed()`
5. **Use LSS for styling** instead of hardcoded positions

### Events & Listeners

1. **Check event phases** when needed (PRE/POST)
2. **Always validate configuration** before processing
3. **Use @Subscribe annotation** for event methods
4. **Handle null cases properly**

### Commands

1. **Support multiple aliases** in constructor
2. **Use withSubCommand()** for sub-commands
3. **Return false for usage help**, true for success
4. **Use Component.translatable()** for i18n

### Tasks & Threading

1. **Use Task.builder()** for periodic operations
2. **Specify proper TimeUnit** (SECONDS, MINUTES, HOURS)
3. **Clean up tasks in disable()**
4. **Use executeOnRenderThread()** for UI updates

### Internationalization

1. **Always provide en_us.json** as fallback
2. **Use Component.translatable()** in code
3. **Organize keys logically** by feature
4. **Test with multiple languages**

### Version Control & Migration

1. **Use SimpleRevision** to track config versions
2. **Implement preConfigurationLoad()** for migrations
3. **Use @IntroducedIn** for new features
4. **Test migrations thoroughly**

### Performance

1. **Cache frequently accessed data**
2. **Use rate limiting** for expensive operations
3. **Debounce rapid updates** (e.g., Debounce.of())
4. **Avoid blocking operations** on main thread

### Icon & Textures

1. **Use sprite sheets** for multiple icons (Icon.sprite32())
2. **Organize textures** in themes/vanilla/textures
3. **Use ResourceLocation.create()** for paths
4. **Provide fallback icons** when needed

---

## Common Annotations Reference

### Configuration Annotations

- `@ConfigName("name")` - Config namespace
- `@SpriteTexture("texture")` - Sprite sheet for icons
- `@SpriteSlot(x, y, size)` - Icon position in sprite
- `@SettingSection("section")` - Group settings
- `@IntroducedIn(namespace, value)` - Version tracking
- `@ShowSettingIf(value, equals)` - Conditional settings
- `@MethodOrder(after)` - Setting order

### Setting Type Annotations

- `@SwitchSetting` - Boolean toggle
- `@SliderSetting(min, max)` - Numeric slider
- `@DropdownSetting` - Dropdown menu
- `@TextFieldSetting` - Text input
- `@ColorPickerSetting` - Color picker
- `@ButtonSetting` - Action button
- `@ActivitySetting` - Opens activity
- `@AccountInfoSetting` - Account info display

### Widget Annotations

- `@AutoWidget` - Auto-generate widget
- `@AutoActivity` - Auto-generate activity
- `@Links("file.lss")` - LSS styling files

### Other Annotations

- `@AddonMain` - Main addon class
- `@Subscribe` - Event listener method
- `@Exclude` - Exclude from config serialization
- `@ShowSettingInParent` - Show in parent config

---

## Common Imports Reference

```java
// Addon Core
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.models.addon.annotation.AddonMain;

// Configuration
import net.labymod.api.addon.AddonConfig;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.loader.annotation.*;
import net.labymod.api.configuration.settings.annotation.*;
import net.labymod.api.client.gui.screen.widget.widgets.input.*;

// HUD Widgets
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.client.gui.hud.hudwidget.HudWidgetCategory;

// UI & Activities
import net.labymod.api.client.gui.screen.activity.Activity;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.widget.Widget;
import net.labymod.api.client.gui.screen.widget.widgets.*;

// Events
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.lifecycle.*;
import net.labymod.api.event.client.chat.*;
import net.labymod.api.event.client.input.*;

// Commands
import net.labymod.api.client.chat.command.Command;
import net.labymod.api.client.chat.command.SubCommand;

// Interaction Menu
import net.labymod.api.client.gui.navigation.elements.BulletPoint;

// Components & Text
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

// Icons & Resources
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.resources.ResourceLocation;

// Tasks
import net.labymod.api.util.concurrent.task.Task;
import java.util.concurrent.TimeUnit;

// LabyMod API Access
import net.labymod.api.Laby;
```

---

## Example Project Checklist

When creating a new LabyMod 4 addon, ensure you have:

- [ ] Proper module structure (api, core, game-runner)
- [ ] build.gradle.kts files configured correctly
- [ ] settings.gradle.kts with LabyGradle plugin
- [ ] gradle.properties with Minecraft versions
- [ ] Main addon class with @AddonMain
- [ ] Configuration class extending AddonConfig
- [ ] Resource folder structure (assets/{namespace}/)
- [ ] i18n files (at least en_us.json)
- [ ] addon.json metadata file
- [ ] Proper @SpriteTexture and sprite sheet if using icons
- [ ] LSS files for custom UI styling
- [ ] Event listeners registered in enable()
- [ ] Proper cleanup in disable()
- [ ] Singleton pattern if needed across classes

---

*This knowledge base was compiled from analyzing multiple production LabyMod 4 addons including Toolbreak-Warning, CustomWidgets, Calculator, OPSUCHT-Utilities, and Global-Tags LabyAddon.*

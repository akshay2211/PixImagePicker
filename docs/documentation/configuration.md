# Configuration

Complete reference for all Pix configuration options.

## Options Class

The `Options` class is your main configuration object. All properties are optional with sensible defaults.

### Media Selection

```kotlin
Options().apply {
    // Maximum number of items to select
    count = 5

    // Selection mode: Picture, Video, or All
    mode = Mode.All

    // Pre-select items (appear selected on open)
    preSelectedUrls = ArrayList(listOf(
        Uri.parse("content://..."),
        Uri.parse("content://...")
    ))
}
```

### Camera

```kotlin
Options().apply {
    // Capture aspect ratio
    ratio = Ratio.RATIO_AUTO  // RATIO_4_3, RATIO_16_9, RATIO_AUTO

    // Start with front-facing camera
    isFrontFacing = false

    // Flash mode for photo/video
    flash = Flash.Auto  // Flash.On, Flash.Off, Flash.Auto

    // Video recording duration limit (seconds)
    videoDurationLimitInSeconds = 10
}
```

### Storage

```kotlin
Options().apply {
    // Custom path for captured media (relative to Pictures)
    // Full path will be: Pictures/Pix/Camera
    path = "Pix/Camera"
}
```

### UI

```kotlin
Options().apply {
    // Number of columns in grid
    spanCount = 4
}
```

### Video Options

```kotlin
Options().apply {
    // Advanced video configuration
    videoOptions = VideoOptions().apply {
        // Video-specific settings
    }
}
```

## Complete Example

```kotlin
val options = Options().apply {
    // Selection
    count = 10
    mode = Mode.All

    // Camera
    ratio = Ratio.RATIO_16_9
    isFrontFacing = false
    flash = Flash.Auto
    videoDurationLimitInSeconds = 60

    // Storage
    path = "MyApp/Media"

    // UI
    spanCount = 3

    // Pre-selection
    preSelectedUrls = ArrayList()
}
```

## Ratio Options

| Ratio | Aspect | Use Case |
|-------|--------|----------|
| `RATIO_4_3` | 4:3 | Traditional photos |
| `RATIO_16_9` | 16:9 | Widescreen, videos |
| `RATIO_AUTO` | Variable | Automatic detection |

## Mode Options

| Mode | Description |
|------|-------------|
| `Mode.Picture` | Select/capture images only |
| `Mode.Video` | Select/capture videos only |
| `Mode.All` | Select/capture both images and videos |

## Flash Modes

| Flash | Description |
|-------|-------------|
| `Flash.On` | Flash always on |
| `Flash.Off` | Flash always off |
| `Flash.Auto` | Flash toggles automatically |

## Default Values

| Option | Default |
|--------|---------|
| `count` | 1 |
| `mode` | Mode.All |
| `ratio` | Ratio.RATIO_AUTO |
| `spanCount` | 4 |
| `path` | "Pix/Camera" |
| `isFrontFacing` | false |
| `flash` | Flash.Auto |
| `videoDurationLimitInSeconds` | 10 |

## Immutability

Note: Some options cannot be changed after the fragment is created. Always configure options before adding the fragment to avoid unexpected behavior.

```kotlin
// CORRECT
val options = Options().apply { count = 5 }
val fragment = pixFragment(options)

// NOT RECOMMENDED
val fragment = pixFragment(Options())
// Changing options here won't affect already-created fragment
```

## Advanced Configuration

For advanced scenarios and fine-tuning, see:

- [Customization Guide](customization.md) - Theme and UI customization
- [API Reference](../../api/index.html) - Detailed API documentation
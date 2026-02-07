# Usage Guide

Complete guide to using Pix Image Picker in your application.

## Fragment Integration

### Adding to Activity Container

The simplest way to add Pix to your activity:

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val options = Options().apply {
            count = 5
            mode = Mode.All
        }

        addPixToActivity(R.id.container, options) { result ->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {
                    val uris = result.data
                    // Process selected media
                }
                PixEventCallback.Status.BACK_PRESSED -> {
                    // User exited picker
                }
            }
        }
    }
}
```

### Manual Fragment Management

For more control, retrieve the fragment directly:

```kotlin
val options = Options().apply {
    count = 10
}

val picker = pixFragment(options) { result ->
    when (result.status) {
        PixEventCallback.Status.SUCCESS -> {
            displayImages(result.data)
        }
        PixEventCallback.Status.BACK_PRESSED -> {}
    }
}

supportFragmentManager.beginTransaction()
    .replace(R.id.fragment_container, picker)
    .addToBackStack(null)
    .commit()
```

## Media Selection Modes

### Picture Only

```kotlin
val options = Options().apply {
    mode = Mode.Picture
    count = 5
}
```

Select only images. Camera will show photo capture mode.

### Video Only

```kotlin
val options = Options().apply {
    mode = Mode.Video
    count = 3
    videoDurationLimitInSeconds = 30
}
```

Select only videos. Camera shows video capture mode.

### Both (Default)

```kotlin
val options = Options().apply {
    mode = Mode.All
    count = 10
}
```

Allow both images and videos in selection.

## Camera Configuration

### Aspect Ratios

```kotlin
val options = Options().apply {
    ratio = Ratio.RATIO_4_3    // Traditional 4:3
    // OR
    ratio = Ratio.RATIO_16_9   // Widescreen
    // OR
    ratio = Ratio.RATIO_AUTO   // Auto (default)
}
```

### Front-Facing Camera

```kotlin
val options = Options().apply {
    isFrontFacing = true  // Start with selfie camera
}
```

### Flash Mode

```kotlin
val options = Options().apply {
    flash = Flash.On      // Always on
    flash = Flash.Off     // Always off
    flash = Flash.Auto    // Auto (default)
}
```

### Video Duration Limit

```kotlin
val options = Options().apply {
    mode = Mode.Video
    videoDurationLimitInSeconds = 60  // Max 1 minute videos
}
```

## Pre-Selection

Pre-select items that appear selected when picker opens:

```kotlin
val selectedUris = listOf(
    Uri.parse("content://..."),
    Uri.parse("content://...")
)

val options = Options().apply {
    preSelectedUrls = ArrayList(selectedUris)
    count = 10  // Can select 10 - 2 already selected = 8 more
}
```

## Result Handling

### Via Callback

```kotlin
pixFragment(options) { result ->
    when (result.status) {
        PixEventCallback.Status.SUCCESS -> {
            val selectedUris: List<Uri> = result.data
            // Process uris
        }
        PixEventCallback.Status.BACK_PRESSED -> {
            Log.d("Pix", "User closed picker")
        }
    }
}
```

### Via Event Bus

Listen globally from anywhere in your app:

```kotlin
class MyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            PixBus.results.collect { result ->
                when (result.status) {
                    PixEventCallback.Status.SUCCESS -> {
                        val uris = result.data
                        updateUI(uris)
                    }
                    PixEventCallback.Status.BACK_PRESSED -> {}
                }
            }
        }
    }
}
```

## Storage Configuration

Specify where captured media is stored:

```kotlin
val options = Options().apply {
    path = "MyApp/Media"  // Relative to Pictures directory
    // Captured files will be stored in: Pictures/MyApp/Media/
}
```

## Grid Customization

```kotlin
val options = Options().apply {
    spanCount = 3  // 3 columns instead of default 4
}
```

## Best Practices

1. **Permission Handling**: Make sure you handle runtime permissions for camera and storage
2. **Fragment Lifecycle**: Ensure options are set before adding fragment
3. **Memory Management**: Handle large image lists efficiently
4. **Error Handling**: Always check result status before using data
5. **Testing**: Use the sample app as reference for integration patterns

## Troubleshooting

### No Results Returned

- Ensure callback is properly attached
- Check permissions are granted
- Verify PixBus collector is in correct scope

### Camera Not Working

- Check CAMERA permission is granted
- Verify device has camera
- Check cameraX dependencies

### Grid Not Showing

- Ensure container has proper dimensions
- Check spanCount is reasonable for screen size
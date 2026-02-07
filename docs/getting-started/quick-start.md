# Quick Start

Get Pix up and running in your app in just a few minutes.

## Basic Setup

### 1. Initialize with Options

Create an `Options` object to configure the picker:

```kotlin
val options = Options().apply {
    ratio = Ratio.RATIO_AUTO              // Image/video capture ratio
    count = 1                             // Number of items to select
    spanCount = 4                         // Grid columns
    path = "Pix/Camera"                   // Custom storage path
    isFrontFacing = false                 // Front-facing camera
    videoDurationLimitInSeconds = 10      // Video length limit
    mode = Mode.All                       // Select images, videos, or both
    flash = Flash.Auto                    // Flash settings
}
```

### 2. Add Pix to Your Activity

Use the built-in helper function:

```kotlin
addPixToActivity(R.id.container, options) { result ->
    when (result.status) {
        PixEventCallback.Status.SUCCESS -> {
            val selectedUris = result.data // List<Uri>
            // Use selected media
        }
        PixEventCallback.Status.BACK_PRESSED -> {
            // User pressed back
        }
    }
}
```

### 3. Handle Results

Results are delivered via:

**Option A: Direct Callback**
```kotlin
pixFragment(options) { result ->
    when (result.status) {
        PixEventCallback.Status.SUCCESS -> {
            // Use result.data (List<Uri>)
        }
        PixEventCallback.Status.BACK_PRESSED -> {}
    }
}
```

**Option B: Event Bus (Global)**
```kotlin
lifecycleScope.launch {
    PixBus.results.collect { result ->
        when (result.status) {
            PixEventCallback.Status.SUCCESS -> {
                val uris = result.data
            }
            PixEventCallback.Status.BACK_PRESSED -> {}
        }
    }
}
```

## Configuration Options

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `ratio` | `Ratio` | `RATIO_AUTO` | Capture ratio (4:3, 16:9, or auto) |
| `count` | `Int` | `1` | Max items to select |
| `spanCount` | `Int` | `4` | Grid columns |
| `mode` | `Mode` | `All` | Select images/videos/both |
| `path` | `String` | `"Pix/Camera"` | Storage directory |
| `isFrontFacing` | `Boolean` | `false` | Front camera on start |
| `videoDurationLimitInSeconds` | `Int` | `10` | Max video length |
| `flash` | `Flash` | `Flash.Auto` | Flash mode |

## Common Patterns

### Fragment Integration

```kotlin
class MyFragment : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val options = Options().apply {
            count = 5
            mode = Mode.Picture
        }

        childFragmentManager.beginTransaction()
            .add(R.id.picker_container, pixFragment(options) { result ->
                if (result.status == PixEventCallback.Status.SUCCESS) {
                    handleImages(result.data)
                }
            })
            .commit()
    }
}
```

### Navigation Integration

See the [Examples](../documentation/examples.md) page for Navigation with NavController integration.

## Next Steps

- Explore [Usage Guide](../documentation/usage.md) for advanced features
- Check [Configuration](../documentation/configuration.md) for all options
- View [Examples](../documentation/examples.md) for common patterns
- Read the [API Reference](../../api/index.html) for detailed documentation
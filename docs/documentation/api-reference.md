# API Reference

Complete API documentation for Pix Image Picker.

## Overview

The Pix API is generated using [Dokka](https://kotlinlang.org/docs/dokka-introduction.html), Kotlin's documentation engine. This provides comprehensive documentation for all public classes, functions, and properties.

## Accessing the API Documentation

### Online Documentation

The full Dokka-generated API documentation is available at:

**[Pix API Documentation](https://akshay2211.github.io/PixImagePicker/api/)**

This includes:
- Complete class hierarchy
- All public methods and properties
- Parameter descriptions
- Return type information
- Usage examples
- Source code links

### Generating Locally

To generate API documentation on your machine:

```bash
./gradlew :pix:dokkaHtml
```

Documentation will be generated at: `pix/build/dokka/html/index.html`

To open in browser:
```bash
./gradlew :pix:dokkaHtmlOpen
```

## Main Classes

### Pix

Entry point for the library.

```kotlin
object Pix {
    fun pixFragment(
        options: Options,
        callback: (PixEventCallback) -> Unit
    ): Fragment
}
```

See full documentation in [API Reference](https://akshay2211.github.io/PixImagePicker/api/)

### Options

Configuration class for customizing the picker.

**Key Properties:**
- `count: Int` - Maximum items to select
- `mode: Mode` - Selection mode (Picture, Video, All)
- `ratio: Ratio` - Capture aspect ratio
- `spanCount: Int` - Grid columns
- `path: String` - Storage path for captures
- `isFrontFacing: Boolean` - Start with front camera
- `flash: Flash` - Flash mode
- `videoDurationLimitInSeconds: Int` - Max video length
- `preSelectedUrls: List<Uri>` - Pre-selected items

**Example:**
```kotlin
val options = Options().apply {
    count = 5
    mode = Mode.All
    ratio = Ratio.RATIO_AUTO
}
```

### PixEventCallback

Result callback interface.

```kotlin
interface PixEventCallback {
    enum class Status {
        SUCCESS,      // Selection successful
        BACK_PRESSED  // User pressed back
    }

    data class Result(
        val status: Status,
        val data: List<Uri>  // Selected URIs
    )
}
```

### PixBus

Global event bus for picker results.

```kotlin
object PixBus {
    val results: Flow<PixEventCallback.Result>

    suspend fun results(
        callback: suspend (PixEventCallback.Result) -> Unit
    )
}
```

**Usage:**
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

### Enums

#### Mode

```kotlin
enum class Mode {
    Picture,  // Images only
    Video,    // Videos only
    All       // Both images and videos
}
```

#### Ratio

```kotlin
enum class Ratio {
    RATIO_4_3,   // 4:3 aspect ratio
    RATIO_16_9,  // 16:9 aspect ratio
    RATIO_AUTO   // Automatic
}
```

#### Flash

```kotlin
enum class Flash {
    On,   // Always on
    Off,  // Always off
    Auto  // Automatic
}
```

## Function Reference

### Creating Picker Fragment

```kotlin
fun pixFragment(
    options: Options,
    callback: (PixEventCallback) -> Unit
): Fragment
```

Creates a Pix picker fragment with the specified configuration.

**Parameters:**
- `options: Options` - Configuration for the picker
- `callback: (PixEventCallback) -> Unit` - Called when user completes selection

**Returns:** A Fragment instance that can be added to your activity/fragment

**Example:**
```kotlin
val options = Options().apply { count = 5 }
val fragment = pixFragment(options) { result ->
    when (result.status) {
        PixEventCallback.Status.SUCCESS -> {
            val uris = result.data
        }
        PixEventCallback.Status.BACK_PRESSED -> {}
    }
}
```

### Adding to Activity

```kotlin
fun Activity.addPixToActivity(
    containerId: Int,
    options: Options,
    callback: (PixEventCallback) -> Unit
): Fragment
```

Convenience extension function to add Pix picker to an activity.

**Parameters:**
- `containerId: Int` - ViewGroup container ID
- `options: Options` - Configuration
- `callback: (PixEventCallback) -> Unit` - Result callback

**Returns:** The added fragment

**Example:**
```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addPixToActivity(R.id.container, Options()) { result ->
            // Handle result
        }
    }
}
```

## Extension Functions

### Fragment.pixFragment

```kotlin
fun Fragment.pixFragment(
    options: Options,
    callback: (PixEventCallback) -> Unit
): Fragment
```

Create picker fragment from within another fragment.

### AppCompatActivity.pixFragment

```kotlin
fun AppCompatActivity.pixFragment(
    options: Options,
    callback: (PixEventCallback) -> Unit
): Fragment
```

Create picker fragment from within an activity.

## Data Classes

### Result

```kotlin
data class Result(
    val status: PixEventCallback.Status,
    val data: List<Uri>
)
```

**Properties:**
- `status: Status` - Operation status (SUCCESS or BACK_PRESSED)
- `data: List<Uri>` - Selected media URIs (empty if BACK_PRESSED)

## Advanced Usage

### Custom Result Handling

```kotlin
val options = Options().apply {
    count = 10
}

val fragment = pixFragment(options) { result ->
    when (result.status) {
        PixEventCallback.Status.SUCCESS -> {
            result.data.forEach { uri ->
                // Process each selected URI
                val bitmap = MediaStore.Images.Media.getBitmap(
                    contentResolver,
                    uri
                )
            }
        }
        PixEventCallback.Status.BACK_PRESSED -> {
            Log.d("Pix", "Selection cancelled")
        }
    }
}
```

### Error Handling

```kotlin
try {
    val fragment = pixFragment(options) { result ->
        try {
            if (result.status == PixEventCallback.Status.SUCCESS) {
                processUris(result.data)
            }
        } catch (e: Exception) {
            Log.e("Pix", "Error processing result", e)
        }
    }
} catch (e: IllegalArgumentException) {
    Log.e("Pix", "Invalid options", e)
}
```

## Frequently Used APIs

### Most Common: Basic Selection

```kotlin
// 1. Create options
val options = Options().apply {
    count = 5
    mode = Mode.All
}

// 2. Create picker
val fragment = pixFragment(options) { result ->
    if (result.status == PixEventCallback.Status.SUCCESS) {
        val selectedUris = result.data
        // Use URIs
    }
}

// 3. Add to activity
supportFragmentManager.beginTransaction()
    .add(R.id.container, fragment)
    .commit()
```

### Common: Using PixBus

```kotlin
lifecycleScope.launch {
    PixBus.results.collect { result ->
        when (result.status) {
            PixEventCallback.Status.SUCCESS -> {
                updateUI(result.data)
            }
            PixEventCallback.Status.BACK_PRESSED -> {}
        }
    }
}
```

## Deprecated APIs

Currently, there are no deprecated APIs in v1.6.8.

Deprecations will be announced in advance for compatibility.

## Version Compatibility

- **Minimum:** API 16 (Android 4.1)
- **Target:** API 34+ (Android 15)
- **Tested:** API 11 - 34

## Documentation Formatting

The API documentation uses [Dokka conventions](https://kotlinlang.org/docs/dokka-introduction.html):

```kotlin
/**
 * Detailed function description.
 *
 * @param paramName Parameter description
 * @return Description of return value
 * @throws ExceptionType When this condition occurs
 *
 * Example usage:
 * ```kotlin
 * // Code example here
 * ```
 */
```

## Finding What You Need

### Search

Use the search box in the [Dokka documentation](https://akshay2211.github.io/PixImagePicker/api/) to find classes, methods, or properties.

### Browse

- **Packages** - Grouped by namespace
- **Classes** - All classes organized hierarchically
- **Functions** - Top-level functions
- **Properties** - Class and object properties

### Quick Links

- [Main Pix object](https://akshay2211.github.io/PixImagePicker/api/-pix/io.ak1.pix/pix.html)
- [Options class](https://akshay2211.github.io/PixImagePicker/api/-pix/io.ak1.pix/-options/index.html)
- [PixEventCallback](https://akshay2211.github.io/PixImagePicker/api/-pix/io.ak1.pix/-pix-event-callback/index.html)
- [PixBus](https://akshay2211.github.io/PixImagePicker/api/-pix/io.ak1.pix/-pix-bus/index.html)

## Troubleshooting

### Can't find a class

1. Try searching in [Dokka docs](https://akshay2211.github.io/PixImagePicker/api/)
2. Check the [GitHub source](https://github.com/akshay2211/PixImagePicker)
3. Ask in [GitHub Discussions](https://github.com/akshay2211/PixImagePicker/discussions)

### API changed in new version

Check the [Changelog](https://github.com/akshay2211/PixImagePicker/releases) for breaking changes.

## Source Code

The source code is available on [GitHub](https://github.com/akshay2211/PixImagePicker).

Each API reference in Dokka includes a link to the source code, allowing you to see the implementation details.

## Contributing to Documentation

Found an issue or have suggestions for the API docs?

- [File an issue](https://github.com/akshay2211/PixImagePicker/issues)
- [Submit a PR](https://github.com/akshay2211/PixImagePicker/pulls)
- [Start a discussion](https://github.com/akshay2211/PixImagePicker/discussions)

See [Contributing Guide](contributing.md) for details.

---

**Next Steps:**
- Review [Configuration](configuration.md) for detailed options
- Check [Examples](examples.md) for common patterns
- Read [Usage Guide](usage.md) for complete walkthrough
# Customization

Customize Pix to match your app's design and branding.

## Theme Customization

### Color Configuration

Define custom colors in your `colors.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- Pix Theme Colors -->
    <color name="primary_color_pix">#075e54</color>
    <color name="primary_light_color_pix">#80075e54</color>
    <color name="surface_color_pix">#ffffff</color>
    <color name="text_color_pix">#807f7f</color>
    <color name="video_counter_color_pix">#E53935</color>
</resources>
```

### Color Meanings

| Color | Usage |
|-------|-------|
| `primary_color_pix` | Primary UI elements (selection checkmarks, buttons) |
| `primary_light_color_pix` | Semi-transparent primary for overlays |
| `surface_color_pix` | Background surfaces |
| `text_color_pix` | Secondary text and icons |
| `video_counter_color_pix` | Video duration counter badge |

## Default Colors

If not overridden, Pix uses Material Design colors:

```xml
<color name="video_counter_color_pix">#E53935</color>
<color name="primary_color_pix">#075e54</color>
<color name="primary_light_color_pix">#80075e54</color>
<color name="surface_color_pix">#ffffff</color>
<color name="text_color_pix">#807f7f</color>
```

## Material 3 Integration

For Material 3 apps, customize theme colors:

```xml
<resources>
    <color name="primary_color_pix">@color/md_theme_primary</color>
    <color name="primary_light_color_pix">@color/md_theme_primary_container</color>
    <color name="surface_color_pix">@color/md_theme_surface</color>
    <color name="text_color_pix">@color/md_theme_on_surface</color>
    <color name="video_counter_color_pix">@color/md_theme_secondary</color>
</resources>
```

## Theme Variants

### Dark Mode

Pix respects system dark mode settings. Define dark color variants:

```xml
<!-- values-night/colors.xml -->
<resources>
    <color name="primary_color_pix">#4db8a8</color>
    <color name="primary_light_color_pix">#804db8a8</color>
    <color name="surface_color_pix">#121212</color>
    <color name="text_color_pix">#9d9d9d</color>
    <color name="video_counter_color_pix">#FF6B6B</color>
</resources>
```

## Styling Your App

### Example: Blue Theme

```xml
<resources>
    <color name="primary_color_pix">#1976D2</color>
    <color name="primary_light_color_pix">#801976D2</color>
    <color name="surface_color_pix">#ffffff</color>
    <color name="text_color_pix">#666666</color>
    <color name="video_counter_color_pix">#D32F2F</color>
</resources>
```

### Example: Orange Theme

```xml
<resources>
    <color name="primary_color_pix">#FF6F00</color>
    <color name="primary_light_color_pix">#80FF6F00</color>
    <color name="surface_color_pix">#ffffff</color>
    <color name="text_color_pix">#757575</color>
    <color name="video_counter_color_pix">#C62828</color>
</resources>
```

## Fragment Integration with Custom Styling

```kotlin
class MyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Pix will automatically use colors from your colors.xml
        val options = Options().apply {
            count = 5
            mode = Mode.All
        }

        val picker = pixFragment(options) { result ->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {
                    val uris = result.data
                }
                PixEventCallback.Status.BACK_PRESSED -> {}
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, picker)
            .commit()
    }
}
```

## App Theme Integration

Ensure your app theme is applied properly:

```xml
<!-- res/values/themes.xml -->
<resources>
    <style name="Theme.MyApp" parent="Theme.MaterialComponents">
        <item name="colorPrimary">@color/primary_color_pix</item>
        <item name="colorSecondary">@color/video_counter_color_pix</item>
        <!-- Other theme attributes -->
    </style>
</resources>
```

## Advanced Customization

For more advanced customization needs:

1. **Custom Colors** - Override any color resource in your app
2. **Layout Modifications** - Extend Pix classes for custom layouts
3. **Custom Callbacks** - Use PixBus for global event handling

## Best Practices

1. **Maintain Consistency** - Use your app's primary color for `primary_color_pix`
2. **Accessibility** - Ensure adequate contrast ratios
3. **Dark Mode** - Always provide dark theme variants
4. **Testing** - Test on various screen sizes and orientations
5. **Material Design** - Follow Material Design guidelines for colors

## Troubleshooting

### Colors Not Applying

- Ensure color resources are in `values/colors.xml`
- Check spelling of color resource names (case-sensitive)
- Rebuild project to update resources
- Clear app cache if changes don't appear

### Dark Mode Not Working

- Create `values-night/colors.xml` with dark variants
- Ensure correct color names match light mode
- Test on device with dark mode enabled
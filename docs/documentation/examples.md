# Examples

Common integration patterns and real-world examples.

## Fragment Direct Integration

Simplest implementation for a single fragment:

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val options = Options().apply {
            count = 5
            mode = Mode.All
        }

        val pickerFragment = pixFragment(options) { result ->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {
                    val selectedImages = result.data
                    displayImages(selectedImages)
                }
                PixEventCallback.Status.BACK_PRESSED -> {
                    Toast.makeText(this, "Picker closed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, pickerFragment)
            .commit()
    }

    private fun displayImages(uris: List<Uri>) {
        // Handle selected images
        uris.forEach { uri ->
            Log.d("Pix", "Selected: $uri")
        }
    }
}
```

## Navigation Component Integration

Using Android Navigation component:

```kotlin
// In your NavGraph XML
<fragment
    android:id="@+id/selectMediaFragment"
    android:name="com.example.SelectMediaFragment" />
```

```kotlin
class SelectMediaFragment : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val options = Options().apply {
            count = 10
            mode = Mode.Picture
        }

        val pickerFragment = pixFragment(options) { result ->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {
                    // Navigate back with results
                    val bundle = Bundle().apply {
                        putParcelableArrayList(
                            "selected_images",
                            ArrayList(result.data)
                        )
                    }
                    findNavController().navigate(
                        R.id.action_selectMedia_to_reviewFragment,
                        bundle
                    )
                }
                PixEventCallback.Status.BACK_PRESSED -> {
                    findNavController().popBackStack()
                }
            }
        }

        childFragmentManager.beginTransaction()
            .add(R.id.picker_container, pickerFragment)
            .commit()
    }
}
```

## ViewPager2 Integration

Using Pix with ViewPager2:

```kotlin
class MediaPagerAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    private val fragments = mutableListOf<Fragment>()

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int) = fragments[position]

    fun addPickerFragment(options: Options, callback: (List<Uri>) -> Unit) {
        val pickerFragment = pixFragment(options) { result ->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {
                    callback(result.data)
                }
                PixEventCallback.Status.BACK_PRESSED -> {}
            }
        }
        fragments.add(pickerFragment)
        notifyItemInserted(fragments.lastIndex)
    }
}
```

## Multiple Pickers in Different Modes

```kotlin
class MultiPickerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multi_picker)

        // Image picker
        val imageOptions = Options().apply {
            count = 5
            mode = Mode.Picture
            spanCount = 4
        }

        // Video picker
        val videoOptions = Options().apply {
            count = 3
            mode = Mode.Video
            videoDurationLimitInSeconds = 30
        }

        binding.imagePickerButton.setOnClickListener {
            showImagePicker(imageOptions)
        }

        binding.videoPickerButton.setOnClickListener {
            showVideoPicker(videoOptions)
        }
    }

    private fun showImagePicker(options: Options) {
        val fragment = pixFragment(options) { result ->
            if (result.status == PixEventCallback.Status.SUCCESS) {
                handleImages(result.data)
            }
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.picker_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun showVideoPicker(options: Options) {
        val fragment = pixFragment(options) { result ->
            if (result.status == PixEventCallback.Status.SUCCESS) {
                handleVideos(result.data)
            }
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.picker_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun handleImages(uris: List<Uri>) {
        // Process images
    }

    private fun handleVideos(uris: List<Uri>) {
        // Process videos
    }
}
```

## Event Bus (Global) Usage

Listen for picker results from anywhere:

```kotlin
class ImageViewModel : ViewModel() {
    val selectedImages = MutableLiveData<List<Uri>>()

    fun observePickerResults() {
        viewModelScope.launch {
            PixBus.results.collect { result ->
                when (result.status) {
                    PixEventCallback.Status.SUCCESS -> {
                        selectedImages.value = result.data
                    }
                    PixEventCallback.Status.BACK_PRESSED -> {
                        // Handle back pressed
                    }
                }
            }
        }
    }
}

class MainActivity : AppCompatActivity() {
    private val viewModel: ImageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.selectedImages.observe(this) { images ->
            updateUI(images)
        }
        viewModel.observePickerResults()

        // Launch picker
        val options = Options()
        addPixToActivity(R.id.container, options)
    }
}
```

## Pre-selected Items

Pre-populate picker with previously selected items:

```kotlin
class ReviewFragment : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val previouslySelected = listOf(
            Uri.parse("content://media/..."),
            Uri.parse("content://media/...")
        )

        val options = Options().apply {
            count = 10
            preSelectedUrls = ArrayList(previouslySelected)
        }

        val pickerFragment = pixFragment(options) { result ->
            if (result.status == PixEventCallback.Status.SUCCESS) {
                // result.data contains all selected items
                saveSelection(result.data)
            }
        }

        childFragmentManager.beginTransaction()
            .add(R.id.picker_container, pickerFragment)
            .commit()
    }
}
```

## Using with Coroutines

Handle picker results with Flow-based approach:

```kotlin
class GalleryViewModel : ViewModel() {
    private val _selectedUris = MutableStateFlow<List<Uri>>(emptyList())
    val selectedUris: StateFlow<List<Uri>> = _selectedUris.asStateFlow()

    fun startPicker() {
        viewModelScope.launch {
            PixBus.results.collect { result ->
                when (result.status) {
                    PixEventCallback.Status.SUCCESS -> {
                        _selectedUris.value = result.data
                    }
                    PixEventCallback.Status.BACK_PRESSED -> {
                        _selectedUris.value = emptyList()
                    }
                }
            }
        }
    }
}

class GalleryFragment : Fragment() {
    private val viewModel: GalleryViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedUris.collect { uris ->
                displayImages(uris)
            }
        }
    }
}
```

## Full Sample App

For complete, working examples, refer to the sample app in the repository:

- [Fragment Sample](https://github.com/akshay2211/PixImagePicker/blob/master/app/src/main/java/io/ak1/pixsample/samples/FragmentSample.kt)
- [Navigation Sample](https://github.com/akshay2211/PixImagePicker/blob/master/app/src/main/java/io/ak1/pixsample/samples/NavControllerSample.kt)
- [ViewPager2 Sample](https://github.com/akshay2211/PixImagePicker/blob/master/app/src/main/java/io/ak1/pixsample/samples/ViewPager2Sample.kt)
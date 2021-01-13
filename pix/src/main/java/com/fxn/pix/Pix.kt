package com.fxn.pix

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.MotionEvent
import android.view.View
import android.view.View.OnLongClickListener
import android.view.View.OnTouchListener
import android.view.ViewPropertyAnimator
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fxn.adapters.InstantImageAdapter
import com.fxn.adapters.MainImageAdapter
import com.fxn.interfaces.OnSelectionListener
import com.fxn.interfaces.WorkFinish
import com.fxn.modals.Img
import com.fxn.pix.Pix
import com.fxn.utility.*
import com.fxn.utility.PermUtil.checkForCamaraWritePermissions
import com.fxn.utility.Utility.Companion.cancelAnimation
import com.fxn.utility.Utility.Companion.convertDpToPixel
import com.fxn.utility.Utility.Companion.getDateDifference
import com.fxn.utility.Utility.Companion.getFingerSpacing
import com.fxn.utility.Utility.Companion.getImageVideoCursor
import com.fxn.utility.Utility.Companion.getScreenSize
import com.fxn.utility.Utility.Companion.getSoftButtonsBarSizePort
import com.fxn.utility.Utility.Companion.getStatusBarSizePort
import com.fxn.utility.Utility.Companion.getValueInRange
import com.fxn.utility.Utility.Companion.hideStatusBar
import com.fxn.utility.Utility.Companion.isViewVisible
import com.fxn.utility.Utility.Companion.manipulateVisibility
import com.fxn.utility.Utility.Companion.scanPhoto
import com.fxn.utility.Utility.Companion.setupStatusBarHidden
import com.fxn.utility.Utility.Companion.showScrollbar
import com.fxn.utility.Utility.Companion.vibe
import com.fxn.utility.ui.FastScrollStateChangeListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.VideoResult
import com.otaliastudios.cameraview.controls.Audio
import com.otaliastudios.cameraview.controls.Facing
import com.otaliastudios.cameraview.controls.Flash
import com.otaliastudios.cameraview.controls.Mode
import com.otaliastudios.cameraview.size.AspectRatio
import com.otaliastudios.cameraview.size.SizeSelectors
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class Pix : AppCompatActivity(), OnTouchListener {
    private lateinit var camera: CameraView
    private var status_bar_height = 0
    private var BottomBarHeight = 0
    private var colorPrimaryDark = 0
    private var zoom = 0.0f
    private var dist = 0.0f
    private val handler = Handler()
    private val video_counter_handler = Handler()
    private var video_counter_runnable: Runnable? = null
    private val mFastScrollStateChangeListener: FastScrollStateChangeListener? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var instantRecyclerView: RecyclerView
    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<*>
    private var initaliseadapter: InstantImageAdapter? = null
    private lateinit var status_bar_bg: View
    private lateinit var mScrollbar: View
    private var topbar: View? = null
    private var bottomButtons: View? = null
    private lateinit var sendButton: View
    private lateinit var mBubbleView: TextView
    private var img_count: TextView? = null
    private lateinit var mHandleView: ImageView
    private lateinit var selection_back: ImageView
    private lateinit var selection_check: ImageView
    private var video_counter_progressbar: ProgressBar? = null
    private var mScrollbarAnimator: ViewPropertyAnimator? = null
    private lateinit var mBubbleAnimator: ViewPropertyAnimator
    private val selectionList: MutableSet<Img?> = HashSet()
    private val mScrollbarHider = Runnable { hideScrollbar() }
    private var mainImageAdapter: MainImageAdapter? = null
    private var mViewHeight = 0f
    private val mHideScrollbar = true
    private var LongSelection = false
    private var options: Options? = null
    private var selection_count: TextView? = null
    private val mScrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (!mHandleView.isSelected && recyclerView.isEnabled) {
                setViewPositions(getScrollProportion(recyclerView))
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (recyclerView.isEnabled) {
                when (newState) {
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        handler.removeCallbacks(mScrollbarHider)
                        if (mScrollbar.visibility != View.VISIBLE) {
                            cancelAnimation(mScrollbarAnimator)
                            if (!isViewVisible(mScrollbar) && (recyclerView.computeVerticalScrollRange()
                                            - mViewHeight > 0)) {
                                mScrollbarAnimator = showScrollbar(mScrollbar, this@Pix)
                            }
                        }
                    }
                    RecyclerView.SCROLL_STATE_IDLE -> if (mHideScrollbar && !mHandleView.isSelected) {
                        handler.postDelayed(mScrollbarHider, sScrollbarHideDelay.toLong())
                    }
                    else -> {
                    }
                }
            }
        }
    }
    private var flash: FrameLayout? = null
    private var front: ImageView? = null
    private var clickme: ImageView? = null
    private var flashDrawable = 0
    private val onCameraTouchListner = OnTouchListener { v, event ->
        if (event.pointerCount > 1) {
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_POINTER_DOWN -> dist = getFingerSpacing(event)
                MotionEvent.ACTION_MOVE -> {
                    val maxZoom = 1f
                    val newDist = getFingerSpacing(event)
                    if (newDist > dist) {
                        //zoom in
                        if (zoom < maxZoom) {
                            zoom = zoom + 0.01f
                        }
                    } else if (newDist < dist && zoom > 0) {
                        //zoom out
                        zoom = zoom - 0.01f
                    }
                    dist = newDist
                    camera.zoom = zoom
                }
                else -> {
                }
            }
        }
        false
    }
    private val onSelectionListener: OnSelectionListener = object : OnSelectionListener {
        override fun onClick(img: Img?, view: View?, position: Int) {
            if (LongSelection) {
                if (selectionList.contains(img)) {
                    selectionList.remove(img)
                    initaliseadapter!!.select(false, position)
                    mainImageAdapter!!.select(false, position)
                } else {
                    if (options!!.count <= selectionList.size) {
                        Toast.makeText(this@Pix, String.format(resources.getString(R.string.selection_limiter_pix),
                                selectionList.size), Toast.LENGTH_SHORT).show()
                        return
                    }
                    img!!.position = position
                    selectionList.add(img)
                    initaliseadapter!!.select(true, position)
                    mainImageAdapter!!.select(true, position)
                }
                if (selectionList.size == 0) {
                    LongSelection = false
                    selection_check.visibility = View.VISIBLE
                    DrawableCompat.setTint(selection_back.drawable, colorPrimaryDark)
                    topbar!!.setBackgroundColor(Color.parseColor("#ffffff"))
                    val anim: Animation = ScaleAnimation(
                            1f, 0f,  // Start and end values for the X axis scaling
                            1f, 0f,  // Start and end values for the Y axis scaling
                            Animation.RELATIVE_TO_SELF, 0.5f,  // Pivot point of X scaling
                            Animation.RELATIVE_TO_SELF, 0.5f) // Pivot point of Y scaling
                    anim.fillAfter = true // Needed to keep the result of the animation
                    anim.duration = 300
                    anim.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation) {}
                        override fun onAnimationEnd(animation: Animation) {
                            sendButton.visibility = View.GONE
                            sendButton.clearAnimation()
                        }

                        override fun onAnimationRepeat(animation: Animation) {}
                    })
                    sendButton.startAnimation(anim)
                }
                selection_count!!.text = selectionList.size.toString() + " " +
                        resources.getString(R.string.pix_selected)
                img_count!!.text = selectionList.size.toString()
            } else {
                img!!.position = position
                selectionList.add(img)
                returnObjects()
                DrawableCompat.setTint(selection_back.drawable, colorPrimaryDark)
                topbar!!.setBackgroundColor(Color.parseColor("#ffffff"))
            }
        }

        override fun onLongClick(img: Img?, view: View?, position: Int) {
            if (options!!.count > 1) {
                vibe(this@Pix, 50)
                LongSelection = true
                if (selectionList.size == 0 && (mBottomSheetBehavior.state
                                != BottomSheetBehavior.STATE_EXPANDED)) {
                    sendButton.visibility = View.VISIBLE
                    val anim: Animation = ScaleAnimation(
                            0f, 1f,  // Start and end values for the X axis scaling
                            0f, 1f,  // Start and end values for the Y axis scaling
                            Animation.RELATIVE_TO_SELF, 0.5f,  // Pivot point of X scaling
                            Animation.RELATIVE_TO_SELF, 0.5f) // Pivot point of Y scaling
                    anim.fillAfter = true // Needed to keep the result of the animation
                    anim.duration = 300
                    sendButton.startAnimation(anim)
                }
                if (selectionList.contains(img)) {
                    selectionList.remove(img)
                    initaliseadapter!!.select(false, position)
                    mainImageAdapter!!.select(false, position)
                } else {
                    if (options!!.count <= selectionList.size) {
                        Toast.makeText(this@Pix, String.format(resources.getString(R.string.selection_limiter_pix),
                                selectionList.size), Toast.LENGTH_SHORT).show()
                        return
                    }
                    img!!.position = position
                    selectionList.add(img)
                    initaliseadapter!!.select(true, position)
                    mainImageAdapter!!.select(true, position)
                }
                selection_check.visibility = View.GONE
                topbar!!.setBackgroundColor(colorPrimaryDark)
                selection_count!!.text = selectionList.size.toString() + " " + resources.getString(R.string.pix_selected)
                img_count!!.text = selectionList.size.toString()
                DrawableCompat.setTint(selection_back.drawable, Color.parseColor("#ffffff"))
            }
        }
    }
    private var video_counter_progress = 0
    private fun hideScrollbar() {
        val transX = resources.getDimensionPixelSize(R.dimen.fastscroll_scrollbar_padding_end).toFloat()
        mScrollbarAnimator = mScrollbar.animate().translationX(transX).alpha(0f)
                .setDuration(Constants.sScrollbarAnimDuration.toLong())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        mScrollbar.visibility = View.GONE
                        mScrollbarAnimator = null
                    }

                    override fun onAnimationCancel(animation: Animator) {
                        super.onAnimationCancel(animation)
                        mScrollbar.visibility = View.GONE
                        mScrollbarAnimator = null
                    }
                })
    }

    fun returnObjects() {
        val list = ArrayList<String>()
        for (i in selectionList) {
            list.add(i!!.url)
            // Log.e("Pix images", "img " + i.getUrl());
        }
        val resultIntent = Intent()
        resultIntent.putStringArrayListExtra(IMAGE_RESULTS, list)
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupStatusBarHidden(this)
        hideStatusBar(this)
        setContentView(R.layout.activity_main_lib)
        initialize()
    }

    override fun onRestart() {
        super.onRestart()
        camera.open()
        camera.mode = Mode.PICTURE
    }

    override fun onResume() {
        super.onResume()
        camera.open()
        camera.mode = Mode.PICTURE
    }

    override fun onPause() {
        camera.close()
        super.onPause()
    }

    private fun initialize() {
        val params = window.attributes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        getScreenSize(this)
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }
        try {
            options = intent.getSerializableExtra(OPTIONS) as Options?
        } catch (e: Exception) {
            e.printStackTrace()
        }
        maxVideoDuration = options!!.videoDurationLimitinSeconds * 1000 //conversion in  milli seconds
        (findViewById<View>(R.id.message_bottom) as TextView).setText(if (options!!.isExcludeVideos) R.string.pix_bottom_message_without_video else R.string.pix_bottom_message_with_video)
        status_bar_height = getStatusBarSizePort(this@Pix)
        requestedOrientation = options!!.screenOrientation
        colorPrimaryDark = ResourcesCompat.getColor(resources, R.color.colorPrimaryPix, theme)
        camera = findViewById(R.id.camera_view)
        camera.mode = Mode.PICTURE
        if (options!!.isExcludeVideos) {
            camera.audio = Audio.OFF
        }
        val width = SizeSelectors.minWidth(Utility.WIDTH)
        val height = SizeSelectors.minHeight(Utility.HEIGHT)
        val dimensions = SizeSelectors.and(width, height) // Matches sizes bigger than width X height
        val ratio = SizeSelectors.aspectRatio(AspectRatio.of(1, 2), 0f) // Matches 1:2 sizes.
        val ratio3 = SizeSelectors.aspectRatio(AspectRatio.of(2, 3), 0f) // Matches 2:3 sizes.
        val ratio2 = SizeSelectors.aspectRatio(AspectRatio.of(9, 16), 0f) // Matches 9:16 sizes.
        val result = SizeSelectors.or(
                SizeSelectors.and(ratio, dimensions),
                SizeSelectors.and(ratio2, dimensions),
                SizeSelectors.and(ratio3, dimensions)
        )
        camera.setPictureSize(result)
        camera.setVideoSize(result)
        camera.setLifecycleOwner(this@Pix)
        if (options!!.isFrontfacing) {
            camera.facing = Facing.FRONT
        } else {
            camera.facing = Facing.BACK
        }
        camera.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                val dir = getExternalFilesDir(options!!.path)
                if (!dir!!.exists()) {
                    dir.mkdirs()
                }
                val photo = File(dir, "IMG_"
                        + SimpleDateFormat("yyyyMMdd_HHmmSS", Locale.ENGLISH).format(Date())
                        + ".jpg")
                result.toFile(photo) { photo ->
                    vibe(this@Pix, 50)
                    val img = Img("", "", photo!!.absolutePath, "", 1)
                    selectionList.add(img)
                    scanPhoto(this@Pix, photo)
                    returnObjects()
                }
            }

            override fun onVideoTaken(result: VideoResult) {
                // A Video was taken!
                vibe(this@Pix, 50)
                val img = Img("", "", result.file.absolutePath, "", 3)
                selectionList.add(img)
                scanPhoto(this@Pix, result.file)
                camera.mode = Mode.PICTURE
                returnObjects()
            }

            override fun onVideoRecordingStart() {
                findViewById<View>(R.id.video_counter_layout).visibility = View.VISIBLE
                video_counter_progress = 0
                video_counter_progressbar!!.progress = 0
                video_counter_runnable = object : Runnable {
                    override fun run() {
                        ++video_counter_progress
                        video_counter_progressbar!!.progress = video_counter_progress
                        val textView = findViewById<TextView>(R.id.video_counter)
                        var counter = ""
                        var min = 0
                        var sec = "" + video_counter_progress
                        if (video_counter_progress > 59) {
                            min = video_counter_progress / 60
                            sec = "" + (video_counter_progress - 60 * min)
                        }
                        if (sec.length == 1) {
                            sec = "0$sec"
                        }
                        counter = "$min:$sec"
                        textView.text = counter
                        video_counter_handler.postDelayed(this, 1000)
                    }
                }
                video_counter_handler.postDelayed(video_counter_runnable!!, 1000)
                clickme!!.animate().scaleX(1.2f).scaleY(1.2f).setDuration(300).setInterpolator(AccelerateDecelerateInterpolator()).start()
                flash!!.animate().alpha(0f).setDuration(300).setInterpolator(AccelerateDecelerateInterpolator()).start()
                findViewById<View>(R.id.message_bottom).animate().alpha(0f).setDuration(300).setInterpolator(AccelerateDecelerateInterpolator()).start()
                front!!.animate().alpha(0f).setDuration(300).setInterpolator(AccelerateDecelerateInterpolator()).start()
            }

            override fun onVideoRecordingEnd() {
                findViewById<View>(R.id.video_counter_layout).visibility = View.GONE
                video_counter_handler.removeCallbacks(video_counter_runnable!!)
                clickme!!.animate().scaleX(1f).scaleY(1f).setDuration(300).setInterpolator(AccelerateDecelerateInterpolator()).start()
                findViewById<View>(R.id.message_bottom).animate().scaleX(1f).scaleY(1f).setDuration(300).setInterpolator(AccelerateDecelerateInterpolator()).start()
                flash!!.animate().alpha(1f).setDuration(300).setInterpolator(AccelerateDecelerateInterpolator()).start()
                front!!.animate().alpha(1f).setDuration(300).setInterpolator(AccelerateDecelerateInterpolator()).start()
            } // And much more
        })
        zoom = 0.0f
        flash = findViewById(R.id.flash)
        clickme = findViewById(R.id.clickme)
        front = findViewById(R.id.front)
        topbar = findViewById(R.id.topbar)
        video_counter_progressbar = findViewById(R.id.video_pbr)
        selection_count = findViewById(R.id.selection_count)
        selection_back = findViewById(R.id.selection_back)
        selection_check = findViewById(R.id.selection_check)
        selection_check.visibility = if (options!!.count > 1) View.VISIBLE else View.GONE
        sendButton = findViewById(R.id.sendButton)
        img_count = findViewById(R.id.img_count)
        mBubbleView = findViewById(R.id.fastscroll_bubble)
        mHandleView = findViewById(R.id.fastscroll_handle)
        mScrollbar = findViewById(R.id.fastscroll_scrollbar)
        mScrollbar.visibility = View.GONE
        mBubbleView.visibility = View.GONE
        bottomButtons = findViewById(R.id.bottomButtons)
        TOPBAR_HEIGHT = convertDpToPixel(56f, this@Pix)
        status_bar_bg = findViewById(R.id.status_bar_bg)
        status_bar_bg.layoutParams.height = status_bar_height
        status_bar_bg.translationY = (-1 * status_bar_height).toFloat()
        status_bar_bg.requestLayout()
        instantRecyclerView = findViewById(R.id.instantRecyclerView)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        instantRecyclerView.layoutManager = linearLayoutManager
        initaliseadapter = InstantImageAdapter(this)
        initaliseadapter!!.addOnSelectionListener(onSelectionListener)
        instantRecyclerView.adapter = initaliseadapter
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.addOnScrollListener(mScrollListener)
        val mainFrameLayout = findViewById<FrameLayout>(R.id.mainFrameLayout)
        val main_content = findViewById<CoordinatorLayout>(R.id.main_content)
        BottomBarHeight = getSoftButtonsBarSizePort(this)
        val lp1 = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT)
        lp1.setMargins(0, status_bar_height, 0, 0)
        main_content.layoutParams = lp1
        val layoutParams = sendButton.layoutParams as FrameLayout.LayoutParams
        layoutParams.setMargins(0, 0, convertDpToPixel(16f, this).toInt(),
                convertDpToPixel(174f, this).toInt())
        sendButton.layoutParams = layoutParams
        mainImageAdapter = MainImageAdapter(this, options!!.spanCount)
        val mLayoutManager = GridLayoutManager(this, MainImageAdapter.SPAN_COUNT)
        mLayoutManager.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (mainImageAdapter!!.getItemViewType(position) == MainImageAdapter.HEADER) {
                    MainImageAdapter.SPAN_COUNT
                } else 1
            }
        }
        recyclerView.layoutManager = mLayoutManager
        recyclerView.setHasFixedSize(true)
        recyclerView.setItemViewCacheSize(20)
        recyclerView.isDrawingCacheEnabled = true
        recyclerView.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
        mainImageAdapter!!.addOnSelectionListener(onSelectionListener)
        mainImageAdapter!!.setHasStableIds(true)
        recyclerView.adapter = mainImageAdapter
        recyclerView.addItemDecoration(HeaderItemDecoration(this, mainImageAdapter!!))
        mHandleView.setOnTouchListener(this)
        onClickMethods()
        flashDrawable = R.drawable.ic_flash_off_black_24dp
        if (options!!.preSelectedUrls.size > options!!.count) {
            val large = options!!.preSelectedUrls.size - 1
            val small = options!!.count
            for (i in large downTo small - 1 + 1) {
                options!!.preSelectedUrls.removeAt(i)
            }
        }
        DrawableCompat.setTint(selection_back.drawable, colorPrimaryDark)
        updateImages()
    }

    private fun onClickMethods() {
        clickme!!.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                findViewById<View>(R.id.clickmebg).visibility = View.GONE
                findViewById<View>(R.id.clickmebg).animate().scaleX(1f).scaleY(1f).setDuration(300).setInterpolator(AccelerateDecelerateInterpolator()).start()
                clickme!!.animate().scaleX(1f).scaleY(1f).setDuration(300).setInterpolator(AccelerateDecelerateInterpolator()).start()
            } else if (event.action == MotionEvent.ACTION_DOWN) {
                findViewById<View>(R.id.clickmebg).visibility = View.VISIBLE
                findViewById<View>(R.id.clickmebg).animate().scaleX(1.2f).scaleY(1.2f).setDuration(300).setInterpolator(AccelerateDecelerateInterpolator()).start()
                clickme!!.animate().scaleX(1.2f).scaleY(1.2f).setDuration(300).setInterpolator(AccelerateDecelerateInterpolator()).start()
            }
            if (event.action == MotionEvent.ACTION_UP && camera.isTakingVideo) {
                camera.stopVideo()
            }
            false
        }
        clickme!!.setOnLongClickListener(OnLongClickListener {
            if (options!!.isExcludeVideos) {
                return@OnLongClickListener false
            }
            camera.mode = Mode.VIDEO
            val dir = getExternalFilesDir(options!!.path)
            if (!dir!!.exists()) {
                dir.mkdirs()
            }
            val video = File(dir, "VID_"
                    + SimpleDateFormat("yyyyMMdd_HHmmSS", Locale.ENGLISH).format(Date())
                    + ".mp4")
            video_counter_progressbar!!.max = maxVideoDuration / 1000
            video_counter_progressbar!!.invalidate()
            camera.takeVideo(video, maxVideoDuration)
            true
        })
        clickme!!.setOnClickListener(View.OnClickListener {
            if (selectionList.size >= options!!.count) {
                Toast.makeText(this@Pix, String.format(resources.getString(R.string.cannot_click_image_pix),
                        "" + options!!.count), Toast.LENGTH_LONG).show()
                return@OnClickListener
            }
            if (camera.mode == Mode.VIDEO) {
                return@OnClickListener
            }
            val oj = ObjectAnimator.ofFloat(camera, "alpha", 1f, 0f, 0f, 1f)
            oj.startDelay = 200L
            oj.duration = 600L
            oj.start()
            camera.takePicture()
            return@OnClickListener
        })
        findViewById<View>(R.id.selection_ok).setOnClickListener { returnObjects() }
        sendButton.setOnClickListener { returnObjects() }
        selection_back.setOnClickListener { mBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED }
        selection_check.setOnClickListener {
            topbar!!.setBackgroundColor(colorPrimaryDark)
            selection_count!!.text = resources.getString(R.string.pix_tap_to_select)
            img_count!!.text = selectionList.size.toString()
            DrawableCompat.setTint(selection_back.drawable, Color.parseColor("#ffffff"))
            LongSelection = true
            selection_check.visibility = View.GONE
        }
        val iv = flash!!.getChildAt(0) as ImageView
        flash!!.setOnClickListener {
            val height = flash!!.height
            iv.animate()
                    .translationY(height.toFloat())
                    .setDuration(100)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            iv.translationY = -(height / 2).toFloat()
                            if (flashDrawable == R.drawable.ic_flash_auto_black_24dp) {
                                flashDrawable = R.drawable.ic_flash_off_black_24dp
                                iv.setImageResource(flashDrawable)
                                camera.flash = Flash.OFF
                            } else if (flashDrawable == R.drawable.ic_flash_off_black_24dp) {
                                flashDrawable = R.drawable.ic_flash_on_black_24dp
                                iv.setImageResource(flashDrawable)
                                camera.flash = Flash.ON
                            } else {
                                flashDrawable = R.drawable.ic_flash_auto_black_24dp
                                iv.setImageResource(flashDrawable)
                                camera.flash = Flash.AUTO
                            }
                            iv.animate().translationY(0f).setDuration(50).setListener(null).start()
                        }
                    })
                    .start()
        }
        front!!.setOnClickListener {
            val oa1 = ObjectAnimator.ofFloat(front, "scaleX", 1f, 0f).setDuration(150)
            val oa2 = ObjectAnimator.ofFloat(front, "scaleX", 0f, 1f).setDuration(150)
            oa1.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    front!!.setImageResource(R.drawable.ic_photo_camera)
                    oa2.start()
                }
            })
            oa1.start()
            if (options!!.isFrontfacing) {
                options!!.isFrontfacing = false
                camera.facing = Facing.BACK
            } else {
                camera.facing = Facing.FRONT
                options!!.isFrontfacing = true
            }
        }
    }

    private fun updateImages() {
        mainImageAdapter!!.clearList()
        val cursor = getImageVideoCursor(this@Pix, options!!.isExcludeVideos)
                ?: return
        val INSTANTLIST = ArrayList<Img>()
        var header = ""
        var limit = 100
        if (cursor.count < limit) {
            limit = cursor.count - 1
        }
        val data = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)
        val mediaType = cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE)
        val contentUrl = cursor.getColumnIndex(MediaStore.Files.FileColumns._ID)
        //int videoDate = cursor.getColumnIndex(MediaStore.Video.Media.DATE_TAKEN);
        val imageDate = cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)
        var calendar: Calendar
        var pos = 0
        for (i in 0 until limit) {
            cursor.moveToNext()
            val path = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    "" + cursor.getInt(contentUrl))
            calendar = Calendar.getInstance()
            calendar.timeInMillis = cursor.getLong(imageDate) * 1000
            //Log.e("time",i+"->"+new SimpleDateFormat("hh:mm:ss dd/MM/yyyy",Locale.ENGLISH).format(calendar.getTime()));
            val dateDifference = getDateDifference(this@Pix, calendar)
            if (!header.equals("" + dateDifference, ignoreCase = true)) {
                header = "" + dateDifference
                pos += 1
                INSTANTLIST.add(Img("" + dateDifference, "", "", "", cursor.getInt(mediaType)))
            }
            val img = Img("" + header, "" + path, cursor.getString(data), "" + pos, cursor.getInt(mediaType))
            img.position = pos
            if (options!!.preSelectedUrls.contains(img.url)) {
                img.selected = true
                selectionList.add(img)
            }
            pos += 1
            INSTANTLIST.add(img)
        }
        if (selectionList.size > 0) {
            LongSelection = true
            sendButton.visibility = View.VISIBLE
            val anim: Animation = ScaleAnimation(
                    0f, 1f,  // Start and end values for the X axis scaling
                    0f, 1f,  // Start and end values for the Y axis scaling
                    Animation.RELATIVE_TO_SELF, 0.5f,  // Pivot point of X scaling
                    Animation.RELATIVE_TO_SELF, 0.5f) // Pivot point of Y scaling
            anim.fillAfter = true // Needed to keep the result of the animation
            anim.duration = 300
            sendButton.startAnimation(anim)
            selection_check.visibility = View.GONE
            topbar!!.setBackgroundColor(colorPrimaryDark)
            selection_count!!.text = selectionList.size.toString() + " " +
                    resources.getString(R.string.pix_selected)
            img_count!!.text = selectionList.size.toString()
            DrawableCompat.setTint(selection_back.drawable, Color.parseColor("#ffffff"))
        }
        mainImageAdapter!!.addImageList(INSTANTLIST)
        initaliseadapter!!.addImageList(INSTANTLIST)
        imageVideoFetcher = object : ImageVideoFetcher(this@Pix) {
            override fun onPostExecute(modelList: ModelList) {
                super.onPostExecute(modelList)
                mainImageAdapter!!.addImageList(modelList.lIST)
                initaliseadapter!!.addImageList(modelList.lIST)
                selectionList.addAll(modelList.selection)
                if (selectionList.size > 0) {
                    LongSelection = true
                    sendButton.visibility = View.VISIBLE
                    val anim: Animation = ScaleAnimation(
                            0f, 1f,  // Start and end values for the X axis scaling
                            0f, 1f,  // Start and end values for the Y axis scaling
                            Animation.RELATIVE_TO_SELF, 0.5f,  // Pivot point of X scaling
                            Animation.RELATIVE_TO_SELF, 0.5f) // Pivot point of Y scaling
                    anim.fillAfter = true // Needed to keep the result of the animation
                    anim.duration = 300
                    sendButton.startAnimation(anim)
                    selection_check.visibility = View.GONE
                    topbar!!.setBackgroundColor(colorPrimaryDark)
                    selection_count!!.text = selectionList.size.toString() + " " +
                            resources.getString(R.string.pix_selected)
                    img_count!!.text = selectionList.size.toString()
                    DrawableCompat.setTint(selection_back.drawable, Color.parseColor("#ffffff"))
                }
            }
        }
        imageVideoFetcher.startingCount = pos
        imageVideoFetcher.header = header
        imageVideoFetcher.setPreSelectedUrls(options!!.preSelectedUrls)
        imageVideoFetcher.execute(getImageVideoCursor(this@Pix, options!!.isExcludeVideos))
        cursor.close()
        setBottomSheetBehavior()
    }

    private fun setBottomSheetBehavior() {
        val bottomSheet = findViewById<View>(R.id.bottom_sheet)
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        mBottomSheetBehavior.peekHeight = convertDpToPixel(194f, this).toInt()
        mBottomSheetBehavior.setBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {}
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                manipulateVisibility(this@Pix, slideOffset, findViewById(R.id.arrow_up),
                        instantRecyclerView, recyclerView, status_bar_bg,
                        topbar!!, bottomButtons!!, sendButton, LongSelection)
                if (slideOffset == 1f) {
                    showScrollbar(mScrollbar, this@Pix)
                    mainImageAdapter!!.notifyDataSetChanged()
                    mViewHeight = mScrollbar.measuredHeight.toFloat()
                    handler.post { setViewPositions(getScrollProportion(recyclerView)) }
                    sendButton.visibility = View.GONE
                    //  fotoapparat.stop();
                } else if (slideOffset == 0f) {
                    initaliseadapter!!.notifyDataSetChanged()
                    hideScrollbar()
                    img_count!!.text = selectionList.size.toString()
                    camera.open()
                }
            }
        })
    }

    private fun getScrollProportion(recyclerView: RecyclerView?): Float {
        val verticalScrollOffset = recyclerView!!.computeVerticalScrollOffset()
        val verticalScrollRange = recyclerView.computeVerticalScrollRange()
        val rangeDiff = verticalScrollRange - mViewHeight
        val proportion = verticalScrollOffset.toFloat() / if (rangeDiff > 0) rangeDiff else 1f
        return mViewHeight * proportion
    }

    private fun setViewPositions(y: Float) {
        val handleY = getValueInRange(0, (mViewHeight - mHandleView.height).toInt(),
                (y - mHandleView.height / 2).toInt())
        mBubbleView.y = handleY + convertDpToPixel(60f, this)
        mHandleView.y = handleY.toFloat()
    }

    private fun setRecyclerViewPosition(y: Float) {
        if (recyclerView != null && recyclerView.adapter != null) {
            val itemCount = recyclerView.adapter!!.itemCount
            val proportion: Float
            proportion = if (mHandleView.y == 0f) {
                0f
            } else if (mHandleView.y + mHandleView.height >= mViewHeight - sTrackSnapRange) {
                1f
            } else {
                y / mViewHeight
            }
            val scrolledItemCount = Math.round(proportion * itemCount)
            val targetPos = getValueInRange(0, itemCount - 1, scrolledItemCount)
            recyclerView.layoutManager!!.scrollToPosition(targetPos)
            if (mainImageAdapter != null) {
                val text = mainImageAdapter!!.getSectionMonthYearText(targetPos)
                mBubbleView.text = text
                if (text.equals("", ignoreCase = true)) {
                    mBubbleView.visibility = View.GONE
                }
            }
        }
    }

    private fun showBubble() {
        if (!isViewVisible(mBubbleView)) {
            mBubbleView.visibility = View.VISIBLE
            mBubbleView.alpha = 0f
            mBubbleAnimator = mBubbleView
                    .animate()
                    .alpha(1f)
                    .setDuration(sBubbleAnimDuration.toLong())
                    .setListener(object : AnimatorListenerAdapter() { // adapter required for new alpha value to stick
                    })
            mBubbleAnimator.start()
        }
    }

    private fun hideBubble() {
        if (isViewVisible(mBubbleView)) {
            mBubbleAnimator = mBubbleView.animate().alpha(0f)
                    .setDuration(sBubbleAnimDuration.toLong())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            mBubbleView.visibility = View.GONE
                        }

                        override fun onAnimationCancel(animation: Animator) {
                            super.onAnimationCancel(animation)
                            mBubbleView.visibility = View.GONE
                        }
                    })
            mBubbleAnimator.start()
        }
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (event.x < mHandleView.x - ViewCompat.getPaddingStart(mHandleView)) {
                    return false
                }
                mHandleView.isSelected = true
                handler.removeCallbacks(mScrollbarHider)
                cancelAnimation(mScrollbarAnimator)
                cancelAnimation(mBubbleAnimator)
                if (!isViewVisible(mScrollbar) && (recyclerView.computeVerticalScrollRange()
                                - mViewHeight > 0)) {
                    mScrollbarAnimator = showScrollbar(mScrollbar, this@Pix)
                }
                if (mainImageAdapter != null) {
                    showBubble()
                }
                mFastScrollStateChangeListener?.onFastScrollStart(this)
                val y = event.rawY
                setViewPositions(y - TOPBAR_HEIGHT)
                setRecyclerViewPosition(y)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val y = event.rawY
                setViewPositions(y - TOPBAR_HEIGHT)
                setRecyclerViewPosition(y)
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mHandleView.isSelected = false
                if (mHideScrollbar) {
                    handler.postDelayed(mScrollbarHider, sScrollbarHideDelay.toLong())
                }
                hideBubble()
                mFastScrollStateChangeListener?.onFastScrollStop(this)
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onBackPressed() {
        if (selectionList.size > 0) {
            for (img in selectionList) {
                options!!.preSelectedUrls = ArrayList()
                mainImageAdapter!!.itemList[img!!.position].selected = false
                mainImageAdapter!!.notifyItemChanged(img.position)
                initaliseadapter!!.itemList[img.position].selected = false
                initaliseadapter!!.notifyItemChanged(img.position)
            }
            LongSelection = false
            if (options!!.count > 1) {
                selection_check.visibility = View.VISIBLE
            }
            DrawableCompat.setTint(selection_back.drawable, colorPrimaryDark)
            topbar!!.setBackgroundColor(Color.parseColor("#ffffff"))
            val anim: Animation = ScaleAnimation(
                    1f, 0f,  // Start and end values for the X axis scaling
                    1f, 0f,  // Start and end values for the Y axis scaling
                    Animation.RELATIVE_TO_SELF, 0.5f,  // Pivot point of X scaling
                    Animation.RELATIVE_TO_SELF, 0.5f) // Pivot point of Y scaling
            anim.fillAfter = true // Needed to keep the result of the animation
            anim.duration = 300
            anim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    sendButton.visibility = View.GONE
                    sendButton.clearAnimation()
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
            sendButton.startAnimation(anim)
            selectionList.clear()
        } else if (mBottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        camera.destroy()
    }

    companion object {
        private const val sBubbleAnimDuration = 1000
        private const val sScrollbarHideDelay = 1000
        private const val OPTIONS = "options"
        private const val sTrackSnapRange = 5
        var IMAGE_RESULTS = "image_results"
        var TOPBAR_HEIGHT = 0f
        private var maxVideoDuration = 40000
        private lateinit var imageVideoFetcher: ImageVideoFetcher
        fun start(context: Fragment, options: Options) {
            checkForCamaraWritePermissions(context, object : WorkFinish {
                override fun onWorkFinish(check: Boolean?) {
                    val i = Intent(context.activity, Pix::class.java)
                    i.putExtra(OPTIONS, options)
                    context.startActivityForResult(i, options.requestCode)
                }
            })
        }

        fun start(context: Fragment, requestCode: Int) {
            start(context, Options.init().setRequestCode(requestCode).setCount(1))
        }

        fun start(context: FragmentActivity, options: Options) {
            checkForCamaraWritePermissions(context, object : WorkFinish {
                override fun onWorkFinish(check: Boolean?) {
                    val i = Intent(context, Pix::class.java)
                    i.putExtra(OPTIONS, options)
                    context.startActivityForResult(i, options.requestCode)
                }
            })
        }

        fun start(context: FragmentActivity, requestCode: Int) {
            start(context, Options.init().setRequestCode(requestCode).setCount(1))
        }
    }
}
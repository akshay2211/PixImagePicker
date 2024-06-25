package io.ak1.pix.helpers

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import androidx.camera.core.ImageCapture
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.FragmentActivity
import io.ak1.pix.R
import io.ak1.pix.models.Flash
import io.ak1.pix.models.Mode
import io.ak1.pix.models.Options
import io.ak1.pix.models.PixViewModel
import io.ak1.pix.utility.PixBindings
import io.ak1.pix.utility.TAG

/**
 * Created By Akshay Sharma on 17,June,2021
 * https://ak1.io
 */

fun PixBindings.setDrawableIconForFlash(options: Options) {
    controlsLayout.flashImage.setImageResource(
        when (options.flash) {
            Flash.Off -> R.drawable.ic_flash_off_black_24dp
            Flash.On -> R.drawable.ic_flash_on_black_24dp
            else -> R.drawable.ic_flash_auto_black_24dp
        }
    )
}

fun ViewGroup.setOnClickForFLash(options: Options, callback: (Options) -> Unit) {
    val iv = getChildAt(0) as ImageView
    setOnClickListener {
        val height = height
        iv.animate()
            .translationY(height.toFloat())
            .setDuration(100)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    iv.translationY = -(height / 2).toFloat()
                    when (options.flash) {
                        Flash.Auto -> {
                            options.flash = Flash.Off
                        }
                        Flash.Off -> {
                            options.flash = Flash.On
                        }
                        else -> {
                            options.flash = Flash.Auto
                        }
                    }
                    callback(options)
                    iv.animate().translationY(0f).setDuration(50).setStartDelay(100)
                        .setListener(null).start()
                }
            })
            .start()
    }
}

@SuppressLint("ClickableViewAccessibility,RestrictedApi")
internal fun PixBindings.setupClickControls(
    model: PixViewModel,
    cameraXManager: CameraXManager?,
    options: Options,
    callback: (Int, Uri) -> Unit
) {
    controlsLayout.messageBottom.setText(
        when (options.mode) {
            Mode.Picture -> R.string.pix_bottom_message_without_video
            Mode.Video -> R.string.pix_bottom_message_with_only_video
            else -> R.string.pix_bottom_message_with_video
        }
    )
    controlsLayout.primaryClickButton.apply {
        var videoCounterProgress: Int

        val videoCounterHandler = Handler(Looper.getMainLooper())
        lateinit var videoCounterRunnable: Runnable

        setOnClickListener {
            if (options.count <= model.selectionListSize) {
                gridLayout.sendButton.context.toast(model.selectionListSize)
                return@setOnClickListener
            }
            cameraXManager?.takePhoto { uri, exc ->
                if (exc == null) {
                    val newUri = Uri.parse(uri.toString())
                    callback(3, newUri)
                } else {
                    Log.e(TAG, "$exc")
                }
            }
            isEnabled = false
            Handler(Looper.getMainLooper()).postDelayed({
                isEnabled = true
            }, 1000L)

        }
        var isRecording = false
        setOnLongClickListener {
            if (options.mode == Mode.Picture) {
                return@setOnLongClickListener false
            }

            if (options.count <= model.selectionListSize) {
                gridLayout.sendButton.context.toast(model.selectionListSize)
                return@setOnLongClickListener false
            }
            callback(4, Uri.EMPTY)
            isRecording = true
            videoCounterLayout.videoCounterLayout.show()
            videoCounterProgress = 0
            videoCounterLayout.videoPbr.progress = 0
            videoCounterRunnable = object : Runnable {
                override fun run() {
                    ++videoCounterProgress

                    videoCounterLayout.videoPbr.progress = videoCounterProgress
                    videoCounterLayout.videoCounter.text =
                        videoCounterProgress.counterText


                    if (videoCounterProgress > options.videoOptions.videoDurationLimitInSeconds) {
                        gridLayout.initialRecyclerviewContainer.apply {
                            alpha = 1f
                            translationY = 0f
                        }
                        callback(5, Uri.EMPTY)
                        isRecording = false
                        videoCounterLayout.videoCounterLayout.hide()
                        videoCounterHandler.removeCallbacks(videoCounterRunnable)
                        videoRecordingEndAnim()
                        cameraXManager?.recording?.stop()
                    } else {
                        videoCounterHandler.postDelayed(this, 1000)
                    }
                }
            }
            videoCounterHandler.postDelayed(videoCounterRunnable, 1000)
            videoRecordingStartAnim()
            val maxVideoDuration = options.videoOptions.videoDurationLimitInSeconds
            videoCounterLayout.videoPbr.max = maxVideoDuration / 1000
            videoCounterLayout.videoPbr.invalidate()
            gridLayout.initialRecyclerviewContainer.animate().translationY(500f).alpha(0f)
                .setDuration(200).start()
            cameraXManager?.takeVideo { uri, exc ->
                if (exc == null) {
                    callback(3, uri)
                } else {
                    Log.e(TAG, "$exc")
                }

            }
            true
        }
        setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                controlsLayout.primaryClickBackground.hide()
                controlsLayout.primaryClickBackground.animate().scaleX(1f).scaleY(1f)
                    .setDuration(300).setInterpolator(
                        AccelerateDecelerateInterpolator()
                    ).start()
                controlsLayout.primaryClickButton.animate().scaleX(1f)
                    .scaleY(1f).setDuration(300).setInterpolator(
                        AccelerateDecelerateInterpolator()
                    ).start()
                fragmentPix.root.requestDisallowInterceptTouchEvent(false)
            } else if (event.action == MotionEvent.ACTION_DOWN) {
                controlsLayout.primaryClickBackground.show()
                controlsLayout.primaryClickBackground.animate().scaleX(1.2f).scaleY(1.2f)
                    .setDuration(300).setInterpolator(AccelerateDecelerateInterpolator()).start()
                controlsLayout.primaryClickButton.animate().scaleX(1.2f)
                    .scaleY(1.2f).setDuration(300)
                    .setInterpolator(AccelerateDecelerateInterpolator()).start()
                fragmentPix.root.requestDisallowInterceptTouchEvent(true)
            }
            if ((event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) && isRecording) {
                gridLayout.initialRecyclerviewContainer.apply {
                    alpha = 1f
                    translationY = 0f
                }
                callback(5, Uri.EMPTY)
                isRecording = false
                videoCounterLayout.videoCounterLayout.hide()
                videoCounterHandler.removeCallbacks(videoCounterRunnable)
                videoRecordingEndAnim()
                cameraXManager?.recording?.stop()
            }
            false
        }
        gridLayout.selectionOk.setOnClickListener { callback(0, Uri.EMPTY) }
        gridLayout.sendButton.setOnClickListener { callback(0, Uri.EMPTY) }
        gridLayout.selectionBack.setOnClickListener { callback(1, Uri.EMPTY) }
        gridLayout.selectionCheck.setOnClickListener {
            gridLayout.selectionCheck.hide()
            callback(2, Uri.EMPTY)
        }
    }
    controlsLayout.flashButton.setOnClickForFLash(options) {
        setDrawableIconForFlash(it)
        cameraXManager?.imageCapture?.flashMode = when (options.flash) {
            Flash.Auto -> ImageCapture.FLASH_MODE_AUTO
            Flash.Off -> ImageCapture.FLASH_MODE_OFF
            Flash.On -> ImageCapture.FLASH_MODE_ON
            else -> ImageCapture.FLASH_MODE_AUTO
        }
    }
    controlsLayout.lensFacing.setOnClickListener {
        val oa1 = ObjectAnimator.ofFloat(
            controlsLayout.lensFacing,
            "scaleX",
            1f,
            0f
        ).setDuration(150)
        val oa2 = ObjectAnimator.ofFloat(
            controlsLayout.lensFacing,
            "scaleX",
            0f,
            1f
        ).setDuration(150)
        oa1.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                controlsLayout.lensFacing.setImageResource(R.drawable.ic_photo_camera)
                oa2.start()
            }
        })
        oa1.start()
        options.isFrontFacing = !options.isFrontFacing
        cameraXManager?.bindCameraUseCases(this)
    }
}

fun PixBindings.longSelectionStatus(
    enabled: Boolean
) {
    val colorPrimaryDark = fragmentPix.root.context.color(R.color.primary_color_pix)
    val colorSurface = fragmentPix.root.context.color(R.color.surface_color_pix)

    if (enabled) {
        gridLayout.selectionCheck.hide()
        gridLayout.selectionCount.setTextColor(colorSurface)
        gridLayout.topbar.setBackgroundColor(colorPrimaryDark)
        DrawableCompat.setTint(gridLayout.selectionBack.drawable, colorSurface)
        DrawableCompat.setTint(gridLayout.selectionCheck.drawable, colorSurface)
    } else {
        gridLayout.selectionCheck.show()
        DrawableCompat.setTint(gridLayout.selectionBack.drawable, colorPrimaryDark)
        DrawableCompat.setTint(gridLayout.selectionCheck.drawable, colorPrimaryDark)
        gridLayout.topbar.setBackgroundColor(colorSurface)
    }
}

fun PixBindings.setSelectionText(fragmentActivity: FragmentActivity, size: Int = 0) {
    gridLayout.selectionCount.text = if (size == 0) {
        gridLayout.selectionOk.hide()
        fragmentActivity.resources.getString(R.string.pix_tap_to_select)
    } else {
        gridLayout.selectionOk.show()
        "$size ${fragmentActivity.resources.getString(R.string.pix_selected)}"
    }
    gridLayout.imgCount.text = size.toString()
}

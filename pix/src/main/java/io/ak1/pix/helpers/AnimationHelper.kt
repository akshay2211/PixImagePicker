package io.ak1.pix.helpers

import android.view.animation.AccelerateDecelerateInterpolator
import io.ak1.pix.databinding.FragmentPixBinding
import io.ak1.pix.utility.PixBindings

/**
 * Created By Akshay Sharma on 17,June,2021
 * https://ak1.io
 */


fun PixBindings.videoRecordingStartAnim() {
    val adInterpolator = AccelerateDecelerateInterpolator()
    controlsLayout.primaryClickButton.animate().apply {
        scaleX(1.2f)
        scaleY(1.2f)
        duration = 300
        interpolator = adInterpolator
    }.start()
    controlsLayout.flashButton.animate().apply {
        alpha(0f)
        duration = 300
        interpolator = adInterpolator
    }.start()
    controlsLayout.messageBottom.animate().apply {
        alpha(0f)
        duration = 300
        interpolator = adInterpolator
    }.start()
    controlsLayout.lensFacing.animate().apply {
        alpha(0f)
        duration = 300
        interpolator = adInterpolator
    }.start()
}

fun PixBindings.videoRecordingEndAnim() {
    val adInterpolator = AccelerateDecelerateInterpolator()
    controlsLayout.primaryClickButton.animate().apply {
        scaleX(1f)
        scaleY(1f)
        duration = 300
        interpolator = adInterpolator
    }.start()
    controlsLayout.flashButton.animate().apply {
        alpha(1f)
        duration = 300
        interpolator = adInterpolator
    }.start()
    controlsLayout.messageBottom.animate().apply {
        scaleX(1f)
        scaleY(1f)
        duration = 300
        interpolator = adInterpolator
    }.start()
    controlsLayout.lensFacing.animate().apply {
        alpha(1f)
        duration = 300
        interpolator = adInterpolator
    }.start()
}

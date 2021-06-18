package io.ak1.pix.helpers

import android.app.Activity
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.core.view.WindowCompat
import io.ak1.pix.utility.WIDTH

/**
 * Created By Akshay Sharma on 17,June,2021
 * https://ak1.io
 */


fun Activity.setupScreen() {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        window.attributes.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
    }
    getScreenSize()
}

fun Activity.hideStatusBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.insetsController?.let {
            it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            it.hide(WindowInsets.Type.statusBars())
        }
    } else {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }
}

fun Activity.showStatusBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.setDecorFitsSystemWindows(false)
        window.insetsController?.show(WindowInsets.Type.statusBars())
    } else {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
    }
}

val Activity.statusBarHeight: Int
    get() {
        val resourceId =
            baseContext.resources.getIdentifier("status_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }
val Activity.navigationBarHeight: Int
    get() {
        val resourceId =
            baseContext.resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }

fun Activity.getScreenSize() {
    WIDTH = DisplayMetrics().apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            display?.getRealMetrics(this)
        } else {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getMetrics(this)
        }
    }.widthPixels
}
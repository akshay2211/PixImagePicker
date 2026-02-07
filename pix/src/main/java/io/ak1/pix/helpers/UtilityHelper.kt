/*
 * Copyright (C) 2026 Akshay Sharma
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.ak1.pix.helpers

import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.ak1.pix.R
import io.ak1.pix.adapters.MainImageAdapter
import io.ak1.pix.utility.HeaderItemDecoration
import io.ak1.pix.utility.IMAGE_VIDEO_URI
import io.ak1.pix.utility.PixBindings
import java.io.File

/**
 * Created By Akshay Sharma on 17,June,2021
 * https://ak1.io
 */

fun Context.toDp(px: Float): Float =
    px / (resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)

fun Context.toPx(dp: Float): Float =
    dp * (resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)

fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

val Int.counterText: String
    get() {
        var min = 0
        var sec = "" + this
        if (this > 59) {
            min = this / 60
            sec = "" + (this - 60 * min)
        }
        if (sec.length == 1) {
            sec = "0$sec"
        }
        return "$min:$sec"
    }

fun Context.scanPhoto(file: File, callback: ((Uri) -> Unit)? = null) {
    MediaScannerConnection.scanFile(
        this,
        arrayOf(file.toString()),
        arrayOf(file.name),
    ) { _, uri ->
        val mainUri = Uri.withAppendedPath(
            IMAGE_VIDEO_URI,
            uri.lastPathSegment
        )
        callback?.invoke(mainUri)
    }
}

fun FragmentActivity.setUpMargins(binding: PixBindings) {
    val height =
        if (this@setUpMargins.navigationBarHeight < 50) 0 else this@setUpMargins.navigationBarHeight
    binding.gridLayout.mainContent.updateLayoutParams<ViewGroup.MarginLayoutParams> {
        updateMargins(
            0,
            this@setUpMargins.statusBarHeight,
            0,
            height
        )
    }
    binding.controlsLayout.controlsLayout.updateLayoutParams<ViewGroup.MarginLayoutParams> {
        updateMargins(0, 0, 0, height)
    }
    binding.gridLayout.sendButton.apply {
        updateLayoutParams<ViewGroup.MarginLayoutParams> {
            updateMargins(
                0,
                0,
                this@apply.context.toPx(16f).toInt(),
                this@apply.context.toPx(160f).toInt() + height
            )
        }
    }
}

internal fun RecyclerView.setupMainRecyclerView(
    context: Context,
    mainImageAdapter: MainImageAdapter,
    onFastScrollListener: RecyclerView.OnScrollListener
) {
    (layoutManager as GridLayoutManager).apply {
        spanCount = mainImageAdapter.spanCount
        spanSizeLookup =
            object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int) = if (mainImageAdapter.getItemViewType(position) ==
                    MainImageAdapter.HEADER
                ) {
                    mainImageAdapter.spanCount
                } else {
                    1
                }
            }
    }
    adapter = mainImageAdapter
    addOnScrollListener(onFastScrollListener)
    addItemDecoration(HeaderItemDecoration(context, mainImageAdapter))
    addOnScrollListener(context.preLoader(mainImageAdapter))
}

fun Context.toast(size: Int) {
    Toast.makeText(
        this,
        String.format(
            resources.getString(R.string.pix_selection_limiter),
            size
        ),
        Toast.LENGTH_SHORT
    ).show()
}

fun Context.color(@ColorRes color: Int) = ResourcesCompat.getColor(resources, color, null)

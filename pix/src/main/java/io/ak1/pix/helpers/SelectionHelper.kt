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

import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.core.view.isVisible
import io.ak1.pix.adapters.InstantImageAdapter
import io.ak1.pix.adapters.MainImageAdapter
import io.ak1.pix.databinding.GridLayoutBinding

/**
 * Created By Akshay Sharma on 17,June,2021
 * https://ak1.io
 */

// TODO: 18/06/21 if possible include in fragment class
internal lateinit var instantImageAdapter: InstantImageAdapter
internal lateinit var mainImageAdapter: MainImageAdapter

fun Int.selection(b: Boolean) {
    instantImageAdapter.select(b, this)
    mainImageAdapter.select(b, this)
}

fun GridLayoutBinding.sendButtonStateAnimation(show: Boolean, withAnim: Boolean = true) {
    if (sendButton.visibility == View.GONE && !show) return
    if (show) {
        if (sendButton.isVisible) return
        sendButton.show()
    } else if (!withAnim) {
        sendButton.hide()
    }
    if (!show && !sendButton.isVisible) return

    var start = 1f
    var end = 0f
    if (show) {
        start = 0f
        end = 1f
    }
    if (withAnim) {
        val anim: Animation = ScaleAnimation(
            start,
            end, // Start and end values for the X axis scaling
            start,
            end, // Start and end values for the Y axis scaling
            Animation.RELATIVE_TO_SELF,
            0.5f, // Pivot point of X scaling
            Animation.RELATIVE_TO_SELF,
            0.5f
        ) // Pivot point of Y scaling
        anim.fillAfter = true // Needed to keep the result of the animation
        anim.duration = 300
        if (!show) {
            anim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    sendButton.hide()
                    sendButton.clearAnimation()
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
        }
        sendButton.startAnimation(anim)
    }
}

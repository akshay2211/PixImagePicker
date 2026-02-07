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
package io.ak1.pix.utility

import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

/**
 * Created By Akshay Sharma on 17,June,2021
 * https://ak1.io
 */

class CustomItemTouchListener(private val binding: PixBindings) : RecyclerView.OnItemTouchListener {
    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        val action = e.action
        return if (binding.gridLayout.instantRecyclerView.canScrollHorizontally(RecyclerView.FOCUS_FORWARD)) {
            when (action) {
                MotionEvent.ACTION_MOVE -> binding.fragmentPix.root.requestDisallowInterceptTouchEvent(true)
            }
            false
        } else {
            when (action) {
                MotionEvent.ACTION_MOVE -> binding.fragmentPix.root.requestDisallowInterceptTouchEvent(false)
            }
            binding.gridLayout.instantRecyclerView.removeOnItemTouchListener(this)
            true
        }
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
}

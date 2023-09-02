package io.ak1.pix.utility

import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import io.ak1.pix.databinding.FragmentPixBinding

/**
 * Created By Akshay Sharma on 17,June,2021
 * https://ak1.io
 */

class CustomItemTouchListener(private val binding: PixBindings) :
    RecyclerView.OnItemTouchListener {
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
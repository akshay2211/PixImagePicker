package io.ak1.pix.helpers

import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import io.ak1.pix.databinding.FragmentPixBinding

/**
 * Created By Akshay Sharma on 17,June,2021
 * https://ak1.io
 */

internal fun FragmentActivity.setup(
    binding: FragmentPixBinding,
    bottomSheetBehavior: BottomSheetBehavior<View>?,
    callback: (Boolean) -> Unit
) {

    var localState = BottomSheetBehavior.STATE_COLLAPSED
    bottomSheetBehavior?.apply {
        peekHeight = this@setup.toPx(194f).toInt()
        addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

                if (localState == BottomSheetBehavior.STATE_COLLAPSED && newState == BottomSheetBehavior.STATE_DRAGGING) {
                    binding.gridLayout.sendButtonStateAnimation(false)

                }
                binding.root.requestDisallowInterceptTouchEvent(newState == BottomSheetBehavior.STATE_DRAGGING)

                localState = newState
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                manipulateVisibility(this@setup, binding, slideOffset)
                if (slideOffset == 1f) {
                    binding.gridLayout.sendButtonStateAnimation(show = false, withAnim = false)
                    callback(true)
                } else if (slideOffset == 0f) {
                    callback(false)
                }
            }
        })
    }

}

private fun manipulateVisibility(
    activity: FragmentActivity, binding: FragmentPixBinding,
    slideOffset: Float
) {
    binding.gridLayout.apply {
        instantRecyclerView.alpha = 1 - slideOffset
        arrowUp.alpha = 1 - slideOffset
        controlsLayout.controlsLayout.alpha = 1 - slideOffset
        topbar.alpha = slideOffset
        recyclerView.alpha = slideOffset
        if (1 - slideOffset == 0f) {
            instantRecyclerView.hide()
            arrowUp.hide()
            controlsLayout.primaryClickButton.hide()
        } else if (instantRecyclerView.isGone && 1 - slideOffset > 0) {
            instantRecyclerView.show()
            arrowUp.show()
            controlsLayout.primaryClickButton.show()
        }
        if (slideOffset > 0 && recyclerView.isInvisible) {
            recyclerView.show()
            topbar.show()
            activity.showStatusBar()

        } else if (recyclerView.isVisible && slideOffset == 0f) {
            activity.hideStatusBar()
            recyclerView.invisible()
            topbar.hide()
        }
    }

}
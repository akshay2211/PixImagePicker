package io.ak1.pix.helpers

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewPropertyAnimator
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import io.ak1.pix.PixFragment
import io.ak1.pix.R
import io.ak1.pix.databinding.FragmentPixBinding
import io.ak1.pix.utility.sScrollbarAnimDuration
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Created By Akshay Sharma on 17,June,2021
 * https://ak1.io
 */


//Video Counter Handler and Runnable
internal val handler = Handler(Looper.getMainLooper())
internal var mScrollbarAnimator: ViewPropertyAnimator? = null
internal var mBubbleAnimator: ViewPropertyAnimator? = null
internal var mViewHeight = 0f
internal const val mHideScrollbar = true
internal const val sBubbleAnimDuration = 1000
internal const val sScrollbarHideDelay = 1000
internal const val sTrackSnapRange = 5
internal var toolbarHeight = 0f

fun cancelAnimation(vararg animator: ViewPropertyAnimator?) {
    animator.forEach {
        it?.cancel()
    }
}

fun showScrollbar(mScrollbar: View?, context: Context): ViewPropertyAnimator {
    val transX = context.resources.getDimensionPixelSize(R.dimen.fastscroll_bubble_size).toFloat()
    mScrollbar!!.translationX = transX
    mScrollbar.visibility = View.VISIBLE
    return mScrollbar.animate().translationX(0f).alpha(1f)
        .setDuration(sScrollbarAnimDuration.toLong())
        .setListener(object :
            AnimatorListenerAdapter() { // adapter required for new alpha value to stick
        })
}

fun getValueInRange(min: Int, max: Int, value: Int): Int {
    val minimum = max(min, value)
    return min(minimum, max)
}

internal fun FragmentPixBinding.setViewPositions(y: Float) {
    val handleY: Int = getValueInRange(
        0, (mViewHeight - gridLayout.fastscrollHandle.height).toInt(),
        (y - gridLayout.fastscrollHandle.height / 2).toInt()
    )
    gridLayout.fastscrollBubble.y = handleY + root.context.toPx(60f)
    gridLayout.fastscrollHandle.y = handleY.toFloat()
}


fun FragmentPixBinding.hideScrollbar() {
    //val transX = resources.getDimensionPixelSize(R.dimen.fastscroll_scrollbar_padding_end).toFloat()
    mScrollbarAnimator =
        gridLayout.fastscrollScrollbar.animate().translationX(
            gridLayout.fastscrollScrollbar.width.toFloat()
        ).alpha(0f)
            .setDuration(sScrollbarAnimDuration.toLong())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    gridLayout.fastscrollScrollbar.hide()
                    mScrollbarAnimator = null
                }

                override fun onAnimationCancel(animation: Animator) {
                    super.onAnimationCancel(animation)
                    gridLayout.fastscrollScrollbar.hide()
                    mScrollbarAnimator = null
                }
            })
}


fun scrollListener(
    fragment: PixFragment,
    binding: FragmentPixBinding
): RecyclerView.OnScrollListener =
    object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (!binding.gridLayout.fastscrollHandle.isSelected && recyclerView.isEnabled) {
                binding.setViewPositions(getScrollProportion(recyclerView))
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (recyclerView.isEnabled) {
                when (newState) {
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        handler.removeCallbacks(fragment.mScrollbarHider)
                        if (binding.gridLayout.fastscrollScrollbar.visibility != View.VISIBLE) {
                            cancelAnimation(mScrollbarAnimator)
                            if (!binding.gridLayout.fastscrollScrollbar.isVisible && (recyclerView.computeVerticalScrollRange()
                                        - mViewHeight > 0)
                            ) {
                                mScrollbarAnimator = showScrollbar(
                                    binding.gridLayout.fastscrollScrollbar,
                                    binding.gridLayout.fastscrollScrollbar.context
                                )
                            }
                        }
                    }
                    RecyclerView.SCROLL_STATE_IDLE -> if (mHideScrollbar && !binding.gridLayout.fastscrollHandle.isSelected) {
                        handler.postDelayed(fragment.mScrollbarHider, sScrollbarHideDelay.toLong())
                    }
                    else -> {
                    }
                }
            }
        }
    }

fun getScrollProportion(recyclerView: RecyclerView?): Float {
    val verticalScrollOffset = recyclerView!!.computeVerticalScrollOffset()
    val verticalScrollRange = recyclerView.computeVerticalScrollRange()
    val rangeDiff = verticalScrollRange - mViewHeight
    val proportion = verticalScrollOffset.toFloat() / if (rangeDiff > 0) rangeDiff else 1f
    return mViewHeight * proportion
}

fun FragmentPixBinding.hideBubble() {
    if (gridLayout.fastscrollBubble.isVisible) {
        mBubbleAnimator = gridLayout.fastscrollBubble.animate().alpha(0f)
            .setDuration(sBubbleAnimDuration.toLong())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    gridLayout.fastscrollBubble.hide()
                    mBubbleAnimator = null
                }

                override fun onAnimationCancel(animation: Animator) {
                    super.onAnimationCancel(animation)
                    gridLayout.fastscrollBubble.hide()
                    mBubbleAnimator = null
                }
            })
        mBubbleAnimator!!.start()
    }
}


fun FragmentPixBinding.setRecyclerViewPosition(y: Float) {
    if (gridLayout.recyclerView.adapter != null) {
        val itemCount = gridLayout.recyclerView.adapter!!.itemCount
        val proportion: Float = when {
            gridLayout.fastscrollHandle.y == 0f -> 0f
            gridLayout.fastscrollHandle.y + gridLayout.fastscrollHandle.height >= mViewHeight - sTrackSnapRange -> 1f
            else -> y / mViewHeight
        }
        val scrolledItemCount = (proportion * itemCount).roundToInt()
        val targetPos: Int = getValueInRange(0, itemCount - 1, scrolledItemCount)
        gridLayout.recyclerView.layoutManager!!.scrollToPosition(targetPos)
        val text = mainImageAdapter.getSectionMonthYearText(targetPos)
        gridLayout.fastscrollBubble.text = text
        if (text.equals("", ignoreCase = true)) {
            gridLayout.fastscrollBubble.hide()
        }
    }
}

fun FragmentPixBinding.showBubble() {
    if (!gridLayout.fastscrollBubble.isVisible) {
        gridLayout.fastscrollBubble.show()
        gridLayout.fastscrollBubble.alpha = 0f
        mBubbleAnimator = gridLayout.fastscrollBubble
            .animate()
            .alpha(1f)
            .setDuration(sBubbleAnimDuration.toLong())
            .setListener(object :
                AnimatorListenerAdapter() { // adapter required for new alpha value to stick
            })
        mBubbleAnimator!!.start()
    }
}

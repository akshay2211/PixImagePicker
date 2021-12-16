package io.ak1.pixsample.custom

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Space
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.ak1.pix.helpers.toPx
import io.ak1.pixsample.R

/**
 * Created By Akshay Sharma on 20,June,2021
 * https://ak1.io
 */

fun fragmentBody(
    context: Context,
    adapter: RecyclerView.Adapter<io.ak1.pixsample.commons.Adapter.ViewHolder>,
    clickCallback: View.OnClickListener
): View {
    val layoutParams = FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.MATCH_PARENT,
        FrameLayout.LayoutParams.MATCH_PARENT
    ).apply {
        this.gravity = Gravity.RIGHT or Gravity.BOTTOM
    }
    return FrameLayout(context).apply {
        this.layoutParams = layoutParams
        addView(RecyclerView(context).apply {
            layoutManager = GridLayoutManager(context, 3)
            setPadding(0, 100, 0, 0)
            this.layoutParams = layoutParams
            this.adapter = adapter
        })
        addView(FloatingActionButton(context).apply {
            this.layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(
                    32,
                    32,
                    32,
                    32 + 160
                ) // 160 is the height of bottom navigation bar in Pixel
                this.gravity = Gravity.RIGHT or Gravity.BOTTOM
            }
            imageTintList = ColorStateList.valueOf(Color.WHITE)
            setImageResource(R.drawable.ic_photo_camera)
            setOnClickListener(clickCallback)
        })
    }
}

fun fragmentBody2(
    context: Context,
    adapter: RecyclerView.Adapter<io.ak1.pixsample.commons.Adapter.ViewHolder>,
): View {
    val layoutParams = LinearLayout.LayoutParams(
        FrameLayout.LayoutParams.MATCH_PARENT,
        FrameLayout.LayoutParams.MATCH_PARENT
    ).apply {
        this.gravity = Gravity.RIGHT or Gravity.BOTTOM
        weight = 1f
    }
    return LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        this.layoutParams = layoutParams
        addView(Space(context).apply {
            this.layoutParams = LinearLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                context.toPx(100f).toInt()
            )
        })
        addView(RecyclerView(context).apply {
            layoutManager = GridLayoutManager(context, 3)
            setPadding(0, 100, 0, 0)
            this.layoutParams = layoutParams
            this.adapter = adapter
        })
    }
}
package com.fxn.interfaces

import android.view.View
import com.fxn.modals.Img

/**
 * Created by akshay on 07/05/18.
 */
interface OnSelectionListener {
    fun onClick(Img: Img?, view: View?, position: Int)
    fun onLongClick(img: Img?, view: View?, position: Int)
}
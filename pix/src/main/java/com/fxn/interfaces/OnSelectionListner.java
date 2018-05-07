package com.fxn.interfaces;

import android.view.View;

import com.fxn.modals.Img;

/**
 * Created by akshay on 07/05/18.
 */


public interface OnSelectionListner {
    void OnClick(Img Img, View view, int position);

    void OnLongClick(Img img, View view, int position);
}
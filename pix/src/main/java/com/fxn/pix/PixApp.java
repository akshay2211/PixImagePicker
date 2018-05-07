package com.fxn.pix;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by akshay on 17/03/18.
 */

public class PixApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);

    }
}

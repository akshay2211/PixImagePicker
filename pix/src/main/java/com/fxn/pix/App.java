package com.fxn.pix;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by akshay on 05/07/18.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }
}

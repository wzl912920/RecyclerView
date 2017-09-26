package com.lynn.simplerecyclerview;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by Lynn.
 */

public class BaseApplication extends Application {
    private static BaseApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        this.instance = this;
        Fresco.initialize(this);
    }

    public static BaseApplication getInstance() {
        return instance;
    }
}

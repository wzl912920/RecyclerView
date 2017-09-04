package com.lynn.simplerecyclerview;

import android.app.Application;

/**
 * Created by Lynn.
 */

public class BaseApplication extends Application {
    private static BaseApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        this.instance = this;
    }

    public static BaseApplication getInstance() {
        return instance;
    }
}

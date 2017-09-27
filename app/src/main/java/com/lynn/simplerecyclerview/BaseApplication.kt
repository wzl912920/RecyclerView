package com.lynn.simplerecyclerview

import android.app.Application

import com.facebook.drawee.backends.pipeline.Fresco

/**
 * Created by Lynn.
 */

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        Fresco.initialize(this)
    }

    companion object {
        var instance : BaseApplication? = null
            private set
    }
}

package com.lynn.simplerecyclerview.base

import android.app.Application

import com.facebook.drawee.backends.pipeline.Fresco
import com.squareup.leakcanary.*

/**
 * Created by Lynn.
 */

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        Fresco.initialize(this)
        LeakCanary.install(this)
    }

    companion object {
        var instance : BaseApplication? = null
            private set
    }
}

package com.lynn.recyclerdemo.base

import android.app.Application

import com.squareup.leakcanary.*

/**
 * Created by Lynn.
 */

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        LeakCanary.install(this)
    }

    companion object {
        lateinit var instance : BaseApplication
            private set
    }
}

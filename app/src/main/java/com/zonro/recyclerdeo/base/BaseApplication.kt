package com.zonro.recyclerdeo.base

import android.app.Application

/**
 * Created by Lynn.
 */

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance : BaseApplication
            private set
    }
}

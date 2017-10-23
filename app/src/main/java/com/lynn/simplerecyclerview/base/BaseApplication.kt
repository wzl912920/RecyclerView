package com.lynn.simplerecyclerview.base

import android.app.Application
import android.content.*
import android.content.res.*

import com.facebook.drawee.backends.pipeline.Fresco
import com.lynn.library.net.*
import com.lynn.library.theme.*
import com.lynn.library.util.*
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
        ThemeConfig.getInstance().init(this).setAutoThemeView(true)
        HttpUtils.init(this , "" , "").setDebugMode(true)
    }

    override fun getApplicationContext() : Context {
        val s = super.getApplicationContext()
        log("----------getApplicationContextCalled----------")
        return s
    }

    override fun getResources() : Resources {
        log("----------getResourcesCalled----------")
        return super.getResources()
    }

    companion object {
        var instance : BaseApplication? = null
            private set
    }
}

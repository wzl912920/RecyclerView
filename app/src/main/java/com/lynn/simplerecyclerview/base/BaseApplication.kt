package com.lynn.simplerecyclerview.base

import android.app.Application
import android.content.res.*
import android.graphics.drawable.*
import android.support.v4.content.res.*

import com.facebook.drawee.backends.pipeline.Fresco
import com.lynn.library.net.*
import com.lynn.library.theme.*
import com.lynn.library.util.*
import com.lynn.simplerecyclerview.util.*
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
        val path = getString(THEME_PATH , "")
        if (!path.isEmpty()) {
            ThemeConfig.getInstance().registerSkinPath(this , path)
        }
        HttpUtils.init(this , "" , "").setDebugMode(true)
    }

    override fun getResources() : Resources {
        val r1 = super.getResources()
        return MRes(r1)
    }

    companion object {
        lateinit var instance : BaseApplication
            private set

        class MRes(val res : Resources) : Resources(res.assets , res.displayMetrics , res.configuration) {
            override fun getColor(id : Int , theme : Theme?) : Int {
                val value = ThemeConfig.getInstance().getThemeColor(id)
                if (value != 0) {
                    return value
                }
                return ResourcesCompat.getColor(res , id , theme)
            }

            override fun getColor(id : Int) : Int {
                return getColor(id , null)
            }

            override fun getDrawable(id : Int , theme : Theme?) : Drawable {
                val value = ThemeConfig.getInstance().getThemeDrawable(id)
                if (null != value) {
                    return value
                }
                return ResourcesCompat.getDrawable(res , id , theme)!!
            }

            override fun getDrawable(id : Int) : Drawable {
                return getDrawable(id , null)
            }

            override fun getDimension(id : Int) : Float {
                val value = ThemeConfig.getInstance().getThemeDimen(id)
                if (value != 0f) {
                    return value
                }
                return super.getDimension(id)
            }
        }
    }
}

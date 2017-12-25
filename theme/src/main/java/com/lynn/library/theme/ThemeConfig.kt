package com.lynn.library.theme

import android.app.*
import android.content.*
import android.content.res.*
import android.graphics.*
import android.graphics.drawable.*
import android.support.annotation.*
import android.util.*
import java.io.*

/**
 * Created by Lynn.
 */

class ThemeConfig private constructor() {
    private var sign = ""
    private val TAG = "Skin"
    private var allViewsThemeEnable = true
    private var isDefaultTheme = true
    private var originalRes : Resources? = null
    private var themeRes : Resources? = null
    private var pkgName : String = ""
    private var originalPkgName : String = ""
    fun init(application : Application) : ThemeConfig {
        application.registerActivityLifecycleCallbacks(LifeCycle())
        originalPkgName = application.packageName
        originalRes = application.resources
        registerSkinPath(application.applicationContext , "")
        return getInstance()
    }

    fun setAutoThemeView(isAllViewsThemeEnabled : Boolean = true) {
        allViewsThemeEnable = isAllViewsThemeEnabled
    }

    fun registerSkinPath(context : Context , path : String , sign : String = "") : ThemeConfig {
        isDefaultTheme = false
        if (!File(path).exists()) {
            Log.e(TAG , "the skin file location is incorrect")
            isDefaultTheme = true
        }
        val wrapper = AssertUtils.PKGWrapper()
        themeRes = AssertUtils.getPlugInResource(context , path , wrapper)

        if (themeRes == originalRes) {
            themeRes = null
            isDefaultTheme = true
            pkgName = ""
        } else {
            pkgName = wrapper.pkgName
        }
        ThemeUtils.map.forEach {
            it.value?.applySkin()
        }
        return this
    }

    fun getColor(@ColorRes id : Int) : Int {
        val themeResId = getThemeResId(id)
        var color = originalRes?.getColor(id) ?: Color.WHITE
        if (themeResId != 0) {
            try {
                color = themeRes?.getColor(themeResId) ?: color
            } catch (e : Exception) {
            }
        }
        return color
    }

    fun getThemeColor(id : Int) : Int {
        var color = 0
        val themeResId = getThemeResId(id)
        if (themeResId != 0) {
            try {
                color = themeRes?.getColor(themeResId) ?: color
            } catch (e : Exception) {
            }
        }
        return color
    }

    fun getDimen(@DimenRes id : Int) : Float {
        val themeResId = getThemeResId(id)
        var dimen = originalRes?.getDimension(id) ?: 0f
        if (themeResId != 0) {
            try {
                dimen = themeRes?.getDimension(themeResId) ?: dimen
            } catch (e : Exception) {
            }
        }
        return dimen
    }

    fun getThemeDimen(id : Int) : Float {
        var dimen = 0f
        val themeResId = getThemeResId(id)
        if (themeResId != 0) {
            try {
                dimen = themeRes?.getDimension(themeResId) ?: dimen
            } catch (e : Exception) {
            }
        }
        return dimen
    }

    fun getDrawable(@DrawableRes id : Int) : Drawable? {
        val themeResId = getThemeResId(id)
        var drawable = originalRes?.getDrawable(id) ?: null
        if (themeResId != 0) {
            try {
                drawable = themeRes?.getDrawable(themeResId) ?: null
            } catch (e : Exception) {
            }
        }
        return drawable
    }

    fun getThemeDrawable(@DrawableRes id : Int) : Drawable? {
        var drawable : Drawable? = null
        val themeResId = getThemeResId(id)
        if (themeResId != 0) {
            try {
                drawable = themeRes?.getDrawable(themeResId) ?: drawable
            } catch (e : Exception) {
            }
        }
        return drawable
    }

    private fun getThemeResId(resId : Int) : Int {
        if (isDefaultTheme || null == originalRes || null == themeRes || originalRes == themeRes || pkgName.isEmpty()) {
            return 0
        }
        val entryName = originalRes?.getResourceEntryName(resId)
        val typeName = originalRes?.getResourceTypeName(resId)
        val themResId = themeRes?.getIdentifier(entryName , typeName , pkgName) ?: 0
        return themResId
    }

    internal fun getTypeName(id : Int) : String {
        if (null == originalRes) {
            return ""
        }
        return originalRes?.getResourceTypeName(id) ?: ""
    }

    internal fun isAllViewsThemeEnable() : Boolean {
        return allViewsThemeEnable
    }

    internal fun isDefaultTheme() : Boolean {
        return isDefaultTheme
    }

    companion object {
        private var inst : ThemeConfig? = null
        fun getInstance() : ThemeConfig {
            if (null == inst) {
                synchronized(ThemeConfig::class.java) {
                    if (null == inst) {
                        inst = ThemeConfig()
                    }
                }
            }
            return inst!!
        }
    }

}
package com.lynn.library.theme

import android.app.*
import android.content.*
import android.content.res.*
import android.util.*
import java.io.*

/**
 * Created by Lynn.
 */

object ThemeConfig {
    private var skinPath : String = ""
    private var sign = ""
    private val TAG = "Skin"
    private var allViewsThemeEnable = true
    private var isDefaultTheme = true
    private var res : Resources? = null
    fun registerSkinPath(context : Context , path : String , sign : String = "") {
        skinPath = path
        isDefaultTheme = false
        if (!File(path).exists()) {
            Log.e(TAG , "the skin file location is incorrect")
            isDefaultTheme = true
        }
        val wrapper = AssertUtils.PKGWrapper()
        res = AssertUtils.getPlugInResource(context , path , wrapper)
        if (wrapper.pkgName == context.packageName) {
            isDefaultTheme = true
        }
        ThemeUtils.map.forEach { item ->
            val value = item.value
            value?.applySkin(context)
        }
    }

    fun setAutoThemeView(isAllViewsThemeEnabled : Boolean = true) {
        allViewsThemeEnable = isAllViewsThemeEnabled
    }

    fun init(application : Application) : ThemeConfig {
        application.registerActivityLifecycleCallbacks(LifeCycle())
        return this
    }

    fun getResource() : Resources? {
        return res
    }

    internal fun getThemePath() : String {
        return skinPath
    }

    internal fun isAllViewsThemeEnable() : Boolean {
        return allViewsThemeEnable
    }

    internal fun isDefaultTheme() : Boolean {
        return isDefaultTheme
    }
}
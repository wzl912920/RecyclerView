package com.lynn.library.theme

import android.content.*
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
    fun registerSkinPath(context : Context , path : String , sign : String = "") {
        skinPath = path
        isDefaultTheme = false
        if (!File(path).exists()) {
            Log.e(TAG , "the skin file location is incorrect")
            isDefaultTheme = true
        }
        val wrapper = AssertUtils.PKGWrapper()
        AssertUtils.getPlugInResource(context , path , wrapper)
        if (wrapper.pkgName == context.packageName) {
            isDefaultTheme = true
        }
    }

    fun setAutoThemeView(isAllViewsThemeEnabled : Boolean = true) {
        allViewsThemeEnable = isAllViewsThemeEnabled
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
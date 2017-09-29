package com.lynn.simplerecyclerview.base

import android.graphics.*
import android.os.*
import android.view.*
import com.lynn.library.permission.*
import com.lynn.library.util.*

/**
 * Created by Lynn.
 */

open class BaseActivity : PermissionsActivity() {
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarLightMode(Color.TRANSPARENT)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }
}

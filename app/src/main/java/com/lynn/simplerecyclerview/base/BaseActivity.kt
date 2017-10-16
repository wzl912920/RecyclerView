package com.lynn.simplerecyclerview.base

import android.graphics.*
import android.os.*
import android.support.v7.app.*
import android.view.*
import com.lynn.library.permission.*
import com.lynn.library.util.*

/**
 * Created by Lynn.
 */

abstract open class BaseActivity : PermissionsActivity() {
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarLightMode(Color.TRANSPARENT)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        //当在非ImageView控件中(Button、TextView等)作为Background、CompoundDrawable时，需要声明
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }
}

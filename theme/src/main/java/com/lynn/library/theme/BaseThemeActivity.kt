package com.lynn.library.theme

import android.os.*
import android.support.v4.view.*
import android.support.v7.app.*

/**
 * Created by Lynn.
 */

abstract open class BaseThemeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState : Bundle?) {
        LayoutInflaterCompat.setFactory(layoutInflater , LayoutInflaterFactory())
        super.onCreate(savedInstanceState)
    }

    protected fun applyTheme() {
        (LayoutInflaterCompat.getFactory(layoutInflater) as LayoutInflaterFactory).applySkin(this)
    }

    override fun onResume() {
        super.onResume()
        applyTheme()
    }
}
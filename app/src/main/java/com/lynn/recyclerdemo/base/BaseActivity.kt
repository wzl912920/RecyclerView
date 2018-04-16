package com.lynn.recyclerdemo.base

import android.os.*
import android.support.v7.app.*
import android.view.*

/**
 * Created by Lynn.
 */

abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }
}

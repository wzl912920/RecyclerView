package com.lynn.library.theme

import android.app.*
import android.os.*
import android.support.v4.view.*
import android.support.v7.app.*
import android.view.*

/**
 * Created by Lynn.
 */

internal class LifeCycle : Application.ActivityLifecycleCallbacks {

    override fun onActivityPaused(activity : Activity?) {
    }

    override fun onActivityResumed(activity : Activity?) {
    }

    override fun onActivityStarted(activity : Activity?) {
    }

    override fun onActivityDestroyed(activity : Activity?) {
        ThemeUtils.map.remove(activity)
    }

    override fun onActivitySaveInstanceState(activity : Activity? , outState : Bundle?) {
    }

    override fun onActivityStopped(activity : Activity?) {
    }

    override fun onActivityCreated(activity : Activity? , savedInstanceState : Bundle?) {
        if (activity == null) {
            return
        }
        var factory : LayoutInflaterFactory? = null
        if (activity is AppCompatActivity && LayoutInflaterCompat.getFactory(activity.layoutInflater) !is LayoutInflaterFactory) {
            factory = LayoutInflaterFactory()
            val s = LayoutInflater::class.java.getDeclaredField("mFactorySet")
            s.isAccessible = true
            s.setBoolean(activity.layoutInflater , false)
            LayoutInflaterCompat.setFactory(activity.layoutInflater , factory)
        }
        factory?.let { ThemeUtils.map.put(activity , factory) }
    }
}

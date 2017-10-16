package com.lynn.library.theme

import android.content.*
import android.os.*
import android.support.v4.view.*
import android.support.v7.app.*
import android.util.*
import android.view.*
import android.widget.*

/**
 * Created by Lynn.
 */

abstract open class BaseThemeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState : Bundle?) {
        LayoutInflaterCompat.setFactory(layoutInflater , LayoutInflaterFactory { parent , name , context , attrs ->
            Log.e("TAG" , "name ================== " + name)
            if (null == context || null == attrs || context !is AppCompatActivity) {
                return@LayoutInflaterFactory null
            }
            val n = attrs.attributeCount
//                val value = attrs.getAttributeValue("theme" , "themeEnabled")
//                var themeEnabled = attrs?.getAttributeBooleanValue("theme" , "themeEnabled" , false)
//                Log.e("A" , "value=$value themeEnabled=$themeEnabled")
            for (i in 0 until n) {
                val iName = attrs.getAttributeName(i)
                val iValue = attrs.getAttributeValue(i)
                Log.e("TAG" , iName + " , " + iValue)
                if (iName == "themeEnabled") {
                    Log.d("TAG" , attrs.getAttributeValue("theme" , name) + "对对对对对对错")
                }
            }
            val sa = attrs.styleAttribute
            val ca = attrs.classAttribute
            val ia = attrs.idAttribute
            val pd = attrs.positionDescription
            var view : View? = null
            view = context.onCreateView(parent , name , context , attrs)
            if (view == null) {
                view = delegate.createView(parent , name , context , attrs)
            }
            if (/*themeEnabled && */null != view) {
                Log.e("TAG" , "view != null================================================================")
                val res = AssertUtils.getPlugInResource(context , "/mnt/sdcard/test.apk")
                val color = res.getColor(res.getIdentifier("textColor" , "color" , "com.lynn.test"))
                if (view is TextView) {
                    view.setTextColor(color)
                }
            } else {
                Log.e("TGATGA" , "sa=$sa ca=$ca ia=$ia pd=$pd")
            }
            view
        })
        super.onCreate(savedInstanceState)
    }

    companion object {
        private class LIFactory(delegate : AppCompatDelegate) : LayoutInflaterFactory {
            private val delegate = delegate
            override fun onCreateView(parent : View? , name : String? , context : Context? , attrs : AttributeSet?) : View? {
                Log.e("TAG" , "name ================== " + name)
                if (null == context || null == attrs || context !is AppCompatActivity) {
                    return null
                }
                val n = attrs.attributeCount
//                val value = attrs.getAttributeValue("theme" , "themeEnabled")
//                var themeEnabled = attrs?.getAttributeBooleanValue("theme" , "themeEnabled" , false)
//                Log.e("A" , "value=$value themeEnabled=$themeEnabled")
                for (i in 0 until n) {
                    Log.e("TAG" , attrs.getAttributeName(i) + " , " + attrs.getAttributeValue(i))
                }
                val sa = attrs.styleAttribute
                val ca = attrs.classAttribute
                val ia = attrs.idAttribute
                val pd = attrs.positionDescription
                val view = delegate.createView(parent , name , context , attrs)
                if (/*themeEnabled && */null != view) {
                    Log.e("TAG" , "view != null================================================================")
                    val res = AssertUtils.getPlugInResource(context , "/mnt/sdcard/test.apk")
                    val color = res.getColor(res.getIdentifier("textColor" , "color" , "com.lynn.test"))
                    if (view is TextView) {
                        view.setTextColor(color)
                    }
                } else {
                    Log.e("TGATGA" , "sa=$sa ca=$ca ia=$ia pd=$pd")
                }
                return view
            }
        }
    }
}
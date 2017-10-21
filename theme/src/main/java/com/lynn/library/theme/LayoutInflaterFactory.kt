package com.lynn.library.theme

import android.app.*
import android.content.*
import android.content.res.*
import android.support.v4.view.LayoutInflaterFactory
import android.support.v7.app.*
import android.util.*
import android.view.*
import android.widget.*
import com.lynn.theme.*
import java.lang.ref.*


/**
 * Created by Lynn.
 */

internal class LayoutInflaterFactory : LayoutInflaterFactory {
    private val attrMap = mutableMapOf<String , MutableMap<String , String>>()
    private val views = mutableMapOf<String , SoftReference<View?>>()
    override fun onCreateView(parent : View? , name : String? , context : Context? , attrs : AttributeSet?) : View? {
        if (null == context || null == attrs || context !is AppCompatActivity || null == name) {
            return null
        }
        val attributes = mutableMapOf<String , String>()
        (0 until attrs.attributeCount).forEach { i ->
            val attrName = attrs.getAttributeName(i)//属性名
            val attrValue = attrs.getAttributeValue(i)//属性值
            if (attrValue.startsWith("@") && attrName in collectList) {
                attributes.put(attrName , attrValue)
            }
        }
        var view : View? = null
        val type = context.obtainStyledAttributes(attrs , R.styleable.theme)
        if (ThemeConfig.getInstance().isAllViewsThemeEnable()) {
            try {
                val themeEnabled = type.getBoolean(0 , true)
                if (themeEnabled) {
                    view = addNewAttr(parent , context , name , attrs , attributes)
                }
            } catch (e : Exception) {
            }
        } else {
            try {
                val themeEnabled = type.getBoolean(0 , false)
                if (themeEnabled) {
                    view = addNewAttr(parent , context , name , attrs , attributes)
                }
            } catch (e : Exception) {
            }
        }
        type.recycle()
        return view
    }

    private fun addNewAttr(parent : View? , context : Activity , name : String , attrs : AttributeSet , attributes : MutableMap<String , String>) : View? {
        if (null == parent) {
            attrMap.clear()
            views.clear()
            return null
        }
        val view = ThemeUtils.createView(parent , context , name , attrs)
        view?.let {
            if (!attributes.isEmpty()) {
                val key = "${getViewId(view)}"
                attrMap.put(key , attributes)
                views.put(key , SoftReference(view))
                applyAttr(view)
            }
        }
        return view
    }

    private fun getViewId(view : View?) : String {
        if (null == view) {
            return ""
        }
        val s = view.toString()
        if (s.indexOf(" ") != -1) {
            return s.substring(0 , s.indexOf(" "))
        }
        return "${view::class.java}${if (s.indexOf("#") == -1) view.id else s.substring(s.indexOf("#") , s.length)}"
    }

    private fun applyAttr(view : View) {
        val attributes = attrMap["${getViewId(view)}"]
        if (null == attributes || attributes.isEmpty()) {
            return
        }
        attributes.forEach NEXT@ { item ->
            val key = item.key
            val value = item.value
            if (value.isEmpty() || value.length <= 1) {
                return@NEXT
            }
            var id = Integer.parseInt(value.substring(1))
//            Log.e("$pkgName" , "$name---$entryName---$typeName")
            val typeName = ThemeConfig.getInstance().getTypeName(id)
            when (key) {
                TEXT_COLOR -> {
                    if (view is TextView) {
                        view.setTextColor(ThemeConfig.getInstance().getColor(id))
                    }
                }
                BACK_GROUND -> {
                    when (typeName) {
                        TYPE_COLOR -> {
                            val value = ThemeConfig.getInstance().getColor(id)
                            view.setBackgroundColor(value)
                        }
                        TYPE_DRAWABLE -> {
                            val value = ThemeConfig.getInstance().getDrawable(id)
                            view.setBackgroundDrawable(value)
                        }
                        TYPE_MIPMAP -> {
                            val value = ThemeConfig.getInstance().getDrawable(id)
                            view.setBackgroundDrawable(value)
                        }
                    }
                }
                SRC , SRC_COMPAT -> {
                    when (typeName) {
                        TYPE_DRAWABLE -> {
                            val value = ThemeConfig.getInstance().getDrawable(id)
                            (view as ImageView).setImageDrawable(value)
                        }
                        TYPE_MIPMAP -> {
                            val value = ThemeConfig.getInstance().getDrawable(id)
                            (view as ImageView).setImageDrawable(value)
                        }
                    }
                }
                TEXT_COLOR_HINT -> {
                    if (view is TextView) {
                        val value = ThemeConfig.getInstance().getColor(id)
                        view.setHintTextColor(value)
                    }
                }
                else -> {
                }
            }
        }
    }

    internal fun applySkin() {
        views.forEach {
            val view = it.value.get()
            view?.let {
                applyAttr(view)
            }
        }
    }

    companion object {
        private val TYPE_COLOR = "color"
        private val TYPE_DIMEN = "dimen"
        private val TYPE_DRAWABLE = "drawable"
        private val TYPE_MIPMAP = "mipmap"

        private val SRC = "src"
        private val SRC_COMPAT = "srcCompat"
        private val TEXT_COLOR = "textColor"
        private val BACK_GROUND = "background"
        private val TEXT_COLOR_HINT = "textColorHint"
        private val collectList = arrayListOf(SRC , SRC_COMPAT , BACK_GROUND , TEXT_COLOR , TEXT_COLOR_HINT)
    }
}

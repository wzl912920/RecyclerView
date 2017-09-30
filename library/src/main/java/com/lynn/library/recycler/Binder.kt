package com.lynn.library.recycler

import android.support.annotation.*
import android.view.*
import com.lynn.library.recycler.Tools.getSuperClazz
import com.lynn.library.recycler.Tools.getTypeKey
import java.lang.ref.*
import java.lang.reflect.*


/**
 * Created by Lynn.
 */
internal class Binder {
    private val type2Holder = mutableMapOf<Int , Class<out BaseViewHolder<*>>>()
    private val type2Layout = mutableMapOf<Int , Int>()
    private val class2MultiType = mutableMapOf<Class<*> , MultiTyper<*>>()
    private val class2Type = mutableMapOf<Class<*> , Int>()
    private val clickIds = mutableListOf<Int>()
    private val longClickIds = mutableListOf<Int>()
    private var srClickEvent : ItemClickEvent? = null
    private var srLongClickEvent : ItemLongClickEvent? = null

    internal fun getClickEvent() : ItemClickEvent? {
        return srClickEvent
    }

    internal fun getLongClickEvent() : ItemLongClickEvent? {
        return srLongClickEvent
    }

    internal fun getClickEventIds() : MutableList<Int> {
        return clickIds
    }

    internal fun getLongClickEventIds() : MutableList<Int> {
        return longClickIds
    }

    internal fun getConstructor(type : Int) : Constructor<*> {
        var returnType = type2Holder[type]
        if (returnType != null) {
            val cons = returnType.getDeclaredConstructor(View::class.java)
            cons.isAccessible = true
            return cons
        }
        throw NullPointerException("you havenot register this type")
    }

    @Synchronized internal fun registerClickEvent(event : ItemClickEvent , @IdRes vararg viewId : Int) {
        srClickEvent = event
        for (i in viewId) {
            clickIds.add(i)
        }
    }

    @Synchronized internal fun registerLongClickEvent(event : ItemLongClickEvent , @IdRes vararg viewId : Int) {
        srLongClickEvent = event
        for (i in viewId) {
            longClickIds.add(i)
        }
    }

    internal fun getLayoutId(type : Int) : Int {
        if (null == type2Layout[type]) {
            throw NullPointerException("None layout Registed")
        }
        return type2Layout[type]!!
    }

    @Synchronized internal fun register(@LayoutRes layoutId : Int , holder : Class<out BaseViewHolder<*>>) {
        val superClass = getSuperClazz(holder)
        val typeKey = getTypeKey(layoutId , holder , superClass)
        val type = layoutId + typeKey.hashCode()
        type2Holder.put(type , holder)
        type2Layout.put(type , layoutId)
        class2Type.put(superClass , type)
    }

    @Synchronized internal fun multiRegister(typer : MultiTyper<*>) {
        class2MultiType.put(getSuperClazz(typer::class.java) , typer)
    }

    @Synchronized internal fun getDataType(data : Any) : Int {
        val obj = class2MultiType[data::class.java]
        if (null != obj && obj is MultiTyper) {
            val multiTyper = obj as MultiTyper<Any>
            if (null != multiTyper) {
                val holder = multiTyper.getViewHolder(data)
                val layoutId = multiTyper.getLayoutId(data)
                val superClass = getSuperClazz(holder)
                val typeKey = getTypeKey(layoutId , holder , superClass)
                val type = layoutId + typeKey.hashCode()
                if (type2Holder[type] == null) {
                    type2Holder.put(type , holder)
                }
                if (type2Layout[type] == null) {
                    type2Layout.put(type , layoutId)
                }
                return type
            }
        }
        return class2Type[data::class.java]!!
    }
}
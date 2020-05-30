package com.zonro.library.recycler

import android.view.*
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
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
    private var tools = Tools()

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

    @Synchronized
    internal fun registerClickEvent(event : ItemClickEvent, @IdRes vararg viewId : Int) {
        srClickEvent = event
        for (i in viewId) {
            clickIds.add(i)
        }
    }

    @Synchronized
    internal fun registerLongClickEvent(event : ItemLongClickEvent, @IdRes vararg viewId : Int) {
        srLongClickEvent = event
        for (i in viewId) {
            longClickIds.add(i)
        }
    }

    internal fun getLayoutId(type : Int) : Int {
        val id = type2Layout[type]
        if (null == id || id == 0) {
            throw NullPointerException("None layout Registed")
        }
        return id
    }

    @Synchronized
    internal fun register(clazz : Class<out BaseViewHolder<*>>) {
        val layoutId = tools.getLayoutId(clazz)
        register(layoutId , clazz)
    }

    @Synchronized
    internal fun register(@LayoutRes layoutId : Int, holder : Class<out BaseViewHolder<*>>) {
        val superClass = tools.getDataClazz(holder)
        val typeKey = tools.getTypeKey(layoutId , holder , superClass)
        val type = layoutId + typeKey.hashCode()
        type2Holder[type] = holder
        type2Layout[type] = layoutId
        class2Type[superClass] = type
    }

    @Synchronized
    internal fun multiRegister(typer : MultiTyper<*>) {
        class2MultiType[tools.getDataClazz(typer::class.java)] = typer
    }

    @Synchronized
    internal fun getDataType(data : Any) : Int {
        val multiple = class2MultiType[data::class.java]
        if (multiple is MultiTyper) {
            val multiTyper = multiple as MultiTyper<Any>
            if (null != multiTyper) {
                val holder = multiTyper.getViewHolder(data)
                val layoutId = multiTyper.getLayoutId(data)
                val superClass = tools.getDataClazz(holder)
                val typeKey = tools.getTypeKey(layoutId , holder , superClass)
                val type = layoutId + typeKey.hashCode()
                if (type2Holder[type] == null) {
                    type2Holder[type] = holder
                }
                if (type2Layout[type] == null) {
                    type2Layout[type] = layoutId
                }
                return type
            }
        }
        return class2Type[data::class.java]
                ?: throw NullPointerException("None Data ${data::class.java} Registed")
    }

    internal fun clear() {
        type2Holder.clear()
        type2Layout.clear()
        class2MultiType.clear()
        class2Type.clear()
        clickIds.clear()
        longClickIds.clear()
        srClickEvent = null
        srLongClickEvent = null
        tools.clear()
    }
}
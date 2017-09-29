package com.lynn.library.recycler

import android.support.annotation.*
import com.lynn.library.recycler.Tools.getSuperClazz
import com.lynn.library.recycler.Tools.getTypeKey


/**
 * Created by Lynn.
 */
internal class Binder {
    private val type2Holder = mutableMapOf<Int , Class<out BaseViewHolder<*>>>()
    private val type2Layout = mutableMapOf<Int , Int>()
    private val class2MultiType = mutableMapOf<Class<*> , MultiTyper>()
    private val class2Type = mutableMapOf<Class<*> , Int>()
    internal fun getHolderClass(type : Int) : Class<out BaseViewHolder<*>> {
        var returnType = type2Holder[type]
        if (returnType != null) {
            return returnType
        }
        throw NullPointerException("you havenot register this type")
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

    @Synchronized internal fun multiRegister(data : Class<*> , typer : MultiTyper) {
        class2MultiType.put(data , typer)
    }

    @Synchronized internal fun getDataType(data : Any) : Int {
        val multiTyper = class2MultiType[data::class.java]
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
        return class2Type[data::class.java]!!
    }

    internal fun release() {
        type2Holder.clear()
        type2Layout.clear()
        class2Type.clear()
        class2MultiType.clear()
    }
}
package com.lynn.library.recycler

import android.support.annotation.*
import android.view.*
import java.lang.reflect.*

/**
 * Created by Lynn.
 */
internal class Tools {
    private val dataClasses = mutableMapOf<Class<*> , Class<*>>()
    internal fun clear() {
        dataClasses.clear()
    }

    internal fun getTypeKey(@LayoutRes layoutId : Int , holder : Class<out BaseViewHolder<*>> , data : Class<*>) : String {
        return "$layoutId-${holder.name}-${data.name}"
    }

    internal fun getLayoutId(clazz : Class<*>) : Int {
        val s = clazz.getAnnotation(LayoutId::class.java)
        if (s is LayoutId) {
            val value = s.value
            if (value != 0) {
                return value
            }
        }
        throw NullPointerException("annotation layout id not set")
    }

    internal fun getDataClazz(clazz : Class<*>) : Class<*> {
        return dataClasses[clazz] ?: kotlin.run {
            val it = clazz.genericInterfaces
            val s = if (it.isNotEmpty()) it[0] else null
            if (null != s && s is ParameterizedType) {
                val p = s.actualTypeArguments
                val s = p[0] as Class<*>
                if (null != s) {
                    dataClasses[clazz] = s
                    return s
                }
            }
            val type = clazz.genericSuperclass
            if (type != null && type is ParameterizedType) {
                val p = type.actualTypeArguments
                val cls = p[0] as Class<*>
                if (cls != null) {
                    dataClasses[clazz] = cls
                    return cls
                }
            }
            throw IllegalStateException("The Class Must have a super class")
        }
    }
}
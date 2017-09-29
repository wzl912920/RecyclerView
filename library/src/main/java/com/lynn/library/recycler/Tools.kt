package com.lynn.library.recycler

import android.support.annotation.*
import android.util.*
import java.lang.reflect.*

/**
 * Created by Lynn.
 */
internal object Tools {
    internal fun getTypeKey(@LayoutRes layoutId : Int , holder : Class<out BaseViewHolder<*>> , data : Class<*>) : String {
        return "$layoutId-${holder.name}-${data.name}"
    }

    internal fun getSuperClazz(clazz : Class<*>) : Class<*> {
        val it = clazz.genericInterfaces
        val s = if (it.isNotEmpty()) it[0] else null
        if (null != s && s is ParameterizedType) {
            val p = s.actualTypeArguments
            val s = p[0] as Class<*>
            if (null != s) {
                return s
            }
        }
        val type = clazz.genericSuperclass
        if (type != null && type is ParameterizedType) {
            val p = type.actualTypeArguments
            val cls = p[0] as Class<*>
            if (cls != null) {
                return cls
            }
        }
        throw IllegalStateException("The Class Must have a super class")
    }
}
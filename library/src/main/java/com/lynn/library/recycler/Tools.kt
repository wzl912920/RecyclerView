package com.lynn.library.recycler

import android.support.annotation.*
import java.lang.reflect.*

/**
 * Created by Lynn.
 */
internal object Tools {
    internal fun getTypeKey(@LayoutRes layoutId : Int , holder : Class<out BaseViewHolder<*>> , data : Class<*>) : String {
        return "$layoutId-${holder.name}-${data.name}"
    }

    internal fun getSuperClazz(clazz : Class<*>) : Class<*> {
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
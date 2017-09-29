package com.lynn.library.recycler

import android.support.annotation.*

/**
 * Created by Lynn.
 */
interface MultiTyper {
    @LayoutRes
    fun getLayoutId(data : Any) : Int

    fun getViewHolder(data : Any) : Class<out BaseViewHolder<*>>
}
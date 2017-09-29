package com.lynn.library.recycler

import android.support.annotation.*

/**
 * Created by Lynn.
 */
interface MultiTyper<T> {
    @LayoutRes
    fun getLayoutId(data : T) : Int

    fun getViewHolder(data : T) : Class<out BaseViewHolder<T>>
}
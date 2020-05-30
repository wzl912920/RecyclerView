package com.zonro.library.recycler

import androidx.annotation.LayoutRes

/**
 * Created by Lynn.
 */
interface MultiTyper<T> {
    @LayoutRes
    fun getLayoutId(data : T) : Int

    fun getViewHolder(data : T) : Class<out BaseViewHolder<T>>
}
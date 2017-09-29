package com.lynn.library.recycler

import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by Lynn.
 */

abstract class BaseViewHolder<T>(itemView : View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(data : T)
}
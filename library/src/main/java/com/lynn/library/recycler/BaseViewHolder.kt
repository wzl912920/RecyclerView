package com.lynn.library.recycler

import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by Lynn.
 */

abstract class BaseViewHolder<T>(itemView : View) : RecyclerView.ViewHolder(itemView) {
    private var srClick : ItemClickEvent? = null
    private var srLongClick : ItemLongClickEvent? = null
    abstract fun bind(data : T)
    open protected fun overrideGlobalClickEvent() : Boolean {
        return false
    }

    internal fun bindClickEvent(event : ItemClickEvent? , ids : MutableList<Int>) {
        if (overrideGlobalClickEvent()) {
            return
        }
        srClick = event
        srClick?.let {
            itemView.setOnClickListener { onItClick(itemView) }
            for (id in ids) {
                val view = itemView.findViewById<View>(id)
                view.setOnClickListener { view -> onItClick(view) }
            }
        }
    }

    internal fun bindLongClickEvent(event : ItemLongClickEvent? , ids : MutableList<Int>) {
        if (overrideGlobalClickEvent()) {
            return
        }
        srLongClick = event
        srLongClick?.let {
            itemView.setOnLongClickListener { onItLongClick(itemView) }
            for (id in ids) {
                val view = itemView.findViewById<View>(id)
                view.setOnLongClickListener { v -> onItLongClick(v) }
            }
        }
    }

    private fun onItClick(view : View) {
        srClick?.onItemClick(view , adapterPosition)
    }

    private fun onItLongClick(view : View) : Boolean {
        var flag = false
        if (null != srLongClick) {
            srLongClick?.onItemLongClick(view , adapterPosition)
            flag = true
        }
        return flag
    }
}
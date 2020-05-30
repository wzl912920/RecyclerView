package com.zonro.library.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer

/**
 * Created by Lynn.
 */
@LayoutId(0)
abstract class BaseViewHolder<T>(override val containerView : View) : RecyclerView.ViewHolder(containerView) , LayoutContainer {
    private var srClick : ItemClickEvent? = null
    private var srLongClick : ItemLongClickEvent? = null

    abstract fun bind(data : T)
    open fun onRefreshData(datas : MutableList<Any>) {
    }

    open fun bindData(data : T , payLoads : MutableList<Any>) {
        if (payLoads != null) {
            onRefreshData(payLoads)
        } else {
            bind(data)
        }
    }

    open protected fun overrideGlobalClickEvent() : Boolean {
        return false
    }

    internal fun bindClickEvent(event : ItemClickEvent?, ids : MutableList<Int>) {
        if (overrideGlobalClickEvent()) {
            return
        }
        srClick = event
        srClick?.let {
            itemView.setOnClickListener { onClick(itemView) }
            for (id in ids) {
                val view = itemView.findViewById<View>(id)
                view.setOnClickListener { view -> onClick(view) }
            }
        }
    }

    internal fun bindLongClickEvent(event : ItemLongClickEvent?, ids : MutableList<Int>) {
        if (overrideGlobalClickEvent()) {
            return
        }
        srLongClick = event
        srLongClick?.let {
            itemView.setOnLongClickListener { onLongClick(itemView) }
            for (id in ids) {
                val view = itemView.findViewById<View>(id)
                view.setOnLongClickListener { v -> onLongClick(v) }
            }
        }
    }

    private fun onClick(view : View) {
        srClick?.onItemClick(view , adapterPosition)
    }

    private fun onLongClick(view : View) : Boolean {
        var flag = false
        if (null != srLongClick) {
            srLongClick?.onItemLongClick(view , adapterPosition)
            flag = true
        }
        return flag
    }
}
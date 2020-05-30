package com.zonro.library.recycler

import androidx.recyclerview.widget.DiffUtil

/**
 * Created by Lynn.
 */
internal class DiffCallback(private val oldData : MutableList<Any>, private val newData : MutableList<Any>) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition : Int , newItemPosition : Int) : Boolean {
        if (oldItemPosition in oldData.indices && newItemPosition in newData.indices) {
            return oldData[oldItemPosition] === newData[newItemPosition]
        }
        return false
    }

    override fun getOldListSize() : Int {
        return oldData?.size ?: 0
    }

    override fun getNewListSize() : Int {
        return newData?.size ?: 0
    }

    override fun areContentsTheSame(oldItemPosition : Int , newItemPosition : Int) : Boolean {
        if (oldItemPosition in oldData.indices && newItemPosition in newData.indices) {
            val oldValue = oldData[oldItemPosition]
            val newValue = newData[newItemPosition]
            return oldValue == newValue && oldValue === newValue
        }
        return false
    }
}

package com.lynn.library.recycler

import android.support.v7.util.*

/**
 * Created by Lynn.
 */
internal class DiffCallback(val oldData : MutableList<Any> , val newData : MutableList<Any>) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition : Int , newItemPosition : Int) : Boolean {
        if (oldItemPosition in oldData.indices && newItemPosition in newData.indices) {
            return oldData[oldItemPosition] === newData[newItemPosition]
        }
        return false
    }

    override fun getOldListSize() : Int {
        return oldData.size
    }

    override fun getNewListSize() : Int {
        return newData.size
    }

    override fun areContentsTheSame(oldItemPosition : Int , newItemPosition : Int) : Boolean {
        if (oldItemPosition in oldData.indices && newItemPosition in newData.indices) {
            return oldData[oldItemPosition] == newData[newItemPosition]
        }
        return false
    }
}

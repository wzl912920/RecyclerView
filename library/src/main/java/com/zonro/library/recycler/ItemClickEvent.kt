package com.zonro.library.recycler

import android.view.*

/**
 * Created by Lynn.
 */

interface ItemClickEvent {
    fun onItemClick(view : View , position : Int)
}
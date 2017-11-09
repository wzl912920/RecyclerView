package com.lynn.simplerecyclerview.drag.viewdraghelper

import android.content.Context
import android.support.v4.widget.NestedScrollView
import android.util.AttributeSet
import android.view.*

/**
 * Created by Lynn.
 */

class NSScrollView : NestedScrollView {
    constructor(context : Context) : super(context) {
    }

    constructor(context : Context , attrs : AttributeSet) : super(context , attrs) {
    }

    constructor(context : Context , attrs : AttributeSet , defStyleAttr : Int) : super(context , attrs , defStyleAttr) {
    }

    private var dc : DragContainer? = null
    override fun onInterceptTouchEvent(ev : MotionEvent?) : Boolean {
        if (dc == null) {
            dc = getChildAt(0) as DragContainer?
        }
        if (dc!!.isDragging()) {
            return false
        }
        return super.onInterceptTouchEvent(ev)
    }
}

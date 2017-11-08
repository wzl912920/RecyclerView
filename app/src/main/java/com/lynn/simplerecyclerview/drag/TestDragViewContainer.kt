package com.lynn.simplerecyclerview.drag

import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.view.*
import android.widget.RelativeLayout
import com.lynn.simplerecyclerview.*

/**
 * Created by Lynn.
 */

class TestDragViewContainer : RelativeLayout {
    constructor(context : Context) : super(context) {}

    constructor(context : Context , attrs : AttributeSet) : super(context , attrs) {}

    constructor(context : Context , attrs : AttributeSet , defStyleAttr : Int) : super(context , attrs , defStyleAttr) {}

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context : Context , attrs : AttributeSet , defStyleAttr : Int , defStyleRes : Int) : super(context , attrs , defStyleAttr , defStyleRes) {
    }

    private var isDragEnabled = false
    override fun onInterceptTouchEvent(ev : MotionEvent) : Boolean {
        val view = findViewById<View?>(R.id.drag_button)
        if (null != view && view.visibility == View.VISIBLE) {
            isDragEnabled = eventInView(ev , view)
            if (isDragEnabled) {
                return isDragEnabled
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event : MotionEvent?) : Boolean {
        if (isDragEnabled) {
            return false
        }
        return super.onTouchEvent(event)
    }

    private fun eventInView(ev : MotionEvent , view : View) : Boolean {
        return (ev.x > view.left) and (ev.x < view.right) and (ev.y > view.top) and (ev.y < view.bottom)
    }

}

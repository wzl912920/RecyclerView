package com.lynn.simplerecyclerview.drag

import android.content.Context
import android.graphics.*
import android.graphics.drawable.*
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v7.widget.*
import android.util.*
import android.view.*
import android.widget.*

/**
 * Created by Lynn.
 */

class TestDragFrameOuter : FrameLayout {
    constructor(context : Context) : super(context) {}

    constructor(context : Context , attrs : AttributeSet?) : super(context , attrs) {}

    constructor(context : Context , attrs : AttributeSet? , defStyleAttr : Int) : super(context , attrs , defStyleAttr) {}

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context : Context , attrs : AttributeSet? , defStyleAttr : Int , defStyleRes : Int) : super(context , attrs , defStyleAttr , defStyleRes) {
    }

    private var floatingView : ImageView? = null

    override fun onViewAdded(child : View?) {
        super.onViewAdded(child)
    }

    override fun dispatchTouchEvent(ev : MotionEvent) : Boolean {
        return super.dispatchTouchEvent(ev)
    }

    override fun dispatchDraw(canvas : Canvas?) {
        super.dispatchDraw(canvas)
        if (canvas == null) {
            return
        }
        floatingView?.let {
            val currentY = currentEvent?.rawY ?: 0f
            val downY = downPoint?.rawY ?: 0f
            val status = canvas.save()
            val top = viewTop
            canvas.translate(0f , top + ((currentY - downY)))
            floatingView?.draw(canvas)
            canvas.restoreToCount(status)
        }
    }

    fun destroyFloatingView() {
        val drawable = floatingView?.drawable
        drawable?.let {
            if (drawable is BitmapDrawable) {
                drawable.bitmap.recycle()
            }
        }
        floatingView = null
    }

    fun refresh() {
        invalidate()
    }

    private var viewTop = 0
    fun initStartOffset(ev : MotionEvent , top : Int) {
        downPoint?.let { downPoint?.recycle() }
        downPoint = ev
        viewTop = top
    }

    private var downPoint : MotionEvent? = null
    private var currentEvent : MotionEvent? = null
    fun setCurrentPosition(event : MotionEvent?) {
        currentEvent?.let { currentEvent?.recycle() }
        currentEvent = event
    }

    fun initFloatingView(view : View) {
        destroyFloatingView()
        view.isDrawingCacheEnabled = true
        val bmp = Bitmap.createBitmap(view.drawingCache)
        view.isDrawingCacheEnabled = false
        val img = AppCompatImageView(context)
        img.setBackgroundColor(Color.RED)
        img.setPadding(0 , 0 , 0 , 0)
        img.setImageBitmap(bmp)
        img.layoutParams = ViewGroup.LayoutParams(view.width , view.height)
        img.measure(view.width , view.height)
        img.layout(0 , 0 , view.width , view.height)
        floatingView = img
    }
}

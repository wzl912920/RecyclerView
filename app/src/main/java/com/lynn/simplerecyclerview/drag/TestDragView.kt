package com.lynn.simplerecyclerview.drag

import android.content.*
import android.graphics.*
import android.graphics.drawable.*
import android.support.v4.view.*
import android.support.v4.widget.*
import android.support.v7.widget.*
import android.util.*
import android.view.*
import android.widget.*
import com.lynn.library.util.*

/**
 * Created by Lynn.
 */

class TestDragView : LinearLayout {
    constructor(context : Context?) : super(context)
    constructor(context : Context? , attrs : AttributeSet?) : super(context , attrs)
    constructor(context : Context? , attrs : AttributeSet? , defStyleAttr : Int) : super(context , attrs , defStyleAttr)
    constructor(context : Context? , attrs : AttributeSet? , defStyleAttr : Int , defStyleRes : Int) : super(context , attrs , defStyleAttr , defStyleRes)

    private var floatingView : ImageView? = null
    private var selectedView : View? = null
    private val TAG = "DragView"
    private var downPoint : MotionEvent? = null
    private var currentPoint : MotionEvent? = null
    private var originalTop = 0
    private val gd = GestureDetector(context , object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e : MotionEvent?) : Boolean {
            return super.onSingleTapConfirmed(e)
        }
    })

    override fun onViewAdded(child : View?) {
        super.onViewAdded(child)
        child?.setOnClickListener { context.showWarning("123") }
        child?.setOnTouchListener { v , ev ->
            gd.onTouchEvent(ev)
            if (currentPoint != null) {
                currentPoint?.recycle()
            }
            currentPoint = MotionEvent.obtain(ev)
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (downPoint != null) {
                        downPoint?.recycle()
                    }
                    downPoint = MotionEvent.obtain(ev)
                    originalTop = v.top
                    floatingView = getFloatView(v)
                    parent?.requestDisallowInterceptTouchEvent(true)
                }
                MotionEvent.ACTION_MOVE -> {
                    if (!isLongPressing()) {
                        parent?.requestDisallowInterceptTouchEvent(false)
                    }
                }
                MotionEvent.ACTION_CANCEL ,
                MotionEvent.ACTION_UP -> {
                    destroyFloatView()
                    downPoint?.recycle()
                    downPoint = null
                    parent?.requestDisallowInterceptTouchEvent(false)
                }
                else -> {
                }
            }
            invalidate()
            true
        }
    }

    private fun isLongPressing() : Boolean {
        val method = gd::class.java.getDeclaredField("mAlwaysInTapRegion")
        method.isAccessible = true
        val result = method.getBoolean(gd)
        method.isAccessible = false
        return result
    }

    private fun isInLongPress() : Boolean {
        val method = gd::class.java.getDeclaredField("mInLongPress")
        method.isAccessible = true
        val result = method.getBoolean(gd)
        method.isAccessible = false
        return result
    }

    override fun dispatchDraw(canvas : Canvas?) {
        super.dispatchDraw(canvas)
        if (isInLongPress()) {
            canvas?.let { dispatchDrag(canvas) }
        }
    }

    private fun dispatchDrag(canvas : Canvas) {
        floatingView?.let {
            val translateY = originalTop + (currentPoint?.y ?: 0f) - (downPoint?.y ?: 0f)
            val status = canvas.save()
            canvas.translate(0f , translateY)
            floatingView?.draw(canvas)
            canvas.restoreToCount(status)
            post { checkPosition(translateY.toInt()) }
        }
    }

    private fun getParentScrollY() : Int {
        return (parent as NestedScrollView).scrollY
    }

    private val fixedOffset = context.dp2px(40f)
    private fun checkPosition(viewTop : Int) {
        val currentView = floatingView ?: return
        if (currentPoint == null) return
        val scrollView = (parent as NestedScrollView?) ?: return
        val minY = 0
        val maxY = scrollView.height

        val startY = viewTop
        val endY = startY + currentView.height
        val scrollY = getParentScrollY()
        if (endY + fixedOffset - scrollY > maxY) {
            Log.e(TAG , ">>>>>>>>>>>>>>")
            val offset = (Math.abs(endY + fixedOffset - scrollY - maxY)).toInt()
//            scrollView.smoothScrollBy(0 , offset)
            scrollView.fling(offset)
        } else if (startY - fixedOffset < scrollY) {
            Log.e(TAG , "<<<<<<<<<<<<<<")
            val offset = (Math.abs(startY - fixedOffset - scrollY)).toInt()
//            scrollView.smoothScrollBy(0 , -offset)
            scrollView.fling(-offset)
        }
        Log.e(TAG , "minY=$minY maxY=$maxY startY=$startY endY=$endY pointY=${currentPoint?.y} originalY=$viewTop")
    }

    private fun getFloatView(view : View) : ImageView {
        selectedView = view
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
        return img
    }

    private fun destroyFloatView() {
        (floatingView?.drawable as BitmapDrawable?)?.bitmap?.recycle()
        floatingView = null
    }
}

package com.lynn.simplerecyclerview.drag

import android.content.*
import android.graphics.*
import android.graphics.drawable.*
import android.os.*
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
    private val gd = GestureDetector(context , object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e : MotionEvent?) : Boolean {
            return super.onSingleTapUp(e)
        }
    })

    private var topViewOffset = 0f
    private var bottomViewOffset = 0f
    override fun onViewAdded(child : View?) {
        super.onViewAdded(child)
        child?.setOnTouchListener { v , ev ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                Log.e(TAG , "TestDragView=${MotionEvent.actionToString(ev.action)}")
            }
            if (isDragEnabled) {
                super.onTouchEvent(ev)
                gd.onTouchEvent(ev)
                when (ev.action) {
                    MotionEvent.ACTION_DOWN -> {
                        if (downPoint != null) {
                            downPoint?.recycle()
                        }
                        selectedView = v
                        downPoint = MotionEvent.obtain(ev)
                        topViewOffset = ev.y
                        bottomViewOffset = v.height - topViewOffset
                        val location = intArrayOf(0 , 0)
                        (parent as View).getLocationOnScreen(location)
                        val top = v.top - getParentScrollY() + location[1]
                        (parent.parent as TestFrame).initStartOffset(MotionEvent.obtain(ev) , top)
                        floatingView = getFloatView(v)
                        parent?.requestDisallowInterceptTouchEvent(true)
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (!isLongPressing()) {
                            parent?.requestDisallowInterceptTouchEvent(false)
                        } else {
                            (parent.parent as TestFrame).setCurrentPosition(MotionEvent.obtain(ev))
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
                if (isInLongPress()) {
                    (parent.parent as TestFrame).refresh()
                    dispatchDrag(ev)
                    if (selectedView?.visibility != View.INVISIBLE) {
                        selectedView?.visibility = View.INVISIBLE
                    }
                } else if ((MotionEvent.ACTION_UP == ev.action) or (MotionEvent.ACTION_CANCEL == ev.action)) {
                    (parent.parent as TestFrame).refresh()
                    dispatchDrag(ev)
                    if (selectedView?.visibility != View.VISIBLE) {
                        selectedView?.visibility = View.VISIBLE
                    }
                }
                true
            } else {
                super.onTouchEvent(ev)
            }
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

    private fun dispatchDrag(ev : MotionEvent) {
        floatingView?.let {
            val y = ev.rawY
            post {
                checkMoveIfNeeded(ev)
                swapIfNeeded(ev)
            }
        }
    }

    private fun getParentScrollY() : Int {
        return (parent as NestedScrollView).scrollY
    }

    private val offset = context.dp2px(40f)
    private fun checkMoveIfNeeded(ev : MotionEvent) {
        val scrollView = (parent as NestedScrollView?) ?: return
        val top = scrollView.top
        val bottom = top + scrollView.height
        val y = ev.rawY
        if (y - topViewOffset - offset < top) {
            val offset = (Math.abs(y - topViewOffset - offset - top)).toInt()
            scrollView.fling(-offset)
        } else if (y + bottomViewOffset + offset > bottom) {
            val offset = (Math.abs(y + bottomViewOffset + offset - bottom)).toInt()
            scrollView.fling(offset)
        }
    }

    private fun swapIfNeeded(ev : MotionEvent) {
        val currentView = selectedView ?: return
        val position = indexOfChild(currentView)

        val currentCenter = ev.y - topViewOffset + currentView.height / 2
        val location = intArrayOf(0 , 0)
        if (position > 0) {
            val pre = getChildAt(position - 1)
            pre.getLocationOnScreen(location)
            val preCenter = location[1] + pre.height / 2
            if (currentCenter < preCenter) {
                swap(pre , position , currentView)
                Log.e(TAG , "swap pre current")
            }
        }
        if (position < childCount - 1) {
            val next = getChildAt(position + 1)
            next.getLocationOnScreen(location)
            val nextCenter = location[1] + next.height / 2
            if (currentCenter > nextCenter) {
                swap(currentView , position , next)
                Log.e(TAG , "swap current next")
            }
        }
    }

    private fun swap(first : View , firstIndex : Int , second : View) {
        removeView(first)
        removeView(second)
        addView(first , firstIndex)
        addView(second , firstIndex)
    }

    private fun getFloatView(view : View) : ImageView {
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
        initParentFloatingView(img)
        return img
    }

    private fun initParentFloatingView(img : ImageView) {
        (parent.parent as TestFrame).initFloatingView(img)
    }


    private fun destroyFloatView() {
        (parent.parent as TestFrame).destroyFloatingView()
        val drawable = floatingView?.drawable
        drawable?.let {
            if (drawable is BitmapDrawable) {
                drawable.bitmap.recycle()
            }
        }
        floatingView = null
    }

    private var isDragEnabled = true
    fun setDragEnabled(enable : Boolean) {
        isDragEnabled = enable
    }
}

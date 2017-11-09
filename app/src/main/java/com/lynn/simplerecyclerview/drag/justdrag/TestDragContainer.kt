package com.lynn.simplerecyclerview.drag.justdrag

import android.content.*
import android.support.v4.widget.*
import android.util.*
import android.view.*
import android.view.animation.*
import android.widget.*
import com.lynn.library.util.*
import java.lang.ref.*

/**
 * Created by Lynn.
 */

class TestDragContainer : LinearLayout , View.OnTouchListener {
    constructor(context : Context?) : super(context)
    constructor(context : Context? , attrs : AttributeSet?) : super(context , attrs)
    constructor(context : Context? , attrs : AttributeSet? , defStyleAttr : Int) : super(context , attrs , defStyleAttr)
    constructor(context : Context? , attrs : AttributeSet? , defStyleAttr : Int , defStyleRes : Int) : super(context , attrs , defStyleAttr , defStyleRes)

    init {
        setOnTouchListener(this)
    }

    private var selectedView : View? = null
    private var downPoint : MotionEvent? = null
    private val gd = GestureDetector(context , GestureDetector.SimpleOnGestureListener())
    private val offset = context.dp2px(50f)
    private var topViewOffset = 0
    private var bottomViewOffset = 0
    private var isDragEnabled = true

    private fun getLocationView(ev : MotionEvent) : View? {
        return (0 until childCount)
                .map { getChildAt(it) }
                .firstOrNull { isTouchPointInView(it , ev.rawX.toInt() , ev.rawY.toInt()) }
    }

    private fun getParentScrollY() : Int {
        return (parent as NestedScrollView).scrollY
    }

    override fun onViewAdded(child : View?) {
        super.onViewAdded(child)
        child?.setOnClickListener { }
    }

    private fun checkMoveIfNeeded(ev : MotionEvent) {
        val scrollView = (parent as NestedScrollView?) ?: return
        val location = intArrayOf(0 , 0)
        scrollView.getLocationOnScreen(location)
        val top = location[1]
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
        var isSwapped = false
        val currentCenter = ev.y - topViewOffset + currentView.height / 2
        val location = intArrayOf(0 , 0)
        if (position > 0) {
            val pre = getChildAt(position - 1)
            pre.getLocationOnScreen(location)
            val preCenter = location[1] + pre.height / 2
            if (currentCenter < preCenter) {
                isSwapped = true
                swap(pre , position - 1 , currentView , false)
            }
        }
        if (!isSwapped && position < childCount - 1) {
            val next = getChildAt(position + 1)
            next.getLocationOnScreen(location)
            val nextCenter = location[1] + next.height / 2
            if (currentCenter > nextCenter) {
                swap(currentView , position , next , true)
            }
        }
    }

    private fun swap(first : View , firstIndex : Int , second : View , isSwapAfter : Boolean) {
        val firstHeight = first.height.toFloat()
        val secondHeight = second.height.toFloat()
        removeView(first)
        removeView(second)
        addView(first , firstIndex)
        addView(second , firstIndex)
        var anim : Animation
        if (isSwapAfter) {
            anim = TranslateAnimation(0f , 0f , firstHeight , 0f)
            anim.duration = 100
            second.startAnimation(anim)
        } else {
            anim = TranslateAnimation(0f , 0f , -secondHeight , 0f)
            anim.duration = 100
            first.startAnimation(anim)
        }
    }

    fun setDragEnabled(enable : Boolean) {
        isDragEnabled = enable
    }

    private fun isTouchPointInView(view : View? , x : Int , y : Int) : Boolean {
        if (view == null) {
            return false
        }
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val left = location[0]
        val top = location[1]
        val right = left + view.measuredWidth
        val bottom = top + view.measuredHeight
        val flag = (y in top..bottom && x in left..right)
        if (flag) {
            topViewOffset = y - location[1]
            val bvo = minHeight - topViewOffset
            bottomViewOffset = if (bvo < 0) 0 else bvo
        }
        return flag
    }

    private var tf : SoftReference<TestDragFrameOuter?> = SoftReference(null)
    override fun onTouch(view : View? , ev : MotionEvent?) : Boolean {
        if (null == view || null == ev) return super.onTouchEvent(ev)
        if (tf.get() == null) {
            tf = SoftReference(parent.parent as TestDragFrameOuter)
        }
        if (isDragEnabled) {
            gd.onTouchEvent(ev)
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (downPoint != null) {
                        downPoint?.recycle()
                    }
                    downPoint = MotionEvent.obtain(ev)
                    selectedView = getLocationView(ev)
                    if (selectedView != null) {
                        maxHeight = selectedView!!.layoutParams.height
                        val location = intArrayOf(0 , 0)
                        (parent as View).getLocationOnScreen(location)
                        val top = selectedView!!.top - getParentScrollY() + location[1]
                        val tf = tf.get()!!
                        tf.initStartOffset(MotionEvent.obtain(ev) , top)
                        tf.initFloatingView(selectedView!! , minHeight)
                    }
                    parent?.requestDisallowInterceptTouchEvent(true)
                }
                MotionEvent.ACTION_MOVE -> {
                    if (!isLongPressing()) {
                        parent?.requestDisallowInterceptTouchEvent(false)
                    } else {
                        tf.get()!!.setCurrentPosition(MotionEvent.obtain(ev))
                    }
                }
                MotionEvent.ACTION_CANCEL ,
                MotionEvent.ACTION_UP -> {
                    tf.get()!!.destroyFloatingView()
                    downPoint?.recycle()
                    downPoint = null
                    parent?.requestDisallowInterceptTouchEvent(false)
                }
                else -> {
                }
            }
            if (isInLongPress()) {
                if (selectedView?.layoutParams?.height ?: 0 == maxHeight) {
                    smallImage()
                }
                tf.get()!!.refresh()
                dispatchDrag(ev)
                if (selectedView?.visibility != View.INVISIBLE) {
                    selectedView?.visibility = View.INVISIBLE
                }
            } else if ((MotionEvent.ACTION_UP == ev.action) or (MotionEvent.ACTION_CANCEL == ev.action)) {
                largeImage()
                tf.get()!!.refresh()
                if (selectedView?.visibility != View.VISIBLE) {
                    selectedView?.visibility = View.VISIBLE
                }
            }
            return true
        } else {
            return super.onTouchEvent(ev)
        }
    }

    private var minHeight = context.dp2px(80f).toInt()
    private var maxHeight = 0

    private fun smallImage() {
        if (maxHeight == 0) return
        val currentView = selectedView ?: return
        val lp = currentView.layoutParams
        lp.height = minHeight
        currentView.layoutParams = lp
    }

    private fun largeImage() {
        if (maxHeight == 0) return
        val currentView = selectedView ?: return
        val lp = currentView.layoutParams
        lp.height = maxHeight
        currentView.layoutParams = lp
        maxHeight = 0
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
        post {
            checkMoveIfNeeded(ev)
            swapIfNeeded(ev)
        }
    }
}

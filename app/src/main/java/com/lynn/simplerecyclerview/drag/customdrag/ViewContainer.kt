package com.lynn.simplerecyclerview.drag.customdrag

import android.animation.*
import android.content.*
import android.graphics.*
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

class ViewContainer : LinearLayout , View.OnTouchListener {
    constructor(context : Context?) : super(context)
    constructor(context : Context? , attrs : AttributeSet?) : super(context , attrs)
    constructor(context : Context? , attrs : AttributeSet? , defStyleAttr : Int) : super(context , attrs , defStyleAttr)
    constructor(context : Context? , attrs : AttributeSet? , defStyleAttr : Int , defStyleRes : Int) : super(context , attrs , defStyleAttr , defStyleRes)

    init {
        setOnTouchListener(this)
    }

    private var selectedView : View? = null
    private var downPoint : MotionEvent? = null
    private val offset = context.dp2px(40f)
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

    private fun checkMoveIfNeeded(ev : MotionEvent) {
        val scrollView = (parent as NestedScrollView?) ?: return
        val location = getLocation(scrollView)
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
        if (position > 0) {
            val pre = getChildAt(position - 1)
            val location = getLocation(pre)
            val preCenter = location[1] + pre.height / 2
            if (currentCenter < preCenter) {
                isSwapped = true
                swap(pre , position - 1 , currentView , false)
            }
        }
        if (!isSwapped && position < childCount - 1) {
            val next = getChildAt(position + 1)
            val location = getLocation(next)
            val nextCenter = location[1] + next.height / 2
            if (currentCenter > nextCenter) {
                swap(currentView , position , next , true)
            }
        }
    }

    private fun swap(first : View , firstIndex : Int , second : View , isSwapAfter : Boolean) {
        val firstHeight = first.height.toFloat()
        val secondHeight = second.height.toFloat()
        if (isSwapAfter) {
            removeView(first)
            addView(first , firstIndex + 1)
        } else {
            removeView(second)
            addView(second , firstIndex)
        }
//        if (second is TestDragViewContainer) {
//            if (second.resetPosition() && selectedView is KoalaEditTextView) {
//                downPoint?.let { getParentContainer().initFloatingView(selectedView!!) }
//            }
//        }
        var anim : TranslateAnimation
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

    private fun getParentContainer() : FrameOuterContainer {
        if (null == tf.get()) {
            tf = SoftReference(parent.parent as FrameOuterContainer)
        }
        return tf.get()!!
    }

    private fun isTouchPointInView(view : View? , x : Int , y : Int) : Boolean {
        if (view == null) {
            return false
        }
        val location = getLocation(view)
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

    override fun onViewAdded(child : View?) {
        super.onViewAdded(child)
        child?.setOnClickListener { }
        child?.setLayerType(View.LAYER_TYPE_HARDWARE , null)
    }

    private var tf : SoftReference<FrameOuterContainer?> = SoftReference(null)
    override fun onTouch(view : View? , ev : MotionEvent?) : Boolean {
        val flag = super.onTouchEvent(ev)
        if (null == view || null == ev) return flag
        val fc = getParentContainer()
        if (isDragEnabled && !flag) {
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (downPoint != null) {
                        downPoint?.recycle()
                    }
                    downPoint = MotionEvent.obtain(ev)
                    selectedView = getLocationView(ev)
                    if (selectedView != null) {
                        val top = selectedView!!.top - getParentScrollY()
                        fc.initStartOffset(MotionEvent.obtain(ev) , top)
                        initSize()
                        smallImage()
                    }
                    parent?.requestDisallowInterceptTouchEvent(true)
                }
                MotionEvent.ACTION_MOVE -> {
                    fc.setCurrentPosition(MotionEvent.obtain(ev))
                    fc.refresh()
                    dispatchDrag(ev)
                }
                MotionEvent.ACTION_CANCEL ,
                MotionEvent.ACTION_UP -> {
                    downPoint?.recycle()
                    downPoint = null
                    largeImage()
                    dispatchDrag(ev)
                    if (selectedView?.visibility != View.VISIBLE) {
                        selectedView?.visibility = View.VISIBLE
                    }
                    parent?.requestDisallowInterceptTouchEvent(false)
                    fc.destroyFloatingView()
                    fc.refresh()
                }
                else -> {
                }
            }
            return true
        } else {
            return flag
        }
    }

    private fun initSize() {
        val content = getContentView() ?: return
        maxHeight = content.bottom - content.top
        originalHeight = content.layoutParams.height
        minHeight = if (maxHeight > fixedMinHeight) fixedMinHeight else maxHeight
    }

    private fun getContentView() : View? {
        return selectedView ?: null
    }

    private var fixedMinHeight = context.dp2px(68f).toInt()
    private var minHeight = 0
    private var maxHeight = 0
    private var originalHeight = 0

    private var hAnim : HeightAnim? = null
    private var anim : ObjectAnimator? = null
    private val animTime = 100L
    private fun smallImage() {
        if (maxHeight == 0) return
        val currentView = selectedView ?: return
        val content = getContentView() ?: return
        hAnim = HeightAnim(content)
        anim?.removeAllListeners()
        anim?.removeAllUpdateListeners()
        anim?.end()
        val currAnim = ObjectAnimator.ofInt(hAnim!! , "x" , content.bottom - content.top , minHeight).setDuration(animTime)
        currAnim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation : Animator?) {
            }

            override fun onAnimationEnd(animation : Animator?) {
                downPoint?.let {
                    content?.post {
                        getParentContainer().initFloatingView(currentView)
                        if (selectedView?.visibility != View.INVISIBLE) {
                            selectedView?.visibility = View.INVISIBLE
                        }
                    }
                }
            }

            override fun onAnimationCancel(animation : Animator?) {
            }

            override fun onAnimationStart(animation : Animator?) {
            }
        })
        currAnim.start()
        anim = currAnim
//        when (currentView) {
//            is KoalaImageView -> {
//                currentView.actionDown()
//            }
//            is KoalaFileView -> {
//                currentView.actionDown()
//            }
//            is KoalaEditTextView -> {
//                currentView.actionDown()
//            }
//        }
    }

    private fun largeImage() {
        if (maxHeight == 0) return
        val currentView = selectedView ?: return
        val content = getContentView() ?: return
        anim?.removeAllListeners()
        anim?.end()
        val currAnim = ObjectAnimator.ofInt(hAnim!! , "x" , content.bottom - content.top , originalHeight).setDuration(animTime)
        currAnim.start()
        anim = currAnim
//        when (currentView) {
//            is KoalaImageView -> {
//                currentView.actionUp()
//            }
//            is KoalaFileView -> {
//                currentView.actionUp()
//            }
//            is KoalaEditTextView -> {
//                currentView.actionUp()
//            }
//        }
        maxHeight = 0
    }

    private fun dispatchDrag(ev : MotionEvent) {
        post {
            checkMoveIfNeeded(ev)
            swapIfNeeded(ev)
        }
    }

    companion object {
        internal fun getLocation(view : View) : IntArray {
            val location = IntArray(2)
            view.getLocationOnScreen(location)
            if (location[1] == 0) {
                val rect = Rect()
                view.getGlobalVisibleRect(rect)
                location[1] = rect.top
            }
            return location
        }

        internal class HeightAnim(view : View) {
            val sr = SoftReference(view)
            fun setX(x : Int) {
                val lp = sr.get()?.layoutParams
                lp?.height = x
                sr.get()?.layoutParams = lp
            }
        }
    }
}

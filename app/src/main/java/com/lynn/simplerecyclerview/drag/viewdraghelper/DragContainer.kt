package com.lynn.simplerecyclerview.drag.viewdraghelper

import android.animation.*
import android.content.Context
import android.os.*
import android.support.annotation.RequiresApi
import android.support.v4.widget.*
import android.view.*
import android.widget.*
import com.lynn.library.util.*
import java.lang.ref.*
import android.view.MotionEvent
import android.util.*


/**
 * Created by lynn on 16/11/21.
 */

class DragContainer : RelativeLayout {

    private var helper : ViewDragHelper? = null
    private val vdc = ViewDragCallback(this)
    private val fixedOffset : Int = context.dp2px(40f).toInt()

    @JvmOverloads constructor(context : Context , attrs : AttributeSet? = null , defStyleAttr : Int = 0) : super(context , attrs , defStyleAttr) {
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context : Context , attrs : AttributeSet , defStyleAttr : Int , defStyleRes : Int) : super(context , attrs , defStyleAttr , defStyleRes) {
    }

    fun isDragging() : Boolean {
        return vdc.isDragging()
    }

    init {
        helper = ViewDragHelper.create(this , 0.01f , vdc)
    }

    override fun onInterceptTouchEvent(ev : MotionEvent) : Boolean {
        return helper!!.shouldInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        helper!!.processTouchEvent(event)
        return true
    }

    private fun onDragChanged(top : Int , height : Int , dy : Int) {
        val p = parent as NSScrollView
        if (top < p.scrollY + fixedOffset) {
            if (dy < 0) {
                p.smoothScrollBy(0 , dy)
            }
        } else if (top + height > p.scrollY + p.height - fixedOffset) {
            if (dy > 0) {
                p.smoothScrollBy(0 , dy)
            }
        }
    }

    override fun onViewAdded(child : View?) {
        super.onViewAdded(child)
    }

    companion object {
        private class ViewDragCallback(container : DragContainer) : ViewDragHelper.Callback() {
            private val sr = SoftReference(container)
            private var current : View? = null
            private var originalHeight = 0
            private var fixedHeight = container.context.dp2px(50f)
            override fun tryCaptureView(child : View , pointerId : Int) : Boolean {
                if (null != sr.get()) {
                    val dc = sr.get()!!
                    current = child
                    originalPosition = dc.indexOfChild(current)
                    smallDragedView()
                    return true
                }
                return false
            }

            private var isDragging = false
            fun isDragging() : Boolean {
                return isDragging
            }

            private var originalPosition = 0

            private fun smallDragedView() {
                if (null == current) return
                isDragging = true
                current!!.bringToFront()
                originalHeight = current!!.height
                val anim = ObjectAnimator.ofInt(ObjAnim(current!!) , "animValue" , current!!.layoutParams.height , fixedHeight.toInt()).setDuration(50)
                anim.start()
            }

            private fun largeDragedView() {
                if (null == current || originalHeight == 0) return
                isDragging = false
                val anim = ObjectAnimator.ofInt(ObjAnim(current!!) , "animValue" , current!!.layoutParams.height , originalHeight).setDuration(50)
                anim.start()
                originalHeight = 0
            }

            override fun clampViewPositionHorizontal(child : View , left : Int , dx : Int) : Int {
                return child?.left ?: 0
            }

            override fun clampViewPositionVertical(child : View , top : Int , dy : Int) : Int {
                return Math.max(0 , top)
            }

            override fun getViewHorizontalDragRange(child : View) : Int {
                return 0
            }

            override fun getViewVerticalDragRange(child : View) : Int {
                return sr.get()?.height ?: 0
            }

            override fun onViewCaptured(capturedChild : View , activePointerId : Int) {
                //pull Start
            }

            //before pre this next after
            private fun adjustPosition(thisView : View , dc : DragContainer) {
                val current = thisView
                var pre : View? = null
                var pre2 : View? = null
                if (originalPosition > 0) {
                    pre = dc.getChildAt(originalPosition - 1)
                    if (originalPosition > 1) {
                        pre2 = dc.getChildAt(originalPosition - 2)
                    }
                }
                var next : View? = null
                var next2 : View? = null
                if (originalPosition < dc.childCount - 1) {
                    next = dc.getChildAt(originalPosition)
                    if (originalPosition < dc.childCount - 2) {
                        next2 = dc.getChildAt(originalPosition + 1)
                    }
                }
                if (null != pre) {
                    if (current.top <= pre.top) {
                        //up
                        val clp = current.layoutParams as LayoutParams
                        clp.addRule(BELOW , pre2?.id ?: 0)
                        current.layoutParams = clp

                        val plp = pre.layoutParams as LayoutParams
                        plp.addRule(BELOW , current.id)
                        pre.layoutParams = plp

                        val nlp = next?.layoutParams as LayoutParams?
                        nlp?.addRule(BELOW , pre.id)
                        next?.layoutParams = nlp

                        originalPosition -= 1
                    } else if (null != next) {
                        //down
                        if (current.bottom >= next.bottom) {
                            val clp = current.layoutParams as LayoutParams
                            clp.addRule(BELOW , next.id)
                            current.layoutParams = clp

                            val nlp = next.layoutParams as LayoutParams
                            nlp.addRule(BELOW , pre?.id)
                            next.layoutParams = nlp

                            val n2lp = next2?.layoutParams as LayoutParams?
                            n2lp?.addRule(BELOW , current.id)
                            next2?.layoutParams = n2lp

                            originalPosition += 1
                        }
                    }
                } else if (null != next && current.bottom >= next.bottom) {
                    //down
                    val clp = current.layoutParams as LayoutParams
                    clp.addRule(BELOW , next.id)
                    current.layoutParams = clp

                    val nlp = next.layoutParams as LayoutParams
                    nlp.addRule(BELOW , pre?.id ?: 0)
                    next.layoutParams = nlp

                    val n2lp = next2?.layoutParams as LayoutParams?
                    n2lp?.addRule(BELOW , current.id)
                    next2?.layoutParams = n2lp

                    originalPosition += 1
                }
            }

            override fun onViewPositionChanged(changedView : View , left : Int , top : Int , dx : Int , dy : Int) {
                if (null == changedView) return
                if (sr.get() == null) return
                val dc = sr.get()!!
                dc.onDragChanged(top , changedView.height , dy)
                adjustPosition(changedView , dc)
            }

            override fun onViewReleased(releasedChild : View , xvel : Float , yvel : Float) {
                if (null == releasedChild) return
                if (sr.get() == null) return
                val dc = sr.get()!!
                largeDragedView()

                dc.removeView(current)
                dc.addView(current , originalPosition)
            }
        }

        private class ObjAnim(view : View) {
            private val sr = SoftReference(view)
            fun setAnimValue(x : Int) {
                val p = sr.get()?.layoutParams ?: return
                p.height = x
                sr.get()?.layoutParams = p
            }
        }
    }
}


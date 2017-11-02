package com.lynn.simplerecyclerview.drag

import android.animation.*
import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.widget.ViewDragHelper
import android.util.AttributeSet
import android.view.*
import android.widget.*
import com.lynn.library.util.*
import java.lang.ref.*


/**
 * Created by lynn on 16/11/21.
 */

class DragContainer : RelativeLayout {

    private var helper : ViewDragHelper? = null
    private val vdc = ViewDragCallback(this)

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
        if (top < p.scrollY) {
            p.smoothScrollBy(0 , -height / 2)
        } else if (top + height > p.scrollY + p.height) {
            p.smoothScrollBy(0 , height / 2)
        }
    }

    override fun addView(child : View?) {
        super.addView(child)
    }

    companion object {
        val mapList = mutableMapOf<String , Int>()

//        interface Callback {
//            fun onPullStart(view : View)
//
//            fun onPull(view : View , progress : Float)
//
//            fun onPullCancel(view : View)
//
//            fun onPullComplete(view : View)
//        }

        private class ViewDragCallback(container : DragContainer) : ViewDragHelper.Callback() {
            private val sr = SoftReference(container)
            private var dragView : View? = null
            private var originalHeight = 0
            private var fixedHeight = container.context.dp2px(50f)
            override fun tryCaptureView(child : View , pointerId : Int) : Boolean {
                if (null != sr.get()) {
                    val dc = sr.get()!!
                    for (i in 0 until dc.childCount) {
                        if (dc.getChildAt(i) === child) {
                            dragView = child
                            originalPosition = dc.indexOfChild(dragView)
                            smallDragedView()
                            return true
                        }
                    }
                }
                return false
            }

            private var isDragging = false
            fun isDragging() : Boolean {
                return isDragging
            }

            private var originalPosition = 0
            fun end() {

            }

            private fun smallDragedView() {
                isDragging = true
                if (null == dragView) return
                dragView!!.bringToFront()
                originalHeight = dragView!!.height
                val anim = ObjectAnimator.ofInt(ObjAnim(dragView!!) , "animValue" , dragView!!.layoutParams.height , fixedHeight.toInt()).setDuration(50)
                anim.start()
                anim.addListener(AnimListener(this))
//                val p = dragView!!.layoutParams
//                p.height = fixedHeight.toInt()
//                dragView!!.layoutParams = p
            }

            private fun largeDragedView() {
                isDragging = false
                if (null == dragView || originalHeight == 0) return
                val anim = ObjectAnimator.ofInt(ObjAnim(dragView!!) , "animValue" , dragView!!.layoutParams.height , originalHeight).setDuration(50)
                anim.start()
                anim.addListener(AnimListener(this))
                originalHeight = 0
//                val p = dragView!!.layoutParams
//                p.height = originalHeight
//                dragView!!.layoutParams = p
            }

            override fun clampViewPositionHorizontal(child : View? , left : Int , dx : Int) : Int {
                return child?.left ?: 0
            }

            override fun clampViewPositionVertical(child : View? , top : Int , dy : Int) : Int {
                return Math.max(0 , top)
            }

            override fun getViewHorizontalDragRange(child : View?) : Int {
                return 0
            }

            override fun getViewVerticalDragRange(child : View?) : Int {
                return sr.get()?.height ?: 0
            }

            override fun onViewCaptured(capturedChild : View? , activePointerId : Int) {
                //pull Start
            }

            //before pre this next after
            private fun adjustPosition(thisView : View , dc : DragContainer) {
                if (dragView == null) return
                var first : View? = null
                var second : View? = null
                var third : View? = null
                var fourth : View? = null
                var last = dc.childCount - 1
                var currentPosition = dc.indexOfChild(thisView)
                if (currentPosition > 1) {
                    first = dc.getChildAt(currentPosition - 2)
                    second = dc.getChildAt(currentPosition - 1)
                } else if (currentPosition > 0) {
                    second = dc.getChildAt(currentPosition - 1)
                }
                if (currentPosition + 2 <= last) {
                    fourth = dc.getChildAt(currentPosition + 2)
                } else if (currentPosition + 1 <= last) {
                    third = dc.getChildAt(currentPosition + 1)
                }
                second?.let {

                }

                if (true) {
//                val thisTopMargin = (thisView.layoutParams as LayoutParams).topMargin
//                if (originalPosition == 0) {
//                    if (dc.childCount > 1) {
//                        //down
//                        val next = dc.getChildAt(0)
//                        if (thisView.top + thisView.height / 2 > next.top + next.height / 2 + (next.layoutParams as LayoutParams).topMargin * 2) {
//                            log("down ------ 1")
//                            var p = thisView.layoutParams as LayoutParams
//                            p.addRule(BELOW , next.id)
//                            thisView.layoutParams = p
//
//                            p = next.layoutParams as LayoutParams
//                            p.addRule(BELOW , 0)
//                            next.layoutParams = p
//
//                            if (dc.childCount > 2) {
//                                val after = dc.getChildAt(1)
//                                p = after.layoutParams as LayoutParams
//                                p.addRule(BELOW , thisView.id)
//                                after.layoutParams = p
//                            }
//                            originalPosition += 1
//                        }
//                    }
//                } else if (originalPosition == dc.childCount - 1) {
//                    val pre = dc.getChildAt(originalPosition - 1)
//                    if (thisView.top + thisView.height / 2 + thisTopMargin * 2 < pre.top + pre.height / 2) {
//                        log("up ------ 2")
//                        var p = pre.layoutParams as LayoutParams
//                        p.addRule(BELOW , thisView.id)
//                        pre.layoutParams = p
//                        p = thisView.layoutParams as LayoutParams
//                        if (dc.childCount > 2) {
//                            val before = dc.getChildAt(originalPosition - 2)
//                            p.addRule(BELOW , before.id)
//                        } else {
//                            p.addRule(BELOW , 0)
//                        }
//                        thisView.layoutParams = p
//                        originalPosition -= 1
//                    }
//                } else {
//                    val pre = dc.getChildAt(originalPosition - 1)
//                    if (thisView.top + thisView.height / 2 + thisTopMargin * 2 < pre.top + pre.height / 2) {
//                        log("up ------ 3")
//                        var beforeId = 0
//                        if (originalPosition - 2 >= 0) {
//                            beforeId = dc.getChildAt(originalPosition - 2).id
//                        }
//                        var p = thisView.layoutParams as LayoutParams
//                        p.addRule(BELOW , beforeId)
//                        thisView.layoutParams = p
//
//                        p = pre.layoutParams as LayoutParams
//                        p.addRule(BELOW , thisView.id)
//                        pre.layoutParams = p
//
//                        if (originalPosition < dc.childCount - 1) {
//                            val next = dc.getChildAt(originalPosition)
//                            p = next.layoutParams as LayoutParams
//                            p.addRule(BELOW , pre.id)
//                        }
//                        originalPosition -= 1
//                    } else {
//                        //down
//                        val next = dc.getChildAt(originalPosition)
//                        if (thisView.top + thisView.height / 2 > next.top + next.height / 2 + (next.layoutParams as LayoutParams).topMargin * 2) {
//                            log("down ------ 4")
//                            log("A=${thisView.top + thisView.height / 2} B=${next.top + next.height / 2}")
//                            var preId = 0
//                            if (originalPosition > 0) {
//                                preId = dc.getChildAt(originalPosition - 1).id
//                            }
//                            var p = thisView.layoutParams as LayoutParams
//                            p.addRule(BELOW , next.id)
//                            thisView.layoutParams = p
//
//                            p = next.layoutParams as LayoutParams
//                            p.addRule(BELOW , preId)
//                            next.layoutParams = p
//
//                            if (originalPosition < dc.childCount - 1 && originalPosition + 1 != dc.childCount - 1) {
//                                val after = dc.getChildAt(originalPosition + 1)
//                                p = after.layoutParams as LayoutParams
//                                p.addRule(BELOW , thisView.id)
//                                after.layoutParams = p
//                            }
//                            originalPosition += 1
//                        }
//                    }
//                }
                }
            }

            override fun onViewPositionChanged(changedView : View? , left : Int , top : Int , dx : Int , dy : Int) {
                if (null == changedView) return
                if (sr.get() == null) return
                val dc = sr.get()!!
//                log("changedView.top=${changedView.top} === top=$top dy=$dy")
                dc.onDragChanged(top , changedView.height , dy)
                adjustPosition(changedView , dc)
                //on view pulled
            }

            override fun onViewReleased(releasedChild : View? , xvel : Float , yvel : Float) {
                if (null == releasedChild) return
                if (sr.get() == null) return
                val dc = sr.get()!!
//                log("release")
                largeDragedView()

                dc.removeView(dragView)
                dc.addView(dragView , originalPosition)
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

        private class AnimListener(callBack : ViewDragCallback) : Animator.AnimatorListener {
            private val sr = SoftReference(callBack)
            override fun onAnimationRepeat(animation : Animator?) {
            }

            override fun onAnimationEnd(animation : Animator?) {
                sr.get()?.end()
            }

            override fun onAnimationCancel(animation : Animator?) {
            }

            override fun onAnimationStart(animation : Animator?) {
            }
        }
    }
}


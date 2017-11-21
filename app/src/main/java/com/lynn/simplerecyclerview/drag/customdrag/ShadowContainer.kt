package com.lynn.simplerecyclerview.drag.customdrag

import android.content.*
import android.graphics.*
import android.support.annotation.*
import android.util.*
import android.view.*
import android.widget.*
import com.bumptech.glide.*
import com.lynn.library.util.*
import com.lynn.simplerecyclerview.*

/**
 * Created by Lynn.
 */

class ShadowContainer : RelativeLayout {
    constructor(context : Context?) : super(context)
    constructor(context : Context? , attrs : AttributeSet?) : super(context , attrs)
    constructor(context : Context? , attrs : AttributeSet? , defStyleAttr : Int) : super(context , attrs , defStyleAttr)
    constructor(context : Context? , attrs : AttributeSet? , defStyleAttr : Int , defStyleRes : Int) : super(context , attrs , defStyleAttr , defStyleRes)


    private var paint = Paint()
    private var rects = RectF(0f , 0f , 0f , 0f)
    private val shadowColor = Color.parseColor("#12000000")
    private val highLightColor = Color.parseColor("#50000000")

    init {
        paint.color = Color.WHITE
        setLayerType(FrameLayout.LAYER_TYPE_SOFTWARE , null)
        paint.isAntiAlias = (true)
    }

    private val radius = context.dp2px(5f)
    private val offset = context.dp2px(1f)
    override fun dispatchDraw(canvas : Canvas?) {
        if (showShadow) {
            val child = getChildAt(0)
            child?.let {
                val shadowColor = if (highLight) {
                    highLightColor
                } else shadowColor
                paint.setShadowLayer(radius , offset , offset , shadowColor)
                rects.set(child.left.toFloat() + offset , child.top.toFloat() + offset , child.right.toFloat() - offset , child.bottom.toFloat() - offset)
                canvas?.drawRoundRect(rects , radius , radius , paint)
            }
        }
        super.dispatchDraw(canvas)
    }

    private var showShadow = true
    fun showShadow(show : Boolean) {
        this.showShadow = show
        invalidate()
    }

    private var highLight = true
    fun showHighLight(highLight : Boolean) {
        this.highLight = highLight
        invalidate()
    }

    private var isDragEnabled = false
    override fun onInterceptTouchEvent(ev : MotionEvent) : Boolean {
        val view = findViewById<View?>(dragId)
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

    private var dragId = R.id.drag_button
    fun setDragViewId(@IdRes id : Int) {
        dragId = id
    }

    private fun eventInView(ev : MotionEvent , view : View) : Boolean {
        return (ev.x > view.left) and (ev.x < view.right) and (ev.y > view.top) and (ev.y < view.bottom)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val s = findViewById<ImageView>(R.id.image) ?: return
        Glide.with(context).load("http://lorempixel.com/400/200").into(s)
    }
}

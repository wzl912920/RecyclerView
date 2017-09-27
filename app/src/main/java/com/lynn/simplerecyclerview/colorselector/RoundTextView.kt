package com.lynn.simplerecyclerview.colorselector

import android.content.Context
import android.graphics.*
import android.os.*
import android.util.AttributeSet
import android.view.*
import android.widget.*
import com.lynn.library.util.*
import com.lynn.simplerecyclerview.*

/**
 * Created by Lynn.
 */

class RoundTextView : TextView {
    constructor(context : Context) : super(context) {}

    constructor(context : Context , attrs : AttributeSet?) : super(context , attrs) {}

    constructor(context : Context , attrs : AttributeSet? , defStyleAttr : Int) : super(context , attrs , defStyleAttr) {}

    constructor(context : Context , attrs : AttributeSet? , defStyleAttr : Int , defStyleRes : Int) : super(context , attrs , defStyleAttr , defStyleRes) {}

    private val bgPaint = Paint()
    private val paint = Paint()
    private val path = Path()

    init {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE , null)
        }
        bgPaint.color = resources.getColor(R.color.color_F6F6F6)
        bgPaint.style = Paint.Style.FILL
        bgPaint.isAntiAlias = true

        paint.color = resources.getColor(R.color.color_DCDCDC)
        paint.style = Paint.Style.STROKE
        paint.isAntiAlias = true
        paint.strokeWidth = context.dp2px(0.5f)
        setLayerType(LAYER_TYPE_SOFTWARE , null)
        gravity = Gravity.CENTER
        textSize = context.dp2px(3f)
    }

    override fun draw(canvas : Canvas) {
        path.reset()
        val w = this.width / 2
        val h = this.height / 2
        path.addCircle(w.toFloat() , h.toFloat() , Math.min(w , h).toFloat() , Path.Direction.CW)
        canvas.clipPath(path)
        canvas.drawCircle(w.toFloat() , h.toFloat() , Math.min(w , h).toFloat() , bgPaint)
        super.draw(canvas)
        canvas.drawCircle(w.toFloat() , h.toFloat() , Math.min(w , h).toFloat() , paint)
    }
}

package com.lynn.simplerecyclerview.loading

import android.content.*
import android.graphics.*
import android.support.annotation.*
import android.util.*
import android.view.*
import com.lynn.library.util.*

/**
 * Created by Lynn.
 */

class MaterialLoadingBar : View {
    constructor(context : Context?) : super(context)
    constructor(context : Context? , attrs : AttributeSet?) : super(context , attrs)
    constructor(context : Context? , attrs : AttributeSet? , defStyleAttr : Int) : super(context , attrs , defStyleAttr)
    constructor(context : Context? , attrs : AttributeSet? , defStyleAttr : Int , defStyleRes : Int) : super(context , attrs , defStyleAttr , defStyleRes)

    private val paint = Paint()
    private val textPaint = Paint()
    private val backGroundPaint = Paint()
    private val animDuration = 4000 / 3f
    private val oval = RectF()
    private var text : String = "Loading"
    private var intArr = intArrayOf(Color.RED , Color.YELLOW , Color.GREEN , Color.CYAN , Color.BLUE , Color.MAGENTA , Color.RED)
    private var paintInited = false

    init {
        textPaint.color = Color.GREEN
        textPaint.isAntiAlias = true
        textPaint.textSize = context.dp2px(10f)
        paint.color = Color.GREEN
        paint.strokeWidth = context.dp2px(3f)
        paint.style = Paint.Style.STROKE
        paint.isAntiAlias = true
        backGroundPaint.color = Color.TRANSPARENT
        backGroundPaint.isAntiAlias = true
    }

    fun setText(text : String) {
        this.text = text
    }

    fun setTextColor(@ColorInt color : Int) {
        textPaint.color = color
    }

    fun setTextSize(size : Float) {
        textPaint.textSize = size
    }

    fun setTypeFace(face : Typeface) {
        textPaint.typeface = face
    }

    fun setColor(vararg color : Int) {
        intArr = color
        paintInited = false
    }

    @Override
    fun setBackGroundColor(@ColorInt color : Int) {
        backGroundPaint.color = color
    }

    private fun initPaint() {
        if (!paintInited && width > 0 && height > 0) {
            paintInited = true
            val w = width.toFloat()
            val h = height.toFloat()
            val x = w / 2
            val y = h / 2
            var sh : Shader = SweepGradient(x , y , intArr , null)
            paint.shader = sh
        }
    }

    override fun draw(canvas : Canvas?) {
        super.draw(canvas)
        initPaint()
        canvas?.apply {
            drawBg(canvas)
        }
    }

    private fun drawBg(canvas : Canvas) {
        val w = width.toFloat()
        val h = height.toFloat()
        val x = w / 2
        val y = h / 2
        val radius = Math.min(x , y) - paint.strokeWidth / 2
        canvas.drawCircle(x , y , radius , backGroundPaint)
        drawArc(canvas , x , y , radius)
    }

    private fun drawArc(canvas : Canvas , x : Float , y : Float , radius : Float) {
        val arch = Path()
        oval.set(x - radius , y - radius , x + radius , y + radius)
        setAngle(arch)
        drawText(canvas)
        canvas.drawPath(arch , paint)
        invalidate()
    }

    private fun drawText(canvas : Canvas) {
        if (null == text || text.isEmpty()) return
        var x = 0f
        var y = 0f
        var textW = textPaint.measureText(text)
        val totalW = width
        val totalH = height
        if (textW > totalW) {
            val chars = text.toCharArray()
            var sb = StringBuilder()
            val list = mutableListOf<String>()
            for (i in chars.indices) {
                val s = chars[i]
                sb.append(s)
                if (textPaint.measureText(sb.toString()) > totalW) {
                    sb = sb.deleteCharAt(sb.length - 1)
                    list.add(sb.toString())
                    sb = StringBuilder(s.toString())
                }
            }
            list.add(sb.toString())
            val r = Rect()
            textPaint.getTextBounds(text , 0 , text.length , r)
            val textHeight = r.bottom - r.top
            val totalLines = totalH / textHeight
            (0 until totalLines).forEach { i ->
                y = (height - totalLines * textHeight + textHeight * i + textHeight / 2).toFloat()
                canvas.drawText(list[i] , 0f , y , textPaint)
            }
        } else {
            x = width / 2f - textW / 2
            val r = Rect()
            textPaint.getTextBounds(text , 0 , text.length , r)
            y = height / 2f + (r.bottom - r.top) / 2
            canvas.drawText(text , x , y , textPaint)
        }
    }

    private val minCircleOffset = 20f
    private val maxCircleOffset = 280f
    private fun setAngle(path : Path) : Int {
        val time = drawingTime.toFloat() + 640f
        val currentDuration = time % (animDuration * 9)
        val currentPosition = (currentDuration / animDuration).toInt()
        val rate = currentDuration % animDuration / animDuration
        var startPosition = 0f
        var offset = 0f
        val fixedPosition = 160f * currentPosition % 720f
        val maxStartOffset = 520f//1:2:2:14:1
        var perSize = 1 / 40f
        val timeRateA = perSize * 5
        val timeRateB = perSize * 10
        val timeRateC = perSize * 10
        val timeRateD = perSize * 10
        val timeRateE = perSize * 5

        perSize = 1 / 20f
        val offsetA = perSize
        val offsetB = perSize * 4
        val offsetC = perSize * 2
        val offsetD = perSize * 12
        val offsetE = perSize
        when {
            rate < timeRateA -> {
                val indeedRate = rate / timeRateA
                startPosition = fixedPosition + maxStartOffset * offsetA * indeedRate
                offset = minCircleOffset
            }
            rate < timeRateA + timeRateB -> {
                val iRate = (rate - timeRateA) / timeRateB
                startPosition = fixedPosition + maxStartOffset * (offsetA + offsetB * iRate)
                offset = minCircleOffset + (maxCircleOffset - minCircleOffset) * iRate
            }
            rate < timeRateA + timeRateB + timeRateC -> {
                val iRate = (rate - (timeRateA + timeRateB)) / timeRateC
                startPosition = fixedPosition + maxStartOffset * (offsetA + offsetB + offsetC * iRate)
                offset = maxCircleOffset
            }
            rate > timeRateA + timeRateB + timeRateC + timeRateD -> {
                val iRate = (rate - (timeRateA + timeRateB + timeRateC + timeRateD)) / timeRateE
                startPosition = fixedPosition + maxStartOffset * (offsetA + offsetB + offsetC + offsetD + offsetE * iRate)
                offset = minCircleOffset
            }
            else -> {
                var iRate = (rate - (timeRateA + timeRateB + timeRateC)) / timeRateD
                startPosition = fixedPosition + maxStartOffset * (offsetA + offsetB + offsetC + offsetD * iRate)
                iRate = (timeRateA + timeRateB + timeRateC + timeRateD - rate) / timeRateD
                offset = minCircleOffset + (maxCircleOffset - minCircleOffset) * iRate
            }
        }
        path.addArc(oval , startPosition , offset)
        return currentPosition
    }
}
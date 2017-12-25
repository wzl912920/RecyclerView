package com.lynn.simplerecyclerview.textview

/**
 * Created by Lynn.
 * copied from https://github.com/suragch/AndroidFontMetrics
 */
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View


class FontMetricsView : View {

    private var mText : String? = null
    private var mTextSize : Int = 0
    private var mTextPaint : TextPaint? = null
    private var mLinePaint : Paint? = null
    private var mRectPaint : Paint? = null
    private var mBounds : Rect? = null
    private var mIsTopVisible : Boolean = false
    private var mIsAscentVisible : Boolean = false
    private var mIsBaselineVisible : Boolean = false
    private var mIsDescentVisible : Boolean = false
    private var mIsBottomVisible : Boolean = false
    private var mIsBoundsVisible : Boolean = false
    private var mIsWidthVisible : Boolean = false
    private var mIsUnderLine : Boolean = false
    // getters
    val fontMetrics : Paint.FontMetrics
        get() = mTextPaint!!.fontMetrics
    val textBounds : Rect?
        get() {
            mTextPaint!!.getTextBounds(mText , 0 , mText!!.length , mBounds)
            return mBounds
        }
    val measuredTextWidth : Float
        get() = mTextPaint!!.measureText(mText)


    constructor(context : Context) : super(context) {
        init()
    }


    constructor(context : Context , attrs : AttributeSet) : super(context , attrs) {
        init()
    }

    private fun init() {
        mText = "abcdefghijklmnopqrstuvwxyz"
        mTextSize = DEFAULT_FONT_SIZE_PX
        mTextPaint = TextPaint()
        mTextPaint!!.isAntiAlias = true
        mTextPaint!!.textSize = mTextSize.toFloat()
        mTextPaint!!.color = Color.BLACK

        mLinePaint = Paint()
        mLinePaint!!.color = Color.RED
        mLinePaint!!.strokeWidth = STROKE_WIDTH

        mRectPaint = Paint()
        mRectPaint!!.color = Color.BLACK
        mRectPaint!!.strokeWidth = STROKE_WIDTH
        mRectPaint!!.style = Paint.Style.STROKE

        mBounds = Rect()

        mIsTopVisible = true
        mIsAscentVisible = true
        mIsBaselineVisible = true
        mIsDescentVisible = true
        mIsBottomVisible = true
        mIsBoundsVisible = true
        mIsWidthVisible = true
        mIsUnderLine = true
        mTextPaint?.isUnderlineText = mIsUnderLine
    }

    override fun onDraw(canvas : Canvas) {
        super.onDraw(canvas)

        // center the text baseline vertically
        val verticalAdjustment = this.height / 2
        canvas.translate(0f , verticalAdjustment.toFloat())

        var startX = paddingLeft.toFloat()
        var startY = 0f
        var stopX = this.measuredWidth.toFloat()
        var stopY = 0f

        // draw text
        canvas.drawText(mText!! , startX , startY , mTextPaint!!) // x=0, y=0

        // draw lines
        startX = 0f

        if (mIsTopVisible) {
            startY = mTextPaint!!.fontMetrics.top
            stopY = startY
            mLinePaint!!.color = resources.getColor(android.R.color.holo_blue_bright)
            canvas.drawLine(startX , startY , stopX , stopY , mLinePaint!!)
        }

        if (mIsAscentVisible) {
            startY = mTextPaint!!.fontMetrics.ascent
            stopY = startY
            //mLinePaint.setColor(Color.GREEN);
            mLinePaint!!.color = resources.getColor(android.R.color.holo_blue_dark)
            canvas.drawLine(startX , startY , stopX , stopY , mLinePaint!!)
        }

        if (mIsBaselineVisible) {
            startY = 0f
            stopY = startY
            mLinePaint!!.color = resources.getColor(android.R.color.holo_purple)
            canvas.drawLine(startX , startY , stopX , stopY , mLinePaint!!)
        }

        if (mIsDescentVisible) {
            startY = mTextPaint!!.fontMetrics.descent
            stopY = startY
            //mLinePaint.setColor(Color.BLUE);
            mLinePaint!!.color = resources.getColor(android.R.color.holo_green_light)
            canvas.drawLine(startX , startY , stopX , stopY , mLinePaint!!)
        }

        if (mIsBottomVisible) {
            startY = mTextPaint!!.fontMetrics.bottom
            stopY = startY
            // mLinePaint.setColor(ORANGE);
            mLinePaint!!.color = resources.getColor(android.R.color.holo_orange_light)
            canvas.drawLine(startX , startY , stopX , stopY , mLinePaint!!)
        }

        if (mIsBoundsVisible) {

            mTextPaint!!.getTextBounds(mText , 0 , mText!!.length , mBounds)
            mRectPaint!!.color = resources.getColor(android.R.color.holo_red_dark)
            val dx = paddingLeft.toFloat()
            canvas.drawRect(mBounds!!.left + dx , mBounds!!.top.toFloat() , mBounds!!.right + dx , mBounds!!.bottom.toFloat() , mRectPaint!!)
        }

        if (mIsWidthVisible) {

            mLinePaint!!.color = resources.getColor(android.R.color.holo_red_light)

            // get measured width
            val width = mTextPaint!!.measureText(mText)

            // get bounding width so that we can compare them
            mTextPaint!!.getTextBounds(mText , 0 , mText!!.length , mBounds)

            // draw vertical line just before the left bounds
            startX = paddingLeft + mBounds!!.left - (width - mBounds!!.width()) / 2
            stopX = startX
            startY = (-verticalAdjustment).toFloat()
            stopY = startY + this.height
            canvas.drawLine(startX , startY , stopX , stopY , mLinePaint!!)

            // draw vertical line just after the right bounds
            startX = startX + width
            stopX = startX
            canvas.drawLine(startX , startY , stopX , stopY , mLinePaint!!)
        }
    }

    override fun onMeasure(widthMeasureSpec : Int , heightMeasureSpec : Int) {

        var width = 200
        var height = 200

        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val widthRequirement = View.MeasureSpec.getSize(widthMeasureSpec)
        if (widthMode == View.MeasureSpec.EXACTLY) {
            width = widthRequirement
        } else if (widthMode == View.MeasureSpec.AT_MOST && width > widthRequirement) {
            width = widthRequirement
        }

        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val heightRequirement = View.MeasureSpec.getSize(heightMeasureSpec)
        if (heightMode == View.MeasureSpec.EXACTLY) {
            height = heightRequirement
        } else if (heightMode == View.MeasureSpec.AT_MOST && width > heightRequirement) {
            height = heightRequirement
        }

        setMeasuredDimension(width , height)
    }

    // setters
    fun setText(text : String) {
        mText = text
        invalidate()
        requestLayout()
    }

    fun setTextSizeInPixels(pixels : Int) {
        mTextSize = pixels
        mTextPaint!!.textSize = mTextSize.toFloat()
        invalidate()
        requestLayout()
    }

    fun setTopVisible(isVisible : Boolean) {
        mIsTopVisible = isVisible
        invalidate()
    }

    fun setAscentVisible(isVisible : Boolean) {
        mIsAscentVisible = isVisible
        invalidate()
    }

    fun setBaselineVisible(isVisible : Boolean) {
        mIsBaselineVisible = isVisible
        invalidate()
    }

    fun setDescentVisible(isVisible : Boolean) {
        mIsDescentVisible = isVisible
        invalidate()
    }

    fun setBottomVisible(isVisible : Boolean) {
        mIsBottomVisible = isVisible
        invalidate()
    }

    fun setBoundsVisible(isVisible : Boolean) {
        mIsBoundsVisible = isVisible
        invalidate()
    }

    fun setWidthVisible(isVisible : Boolean) {
        mIsWidthVisible = isVisible
        invalidate()
    }

    fun setUnderline(underLine : Boolean) {
        mIsUnderLine = underLine
        mTextPaint?.isUnderlineText = underLine
        invalidate()
    }

    companion object {

        val DEFAULT_FONT_SIZE_PX = 100
        //private static final int PURPLE = Color.parseColor("#9315db");
        //private static final int ORANGE = Color.parseColor("#ff8a00");
        private val STROKE_WIDTH = 5.0f
    }

}
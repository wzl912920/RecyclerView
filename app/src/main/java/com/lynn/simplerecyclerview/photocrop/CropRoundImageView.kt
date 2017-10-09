package com.lynn.simplerecyclerview.photocrop

import android.content.*
import android.graphics.*
import android.graphics.drawable.*
import android.os.*
import android.util.*
import android.widget.*

import android.view.MotionEvent
import android.view.View
import com.lynn.library.util.*
import java.util.concurrent.locks.*

/**
 * Created by wzl on 2016/5/10.
 */
class CropRoundImageView @JvmOverloads constructor(context : Context , attrs : AttributeSet? = null , defStyleAttr : Int = 0) : android.support.v7.widget.AppCompatImageView(context , attrs , defStyleAttr) , View.OnTouchListener {
    private val bgPaint = Paint()
    private val paint = Paint()
    private val path = Path()

    private var bitmap : Bitmap? = null
    private val rect = RectF()

    private val originalMatrix = Matrix()
    private val savedMatrix = Matrix()
    private val downPoint = PointF()
    private val midPoint = PointF()
    private val minScale = 0.1f
    private var maxScale = Float.MAX_VALUE
    private var oldDistance : Float = 0f

    private val margin : Int = context.dp2px(70f).toInt()
    private val circleLeft = margin / 2
    private val circleWidth = context.screenWidth - margin
    private val circleRadius = circleWidth / 2
    private val circleTop = (context.screenHeight - circleWidth) / 4
    private val circleHeight = circleWidth

    init {
        init()
    }

    private fun init() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE , null)
        }
        val p = layoutParams
        if (null != p) {
            p.width = context.screenWidth
            p.height = context.screenHeight
            layoutParams = p
        }
        bgPaint.color = Color.parseColor("#AAFFFFFF")
        bgPaint.style = Paint.Style.FILL
        bgPaint.isAntiAlias = true

        paint.color = Color.parseColor("#DCDCDC")
        paint.style = Paint.Style.STROKE
        paint.isAntiAlias = true
        paint.strokeWidth = context.dp2px(0.5f)
        setOnTouchListener(this)
    }

    override fun onDraw(canvas : Canvas) {
        path.reset()
        super.onDraw(canvas)
    }

    private val lock = ReentrantLock()
    fun cropImageView(fileName : String , listener : CropImageListener) {
        Thread {
            var filePath = ""
            lock.lock()
            val start = System.currentTimeMillis()
            try {
                if (null == bitmap) {
                    bitmap = (drawable as BitmapDrawable).bitmap
                }
                val rectF = RectF()
                rectF.set(0f , 0f , bitmap!!.width.toFloat() , bitmap!!.height.toFloat())
                val m = Matrix()
                m.set(imageMatrix)
                m.mapRect(rectF)
                val scaledBmp = Bitmap.createScaledBitmap(bitmap , (rectF.width() * scaleX).toInt() , (rectF.height() * scaleY).toInt() , true)
                val bmp = Bitmap.createBitmap(scaledBmp , (circleLeft - rectF.left).toInt() , (circleTop - rectF.top).toInt() , circleWidth , circleHeight)
                val path = context.externalImgDir + "/" + fileName
                bitmapToFile(bmp , path)
                scaledBmp.recycle()
                bmp.recycle()
                filePath = path
            } catch (e : Exception) {
                e.printStackTrace()
            } finally {
                Handler(Looper.getMainLooper()).post { listener?.onSuccess(filePath) }
                lock.unlock()
            }
            val end = System.currentTimeMillis()
            log("耗时${(start - end)}ms")
        }.start()
    }

    override fun draw(canvas : Canvas) {
        val w = this.width / 2
        val h = circleTop + circleRadius
        super.draw(canvas)
        path.reset()
        path.addRect(0f , 0f , width.toFloat() , height.toFloat() , Path.Direction.CW)
        path.addCircle(w.toFloat() , h.toFloat() , (circleWidth / 2).toFloat() , Path.Direction.CW)
        path.fillType = Path.FillType.EVEN_ODD
        canvas.drawPath(path , bgPaint)
        canvas.drawCircle(w.toFloat() , h.toFloat() , (circleWidth / 2).toFloat() , paint)
    }

    override fun onTouch(v : View , event : MotionEvent) : Boolean {
        if (scaleType != ImageView.ScaleType.MATRIX) {
            scaleType = ImageView.ScaleType.MATRIX
        }
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                originalMatrix.set(imageMatrix)
                savedMatrix.set(originalMatrix)
                downPoint.set(event.x , event.y)
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                oldDistance = spacing(event)
                if (oldDistance > 10f) {
                    savedMatrix.set(originalMatrix)
                    midPoint(midPoint , event)
                }
            }
            MotionEvent.ACTION_UP , MotionEvent.ACTION_POINTER_UP -> checkScale()
            MotionEvent.ACTION_MOVE -> {
                val count = event.pointerCount
                if (count == 1) {
                    originalMatrix.set(savedMatrix)
                    originalMatrix.postTranslate(event.x - downPoint.x , event.y - downPoint.y)
                } else {
                    val newDist = spacing(event)
                    val scale = newDist / oldDistance
                    if (newDist > 10f) {
                        originalMatrix.set(savedMatrix)
                        originalMatrix.postScale(scale , scale , midPoint.x , midPoint.y)
                    }
                }
            }
            else -> {
            }
        }
        center()
        imageMatrix = originalMatrix
        return true
    }

    override fun setImageBitmap(bm : Bitmap?) {
        super.setImageBitmap(bm)
        tou(SystemClock.uptimeMillis())
    }

    private fun tou(time : Long) {
        var time = time
        val eventD = MotionEvent.obtain(time , time , MotionEvent.ACTION_DOWN , circleWidth.toFloat() , circleHeight.toFloat() , 0)
        onTouch(this , eventD)
        time += 10
        val eventU = MotionEvent.obtain(time , time , MotionEvent.ACTION_UP , circleWidth.toFloat() , circleHeight.toFloat() , 0)
        onTouch(this , eventU)
        eventD.recycle()
        eventU.recycle()
    }

    private fun center() {
        val m = Matrix()
        m.set(originalMatrix)
        try {
            bitmap = (drawable as BitmapDrawable).bitmap
            if (null == bitmap) {
                return
            }
        } catch (e : Exception) {
            e.printStackTrace()
            return
        }

        rect.set(0f , 0f , bitmap!!.width.toFloat() , bitmap!!.height.toFloat())
        m.mapRect(rect)
        var height = rect.height()
        var width = rect.width()
        var needReInit = false
        if (width / circleWidth >= height / circleHeight) {
            if (height < circleHeight) {
                originalMatrix.postScale(circleHeight / height , circleHeight / height , midPoint.x , midPoint.y)
                needReInit = true
            }
        } else {
            if (width < circleWidth) {
                originalMatrix.postScale(circleWidth / width , circleWidth / width , midPoint.x , midPoint.y)
                needReInit = true
            }
        }
        if (needReInit) {
            m.set(originalMatrix)
            rect.set(0f , 0f , bitmap!!.width.toFloat() , bitmap!!.height.toFloat())
            m.mapRect(rect)
            height = rect.height()
            width = rect.width()
        }

        var deltaX = 0f
        var deltaY = 0f

        when {
            height < circleHeight -> {
                deltaY = circleHeight - height
            }
            rect.top > circleTop -> {
                deltaY = circleTop - rect.top
            }
            rect.bottom < circleTop + circleHeight -> {
                deltaY = circleTop + circleHeight - rect.bottom
            }
        }
        when {
            width < circleWidth -> {
                deltaX = circleWidth - width
            }
            rect.left > circleLeft -> {
                deltaX = circleLeft - rect.left
            }
            rect.right < circleLeft + circleWidth -> {
                deltaX = circleLeft + circleWidth - rect.right
            }
        }
        originalMatrix.postTranslate(deltaX , deltaY)
    }

    private fun midPoint(point : PointF , event : MotionEvent) {
        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)
        point.set(x / 2 , y / 2)
    }


    private fun spacing(event : MotionEvent) : Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return Math.sqrt((x * x + y * y).toDouble()).toFloat()
    }

    private fun checkScale() {
        val p = FloatArray(9)
        originalMatrix.getValues(p)
        if (p[0] < minScale) {
            originalMatrix.setScale(minScale , minScale)
        }
        if (p[0] > maxScale) {
            originalMatrix.set(savedMatrix)
        }
    }

    companion object {
        interface CropImageListener {
            fun onSuccess(path : String)
        }
    }
}


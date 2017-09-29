package com.lynn.simplerecyclerview.colorselector

import android.content.Context
import android.graphics.*
import android.graphics.drawable.*
import android.os.*
import android.support.annotation.RequiresApi
import android.support.v7.graphics.*
import android.util.AttributeSet
import android.view.*
import com.lynn.library.util.*
import java.lang.ref.*
import android.graphics.RectF
import java.util.concurrent.*
import java.util.concurrent.locks.*


/**
 * Created by Lynn.
 */

class ColorPan : View , View.OnTouchListener {
    constructor(context : Context) : super(context) {}

    constructor(context : Context , attrs : AttributeSet?) : super(context , attrs) {}

    constructor(context : Context , attrs : AttributeSet? , defStyleAttr : Int) : super(context , attrs , defStyleAttr) {}

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context : Context , attrs : AttributeSet? , defStyleAttr : Int , defStyleRes : Int) : super(context , attrs , defStyleAttr , defStyleRes) {
    }

    private var paint : Paint = Paint()
    private var type = TYPE.SWEEP
    private val p = Path()
    private var pointPaint = Paint()
    private var point : Point? = null
    private val pointRadius = context.dp2px(5f)

    init {
        setOnTouchListener(this)
        paint.isAntiAlias = true
        pointPaint.color = Color.GRAY
        pointPaint.strokeWidth = context.dp2px(1f)
        pointPaint.style = Paint.Style.STROKE
        pointPaint.isAntiAlias = true
    }

    override fun onTouch(v : View? , event : MotionEvent?) : Boolean {
        if (v != null && event != null) {
            val location = IntArray(2)
            getLocationInWindow(location)
            if (threadLock.isLocked) {
                try {
                    queueLock.lock()
                    queue.poll()
                    queue.put(newThread(location , event))
                } finally {
                    queueLock.unlock()
                }
            } else {
                newThread(location , event).start()
            }
        }
        return true
    }

    private fun newThread(location : IntArray , event : MotionEvent?) : Thread {
        return Thread {
            threadLock.lock()
            try {
                if (null != event) {
                    val back = background
                    var bmp : Bitmap
                    bmp = if (back is BitmapDrawable) {
                        back.bitmap
                    } else {
                        viewToBitmap(this , width.toFloat() , height.toFloat() , true , Bitmap.Config.RGB_565)
                    }
                    Palette.from(bmp).generate(swatchListener)
                    colorPanListener?.let {
                        Handler(Looper.getMainLooper()).post {
                            val finalX = getPos(event.x.toInt() - location[0] , bmp.width)
                            val finalY = getPos(event.y.toInt() - location[1] , bmp.height)
                            if (containsPoint(finalX , finalY)) {
                                colorPanListener?.get()?.onTouchColor(this , bmp.getPixel(finalX , finalY))
                            }
                        }
                    }
                }
            } finally {
                threadLock.unlock()
                try {
                    queueLock.lock()
                    val s = queue.poll()
                    s?.start()
                } finally {
                    queueLock.unlock()
                }
            }
        }
    }

    private val threadLock = ReentrantLock()
    private val queueLock = ReentrantLock()
    private val queue = LinkedBlockingQueue<Thread>(1)

    fun getPos(s : Int , t : Int) : Int {
        return if (s < 0) 0 else if (s >= t) t - 1 else s
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        queue.clear()
    }

    override fun onDraw(canvas : Canvas?) {
        super.onDraw(canvas)
        p.reset()
        val width = width.toFloat()
        val height = height.toFloat()
        val centerX = width / 2
        val centerY = height / 2
        val radius = Math.min(centerX , centerY)
        p.addCircle(centerX , centerY , radius , Path.Direction.CCW)
        paint.shader = when (type) {
            TYPE.SWEEP -> {
                SweepGradient(centerX , centerY , colors , null)
            }
            TYPE.RADIAL -> {
                RadialGradient(centerX , centerY , radius , colors , null , Shader.TileMode.CLAMP)
            }
            else -> {
                LinearGradient(centerX , 0f + (height / 2 - radius) , centerX , height + centerY - radius , colors , null , Shader.TileMode.CLAMP)
            }
        }
        canvas?.drawPath(p , paint)
        point?.let {
            canvas?.drawCircle(point!!.x.toFloat() , point!!.y.toFloat() , pointRadius , pointPaint)
        }
    }

    private fun containsPoint(x : Int , y : Int) : Boolean {
        val bounds = RectF()
        p.computeBounds(bounds , true)
        val region = Region()
        region.setPath(p , Region(bounds.left.toInt() , bounds.top.toInt() , bounds.right.toInt() , bounds.bottom.toInt()))
        val flag = region.contains(x , y)
        if (flag) {
            if (point == null) {
                point = Point()
            }
            point?.set(x , y)
            invalidate()
        }
        return flag
    }

    private var colorPanListener : SoftReference<OnColorTouched>? = null
    fun setListener(ls : OnColorTouched) {
        this.colorPanListener = SoftReference(ls)
    }

    private val list = mutableListOf<Palette.Swatch>()
    private val swatchListener = Palette.PaletteAsyncListener { palette ->
        if (palette != null) {
            val active = palette.vibrantSwatch//有活力的
            val activeD = palette.darkVibrantSwatch//有活力的，暗色
            val activeL = palette.lightVibrantSwatch//有活力的，亮色
            val gental = palette.mutedSwatch//柔和的
            val gentalD = palette.darkMutedSwatch//柔和的，暗色
            val gentalL = palette.lightMutedSwatch//柔和的,亮色
            list.clear()
            active?.let { list.add(active) }
            activeD?.let { list.add(activeD) }
            activeL?.let { list.add(activeL) }
            gental?.let { list.add(gental) }
            gentalD?.let { list.add(gentalD) }
            gentalL?.let { list.add(gentalL) }
            colorPanListener?.get()?.onGetMainColor(list)
        }
    }

    fun setType(s : TYPE) {
        type = s
    }

    companion object {
        enum class TYPE {
            SWEEP ,
            LINEAR ,
            RADIAL
        }

        open interface OnColorTouched {
            fun onTouchColor(view : View , color : Int)
            fun onGetMainColor(list : MutableList<Palette.Swatch>)
        }

        private var colors : IntArray = intArrayOf(Color.parseColor("#000000") , Color.parseColor("#00000F") , Color.parseColor("#0000F0") , Color.parseColor("#0000FF") , Color.parseColor("#000F00") , Color.parseColor("#000F0F") , Color.parseColor("#000FF0") , Color.parseColor("#000FFF") , Color.parseColor("#00F000") , Color.parseColor("#00F00F") , Color.parseColor("#00F0F0") , Color.parseColor("#00F0FF") , Color.parseColor("#00FF00") , Color.parseColor("#00FF0F") , Color.parseColor("#00FFF0") , Color.parseColor("#00FFFF") , Color.parseColor("#0F0000") , Color.parseColor("#0F000F") , Color.parseColor("#0F00F0") , Color.parseColor("#0F00FF") , Color.parseColor("#0F0F00") , Color.parseColor("#0F0F0F") , Color.parseColor("#0F0FF0") , Color.parseColor("#0F0FFF") , Color.parseColor("#0FF000") , Color.parseColor("#0FF00F") , Color.parseColor("#0FF0F0") , Color.parseColor("#0FF0FF") , Color.parseColor("#0FFF00") , Color.parseColor("#0FFF0F") , Color.parseColor("#0FFFF0") , Color.parseColor("#0FFFFF") , Color.parseColor("#F00000") , Color.parseColor("#F0000F") , Color.parseColor("#F000F0") , Color.parseColor("#F000FF") , Color.parseColor("#F00F00") , Color.parseColor("#F00F0F") , Color.parseColor("#F00FF0") , Color.parseColor("#F00FFF") , Color.parseColor("#F0F000") , Color.parseColor("#F0F00F") , Color.parseColor("#F0F0F0") , Color.parseColor("#F0F0FF") , Color.parseColor("#F0FF00") , Color.parseColor("#F0FF0F") , Color.parseColor("#F0FFF0") , Color.parseColor("#F0FFFF") , Color.parseColor("#FF0000") , Color.parseColor("#FF000F") , Color.parseColor("#FF00F0") , Color.parseColor("#FF00FF") , Color.parseColor("#FF0F00") , Color.parseColor("#FF0F0F") , Color.parseColor("#FF0FF0") , Color.parseColor("#FF0FFF") , Color.parseColor("#FFF000") , Color.parseColor("#FFF00F") , Color.parseColor("#FFF0F0") , Color.parseColor("#FFF0FF") , Color.parseColor("#FFFF00") , Color.parseColor("#FFFF0F") , Color.parseColor("#FFFFF0") , Color.parseColor("#FFFFFF"))

        init {
            //Android 常量颜色值
            colors = intArrayOf(Color.BLACK , Color.DKGRAY , Color.GRAY , Color.LTGRAY , Color.WHITE , Color.RED , Color.YELLOW , Color.GREEN , Color.CYAN , Color.BLUE , Color.MAGENTA , Color.BLACK)
            //六大主色
            colors = intArrayOf(Color.RED , Color.YELLOW , Color.GREEN , Color.CYAN , Color.BLUE , Color.MAGENTA , Color.RED)

        }
    }
}

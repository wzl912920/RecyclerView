package com.lynn.simplerecyclerview.drag.justdrag

import android.content.Context
import android.graphics.*
import android.graphics.drawable.*
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v7.widget.*
import android.util.*
import android.view.*
import android.widget.*

/**
 * Created by Lynn.
 */

class TestDragFrameOuter : FrameLayout {
    constructor(context : Context) : super(context) {}

    constructor(context : Context , attrs : AttributeSet?) : super(context , attrs) {}

    constructor(context : Context , attrs : AttributeSet? , defStyleAttr : Int) : super(context , attrs , defStyleAttr) {}

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context : Context , attrs : AttributeSet? , defStyleAttr : Int , defStyleRes : Int) : super(context , attrs , defStyleAttr , defStyleRes) {
    }

    private var floatingView : ImageView? = null
    private var viewTop = 0
    private var downPoint : MotionEvent? = null
    private var currentEvent : MotionEvent? = null
    override fun dispatchDraw(canvas : Canvas?) {
        super.dispatchDraw(canvas)
        if (canvas == null) {
            return
        }
        floatingView?.let {
            val currentY = currentEvent?.rawY ?: 0f
            val downY = downPoint?.rawY ?: 0f
            val status = canvas.save()
            val top = viewTop
            canvas.translate(0f , top + ((currentY - downY)))
            floatingView?.draw(canvas)
            canvas.restoreToCount(status)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        destroyFloatingView()
        destroyPoint()
    }

    fun destroyFloatingView() {
        val drawable = floatingView?.drawable
        drawable?.let {
            if (drawable is BitmapDrawable) {
                drawable.bitmap.recycle()
            }
        }
        floatingView = null
    }

    fun refresh() {
        invalidate()
    }

    fun initStartOffset(ev : MotionEvent , top : Int) {
        destroyPoint()
        downPoint = ev
        viewTop = top
    }

    private fun destroyPoint() {
        downPoint?.let { downPoint?.recycle() }
    }

    fun setCurrentPosition(event : MotionEvent?) {
        currentEvent?.let { currentEvent?.recycle() }
        currentEvent = event
    }

    fun initFloatingView(view : View , minHeight : Int) {
        destroyFloatingView()
        view.isDrawingCacheEnabled = true
        val bmp = Bitmap.createScaledBitmap(view.drawingCache , view.drawingCache.width , minHeight , false)
        view.isDrawingCacheEnabled = false
        val img = AppCompatImageView(context)
        img.setBackgroundColor(Color.RED)
        img.setPadding(0 , 0 , 0 , 0)
        img.setImageBitmap(doBlur(bmp , 20 , false))
        img.layoutParams = ViewGroup.LayoutParams(view.width , view.height)
        img.measure(view.width , minHeight)
        img.layout(0 , 0 , view.width , minHeight)
        floatingView = img
    }

    fun doBlur(sentBitmap : Bitmap , radius : Int , canReuseInBitmap : Boolean) : Bitmap? {
        val bitmap : Bitmap
        if (canReuseInBitmap) {
            bitmap = sentBitmap
        } else {
            bitmap = sentBitmap.copy(sentBitmap.config , true)
        }

        if (radius < 1) {
            return null
        }

        val w = bitmap.width
        val h = bitmap.height

        val pix = IntArray(w * h)
        bitmap.getPixels(pix , 0 , w , 0 , 0 , w , h)

        val wm = w - 1
        val hm = h - 1
        val wh = w * h
        val div = radius + radius + 1

        val r = IntArray(wh)
        val g = IntArray(wh)
        val b = IntArray(wh)
        var rsum : Int
        var gsum : Int
        var bsum : Int
        var x : Int
        var y : Int
        var i : Int
        var p : Int
        var yp : Int
        var yi : Int
        var yw : Int
        val vmin = IntArray(Math.max(w , h))

        var divsum = div + 1 shr 1
        divsum *= divsum
        val dv = IntArray(256 * divsum)
        i = 0
        while (i < 256 * divsum) {
            dv[i] = i / divsum
            i++
        }

        yi = 0
        yw = yi

        val stack = Array(div) { IntArray(3) }
        var stackpointer : Int
        var stackstart : Int
        var sir : IntArray
        var rbs : Int
        val r1 = radius + 1
        var routsum : Int
        var goutsum : Int
        var boutsum : Int
        var rinsum : Int
        var ginsum : Int
        var binsum : Int

        y = 0
        while (y < h) {
            bsum = 0
            gsum = bsum
            rsum = gsum
            boutsum = rsum
            goutsum = boutsum
            routsum = goutsum
            binsum = routsum
            ginsum = binsum
            rinsum = ginsum
            i = -radius
            while (i <= radius) {
                p = pix[yi + Math.min(wm , Math.max(i , 0))]
                sir = stack[i + radius]
                sir[0] = p and 0xff0000 shr 16
                sir[1] = p and 0x00ff00 shr 8
                sir[2] = p and 0x0000ff
                rbs = r1 - Math.abs(i)
                rsum += sir[0] * rbs
                gsum += sir[1] * rbs
                bsum += sir[2] * rbs
                if (i > 0) {
                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]
                } else {
                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]
                }
                i++
            }
            stackpointer = radius

            x = 0
            while (x < w) {

                r[yi] = dv[rsum]
                g[yi] = dv[gsum]
                b[yi] = dv[bsum]

                rsum -= routsum
                gsum -= goutsum
                bsum -= boutsum

                stackstart = stackpointer - radius + div
                sir = stack[stackstart % div]

                routsum -= sir[0]
                goutsum -= sir[1]
                boutsum -= sir[2]

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1 , wm)
                }
                p = pix[yw + vmin[x]]

                sir[0] = p and 0xff0000 shr 16
                sir[1] = p and 0x00ff00 shr 8
                sir[2] = p and 0x0000ff

                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]

                rsum += rinsum
                gsum += ginsum
                bsum += binsum

                stackpointer = (stackpointer + 1) % div
                sir = stack[stackpointer % div]

                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]

                rinsum -= sir[0]
                ginsum -= sir[1]
                binsum -= sir[2]

                yi++
                x++
            }
            yw += w
            y++
        }
        x = 0
        while (x < w) {
            bsum = 0
            gsum = bsum
            rsum = gsum
            boutsum = rsum
            goutsum = boutsum
            routsum = goutsum
            binsum = routsum
            ginsum = binsum
            rinsum = ginsum
            yp = -radius * w
            i = -radius
            while (i <= radius) {
                yi = Math.max(0 , yp) + x

                sir = stack[i + radius]

                sir[0] = r[yi]
                sir[1] = g[yi]
                sir[2] = b[yi]

                rbs = r1 - Math.abs(i)

                rsum += r[yi] * rbs
                gsum += g[yi] * rbs
                bsum += b[yi] * rbs

                if (i > 0) {
                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]
                } else {
                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]
                }

                if (i < hm) {
                    yp += w
                }
                i++
            }
            yi = x
            stackpointer = radius
            y = 0
            while (y < h) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = -0x1000000 and pix[yi] or (dv[rsum] shl 16) or (dv[gsum] shl 8) or dv[bsum]

                rsum -= routsum
                gsum -= goutsum
                bsum -= boutsum

                stackstart = stackpointer - radius + div
                sir = stack[stackstart % div]

                routsum -= sir[0]
                goutsum -= sir[1]
                boutsum -= sir[2]

                if (x == 0) {
                    vmin[y] = Math.min(y + r1 , hm) * w
                }
                p = x + vmin[y]

                sir[0] = r[p]
                sir[1] = g[p]
                sir[2] = b[p]

                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]

                rsum += rinsum
                gsum += ginsum
                bsum += binsum

                stackpointer = (stackpointer + 1) % div
                sir = stack[stackpointer]

                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]

                rinsum -= sir[0]
                ginsum -= sir[1]
                binsum -= sir[2]

                yi += w
                y++
            }
            x++
        }

        bitmap.setPixels(pix , 0 , w , 0 , 0 , w , h)

        return bitmap
    }
}

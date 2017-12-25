package com.lynn.simplerecyclerview.drag

import android.app.*
import android.content.*
import android.graphics.*
import android.os.*
import android.text.*
import android.text.style.*
import com.lynn.library.util.*
import com.lynn.simplerecyclerview.*
import com.lynn.simplerecyclerview.base.*
import com.lynn.simplerecyclerview.drag.customdrag.*
import com.lynn.simplerecyclerview.drag.recyclerview.*
import com.lynn.simplerecyclerview.drag.viewdraghelper.*
import kotlinx.android.synthetic.main.activity_drag.*
import android.text.TextPaint
import android.text.style.UpdateAppearance
import android.text.style.CharacterStyle


/**
 * Created by Lynn.
 */

class DragActivity : BaseActivity() {
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drag)
        view_drag_hp.setOnClickListener { ViewDragHelperActivity.startActivity(this) }
        recycle_view.setOnClickListener { RecyclerDragActivity.startActivity(this) }
        drag.setOnClickListener { TestDragActivity.startActivity(this) }
        val txt = SpannableString(recycle_view.text)
        txt.setSpan(ColoredUnderlineSpan(Color.CYAN) , 1 , txt.length , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        recycle_view.text = txt
    }

    companion object {
        fun startActivity(act : Activity) {
            val i = Intent(act , DragActivity::class.java)
            act.startActivity(i)
        }

        private class ColoredUnderlineSpan(private val mColor : Int) : CharacterStyle() , UpdateAppearance {
            override fun updateDrawState(tp : TextPaint) {
                try {
                    val method = TextPaint::class.java.getMethod("setUnderlineText" , Integer.TYPE , java.lang.Float.TYPE)
                    //baseline偏移，会让文字上下位置产生偏移，主要用于上下标
                    tp.baselineShift += (tp.ascent() / 2).toInt()
                    tp.textSize /= 3
                    method.invoke(tp , mColor , tp.textSize / 10)
                } catch (e : Exception) {
                    tp.isUnderlineText = true
                }
            }
        }

        //全部文本都设置该span会不显示，amazing
        private class UnderLineSpan : ReplacementSpan() {
            private var mWidth : Int = 0

            override fun getSize(paint : Paint , text : CharSequence? , start : Int , end : Int , fm : Paint.FontMetricsInt?) : Int {
                mWidth = paint.measureText(text , start , end).toInt()
                return mWidth
            }

            override fun draw(canvas : Canvas , text : CharSequence? , start : Int , end : Int , x : Float , top : Int , y : Int , bottom : Int , paint : Paint?) {
                paint?.isUnderlineText
                canvas.drawRect(x , bottom.toFloat() , x + mWidth , bottom.toFloat() + 6 , paint)
                canvas.drawText(text , start , end , x , y.toFloat() , paint)
            }
        }
    }
}

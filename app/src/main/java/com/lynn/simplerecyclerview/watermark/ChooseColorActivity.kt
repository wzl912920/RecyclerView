package com.lynn.simplerecyclerview.watermark

import android.app.*
import android.content.*
import android.os.*
import android.support.v7.graphics.*
import android.view.*
import com.lynn.simplerecyclerview.*
import kotlinx.android.synthetic.main.activity_add_water_mark.*


/**
 * Created by Lynn.
 */

class ChooseColorActivity : BaseActivity() , ColorPan.Companion.OnColorTouched {
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_water_mark)
        l.setType(ColorPan.Companion.TYPE.LINEAR)
        r.setType(ColorPan.Companion.TYPE.RADIAL)
        s.setType(ColorPan.Companion.TYPE.SWEEP)
        l.setListener(this)
        r.setListener(this)
        s.setListener(this)
    }

    override fun onTouchColor(view : View , color : Int) {
        when (view) {
            l -> {
                color_l.setBackgroundColor(color)
                color_l.text = "#${Integer.toHexString(color).toUpperCase()}"
            }
            s -> {
                color_s.setBackgroundColor(color)
                color_s.text = "#${Integer.toHexString(color).toUpperCase()}"
            }
            else -> {
                color_r.setBackgroundColor(color)
                color_r.text = "#${Integer.toHexString(color).toUpperCase()}"
            }
        }
    }

    override fun onGetMainColor(list : MutableList<Palette.Swatch>) {
        list.indices.forEach { i ->
            when (i) {
                1 -> {
                    b.setBackgroundColor(list[i].rgb)
                    b.setTextColor(list[i].bodyTextColor)
                }
                2 -> {
                    c.setBackgroundColor(list[i].rgb)
                    c.setTextColor(list[i].bodyTextColor)
                }
                3 -> {
                    d.setBackgroundColor(list[i].rgb)
                    d.setTextColor(list[i].bodyTextColor)
                }
                4 -> {
                    e.setBackgroundColor(list[i].rgb)
                    e.setTextColor(list[i].bodyTextColor)
                }
                5 -> {
                    f.setBackgroundColor(list[i].rgb)
                    f.setTextColor(list[i].bodyTextColor)
                }
                else -> {
                    a.setBackgroundColor(list[i].rgb)
                    a.setTextColor(list[i].bodyTextColor)
                }
            }
        }
    }

    companion object {
        fun startActivity(act : Activity) {
            val i = Intent(act , ChooseColorActivity::class.java)
            act.startActivity(i)
        }
    }
}

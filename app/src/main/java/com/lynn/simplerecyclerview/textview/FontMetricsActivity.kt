package com.lynn.simplerecyclerview.textview

import android.app.*
import android.content.*
import com.lynn.simplerecyclerview.base.BaseActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager;
import com.lynn.simplerecyclerview.*
import kotlinx.android.synthetic.main.activity_font_metrics.*

/**
 * Created by Lynn.
 * copied from https://github.com/suragch/AndroidFontMetrics
 */

internal class FontMetricsActivity : BaseActivity() , View.OnClickListener {

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_font_metrics)

        etTextString.setText("abcdefghijklmnopqrstuvwxyz")
        etFontSize.setText("100")

        findViewById(R.id.updateButton).setOnClickListener(this)

        cbTop.setOnClickListener(this)
        cbAscent.setOnClickListener(this)
        cbBaseline.setOnClickListener(this)
        cbDescent.setOnClickListener(this)
        cbBottom.setOnClickListener(this)
        cbTextBounds.setOnClickListener(this)
        cbWidth.setOnClickListener(this)
        underline.setOnClickListener(this)
        updateTextViews()
    }

    override fun onClick(v : View) {
        when (v.id) {
            R.id.updateButton -> {
                viewWindow.setText(etTextString.text.toString())
                var fontSize : Int = try {
                    Integer.valueOf(etFontSize.text.toString())
                } catch (e : NumberFormatException) {
                    FontMetricsView.DEFAULT_FONT_SIZE_PX
                }

                viewWindow.setTextSizeInPixels(fontSize)
                updateTextViews()
                hideKeyboard(currentFocus)
            }
            R.id.cbTop -> viewWindow.setTopVisible(cbTop.isChecked)
            R.id.cbAscent -> viewWindow.setAscentVisible(cbAscent.isChecked)
            R.id.cbBaseline -> viewWindow.setBaselineVisible(cbBaseline.isChecked)
            R.id.cbDescent -> viewWindow.setDescentVisible(cbDescent.isChecked)
            R.id.cbBottom -> viewWindow.setBottomVisible(cbBottom.isChecked)
            R.id.cbTextBounds -> viewWindow.setBoundsVisible(cbTextBounds.isChecked)
            R.id.cbWidth -> viewWindow.setWidthVisible(cbWidth.isChecked)
            R.id.underline -> {
                viewWindow.setUnderline(underline.isChecked)
                underlineText.text = underline.isChecked.toString()
            }
        }
    }

    private fun updateTextViews() {
        tvTop.text = viewWindow.fontMetrics.top.toString()
        tvAscent.text = viewWindow.fontMetrics.ascent.toString()
        tvBaseline.text = 0f.toString()
        tvDescent.text = viewWindow.fontMetrics.descent.toString()
        tvBottom.text = viewWindow.fontMetrics.bottom.toString()
        tvTextBounds.text = "w = " + (viewWindow.textBounds!!.width().toString() +
                "     h = " + viewWindow.textBounds!!.height().toString())
        tvWidth.text = viewWindow.measuredTextWidth.toString()
        tvLeadingValue.text = viewWindow.fontMetrics.leading.toString()
        underlineText.text = underline.isChecked.toString()
    }

    private fun hideKeyboard(view : View?) {
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view!!.windowToken , 0)
        }
    }

    companion object {
        fun startActivity(act : Activity) {
            val i = Intent(act , FontMetricsActivity::class.java)
            act.startActivity(i)
        }
    }
}

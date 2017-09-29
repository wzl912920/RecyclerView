package com.lynn.simplerecyclerview.common

import android.content.Context
import android.graphics.Typeface
import android.support.v7.widget.AppCompatEditText
import android.text.Html
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.util.AttributeSet

import org.apache.commons.lang.StringEscapeUtils

/**
 * Created by Lynn.
 */

class StyledEditText : AppCompatEditText {
    constructor(context : Context) : super(context) {}

    constructor(context : Context , attrs : AttributeSet) : super(context , attrs) {}

    constructor(context : Context , attrs : AttributeSet , defStyleAttr : Int) : super(context , attrs , defStyleAttr) {}

    fun onBoldClick() {
        val selectionStart = selectionStart
        val selectionEnd = selectionEnd
        val text = text
        if (selectionStart in 0..(selectionEnd - 1) && selectionEnd <= text.length) {
            val spans = text.getSpans(selectionStart , selectionEnd , StyleSpan::class.java)
            if (null != spans && spans.isNotEmpty()) {
                val ss = text.getSpans(selectionStart , selectionStart + 1 , StyleSpan::class.java)
                var isBoldExists = false
                if (null != ss && ss.isNotEmpty()) {
                    ss.indices
                            .filter { ss[it].style == Typeface.BOLD }
                            .forEach { isBoldExists = true }
                }
                for (i in spans.indices) {
                    val span = spans[i]
                    if (span.style == Typeface.BOLD) {
                        val spanStart = text.getSpanStart(span)
                        val spanEnd = text.getSpanEnd(span)
                        if (spanStart < selectionStart) {
                            text.setSpan(StyleSpan(span.style) , spanStart , selectionStart , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                        if (spanEnd > selectionEnd) {
                            text.setSpan(StyleSpan(span.style) , selectionEnd , spanEnd , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                        text.removeSpan(span)
                    }
                }
                if (!isBoldExists) {
                    text.setSpan(StyleSpan(Typeface.BOLD) , selectionStart , selectionEnd , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            } else {
                text.setSpan(StyleSpan(Typeface.BOLD) , selectionStart , selectionEnd , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }

    fun onItalicClick() {
        val selectionStart = selectionStart
        val selectionEnd = selectionEnd
        val text = text
        if (selectionStart in 0..(selectionEnd - 1) && selectionEnd <= text.length) {
            val spans = text.getSpans(selectionStart , selectionEnd , StyleSpan::class.java)
            if (null != spans && spans.isNotEmpty()) {
                val ss = text.getSpans(selectionStart , selectionStart + 1 , StyleSpan::class.java)
                var isItalicExists = false
                if (null != ss && ss.isNotEmpty()) {
                    ss.indices
                            .filter { ss[it].style == Typeface.ITALIC }
                            .forEach { isItalicExists = true }
                }
                for (i in spans.indices) {
                    val span = spans[i]
                    if (span.style == Typeface.ITALIC) {
                        val spanStart = text.getSpanStart(span)
                        val spanEnd = text.getSpanEnd(span)
                        if (spanStart < selectionStart) {
                            text.setSpan(StyleSpan(span.style) , spanStart , selectionStart , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                        if (spanEnd > selectionEnd) {
                            text.setSpan(StyleSpan(span.style) , selectionEnd , spanEnd , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                        text.removeSpan(span)
                    }
                }
                if (!isItalicExists) {
                    text.setSpan(StyleSpan(Typeface.ITALIC) , selectionStart , selectionEnd , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            } else {
                text.setSpan(StyleSpan(Typeface.ITALIC) , selectionStart , selectionEnd , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }

    fun onStrikeClick() {
        val selectionStart = selectionStart
        val selectionEnd = selectionEnd
        val text = text
        if (selectionStart in 0..(selectionEnd - 1) && selectionEnd <= text.length) {
            val spans = text.getSpans(selectionStart , selectionEnd , StrikethroughSpan::class.java)
            if (null != spans && spans.isNotEmpty()) {
                val ss = text.getSpans(selectionStart , selectionStart + 1 , StrikethroughSpan::class.java)
                var isItalicExists = false
                if (null != ss && ss.isNotEmpty()) {
                    isItalicExists = true
                }
                for (i in spans.indices) {
                    val span = spans[i]
                    val spanStart = text.getSpanStart(span)
                    val spanEnd = text.getSpanEnd(span)
                    if (spanStart < selectionStart) {
                        text.setSpan(StrikethroughSpan() , spanStart , selectionStart , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    if (spanEnd > selectionEnd) {
                        text.setSpan(StrikethroughSpan() , selectionEnd , spanEnd , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    text.removeSpan(span)
                }
                if (!isItalicExists) {
                    text.setSpan(StrikethroughSpan() , selectionStart , selectionEnd , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            } else {
                text.setSpan(StrikethroughSpan() , selectionStart , selectionEnd , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }

    fun onNormalClick() {
        val selectionStart = selectionStart
        val selectionEnd = selectionEnd
        val text = text
        if (selectionStart in 0..(selectionEnd - 1) && selectionEnd <= text.length) {
            val spans = text.getSpans(selectionStart , selectionEnd , StyleSpan::class.java)
            if (null != spans && spans.isNotEmpty()) {
                for (i in spans.indices) {
                    val span = spans[i]
                    val spanStart = text.getSpanStart(span)
                    val spanEnd = text.getSpanEnd(span)
                    if (spanStart < selectionStart) {
                        text.setSpan(StyleSpan(span.style) , spanStart , selectionStart , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    if (spanEnd > selectionEnd) {
                        text.setSpan(StyleSpan(span.style) , selectionEnd , spanEnd , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    text.removeSpan(span)
                }
            }
            val sspans = text.getSpans(selectionStart , selectionEnd , StrikethroughSpan::class.java)
            if (null != sspans && sspans.isNotEmpty()) {
                for (i in sspans.indices) {
                    val span = sspans[i]
                    val spanStart = text.getSpanStart(span)
                    val spanEnd = text.getSpanEnd(span)
                    if (spanStart < selectionStart) {
                        text.setSpan(StrikethroughSpan() , spanStart , selectionStart , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    if (spanEnd > selectionEnd) {
                        text.setSpan(StrikethroughSpan() , selectionEnd , spanEnd , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    text.removeSpan(span)
                }
            }
        }
    }

    fun toHtml() : String? {
        val editable = text
        return StringEscapeUtils.unescapeHtml(Html.toHtml(editable))
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isEnabled = false
        isEnabled = true
    }
}

package com.lynn.simplerecyclerview;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * Created by Lynn.
 */

public class StyledEditText extends AppCompatEditText {
    public StyledEditText(Context context) {
        super(context);
    }

    public StyledEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StyledEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void onBoldClick() {
        int selectionStart = getSelectionStart();
        int selectionEnd = getSelectionEnd();
        Editable text = getText();
        if (selectionStart < selectionEnd && selectionStart >= 0 && selectionEnd <= text.length()) {
            StyleSpan[] spans = text.getSpans(selectionStart, selectionEnd, StyleSpan.class);
            if (null != spans && spans.length > 0) {
                StyleSpan[] ss = text.getSpans(selectionStart, selectionStart + 1, StyleSpan.class);
                boolean isBoldExists = false;
                if (null != ss && ss.length > 0) {
                    for (int i = 0; i < ss.length; i++) {
                        if (ss[i].getStyle() == Typeface.BOLD) {
                            isBoldExists = true;
                        }
                    }
                }
                for (int i = 0; i < spans.length; i++) {
                    StyleSpan span = spans[i];
                    if (span.getStyle() == Typeface.BOLD) {
                        int spanStart = text.getSpanStart(span);
                        int spanEnd = text.getSpanEnd(span);
                        if (spanStart < selectionStart) {
                            text.setSpan(new StyleSpan(span.getStyle()), spanStart, selectionStart, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        if (spanEnd > selectionEnd) {
                            text.setSpan(new StyleSpan(span.getStyle()), selectionEnd, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        text.removeSpan(span);
                    }
                }
                if (!isBoldExists) {
                    text.setSpan(new StyleSpan(Typeface.BOLD), selectionStart, selectionEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            } else {
                text.setSpan(new StyleSpan(Typeface.BOLD), selectionStart, selectionEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    public void onItalicClick() {
        int selectionStart = getSelectionStart();
        int selectionEnd = getSelectionEnd();
        Editable text = getText();
        if (selectionStart < selectionEnd && selectionStart >= 0 && selectionEnd <= text.length()) {
            StyleSpan[] spans = text.getSpans(selectionStart, selectionEnd, StyleSpan.class);
            if (null != spans && spans.length > 0) {
                StyleSpan[] ss = text.getSpans(selectionStart, selectionStart + 1, StyleSpan.class);
                boolean isItalicExists = false;
                if (null != ss && ss.length > 0) {
                    for (int i = 0; i < ss.length; i++) {
                        if (ss[i].getStyle() == Typeface.ITALIC) {
                            isItalicExists = true;
                        }
                    }
                }
                for (int i = 0; i < spans.length; i++) {
                    StyleSpan span = spans[i];
                    if (span.getStyle() == Typeface.ITALIC) {
                        int spanStart = text.getSpanStart(span);
                        int spanEnd = text.getSpanEnd(span);
                        if (spanStart < selectionStart) {
                            text.setSpan(new StyleSpan(span.getStyle()), spanStart, selectionStart, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        if (spanEnd > selectionEnd) {
                            text.setSpan(new StyleSpan(span.getStyle()), selectionEnd, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        text.removeSpan(span);
                    }
                }
                if (!isItalicExists) {
                    text.setSpan(new StyleSpan(Typeface.ITALIC), selectionStart, selectionEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            } else {
                text.setSpan(new StyleSpan(Typeface.ITALIC), selectionStart, selectionEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    public void onStrikeClick() {
        int selectionStart = getSelectionStart();
        int selectionEnd = getSelectionEnd();
        Editable text = getText();
        if (selectionStart < selectionEnd && selectionStart >= 0 && selectionEnd <= text.length()) {
            StrikethroughSpan[] spans = text.getSpans(selectionStart, selectionEnd, StrikethroughSpan.class);
            if (null != spans && spans.length > 0) {
                StrikethroughSpan[] ss = text.getSpans(selectionStart, selectionStart + 1, StrikethroughSpan.class);
                boolean isItalicExists = false;
                if (null != ss && ss.length > 0) {
                    isItalicExists = true;
                }
                for (int i = 0; i < spans.length; i++) {
                    StrikethroughSpan span = spans[i];
                    int spanStart = text.getSpanStart(span);
                    int spanEnd = text.getSpanEnd(span);
                    if (spanStart < selectionStart) {
                        text.setSpan(new StrikethroughSpan(), spanStart, selectionStart, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    if (spanEnd > selectionEnd) {
                        text.setSpan(new StrikethroughSpan(), selectionEnd, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    text.removeSpan(span);
                }
                if (!isItalicExists) {
                    text.setSpan(new StrikethroughSpan(), selectionStart, selectionEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            } else {
                text.setSpan(new StrikethroughSpan(), selectionStart, selectionEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    public void onNormalClick() {
        int selectionStart = getSelectionStart();
        int selectionEnd = getSelectionEnd();
        Editable text = getText();
        if (selectionStart < selectionEnd && selectionStart >= 0 && selectionEnd <= text.length()) {
            StyleSpan[] spans = text.getSpans(selectionStart, selectionEnd, StyleSpan.class);
            if (null != spans && spans.length > 0) {
                for (int i = 0; i < spans.length; i++) {
                    StyleSpan span = spans[i];
                    int spanStart = text.getSpanStart(span);
                    int spanEnd = text.getSpanEnd(span);
                    if (spanStart < selectionStart) {
                        text.setSpan(new StyleSpan(span.getStyle()), spanStart, selectionStart, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    if (spanEnd > selectionEnd) {
                        text.setSpan(new StyleSpan(span.getStyle()), selectionEnd, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    text.removeSpan(span);
                }
            }
            StrikethroughSpan[] sspans = text.getSpans(selectionStart, selectionEnd, StrikethroughSpan.class);
            if (null != sspans && sspans.length > 0) {
                for (int i = 0; i < sspans.length; i++) {
                    StrikethroughSpan span = sspans[i];
                    int spanStart = text.getSpanStart(span);
                    int spanEnd = text.getSpanEnd(span);
                    if (spanStart < selectionStart) {
                        text.setSpan(new StrikethroughSpan(), spanStart, selectionStart, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    if (spanEnd > selectionEnd) {
                        text.setSpan(new StrikethroughSpan(), selectionEnd, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    text.removeSpan(span);
                }
            }
        }
    }

    public String toHtml() {
        Editable editable = getText();
        return StringEscapeUtils.unescapeHtml(Html.toHtml(editable));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setEnabled(false);
        setEnabled(true);
    }
}

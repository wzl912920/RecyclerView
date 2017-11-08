package com.lynn.simplerecyclerview.drag

import android.content.Context
import android.graphics.*
import android.graphics.drawable.*
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.*
import android.util.AttributeSet
import android.view.*
import android.widget.*

/**
 * Created by Lynn.
 */

class TestDragScrollView : NestedScrollView {

    constructor(context : Context) : super(context) {}

    constructor(context : Context , attrs : AttributeSet) : super(context , attrs) {}

    constructor(context : Context , attrs : AttributeSet , defStyleAttr : Int) : super(context , attrs , defStyleAttr) {}
}

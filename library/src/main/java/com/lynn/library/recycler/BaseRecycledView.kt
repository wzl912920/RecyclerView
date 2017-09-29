package com.lynn.library.recycler

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet

/**
 * Created by Lynn.
 */

class BaseRecycledView @JvmOverloads constructor(context : Context , attrs : AttributeSet? = null , defStyle : Int = 0) : RecyclerView(context , attrs , defStyle) {
    private val adapter : BaseRecycledAdapter = BaseRecycledAdapter()

    init {
        setAdapter(adapter)
        layoutManager = LinearLayoutManager(context)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        adapter.release()
    }
}

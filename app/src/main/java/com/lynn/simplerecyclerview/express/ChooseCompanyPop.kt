package com.lynn.simplerecyclerview.express

import android.app.Activity
import android.content.*
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.support.v7.widget.*
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.*
import com.lynn.library.recycler.*
import com.lynn.simplerecyclerview.*
import kotlinx.android.extensions.*
import kotlinx.android.synthetic.main.widget_pop_list_item.*
import java.lang.ref.*

/**
 * Created by Lynn.
 */

class ChooseCompanyPop(context : Context) {

    private val popupWindow : PopupWindow?
    val isShowing : Boolean
        get() = popupWindow?.isShowing ?: false
    private lateinit var adapter : BaseRecycledAdapter

    init {
        val root = LayoutInflater.from(context).inflate(R.layout.widget_popup_window , null , false)
        initViews(root)
        popupWindow = PopupWindow(root , LinearLayout.LayoutParams.WRAP_CONTENT , LinearLayout.LayoutParams.WRAP_CONTENT)
        popupWindow.setBackgroundDrawable(BitmapDrawable())
        popupWindow.isOutsideTouchable = true
//        popupWindow.setTouchInterceptor { v , event ->
//            event.action == MotionEvent.ACTION_OUTSIDE
//        }
    }

    fun setCallBack(list : MutableList<String> , listener : ItemClickEvent) {
        this.sr = SoftReference(listener)
        adapter.list.addAll(list)
        adapter.registerGlobalClickEvent(listener)
        adapter.notifyDataSetChanged()
    }

    private var sr : SoftReference<ItemClickEvent?> = SoftReference(null)

    private fun initViews(view : View) {
        val rv = view.findViewById<RecyclerView>(R.id.recycle_view)
        adapter = rv.adapter as BaseRecycledAdapter
        adapter.register(R.layout.widget_pop_list_item , Item::class.java)
        sr?.get()?.let { adapter.registerGlobalClickEvent(sr?.get()!!) }
    }

    fun show(view : View) {
        if (null == popupWindow || popupWindow.isShowing) return
        if (Build.VERSION.SDK_INT == 24) {
            val a = IntArray(2)
            view.getLocationInWindow(a)
            popupWindow.showAtLocation((view.context as Activity).window.decorView , Gravity.NO_GRAVITY , 0 , a[1])
            popupWindow.update()
        } else {
            popupWindow.showAsDropDown(view , 0 , 0)
        }
    }

    fun dismiss() {
        if (null == popupWindow || !popupWindow.isShowing) return
        popupWindow.dismiss()
    }

    companion object {
        private class Item(containerView : View) : BaseViewHolder<String>(containerView) {
            override fun bind(data : String) {
                text_view.text = data
            }
        }
    }
}

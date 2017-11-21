package com.moretech.coterie.utils

import android.app.Dialog
import android.content.*
import android.graphics.*
import android.os.*
import android.support.v7.widget.*
import android.view.*
import android.widget.*
import com.lynn.library.recycler.*
import com.lynn.library.util.*
import com.lynn.simplerecyclerview.*
import com.lynn.simplerecyclerview.base.*
import kotlinx.android.synthetic.main.widget_menu_dialog.*
import kotlinx.android.synthetic.main.widget_menu_dialog_list_item.*
import kotlinx.android.synthetic.main.widget_menu_dialog_list_item.view.*


/**
 * Created by Lynn.
 */

class MenuDialog : Dialog {
    private lateinit var adapter : BaseRecycledAdapter

    constructor(context : Context) : super(context) {
        init()
    }

    private fun init() {
        val v = LayoutInflater.from(context).inflate(R.layout.widget_menu_dialog , null , false)
        setContentView(v)
        recycle_view.layoutManager = LinearLayoutManager(context)
        adapter = recycle_view.adapter as BaseRecycledAdapter
        adapter.register(R.layout.widget_menu_dialog_list_item , StringItemHolder::class.java)
        adapter.registerGlobalClickEvent(object : ItemClickEvent {
            override fun onItemClick(view : View , position : Int) {
                listener?.onItemClick(position)
            }
        })
        recycle_view.addItemDecoration(Decoration())
    }

    fun setData(list : MutableList<String> , listener : IOnItemClickListener?) {
        if (list?.size > 0) {
            adapter.list.clear()
            adapter.list.addAll(list)
            adapter.notifyDataSetChanged()
        }
        this.listener = listener
    }

    private var listener : IOnItemClickListener? = null

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        window?.setLayout((context.screenWidth - context.dp2px(20f)).toInt() , WindowManager.LayoutParams.WRAP_CONTENT)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL)
        window?.setWindowAnimations(R.style.DialogFullScreen)
    }

    companion object {
        class StringItemHolder(containerView : View) : BaseViewHolder<String>(containerView) {
            override fun bind(data : String) {
                text.text = data
                if (data.contains(itemView.context.resources.getString(R.string.delete)) || data.contains(itemView.context.resources.getString(R.string.cancel))) {
                    text.setTextColor(itemView.context.resources.getColor(R.color.color_FF3049))
                } else {
                    text.setTextColor(itemView.context.resources.getColor(R.color.color_333333))
                }
            }
        }

        private class Decoration : RecyclerView.ItemDecoration() {
            private val paint : Paint = Paint()
            val topOffset = BaseApplication.instance.dp2px(0.5f)

            init {
                paint.color = BaseApplication.instance.resources.getColor(R.color.color_DCDCDC)
            }

            override fun onDrawOver(canvas : Canvas? , parent : RecyclerView? , state : RecyclerView.State?) {
                if (paint == null || parent == null || null == canvas) return
                val index = parent.childCount - 2
                val child = parent.getChildAt(index)
                val left = 0f
                val right = child.right.toFloat()
                val top = (child.bottom - topOffset).toFloat()
                val bottom = child.bottom.toFloat()
                canvas.drawRect(left , top , right , bottom , paint)
            }
        }

        interface IOnItemClickListener {
            fun onItemClick(position : Int)
        }
    }

}

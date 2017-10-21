package com.moretech.coterie.utils

import android.app.Dialog
import android.content.*
import android.graphics.*
import android.os.*
import android.view.*
import android.support.v7.widget.RecyclerView
import android.widget.*
import com.lynn.library.recycler.*
import com.lynn.library.util.*
import com.lynn.simplerecyclerview.*
import com.lynn.simplerecyclerview.base.*
import kotlinx.android.synthetic.main.widget_menu_dialog.*


/**
 * Created by Lynn.
 */

class MenuDialog : Dialog {
    private lateinit var adapter : BaseRecycledAdapter

    constructor(context : Context) : super(context , R.style.DialogFullScreen) {
        init()
    }

    private fun init() {
        val v = LayoutInflater.from(context).inflate(R.layout.widget_menu_dialog , null , false)
        setContentView(v)
        adapter = recycle_view.adapter as BaseRecycledAdapter
        adapter.register(R.layout.widget_menu_dialog_list_item , ItemHolder::class.java)
        adapter.registerGlobalClickEvent(object : ItemClickEvent {
            override fun onItemClick(view : View , potision : Int) {
                listener?.onItemClick(potision)
            }
        })
        recycle_view.addItemDecoration(Decoration())
    }

    fun setData(list : MutableList<String>) {
        if (list.size > 0) {
            adapter.list.clear()
            adapter.list.addAll(list)
            adapter.notifyDataSetChanged()
        }
    }

    private var listener : IOnItemClickListener? = null
    fun setOnItemClickListener(listener : IOnItemClickListener) {
        this.listener = listener
    }

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT , context.screenHeight - context.statusBarHeight)
        window.setWindowAnimations(R.style.DialogFullScreen)
    }

    companion object {
        private class ItemHolder(itemView : View) : BaseViewHolder<String>(itemView) {
            private val textView = itemView.findViewById<TextView>(R.id.text)
            override fun bind(data : String) {
                textView.text = data
            }
        }

        private class Decoration : RecyclerView.ItemDecoration() {
            private val paint : Paint = Paint()

            init {
                paint.color = BaseApplication.instance!!.resources.getColor(R.color.color_DCDCDC)
            }

            override fun onDrawOver(canvas : Canvas? , parent : RecyclerView? , state : RecyclerView.State?) {
                if (parent == null || null == canvas) return
                val offset = BaseApplication.instance!!.dp2px(24f)
                val topOffset = BaseApplication.instance!!.dp2px(0.5f)
                val childCount = parent.childCount
                for (i in 0 until childCount) {
                    val child = parent.getChildAt(i)
                    val left = (child.left + offset).toFloat()
                    val right = (child.right - offset).toFloat()
                    val top = (child.bottom - topOffset).toFloat()
                    val bottom = child.bottom.toFloat()
                    canvas.drawRect(left , top , right , bottom , paint)
                }
            }
        }

        interface IOnItemClickListener {
            fun onItemClick(position : Int)
        }
    }

}

package com.lynn.recyclerdemo

import android.graphics.*
import android.os.*
import android.view.*

import com.lynn.recyclerdemo.base.*
import com.lynn.library.recycler.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_demo.*


class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initAdapter()
    }

    private fun initAdapter() {
        val adapter = BaseRecyclerAdapter()
        recycler_view.adapter = adapter
        adapter.register(R.layout.layout_demo , Demo1::class.java)

        adapter.register(Demo2::class.java)

        adapter.multiRegister(object : MultiTyper<Int> {
            override fun getLayoutId(data : Int) : Int {
                return R.layout.layout_demo
            }

            override fun getViewHolder(data : Int) : Class<out BaseViewHolder<Int>> {
                if (data == 0) {
                    return DemoMultiOne::class.java
                }
                return DemoMultiTwo::class.java
            }
        })
        initData(adapter)
    }

    private fun initData(adapter : BaseRecyclerAdapter) {
        adapter.list.add("0")
        adapter.list.add(false)
        adapter.list.add(0)
        adapter.list.add("1")
        adapter.list.add(true)
        adapter.list.add(1)
        adapter.notifyDataSetChanged()
    }

    companion object {
        class Demo1(containerView : View) : BaseViewHolder<String>(containerView) {
            override fun bind(data : String) {
                demo_text.setTextColor(Color.BLUE)
                demo_text.text = data
            }
        }

        @LayoutId(R.layout.layout_demo)
        class Demo2(containerView : View) : BaseViewHolder<Boolean>(containerView) {
            override fun bind(data : Boolean) {
                demo_text.setTextColor(Color.BLACK)
                demo_text.text = data.toString()
            }
        }

        class DemoMultiOne(containerView : View) : BaseViewHolder<Int>(containerView) {
            override fun bind(data : Int) {
                demo_text.setTextColor(Color.RED)
                demo_text.text = data.toString()
            }
        }

        class DemoMultiTwo(containerView : View) : BaseViewHolder<Int>(containerView) {
            override fun bind(data : Int) {
                demo_text.setTextColor(Color.CYAN)
                demo_text.text = data.toString()
            }
        }
    }
}

package com.lynn.simplerecyclerview

import android.*
import android.app.*
import android.graphics.*
import android.net.*
import android.os.*
import android.support.v7.widget.*
import android.widget.TextView

import kotlinx.android.synthetic.main.activity_main.*
import android.view.*
import com.facebook.drawee.view.*
import com.lynn.library.recycler.*
import com.lynn.library.util.*
import com.lynn.simplerecyclerview.base.*
import com.lynn.simplerecyclerview.colorselector.*

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        simpleRecyclerDemo()
    }

    private fun simpleRecyclerDemo() {
        val adapter : BaseRecycledAdapter = recycle_view.adapter as BaseRecycledAdapter
        adapter.register(R.layout.layout_test_type_normal , HolderNormal::class.java)
        adapter.multiRegister(object : MultiTyper<DataImg> {
            override fun getLayoutId(data : DataImg) : Int {
                return R.layout.layout_test_type_img
            }

            override fun getViewHolder(data : DataImg) : Class<out BaseViewHolder<DataImg>> {
                return HolderImg::class.java
            }
        })
        val n = DataNormal()
        adapter.list.add(n)
        var x = DataImg("http://img.juimg.com/tuku/yulantu/120926/219049-12092612154377.jpg")
        adapter.list.add(x)
        x = DataImg("http://img1.juimg.com/170409/330818-1F40Z9160774.jpg")
        adapter.list.add(x)
        x = DataImg("http://img1.juimg.com/170630/355861-1F63012563242.jpg")
        adapter.list.add(x)
        x = DataImg("http://img1.juimg.com/170715/330800-1FG50P12715.jpg")
        adapter.list.add(x)
        x = DataImg("http://img1.juimg.com/170715/330800-1FG509312761.jpg")
        adapter.list.add(x)
        x = DataImg("http://img1.juimg.com/170802/330854-1FP2154R385.jpg")
        adapter.list.add(x)
        test()
        askPermission(*permissions)
        showSuccess("Thank you😊!!!")
    }

    val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE ,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)

    fun test() {
        val fruits = listOf("banana" , "avocado" , "apple" , "kiwi")
        with(fruits) {
            filter { it.startsWith("a") }
            sortedBy { it }
            map { it.toUpperCase() }
            forEach { println(it) }
        }
    }

    companion object {
        class DataNormal

        class DataImg(url : String) {
            var url : String = url
        }

        class HolderNormal(itemView : View) : BaseViewHolder<DataNormal>(itemView) {
            private var tv : TextView

            init {
                val lp = itemView.layoutParams
                lp.height = (itemView.context.screenHeight - itemView.context.statusBarHeight) / 3
                tv = itemView.findViewById<TextView>(R.id.text_view)
            }

            override fun bind(data : DataNormal) {
                tv.text = "111111111111111111111"
            }
        }


        class HolderImg(itemView : View) : BaseViewHolder<DataImg>(itemView) {
            private var img : SimpleDraweeView
            private var txt : TextView
            private var data : DataImg? = null

            init {
                val lp = itemView.layoutParams
                lp.height = (itemView.context.screenHeight - itemView.context.statusBarHeight) / 3
                img = itemView.findViewById<SimpleDraweeView>(R.id.image_view)
                txt = itemView.findViewById<TextView>(R.id.text_view)
                itemView.setOnClickListener { onItemClick() }
            }

            private fun onItemClick() {
                when (adapterPosition) {
                    0 -> {
                        ChooseColorActivity.startActivity(itemView.context as Activity)
                    }
                    1 -> {
                    }
                    2 -> {
                    }
                    3 -> {
                    }
                    4 -> {
                    }
                    5 -> {
                    }
                    else -> {
                    }
                }
            }

            override fun bind(data : DataImg) {
                this.data = data
                img.setImageURI(Uri.parse(data?.url))
                txt.text = ""
                when (adapterPosition) {
                    0 -> {
                        txt.text = "颜色选择器"
                    }
                    1 -> {
                    }
                    2 -> {
                    }
                    3 -> {
                    }
                    4 -> {
                    }
                    5 -> {
                    }
                    else -> {
                    }
                }
            }

        }
    }
}

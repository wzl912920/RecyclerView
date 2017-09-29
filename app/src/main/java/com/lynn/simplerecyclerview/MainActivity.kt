package com.lynn.simplerecyclerview

import android.*
import android.app.*
import android.graphics.*
import android.net.*
import android.os.*
import android.support.v7.widget.*
import android.widget.TextView

import kotlinx.android.synthetic.main.activity_main.*
import android.text.Spannable
import android.text.style.StrikethroughSpan
import android.text.Editable
import android.text.Html
import android.view.*
import org.xml.sax.XMLReader
import android.support.v7.widget.LinearLayoutManager
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
        recycle_view.addItemDecoration(TopLargeDecoration())
        recycle_view.addOnScrollListener(TopScrollListener())
        adapter.register(R.layout.layout_test_type_normal , HolderNormal::class.java)
        adapter.register(R.layout.layout_test_type_img , HolderImg::class.java)
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

        class MyTagHandler : Html.TagHandler {
            //Html.fromHtml("G友咯<i><strike>loo</strike></i><strike>k</strike><strike><b>哦</b></strike><strike>咯</strike>空" , null , MyTagHandler())
            /**
             * 参数：
             * opening：为true时表示某个标签开始解析,为false时表示该标签解析完
             * tag:当前解析的标签
             * output:文本中的内容
             * xmlReader:xml解析器
             */
            override fun handleTag(opening : Boolean , tag : String , output : Editable , xmlReader : XMLReader) {
//                Log.e("TAG-->" , tag)
//                Log.e("output-->" , output.toString())
                if (tag.equals("strike" , ignoreCase = true)) {//自定义解析<strike></strike>标签
                    val len = output.length
//                    Log.e("opening-->" , opening.toString() + "")
                    if (opening) {//开始解析该标签，打一个标记
                        output.setSpan(StrikethroughSpan() , len , len , Spannable.SPAN_MARK_MARK)
                    } else {//解析结束，读出所有标记，取最后一个标记为当前解析的标签的标记（因为解析方式是便读便解析）
                        val spans = output.getSpans(0 , len , StrikethroughSpan::class.java)
                        if (spans.size > 0) {
                            for (i in spans.indices.reversed()) {
                                if (output.getSpanFlags(spans[i]) == Spannable.SPAN_MARK_MARK) {
                                    val start = output.getSpanStart(spans[i])
                                    output.removeSpan(spans[i])
                                    if (start != len) {
                                        output.setSpan(StrikethroughSpan() , start , len , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                                    }
                                    break
                                }
                            }
                        }
                    }
                } else {//其他标签不再处理
//                    Log.e("TAG-->" , tag + "--不做处理")
                }
            }
        }

        class TopLargeDecoration : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect : Rect? , view : View? , parent : RecyclerView? , state : RecyclerView.State?) {
                super.getItemOffsets(outRect , view , parent , state)
                if (null == outRect || null == parent || null == state) return
                val currentPosition = parent.getChildLayoutPosition(view)
                val lm : LinearLayoutManager = parent.layoutManager as LinearLayoutManager
                val first = lm.findFirstVisibleItemPosition()
                val view = lm.findViewByPosition(first + 1)
                val max = (parent.context.screenHeight - parent.context.statusBarHeight).toFloat() / 3
                var top = max
                if (null != view) {
                    top = view.top.toFloat()
                }
                var rate : Float = top / max
                if (currentPosition == first + 1) {
                    outRect.bottom = -(max / 2 * rate).toInt()
                } else if (currentPosition > first + 1) {
                    outRect.bottom = (-max / 2).toInt()
                }
            }
        }

        class TopScrollListener : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView : RecyclerView? , dx : Int , dy : Int) {
                super.onScrolled(recyclerView , dx , dy)
                val layoutManager = recyclerView?.layoutManager as LinearLayoutManager
                val firstPosition = layoutManager.findFirstVisibleItemPosition()
                val lastPosition = layoutManager.findLastVisibleItemPosition()
                val visibleCount = lastPosition - firstPosition
                for (i in firstPosition - 1..firstPosition + visibleCount + 1) {
                    val view = layoutManager.findViewByPosition(i)
                    if (view != null) {
                        val translationY = view.translationY
                        if (i > firstPosition && translationY != 0f) {
                            view.translationY = 0f
                        }
                    }
                }
                val firstView = layoutManager.findViewByPosition(firstPosition)
                if (null != firstView) {
                    val firstViewTop = firstView.top
                    firstView.translationY = -firstViewTop / 2.0f
                }
                recyclerView?.apply {
                    post { invalidateItemDecorations() }
                }
            }
        }

        class CenterLargeDecoration : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect : Rect? , view : View? , parent : RecyclerView? , state : RecyclerView.State?) {
                super.getItemOffsets(outRect , view , parent , state)
                if (null == outRect || null == parent || null == state) return
                val currentPosition = parent.getChildLayoutPosition(view)
                val lm : LinearLayoutManager = parent.layoutManager as LinearLayoutManager
                val first = lm.findFirstVisibleItemPosition()
                val view = lm.findViewByPosition(first + 2)
                val max = (parent.context.screenHeight - parent.context.statusBarHeight).toFloat() / 3
                var top = max
                if (null != view) {
                    top = view.top.toFloat()
                }
                var rate : Float = top / max
                if (currentPosition < first + 2) {
                    outRect.bottom = (-max / 2).toInt()
                } else if (currentPosition > first + 3) {
                    outRect.bottom = (-max / 2).toInt()
                } else if (currentPosition == first + 2) {
                    outRect.bottom = ((rate - 1) * max).toInt()
                } else if (currentPosition == first + 3) {
                    outRect.bottom = (max / 2 - rate * max).toInt()
                }
            }
        }

        class CenterScrollListener : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView : RecyclerView? , dx : Int , dy : Int) {
                super.onScrolled(recyclerView , dx , dy)
                recyclerView?.apply {
                    post { invalidateItemDecorations() }
                }
            }
        }
    }
}

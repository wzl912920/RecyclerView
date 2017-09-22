package com.lynn.simplerecyclerview

import android.*
import android.content.*
import android.graphics.*
import android.os.*
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.*
import android.widget.TextView
import android.widget.ImageView

import kotlinx.android.synthetic.main.activity_main.*
import android.text.Spannable
import android.text.style.StrikethroughSpan
import android.text.Editable
import android.text.Html
import android.view.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import org.xml.sax.XMLReader
import android.support.v7.widget.LinearLayoutManager
import com.lynn.library.permission.*
import com.lynn.library.recycler.*

class MainActivity : PermissionsActivity() , BinderTools {
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        simpleRecyclerDemo()
    }

    private fun simpleRecyclerDemo() {
        val adapter : BaseRecycledAdapter<Any> = recycle_view.adapter as BaseRecycledAdapter<Any>
        recycle_view.addItemDecoration(TopLargeDecoration())

        recycle_view.addOnScrollListener(TopScrollListener())
        adapter.register(TYPE_NORMAL , R.layout.layout_test_type_normal)
        adapter.register(TYPE_IMG , R.layout.layout_test_type_img)
        adapter.setBinder(this)
        for (i in 0..90) {
            var x = DataImg("http://img.juimg.com/tuku/yulantu/120926/219049-12092612154377.jpg")
            adapter.add(x)
            x = DataImg("http://img1.juimg.com/170409/330818-1F40Z9160774.jpg")
            adapter.add(x)
            x = DataImg("http://img1.juimg.com/170630/355861-1F63012563242.jpg")
            adapter.add(x)
            x = DataImg("http://img1.juimg.com/170715/330800-1FG50P12715.jpg")
            adapter.add(x)
            x = DataImg("http://img1.juimg.com/170715/330800-1FG509312761.jpg")
            adapter.add(x)
            x = DataImg("http://img1.juimg.com/170802/330854-1FP2154R385.jpg")
            adapter.add(x)
        }
        adapter.notifyDataSetChanged()

        test()

        val map = mapOf("a" to 2 , "b" to 3)
        for ((a , b) in map) {
            print(a + "====" + b.toString())
        }

        haha@ for (i in 1..10) {
            continue@haha
            break@haha
        }
        askPermission(Manifest.permission.BIND_ACCESSIBILITY_SERVICE , Manifest.permission.READ_EXTERNAL_STORAGE , Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    fun test() {
        val fruits = listOf("banana" , "avocado" , "apple" , "kiwi")
        with(fruits) {
            filter { it.startsWith("a") }
            sortedBy { it }
            map { it.toUpperCase() }
            forEach { println(it) }
        }
    }

    override fun getHolder(view : View , type : Int) : BaseViewHolder<*> {
        return if (type == TYPE_IMG) HolderImg(view) else HolderNormal(view)
    }

    override fun <T : Any?> getType(t : T) : Int {
        return if (t is DataImg) TYPE_IMG else TYPE_NORMAL
    }

    companion object {
        val TYPE_NORMAL : Int = 0
        val TYPE_IMG : Int = 3
        val maxHeight = ((Utils.getScreenHeight() - Utils.getStatusBarHeight()) / 3).toFloat()


        class DataNormal

        class DataImg(url : String) {
            var url : String = ""

            init {
                this.url = url
            }
        }

        class HolderNormal(itemView : View) : BaseViewHolder<DataNormal>(itemView) {
            var tv : TextView? = null

            init {
                val lp = itemView.layoutParams
                lp.height = maxHeight.toInt()
                tv = itemView.findViewById(R.id.text_view) as TextView?
            }

            override fun bind(data : DataNormal?) {
                tv?.text = "111111111111111111111"
            }
        }


        class HolderImg(itemView : View) : BaseViewHolder<DataImg>(itemView) {
            var img : ImageView?

            init {
                val lp = itemView.layoutParams
                lp.height = maxHeight.toInt()
                img = itemView.findViewById(R.id.image_view) as ImageView
            }

            override fun bind(data : DataImg?) {
                Glide.with(itemView?.context?.applicationContext).load(data?.url).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).override(Target.SIZE_ORIGINAL , Target.SIZE_ORIGINAL).into(object : SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL , Target.SIZE_ORIGINAL) {
                    override fun onResourceReady(resource : Bitmap , glideAnimation : GlideAnimation<in Bitmap>) {
                        img?.setImageBitmap(resource)
                    }
                })
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
                val max = maxHeight
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
                val max = maxHeight.toFloat()
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

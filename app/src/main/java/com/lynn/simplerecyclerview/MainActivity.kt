package com.lynn.simplerecyclerview

import android.graphics.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.*
import com.lynn.library.*

import kotlinx.android.synthetic.main.activity_main.*
import android.text.Spannable
import android.text.style.StrikethroughSpan
import android.text.Editable
import android.text.Html
import com.bumptech.glide.*
import com.bumptech.glide.load.engine.*
import com.bumptech.glide.request.animation.*
import com.bumptech.glide.request.target.*
import com.bumptech.glide.request.target.Target
import org.xml.sax.*


class MainActivity : AppCompatActivity() , BinderTools {
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        simpleRecyclerDemo()
    }

    private fun simpleRecyclerDemo() {
        val adapter : BaseRecycledAdapter<Object> = recycle_view.adapter as BaseRecycledAdapter<Object>
        adapter.register(TYPE_NORMAL , R.layout.layout_test_type_normal)
        adapter.register(TYPE_A , R.layout.layout_test_type_a)
        adapter.register(TYPE_TEXT , R.layout.layout_test_type_text)
        adapter.register(TYPE_IMG , R.layout.layout_test_type_img)
        adapter.setBinder(this)
        for (i in 0..90) {
            var x = DataNormal() as Object
            adapter.add(x)
            x = DataA() as Object
            adapter.add(x)
            x = DataText() as Object
            adapter.add(x)
            x = DataImg("http://img.juimg.com/tuku/yulantu/120926/219049-12092612154377.jpg") as Object
            adapter.add(x)
        }
        adapter.notifyDataSetChanged()
    }

    override fun getHolder(view : View , type : Int) : BaseViewHolder<*> {
        return if (type == TYPE_A) HolderA(view) else if (type == TYPE_TEXT) HolderText(view) else if (type == TYPE_IMG) HolderImg(view) else HolderNormal(view)
    }

    override fun <T : Any?> getType(t : T) : Int {
        return if (t is DataA) TYPE_A else if (t is DataText) TYPE_TEXT else if (t is DataImg) TYPE_IMG else TYPE_NORMAL
    }

    companion object {
        val TYPE_NORMAL : Int = 0
        val TYPE_A : Int = 1
        val TYPE_TEXT : Int = 2
        val TYPE_IMG : Int = 3

        class DataNormal
        class DataText
        class DataA {
            var strA : String = "abcd"
            var strB : Int = 10
        }

        class DataImg(url : String) {
            var url : String = ""

            init {
                this.url = url
            }
        }

        class HolderNormal(itemView : View) : BaseViewHolder<DataNormal>(itemView) {
            var tv : TextView? = null

            init {
                tv = itemView.findViewById(R.id.text_view) as TextView?
            }

            override fun bind(data : DataNormal?) {
                tv?.text = "111111111111111111111"
            }
        }

        class HolderA(itemView : View) : BaseViewHolder<DataA>(itemView) {
            var tv : TextView? = null
            var tvb : TextView? = null

            init {
                tv = itemView.findViewById(R.id.text_view) as TextView?
                tvb = itemView.findViewById(R.id.text_view_b) as TextView?
            }

            override fun bind(data : DataA?) {
                tv?.text = data?.strA
                tvb?.text = data?.strB.toString()
            }
        }

        class HolderText(itemView : View) : BaseViewHolder<DataText>(itemView) {
            private var text : StyledEditText? = null

            init {
                text = itemView.findViewById(R.id.text) as StyledEditText
                itemView.findViewById(R.id.bold).setOnClickListener { onBoldClick() }
                itemView.findViewById(R.id.italic).setOnClickListener { onItalicClick() }
                itemView.findViewById(R.id.strike).setOnClickListener { onStrikeClick() }
                itemView.findViewById(R.id.normal).setOnClickListener { onNormalClick() }
            }

            override fun bind(data : DataText?) {
                text?.setText(Html.fromHtml("G友咯<i><strike>loo</strike></i><strike>k</strike><strike><b>哦</b></strike><strike>咯</strike>空" , null , MyTagHandler()))
            }

            fun onBoldClick() {
                text?.onBoldClick()
//                Log.e("AAA" , text?.toHtml())
            }

            fun onItalicClick() {
                text?.onItalicClick()
//                Log.e("AAA" , text?.toHtml())
            }

            fun onStrikeClick() {
                text?.onStrikeClick()
//                Log.e("AAA" , text?.toHtml())
            }

            fun onNormalClick() {
                text?.onNormalClick()
//                Log.e("AAA" , text?.toHtml())
            }

        }

        class HolderImg(itemView : View) : BaseViewHolder<DataImg>(itemView) {
            var img : ImageView? = null

            init {
                img = itemView.findViewById(R.id.image_view) as ImageView
            }

            override fun bind(data : DataImg?) {
                Glide.with(itemView?.context?.applicationContext).load(data?.url).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).override(Target.SIZE_ORIGINAL , Target.SIZE_ORIGINAL).into(object : SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL , Target.SIZE_ORIGINAL) {
                    override fun onResourceReady(resource : Bitmap , glideAnimation : GlideAnimation<in Bitmap>) {
                        img?.setImageBitmap(resource)
                    }
                })
                //        var url = "https://ss2.baidu.com/6ONYsjip0QIZ8tyhnq/it/u=1702098461,2395962392&fm=58&s=52763C728AB05A820B7D86C40200F0A1"
                //        Glide.with(this).load(url).bitmapTransform(BlurTransformation(this , 20)).into(image_view)
            }

        }

        class MyTagHandler : Html.TagHandler {

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
    }
}

package com.lynn.simplerecyclerview

import android.*
import android.app.*
import android.net.*
import android.os.*
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
        //多类型注册方式
        adapter.multiRegister(object : MultiTyper<DataNormal> {
            override fun getLayoutId(data : DataNormal) : Int {
                return R.layout.layout_test_type_normal
            }

            override fun getViewHolder(data : DataNormal) : Class<out BaseViewHolder<DataNormal>> {
                if (data.type == 1) {
                    return NormalHolderA::class.java
                }
                return NormalHolderB::class.java
            }

        })
        //唯一类型注册方式
        adapter.register(R.layout.layout_test_type_img , HolderImg::class.java)
        //HolderImg自己单独处理点击事件
        adapter.registerGlobalClickEvent(object : ItemClickEvent {
            override fun onItemClick(view : View , potision : Int) {
                if (view.id == R.id.text_view) {
                    showWarning("✅✅✅✅")
                } else {
                    showError("xxxxxxxx")
                }
            }
        } , R.id.text_view)
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
        var n = DataNormal()
        adapter.list.add(n)
        n = DataNormal()
        adapter.list.add(n)
        n = DataNormal()
        adapter.list.add(n)
        n = DataNormal()
        n.type = 0
        adapter.list.add(n)
        test()
        //权限申请示例,回回调到onPermissionGranted
        askPermission(*permissions)
        showSuccess("Thank you for syncing😊!!!")
    }

    //如果该值返回true，申请权限被拒绝时，会回调到onPermissionDenied
    override fun dealSelf() : Boolean {
        return super.dealSelf()
    }

    override fun onPermissionGranted(type : Int) {
    }

    override fun onPermissionDenied(type : Int , permissions : MutableList<String>) {
    }

    val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE ,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)

    fun test() {
    }

    companion object {
        class DataNormal {
            var type = 1
        }

        class DataImg(url : String) {
            var url : String = url
        }

        class NormalHolderA(itemView : View) : BaseViewHolder<DataNormal>(itemView) {
            private var tv : TextView = itemView.findViewById(R.id.text_view)
            override fun bind(data : DataNormal) {
                tv.text = "AAAAAAAAAAAAAAAAAAAA"
            }
        }

        class NormalHolderB(itemView : View) : BaseViewHolder<DataNormal>(itemView) {
            private var tv : TextView

            init {
                val lp = itemView.layoutParams
                lp.height = (itemView.context.screenHeight - itemView.context.statusBarHeight) / 3
                tv = itemView.findViewById<TextView>(R.id.text_view)
            }

            override fun bind(data : DataNormal) {
                tv.text = "BBBBBBBBBBB"
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

            override fun overrideGlobalClickEvent() : Boolean {
                return true
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

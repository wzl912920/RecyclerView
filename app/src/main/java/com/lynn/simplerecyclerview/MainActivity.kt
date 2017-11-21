package com.lynn.simplerecyclerview

import android.*
import android.app.*
import android.content.*
import android.net.*
import android.os.*

import kotlinx.android.synthetic.main.activity_main.*
import android.view.*
import com.lynn.library.recycler.*
import com.lynn.library.util.*
import com.lynn.simplerecyclerview.base.*
import com.lynn.simplerecyclerview.colorselector.*
import com.lynn.simplerecyclerview.drag.*
import com.lynn.simplerecyclerview.express.*
import com.lynn.simplerecyclerview.loading.*
import com.lynn.simplerecyclerview.photocrop.*
import com.lynn.simplerecyclerview.serviceExample.*
import com.lynn.simplerecyclerview.theme.*
import kotlinx.android.synthetic.main.layout_test_type_img.*
import java.io.Serializable

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fab.setOnClickListener {
            binder?.getService()
            recycle_view.adapter.notifyItemChanged(1 , Temp("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1508726298&di=416d8ad060c01fabe5f1696b5cd42e04&imgtype=jpg&er=1&src=http%3A%2F%2Fd.hiphotos.baidu.com%2Fzhidao%2Fpic%2Fitem%2F72f082025aafa40fe871b36bad64034f79f019d4.jpg" , 6))
        }
        loading.setOnClickListener {
            LoadingActivity.startActivity(this)
        }

        simpleRecyclerDemo()
        bindService()
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
        //权限申请示例,回回调到onPermissionGranted
        askPermission(*permissions)
        showSuccess("Thank you for syncing😊!!!")
        test()
    }

    val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE ,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)

    private var binder : TestService.Companion.MyBinder? = null
    private var conn : ServiceConnection? = null
    private fun bindService() {
        val i = Intent(this , TestService::class.java)
        conn = object : ServiceConnection {
            override fun onServiceDisconnected(name : ComponentName?) {
                binder = null
            }

            override fun onServiceConnected(name : ComponentName? , service : IBinder?) {
                binder = service as TestService.Companion.MyBinder
            }
        }
        bindService(i , conn , Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        conn?.apply { unbindService(conn) }
    }

    //如果该值返回true，申请权限被拒绝时，会回调到onPermissionDenied
    override fun dealSelf() : Boolean {
        return super.dealSelf()
    }

    override fun onPermissionGranted(type : Int) {
    }

    override fun onPermissionDenied(type : Int , permissions : MutableList<String>) {
    }

    fun test() {
    }

    companion object {
        class ResponseData : Serializable {

        }

        class DataNormal {
            var type = 1
        }

        class Temp(s : String , t : Int = 2) {
            val img = s
            val s = TempObj(s)
        }

        class TempObj(str : String) {
            val s = str
        }

        class DataImg(url : String) {
            var url : String = url
        }

        class NormalHolderA(containerView : View) : BaseViewHolder<DataNormal>(containerView) {
            override fun bind(data : DataNormal) {
                text_view.text = "AAAAAAAAAAAAAAAAAAAA"
            }
        }

        class NormalHolderB(containerView : View) : BaseViewHolder<DataNormal>(containerView) {
            init {
                val lp = itemView.layoutParams
                lp.height = (itemView.context.screenHeight - itemView.context.statusBarHeight) / 3
            }

            override fun bind(data : DataNormal) {
                text_view.text = "BBBBBBBBBBB"
            }
        }


        class HolderImg(containerView : View) : BaseViewHolder<DataImg>(containerView) {
            init {
                val lp = itemView.layoutParams
                lp.height = (itemView.context.screenHeight - itemView.context.statusBarHeight) / 3
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
                        ChoosePhotoActivity.startActivity(itemView.context as Activity)
                    }
                    2 -> {
                        DragActivity.startActivity(itemView.context as Activity)
                    }
                    3 -> {
                        ThemeTestActivity.startActivity(itemView.context as Activity)
                    }
                    4 -> {
                        ExpressActivity.startActivity(itemView.context as Activity)
                    }
                    5 -> {
                    }
                    else -> {
                    }
                }
            }

            override fun bind(data : DataImg) {
                image_view.setImageURI(Uri.parse(data?.url))
                text_view.text = ""
                when (adapterPosition) {
                    0 -> {
                        val ctx = itemView.context
                        val c : CharSequence = ctx.resources.getText(R.string.color_picker)
                        text_view.setText(c)
                    }
                    1 -> {
                        text_view.text = "图片裁剪demo"
                    }
                    2 -> {
                        text_view.text = "Drag Demo"
                    }
                    3 -> {
                        text_view.text = "主题换色"
                    }
                    4 -> {
                        text_view.text = "快递查询"
                    }
                    5 -> {
                    }
                    else -> {
                    }
                }
            }

            //局部刷新
            override fun bind(data : DataImg , payLoads : MutableList<Any>) {
                val t = payLoads[0] as Temp
                image_view.setImageURI(Uri.parse(t.img))
                text_view.text = t.s.s
            }
        }
    }
}

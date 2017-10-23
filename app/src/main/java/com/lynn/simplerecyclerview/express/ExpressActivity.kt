package com.lynn.simplerecyclerview.express

import android.app.*
import android.content.*
import android.os.Bundle
import android.view.*
import com.lynn.library.net.*
import com.lynn.library.recycler.*
import com.lynn.library.util.*
import com.lynn.simplerecyclerview.*

import com.lynn.simplerecyclerview.base.BaseActivity
import kotlinx.android.synthetic.main.activity_express.*
import kotlinx.android.synthetic.main.layout_express_list_item.*
import java.io.*

/**
 * Created by Lynn.
 */

class ExpressActivity : BaseActivity() {
    private val names = arrayListOf("申通" , "EMS" , "顺丰" , "圆通" , "中通" , "韵达" , "天天" , "汇通" , "全峰" , "德邦" , "宅急送")
    private val values = arrayListOf("shentong" , "ems" , "shunfeng" , "yuantong" , "zhongtong" , "yunda" , "tiantian" , "huitongkuaidi" , "quanfengkuaidi" , "debangwuliu" , "zhaijisong")
    private lateinit var pop : ChooseCompanyPop
    private var typeString = values[1]
    private var postId = "1107933494942"
    private lateinit var adapter : BaseRecycledAdapter
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_express)
        adapter = recycle_view.adapter as BaseRecycledAdapter
        adapter.register(R.layout.layout_express_list_item , ItemHolder::class.java)
        pop = ChooseCompanyPop(this)
        order_num.setText(postId)
        order_num.setSelection(order_num.length())
        type.text = names[1]
        pop.setCallBack(names , object : ItemClickEvent {
            override fun onItemClick(view : View , potision : Int) {
                pop.dismiss()
                type.text = names[potision]
                typeString = values[potision]
            }
        })
        type.setOnClickListener { pop.show(type) }
        search.setOnClickListener {
            postId = order_num.text.toString()
            HttpUtils.getInstance().get(object : BaseRequest<ExpressActivity , Response>(this) {
                init {
                    addParam("type" , typeString)
                    addParam("postid" , postId)
                }

                override fun onError(k : Response? , msg : String? , httpCode : Int) {
                    k?.message.let { showError(k?.message!!) }
                }

                override fun getUrl() : String {
                    return "http://www.kuaidi100.com/query"
                }

                override fun onSuccess(k : Response? , response : String? , httpCode : Int) {
                    if (null == k?.data) {
                        k?.message.let {
                            showError(k?.message!!)
                        }
                    } else {
                        k?.data?.let {
                            adapter.list.clear()
                            adapter.list.addAll(k?.data!!)
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
            })
        }
    }

    companion object {
        fun startActivity(act : Activity) {
            val i = Intent(act , ExpressActivity::class.java)
            act.startActivity(i)
        }

        private class ItemHolder(view : View) : BaseViewHolder<Item>(view) {
            override fun bind(data : Item) {
                time.text = data?.ftime
                location.text = data?.location
                desc.text = data?.context
            }
        }

        class Item {
            var time : String? = null//2017-10-14 12:46:30" ,
            var ftime : String? = null//2017-10-14 12:46:30" ,
            var context : String? = null//[青岛市]投递并签收，签收人：他人收 快递柜" ,
            var location : String? = null//青岛市"
        }

        class Response : Serializable {
            var message : String? = null//:String? = null//ok",
            var nu : String? = null//1107933494942",
            var ischeck : String? = null//1",
            var condition : String? = null//F00",
            var com : String? = null//ems",
            var status : String? = null//200",
            var state : String? = null//3",
            var data : MutableList<Item>? = null
        }
    }
}

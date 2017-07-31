package com.lynn.simplerecyclerview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.*
import com.lynn.simplerecyclerview.base.*

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() , BinderTools {
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val adapter : BaseRecycledAdapter<Object> = recycle_view.adapter as BaseRecycledAdapter<Object>
        adapter.register(TYPE_NORMAL , R.layout.layout_test)
        adapter.register(TYPE_A , R.layout.layout_test_b)
        adapter.setBinder(this)
        for (i in 0..90) {
            var x = "xxxx" as Object
            adapter.add(x)
            x = A() as Object
            adapter.add(x)
        }
        adapter.notifyDataSetChanged()
    }

    class A {
        var strA : String = "abcd"
        var strB : Int = 10
    }

    override fun getHolder(view : View , type : Int) : BaseViewHolder<*> {
        return if (type == TYPE_A) HolderA(view) else Holder(view)
    }

    override fun <T : Any?> getType(t : T) : Int {
        return if (t is A) return TYPE_A else return TYPE_NORMAL
    }

    companion object {
        val TYPE_NORMAL : Int = 0
        val TYPE_A : Int = 1

        class Holder(itemView : View) : BaseViewHolder<String>(itemView) {
            var tv : TextView? = null

            init {
                tv = itemView.findViewById(R.id.text_view) as TextView?
            }

            override fun bind(data : String?) {
                tv?.text = data
            }
        }

        class HolderA(itemView : View) : BaseViewHolder<A>(itemView) {
            var tv : TextView? = null
            var tvb : TextView? = null

            init {
                tv = itemView.findViewById(R.id.text_view) as TextView?
                tvb = itemView.findViewById(R.id.text_view_b) as TextView?
            }

            override fun bind(data : A?) {
                tv?.text = data?.strA
                tvb?.text = data?.strB.toString()
            }
        }
    }
}

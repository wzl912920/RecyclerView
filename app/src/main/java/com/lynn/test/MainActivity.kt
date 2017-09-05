package com.lynn.test

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.lynn.library.BaseRecycledAdapter
import com.lynn.library.BaseViewHolder
import com.lynn.library.BinderTools
import com.lynn.simplerecyclerview.*

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() , BinderTools {
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val adapter : BaseRecycledAdapter<Any> = recycle_view.adapter as BaseRecycledAdapter<Any>
        adapter.register(TYPE_A , R.layout.layout_test_recycler)
        adapter.register(TYPE_B , R.layout.layout_test_recycler)
        adapter.setBinder(this)
        for (i in 0..100) {
            adapter.add(A())
            adapter.add(B())
        }
        adapter.notifyDataSetChanged()
    }

    override fun <T : Any?> getType(t : T) : Int {
        return if (t is A) TYPE_A else TYPE_B
    }

    override fun getHolder(view : View , type : Int) : BaseViewHolder<*> {
        when (type) {
            TYPE_A -> {
                return HolderA(view)
            }
            else -> {
                return HolderB(view)
            }
        }
    }

    companion object {
        val TYPE_A : Int = 0
        val TYPE_B : Int = 1

        class A {
            val str : String = "hahahahaha"
        }

        class B {
            val str : String = "biubiubiubiubiu"
        }

        class HolderA(view : View) : BaseViewHolder<A>(view) , View.OnClickListener {
            override fun onClick(v : View?) {
                assert(v != null)
                Toast.makeText(v?.context , "哈哈哈哈哈哈" , Toast.LENGTH_SHORT).show()
            }

            var tv : TextView? = null

            init {
                view.setOnClickListener(this)
                tv = view.findViewById(R.id.text_view) as TextView?
            }

            override fun bind(data : A?) {
                tv?.text = data?.str
            }
        }

        class HolderB(view : View) : BaseViewHolder<B>(view) {
            var tv : TextView? = null

            init {
                tv = view.findViewById(R.id.text_view) as TextView?
            }

            override fun bind(data : B?) {
                tv?.text = data?.str
            }
        }
    }
}

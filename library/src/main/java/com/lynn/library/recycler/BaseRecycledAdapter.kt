package com.lynn.library.recycler

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.*

import java.util.ArrayList

/**
 * Created by Lynn.
 */

class BaseRecycledAdapter : RecyclerView.Adapter<BaseViewHolder<Any>>() {
    val list = ArrayList<Any>()
    private var tools : Binder = Binder()

    override fun onCreateViewHolder(parent : ViewGroup , viewType : Int) : BaseViewHolder<Any> {
        val view = LayoutInflater.from(parent.context).inflate(tools.getLayoutId(viewType) , parent , false)
        val clazz = tools.getHolderClass(viewType)
        val constructor = clazz.getConstructor(View::class.java)
        return constructor.newInstance(view) as BaseViewHolder<Any>
    }

    fun release() {
        tools.release()
    }

    fun register(@LayoutRes layoutId : Int , clazz : Class<out BaseViewHolder<out Any>>) {
        tools.register(layoutId , clazz)
    }

    fun multiRegister(typer : MultiTyper<out Any>) {
        tools.multiRegister(typer)
    }

    override fun getItemViewType(position : Int) : Int {
        return tools.getDataType(list[position])
    }

    override fun onBindViewHolder(holder : BaseViewHolder<Any> , position : Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() : Int {
        return list.size
    }
}

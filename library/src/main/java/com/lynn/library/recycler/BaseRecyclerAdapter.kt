package com.lynn.library.recycler

import android.support.annotation.*
import android.support.v7.util.*
import android.support.v7.widget.RecyclerView
import android.view.*

import java.util.ArrayList

/**
 * Created by Lynn.
 */

class BaseRecyclerAdapter : RecyclerView.Adapter<BaseViewHolder<Any>>() {
    val list = ArrayList<Any>()
    private var tools : Binder = Binder()

    fun register(@LayoutRes layoutId : Int , clazz : Class<out BaseViewHolder<out Any>>) {
        tools.register(layoutId , clazz)
    }

    fun register(clazz : Class<out BaseViewHolder<out Any>>) {
        tools.register(clazz)
    }

    fun multiRegister(typer : MultiTyper<out Any>) {
        tools.multiRegister(typer)
    }

    fun attachTo(rv : RecyclerView) {
        rv.adapter = this
    }

    fun refresh(diffUtil : DiffUtil.Callback) {
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        diffResult.dispatchUpdatesTo(this)
    }

    fun refresh(data : MutableList<Any>) {
        refresh(DiffCallback(data , list))
    }

    fun registerGlobalClickEvent(event : ItemClickEvent , @IdRes vararg viewIds : Int) {
        tools.registerClickEvent(event , *viewIds)
    }

    fun registerGlobalLongClickEvent(event : ItemLongClickEvent , @IdRes vararg viewIds : Int) {
        tools.registerLongClickEvent(event , *viewIds)
    }

    override fun getItemViewType(position : Int) : Int {
        return tools.getDataType(list[position])
    }

    override fun getItemCount() : Int {
        return list.size
    }

    override fun onCreateViewHolder(parent : ViewGroup , viewType : Int) : BaseViewHolder<Any> {
        val view = LayoutInflater.from(parent.context).inflate(tools.getLayoutId(viewType) , parent , false)
        val constructor = tools.getConstructor(viewType)
        val holder = constructor.newInstance(view) as BaseViewHolder<Any>
        holder.bindClickEvent(tools.getClickEvent() , tools.getClickEventIds())
        holder.bindLongClickEvent(tools.getLongClickEvent() , tools.getLongClickEventIds())
        return holder
    }

    override fun onBindViewHolder(holder : BaseViewHolder<Any> , position : Int) {
        holder.bind(list[position])
    }

    override fun onBindViewHolder(holder : BaseViewHolder<Any> , position : Int , payloads : MutableList<Any>?) {
        if (payloads?.isEmpty() != true) {
            holder.bindData(list[position] , payloads!!)
        } else {
            onBindViewHolder(holder , position)
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView : RecyclerView?) {
        super.onDetachedFromRecyclerView(recyclerView)
        tools.clear()
    }
}

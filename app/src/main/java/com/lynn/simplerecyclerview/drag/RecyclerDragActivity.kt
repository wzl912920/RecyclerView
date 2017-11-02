package com.lynn.simplerecyclerview.drag

import android.animation.*
import android.app.*
import android.content.*
import android.graphics.*
import android.os.*
import android.support.v7.widget.*
import android.support.v7.widget.helper.*
import android.view.*
import com.lynn.library.recycler.*
import com.lynn.library.util.*
import com.lynn.simplerecyclerview.*
import com.lynn.simplerecyclerview.base.*
import kotlinx.android.synthetic.main.activity_recycler_drag.*
import kotlinx.android.synthetic.main.layout_recycler_drag_item.*
import java.lang.ref.*
import java.util.*

/**
 * Created by Lynn.
 */

class RecyclerDragActivity : BaseActivity() {
    private lateinit var adapter : BaseRecycledAdapter
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_drag)
        adapter = recycle_view.adapter as BaseRecycledAdapter
        recycle_view.addItemDecoration(DividerDecoration(dp2px(8f).toInt()))
        val height = dp2px(40f).toInt()
        val helper = ItemTouchHelper(ItemTouchHelperCallback(height))
        helper.attachToRecyclerView(recycle_view)
        adapter.register(R.layout.layout_recycler_drag_item , DragItem::class.java)
        adapter.list.add(Color.RED)
        adapter.list.add(Color.BLUE)
        adapter.list.add(Color.YELLOW)
        adapter.list.add(Color.GREEN)
        adapter.list.add(Color.CYAN)
        adapter.list.add(Color.MAGENTA)
        adapter.list.add(Color.LTGRAY)
    }

    companion object {
        fun startActivity(act : Activity) {
            val i = Intent(act , RecyclerDragActivity::class.java)
            act.startActivity(i)
        }

        private class DragItem(view : View) : BaseViewHolder<Int>(view) {
            override fun bind(data : Int) {
                text_view.setBackgroundColor(data)
                val lp = itemView.layoutParams as ViewGroup.MarginLayoutParams
                lp.height = (Math.random() * 600 + 100).toInt()
                lp.leftMargin = adapterPosition * 30
                itemView.layoutParams = lp
            }
        }

        private class DividerDecoration(val offset : Int) : RecyclerView.ItemDecoration() {
            override fun onDraw(c : Canvas , parent : RecyclerView , state : RecyclerView.State?) {
                super.onDraw(c , parent , state)
            }

            override fun getItemOffsets(outRect : Rect , itemPosition : Int , parent : RecyclerView?) {
                parent.let { outRect.set(offset , if (itemPosition == 0) offset else offset / 2 , offset , if (itemPosition == parent!!.childCount - 1) offset else offset / 2) }
            }

            override fun onDrawOver(c : Canvas , parent : RecyclerView , state : RecyclerView.State?) {
                super.onDrawOver(c , parent , state)
            }
        }

        private class ItemTouchHelperCallback(val minHeight : Int) : ItemTouchHelper.Callback() {
            private var originalHeight = 0
            override fun getMovementFlags(recyclerView : RecyclerView , viewHolder : RecyclerView.ViewHolder) : Int {
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                return ItemTouchHelper.Callback.makeMovementFlags(dragFlags , 0)
            }

            override fun canDropOver(recyclerView : RecyclerView? , current : RecyclerView.ViewHolder? , target : RecyclerView.ViewHolder?) : Boolean {
                return true
            }

            override fun onMove(recyclerView : RecyclerView , viewHolder : RecyclerView.ViewHolder , target : RecyclerView.ViewHolder) : Boolean {
                val adapter = recyclerView.adapter as BaseRecycledAdapter
                viewHolder.itemView.post { recyclerView.invalidateItemDecorations() }
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition
                Collections.swap(adapter.list , fromPosition , toPosition)
                adapter.notifyItemMoved(fromPosition , toPosition)
                return true
            }

            private var dragView : SoftReference<View?> = SoftReference(null)
            override fun onSelectedChanged(viewHolder : RecyclerView.ViewHolder? , actionState : Int) {
                super.onSelectedChanged(viewHolder , actionState)
                when (actionState) {
                    ItemTouchHelper.ACTION_STATE_DRAG -> {
                        viewHolder?.let { dragView = SoftReference(viewHolder.itemView) }
                        smallDragedView()
                    }
                    ItemTouchHelper.ACTION_STATE_IDLE -> {
                        largeDragedView()
                    }
                    else -> {
                    }
                }
            }

            private fun smallDragedView() {
                if (null == dragView.get()) return
                originalHeight = dragView!!.get()!!.height
                val anim = ObjectAnimator.ofInt(ObjAnim(dragView) , "animValue" , dragView.get()!!.layoutParams.height , minHeight).setDuration(50)
                anim.start()
            }

            private fun largeDragedView() {
                if (null == dragView || originalHeight == 0) return
                val anim = ObjectAnimator.ofInt(ObjAnim(dragView) , "animValue" , dragView.get()!!.layoutParams.height , originalHeight).setDuration(50)
                anim.start()
                originalHeight = 0
            }

            override fun onSwiped(viewHolder : RecyclerView.ViewHolder , direction : Int) {}
        }

        private class ObjAnim(view : SoftReference<View?>) {
            private val sr = view
            fun setAnimValue(x : Int) {
                val p = sr.get()?.layoutParams ?: return
                p.height = x
                sr.get()?.layoutParams = p
            }
        }
    }
}
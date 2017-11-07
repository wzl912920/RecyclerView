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
//        recycle_view.addItemDecoration(DividerDecoration(dp2px(8f).toInt()))
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
            var isLongPressed = false
            override fun bind(data : Int) {
                text_view.setBackgroundColor(data)
                drag_button.setOnLongClickListener {
                    isLongPressed = true
                    false
                }
                itemView.setOnTouchListener { v , event ->
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        isLongPressed = false
                    }
                    false
                }
                val lp = itemView.layoutParams as ViewGroup.MarginLayoutParams
                lp.height = (Math.random() * 600 + 100).toInt()
                lp.leftMargin = adapterPosition * 30
                itemView.layoutParams = lp
            }
        }

//        private class DividerDecoration(val offset : Int) : RecyclerView.ItemDecoration() {
//            override fun onDraw(c : Canvas , parent : RecyclerView , state : RecyclerView.State?) {
//                super.onDraw(c , parent , state)
//            }
//
//            override fun getItemOffsets(outRect : Rect , itemPosition : Int , parent : RecyclerView?) {
//                parent.let { outRect.set(offset , if (itemPosition == 0) offset else offset / 2 , offset , if (itemPosition == parent!!.childCount - 1) offset else offset / 2) }
//            }
//
//            override fun onDrawOver(c : Canvas , parent : RecyclerView , state : RecyclerView.State?) {
//                super.onDrawOver(c , parent , state)
//            }
//        }

        private class ItemTouchHelperCallback(val minHeight : Int) : ItemTouchHelper.Callback() {
            private var originalHeight = 0
            override fun getMovementFlags(recyclerView : RecyclerView , viewHolder : RecyclerView.ViewHolder) : Int {
                if (viewHolder is DragItem) {
                    if (!viewHolder.isLongPressed) {
                        return 0
                    }
                }
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                return ItemTouchHelper.Callback.makeMovementFlags(dragFlags , 0)
            }

            override fun canDropOver(recyclerView : RecyclerView? , current : RecyclerView.ViewHolder? , target : RecyclerView.ViewHolder?) : Boolean {
                return true
            }

            override fun chooseDropTarget(selected : RecyclerView.ViewHolder? , dropTargets : MutableList<RecyclerView.ViewHolder>? , curX : Int , curY : Int) : RecyclerView.ViewHolder? {
                if (selected == null || dropTargets == null) return null
                val right = curX + selected.itemView.width
                val bottom = curY + selected.itemView.height
                var winner : RecyclerView.ViewHolder? = null
                var winnerScore = -1
                val dx = curX - selected.itemView.left
                val dy = curY - selected.itemView.top
                val targetsSize = dropTargets.size
                for (i in 0 until targetsSize) {
                    val target = dropTargets.get(i)
                    if (dx > 0) {
                        val diff = target.itemView.right - right
                        if (diff < 0 && target.itemView.right > selected.itemView.right) {
                            val score = Math.abs(diff)
                            if (score > winnerScore) {
                                winnerScore = score
                                winner = target
                            }
                        }
                    }
                    if (dx < 0) {
                        val diff = target.itemView.left - curX
                        if (diff > 0 && target.itemView.left < selected.itemView.left) {
                            val score = Math.abs(diff)
                            if (score > winnerScore) {
                                winnerScore = score
                                winner = target
                            }
                        }
                    }
                    if (dy < 0) {
                        val diff = target.itemView.top + target.itemView.height / 2 - curY
                        if (diff > 0 && target.itemView.top + target.itemView.height / 2 < selected.itemView.top + selected.itemView.height / 2) {
                            val score = Math.abs(diff)
                            if (score > winnerScore) {
                                winnerScore = score
                                winner = target
                            }
                        }
                    }

                    if (dy > 0) {
                        val diff = target.itemView.bottom - target.itemView.height / 2 - bottom
                        if (diff < 0 && target.itemView.bottom - target.itemView.height / 2 > selected.itemView.bottom - selected.itemView.height / 2) {
                            val score = Math.abs(diff)
                            if (score > winnerScore) {
                                winnerScore = score
                                winner = target
                            }
                        }
                    }
                }
                return winner
            }

            override fun onMove(recyclerView : RecyclerView , viewHolder : RecyclerView.ViewHolder , target : RecyclerView.ViewHolder) : Boolean {
                val adapter = recyclerView.adapter as BaseRecycledAdapter
                viewHolder.itemView.post { recyclerView.invalidateItemDecorations() }
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition
                Collections.swap(adapter.list , fromPosition , toPosition)
                adapter.notifyItemMoved(fromPosition , toPosition)
//                recyclerView.post { recyclerView.invalidateItemDecorations() }
                return true
            }

            override fun getMoveThreshold(viewHolder : RecyclerView.ViewHolder?) : Float {
                return 0.1f
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

            override fun isLongPressDragEnabled() : Boolean {
                return super.isLongPressDragEnabled()
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
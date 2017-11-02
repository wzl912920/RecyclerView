package com.lynn.simplerecyclerview.drag

import android.app.*
import android.content.*
import android.os.*
import com.lynn.simplerecyclerview.*
import com.lynn.simplerecyclerview.base.*
import kotlinx.android.synthetic.main.activity_drag.*

/**
 * Created by Lynn.
 */

class DragActivity : BaseActivity() {
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drag)
        view_drag_hp.setOnClickListener { ViewDragHelperActivity.startActivity(this) }
        recycle_view.setOnClickListener { RecyclerDragActivity.startActivity(this) }
    }

    companion object {
        fun startActivity(act : Activity) {
            val i = Intent(act , DragActivity::class.java)
            act.startActivity(i)
        }
    }
}

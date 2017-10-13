package com.lynn.simplerecyclerview.drag

import android.app.*
import android.content.*
import android.os.*
import com.lynn.simplerecyclerview.*
import com.lynn.simplerecyclerview.base.*

/**
 * Created by Lynn.
 */

class ViewDragHelperActivity : BaseActivity() {
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_drag_helper)
    }

    companion object {
        fun startActivity(act : Activity) {
            val i = Intent(act , ViewDragHelperActivity::class.java)
            act.startActivity(i)
        }
    }
}

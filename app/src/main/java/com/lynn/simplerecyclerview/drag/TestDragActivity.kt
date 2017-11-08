package com.lynn.simplerecyclerview.drag

import android.app.*
import android.content.*
import android.os.*
import com.lynn.simplerecyclerview.*
import com.lynn.simplerecyclerview.base.*

/**
 * Created by Lynn.
 */

class TestDragActivity : BaseActivity() {
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
    }

    companion object {
        fun startActivity(act : Activity) {
            val i = Intent(act , TestDragActivity::class.java)
            act.startActivity(i)
        }
    }
}

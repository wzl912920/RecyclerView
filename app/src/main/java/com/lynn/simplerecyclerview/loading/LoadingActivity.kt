package com.lynn.simplerecyclerview.loading

import android.app.*
import android.content.*
import android.os.*
import com.lynn.simplerecyclerview.*
import com.lynn.simplerecyclerview.base.*
import kotlinx.android.synthetic.main.activity_loading.*

/**
 * Created by Lynn.
 */

class LoadingActivity : BaseActivity() {
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)
    }

    companion object {
        fun startActivity(act : Activity) {
            val i = Intent(act , LoadingActivity::class.java)
            act.startActivity(i)
        }
    }
}

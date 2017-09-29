package com.lynn.simplerecyclerview.photocrop

import android.app.*
import android.content.*
import android.os.*
import com.lynn.simplerecyclerview.*
import com.lynn.simplerecyclerview.base.*
import kotlinx.android.synthetic.main.activity_choose_photo.*

/**
 * Created by Lynn.
 */

class ChoosePhotoActivity : BaseActivity() {
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_photo)
        touch_area.setOnClickListener { openAlbum() }
    }

    private fun openAlbum() {}

    companion object {
        fun startActivity(act : Activity) {
            val i = Intent(act , ChoosePhotoActivity::class.java)
            act.startActivity(i)
        }
    }
}

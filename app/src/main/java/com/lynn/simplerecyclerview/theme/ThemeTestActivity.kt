package com.lynn.simplerecyclerview.theme

import android.app.*
import android.content.*
import android.os.*
import com.lynn.library.theme.*
import com.lynn.simplerecyclerview.*
import com.lynn.simplerecyclerview.base.*
import kotlinx.android.synthetic.main.activity_theme_test.*

/**
 * Created by Lynn.
 */

class ThemeTestActivity : BaseActivity() {
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_theme_test)
        a.setOnClickListener {
            //the apk is in the app folder
            ThemeConfig.getInstance().registerSkinPath(this , "/mnt/sdcard/test.skin")
        }
        b.setOnClickListener {
            ThemeConfig.getInstance().registerSkinPath(this , "")
        }
    }

    companion object {
        fun startActivity(act : Activity) {
            val i = Intent(act , ThemeTestActivity::class.java)
            act.startActivity(i)
        }
    }
}

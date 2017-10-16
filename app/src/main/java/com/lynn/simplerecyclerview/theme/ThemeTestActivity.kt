package com.lynn.simplerecyclerview.theme

import android.app.*
import android.content.*
import android.os.*
import android.support.v7.app.*
import com.lynn.library.theme.*
import com.lynn.simplerecyclerview.*
import kotlinx.android.synthetic.main.activity_theme_test.*

/**
 * Created by Lynn.
 */

class ThemeTestActivity : BaseThemeActivity() {
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_theme_test)
        a.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            recreate()
        }
        b.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            recreate()
        }
    }

    companion object {
        fun startActivity(act : Activity) {
            val i = Intent(act , ThemeTestActivity::class.java)
            act.startActivity(i)
        }
    }
}

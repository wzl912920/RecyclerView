package com.lynn.simplerecyclerview.theme

import android.*
import android.app.*
import android.content.*
import android.os.*
import android.text.*
import com.lynn.library.theme.*
import com.lynn.library.util.*
import com.lynn.simplerecyclerview.*
import com.lynn.simplerecyclerview.R
import com.lynn.simplerecyclerview.base.*
import com.lynn.simplerecyclerview.util.*
import kotlinx.android.synthetic.main.activity_theme_test.*

/**
 * Created by Lynn.
 */

class ThemeTestActivity : BaseActivity() {
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_theme_test)
        load_skin.setOnClickListener {
            askPermission(GET_SKIN_FILE , Manifest.permission.READ_EXTERNAL_STORAGE , Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        b.setOnClickListener {
            putString(THEME_PATH , "")
            ThemeConfig.getInstance().registerSkinPath(this , "")
        }
        b.setTextColor(resources.getColor(R.color.color_b))
    }

    private fun loadSkin() {
        val path = getString(THEME_PATH , "")
        ThemeConfig.getInstance().registerSkinPath(this , path)
    }

    override fun onPermissionGranted(type : Int) {
        super.onPermissionGranted(type)
        when (type) {
            GET_SKIN_FILE -> {
                openFileManager()
            }
        }
    }

    override fun onActivityResult(requestCode : Int , resultCode : Int , data : Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CHOOSE_FILE_REQUEST_CODE) {
                data?.let {
                    val uri = data.data
                    val path = getAlbumPhotoPath(uri)
                    if (TextUtils.isEmpty(path)) {
                        return
                    }
                    putString(THEME_PATH , path)
                    loadSkin()
                }
            }
        }
    }

    private fun openFileManager() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/skin"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent , CHOOSE_FILE_REQUEST_CODE)
    }

    companion object {
        private val GET_SKIN_FILE = 0x98
        private val CHOOSE_FILE_REQUEST_CODE = 0x99
        fun startActivity(act : Activity) {
            val i = Intent(act , ThemeTestActivity::class.java)
            act.startActivity(i)
        }
    }
}

package com.lynn.simplerecyclerview.photocrop

import android.*
import android.app.*
import android.content.*
import android.net.*
import android.os.*
import android.provider.*
import android.text.*
import com.facebook.drawee.backends.pipeline.*
import com.lynn.library.util.*
import com.lynn.simplerecyclerview.R
import com.lynn.simplerecyclerview.base.*
import com.lynn.simplerecyclerview.util.*
import com.moretech.coterie.utils.*
import kotlinx.android.synthetic.main.activity_choose_photo.*
import java.io.*

/**
 * Created by Lynn.
 */

class ChoosePhotoActivity : BaseActivity() , MenuDialog.Companion.IOnItemClickListener {
    private lateinit var chooseDialog : MenuDialog
    private val CAPTURE_IMG = 0x1011
    private val CHOOSE_IMG_REQUEST_CODE = 0x1012
    private val CROP_IMG = 0x1013
    private var captureName = ""
    private var selectedImagePath = ""
    private val cropImg = "crop.png"

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_photo)
        initDialog()
        touch_area.setOnClickListener {
            if (hasPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                openAlbum()
            } else {
                askPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun initDialog() {
        chooseDialog = MenuDialog(this)
        val list = arrayListOf("拍照" , "相册" , "取消")
        chooseDialog.setData(list , this)
    }

    private fun openAlbum() {
        chooseDialog.show()
    }

    override fun onItemClick(position : Int) {
        chooseDialog.dismiss()
        when (position) {
            0 -> {
                var file : File
                if (!TextUtils.isEmpty(captureName)) {
                    file = File(externalImgDir , captureName)
                    if (file.exists() && file.isFile) {
                        file.delete()
                    }
                }
                captureName = "capture_" + System.currentTimeMillis() + ".jpg"
                file = File(externalImgDir , captureName)
                if (!file.exists()) {
                    try {
                        if (file.createNewFile()) {
                            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            intent.putExtra(MediaStore.EXTRA_OUTPUT , getUri(file))
                            startActivityForResult(intent , CAPTURE_IMG)
                            overridePendingTransition(R.anim.activity_anim_bottom_to_top , R.anim.activity_anim_not_change)
                        }
                    } catch (e : IOException) {
                        e.printStackTrace()
                    }
                }
            }
            1 -> {
                val p = Intent(Intent.ACTION_PICK ,
                               android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(p , CHOOSE_IMG_REQUEST_CODE)
                overridePendingTransition(R.anim.activity_anim_bottom_to_top , R.anim.activity_anim_not_change)
            }
        }
    }

    override fun onActivityResult(requestCode : Int , resultCode : Int , data : Intent?) {
        super.onActivityResult(requestCode , resultCode , data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CHOOSE_IMG_REQUEST_CODE -> {
                    if (null == data) return
                    val uri = data.data
                    val path = getAlbumPhotoPath(uri)
                    if (TextUtils.isEmpty(path)) {
                        return
                    }
                    selectedImagePath = path
                    CropImageActivity.startActivity(this , path , CROP_IMG , cropImg)
                }
                CAPTURE_IMG -> {
                    val file = File(externalImgDir , captureName)
                    selectedImagePath = file.absolutePath
                    CropImageActivity.startActivity(this , selectedImagePath , CROP_IMG , cropImg)
                }
                CROP_IMG -> {
                    if (data == null) return
                    val filePath = data.getStringExtra(Param.DATA)
                    if (TextUtils.isEmpty(filePath)) {
                        return
                    }
                    selectedImagePath = filePath
                    val imagePipeline = Fresco.getImagePipeline()
                    val uri : Uri = Uri.fromFile(File(filePath))
                    imagePipeline.evictFromMemoryCache(uri)
                    imagePipeline.evictFromDiskCache(uri)
                    head_img.setImageURI(Uri.fromFile(File(filePath)))
                }
                else -> {
                }
            }
        }
    }

    companion object {
        fun startActivity(act : Activity) {
            val i = Intent(act , ChoosePhotoActivity::class.java)
            act.startActivity(i)
        }
    }
}

package com.lynn.simplerecyclerview.photocrop

import android.app.*
import android.content.*
import android.content.pm.*
import android.os.*
import android.text.*
import android.view.*
import android.widget.*

import android.graphics.BitmapFactory
import android.graphics.Bitmap
import com.lynn.library.util.*
import com.lynn.simplerecyclerview.*
import com.lynn.simplerecyclerview.base.*
import com.lynn.simplerecyclerview.util.*


/**
 * Created by lynn on 16/8/29.
 */
class CropImageActivity : BaseActivity() , View.OnClickListener {
    private var url = ""
    private lateinit var imageView : CropRoundImageView
    private lateinit var name : String
    //    private DialogUtil.LoadingDialog dialog;

    public override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        setContentView(R.layout.activity_crop_img)
        init()
    }

    private fun init() {
        findViewById(R.id.complete).setOnClickListener(this)
        findViewById(R.id.cancel).setOnClickListener(this)
        url = intent.getStringExtra(IMAGE_URL)
        if (TextUtils.isEmpty(url)) {
            finish()
        }
        //        dialog = DialogUtil.getLoadingDialog(this);

        imageView = findViewById(R.id.user_head_img) as CropRoundImageView

        name = intent.getStringExtra(PIC_NAME)
        getPageView(url)
        initListener()
    }

    private fun getPageView(url : String) : View {
        imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
        imageView.isDrawingCacheEnabled = true
        imageView.setImageBitmap(getBitmap(url))
        return imageView
    }

    private fun getBitmap(url : String) : Bitmap {
        val opts = BitmapFactory.Options()
        opts.inPreferredConfig = Bitmap.Config.RGB_565
        opts.inJustDecodeBounds = true
        BitmapFactory.decodeFile(url , opts)
        //设置位图缩放比例 width，hight设为原来的四分一（该参数请使用2的整数倍）
        // 这也减小了位图占用的内存大小；例如，一张分辨率为2048*1536px的图像使用inSampleSize值为4的设置来解码，产生的Bitmap大小约为512*384px。相较于完整图片占用12M的内存，这种方式只需0.75M内存(假设Bitmap配置为ARGB_8888)。
        opts.inSampleSize = computeSampleSize(opts , 1024f , 1024f)
        //设置解码位图的尺寸信息
        opts.inInputShareable = true
        opts.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(url , opts)
    }

    private fun initListener() {}

    override fun onClick(view : View) {
        when (view.id) {
            R.id.complete -> saveFile()
            R.id.cancel -> finish()
            else -> {
            }
        }
    }

    /**
     * 将所截取图形保存为文件
     */
    private fun saveFile() {
        imageView.cropImageView(name!! , object : CropRoundImageView.Companion.CropImageListener {
            override fun onSuccess(path : String) {
                onCropSuccess(path)
            }

        })
    }

    private fun onCropSuccess(path : String) {
        val intent = Intent()
        intent.putExtra(Param.DATA , path)
        setResult(Activity.RESULT_OK , intent)
        finish()
    }

    companion object {
        private val IMAGE_URL = "image_url"
        private val PIC_NAME = "pic_name"

        fun startActivity(activity : Activity , url : String , requestCode : Int , picName : String) {
            val intent = Intent(activity , CropImageActivity::class.java)
            intent.putExtra(IMAGE_URL , url)
            intent.putExtra(PIC_NAME , picName)
            activity.startActivityForResult(intent , requestCode)
        }
    }
}


package com.lynn.library.util

import android.*
import android.app.*
import android.content.*
import android.content.pm.*
import android.database.*
import android.graphics.*
import android.net.*
import android.os.*
import android.provider.*
import android.support.annotation.*
import android.support.v4.content.*
import android.support.v4.graphics.drawable.*
import android.text.*
import android.util.*
import android.view.*
import android.widget.*
import java.io.*
import com.lynn.util.R
import java.lang.ref.*
import java.math.*
import java.security.*
import java.util.regex.*

/**
 * Created by Lynn.
 */
//the screen height
val Context.screenHeight : Int
    get() = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.height
//the screen width
val Context.screenWidth : Int
    get() = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.width
//the statusbar height
val Context.statusBarHeight : Int
    get() {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height" , "dimen" ,
                                                 "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

//convert dp to px
fun Context.dp2px(dpValue : Float) : Float {
    val scale = resources.displayMetrics.density
    return dpValue * scale + 0.5f
}

//convert px to dp
fun Context.px2dp(pxValue : Float) : Float {
    val scale = resources.displayMetrics.density
    return pxValue / scale + 0.5f
}

//convert sp to px
fun Context.sp2px(spValue : Float) : Float {
    return spValue * resources.displayMetrics.scaledDensity + 0.5f
}

//convert px to sp
fun Context.px2sp(pxValue : Float) : Float {
    return pxValue / resources.displayMetrics.scaledDensity + 0.5f
}

//is net work connected(mobile or wifi)
fun Context.isNetworkAvailable() : Boolean {
    if (checkCallingOrSelfPermission(Manifest.permission.INTERNET) !== PackageManager.PERMISSION_GRANTED) {
        return false
    } else {
        val connectivity = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivity == null) {
        } else {
            val info = connectivity.allNetworkInfo
            if (info != null) {
                for (i in info.indices) {
                    if (info[i].isAvailable) {
                        return true
                    }
                }
            }
        }
    }
    return false
}

//    is wifi available
fun Context.isWifiAvailable() : Boolean {
    val mConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val mWiFiNetworkInfo = mConnectivityManager
            .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
    return mWiFiNetworkInfo?.isAvailable ?: false
}

//    is mobile available
fun Context.isMobileAvailable() : Boolean {
    val mConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val mMobileNetworkInfo = mConnectivityManager
            .getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
    return mMobileNetworkInfo?.isAvailable ?: false
}

//show statusbar
fun Activity.showStatusBar() {
    val decorView = window.decorView
    decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
}

//hide statusbar
fun Activity.hideStatusBar(activity : Activity) {
    val decorView = activity.window.decorView
    decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
}

/** Get_Color Start */
/**
 * calculate the mid color
 *
 * @param startColor
 * @param endColor
 * @param franch
 * @return
 */
@Synchronized
fun caculateColor(@ColorInt startColor : Int , @ColorInt endColor : Int , franch : Float) : Int {
    try {
        val strStartColor = "#" + if (startColor == 0) "00000000" else Integer.toHexString(startColor)
        val strEndColor = "#" + if (endColor == 0) "00000000" else Integer.toHexString(endColor)
        return Color.parseColor(caculateColor(strStartColor , strEndColor , franch))
    } catch (e : Exception) {
        return startColor
    }
}

/**
 * calculate the mid color
 *
 * @param startColor format #FFFFFFFF
 * @param endColor   format #FFFFFFFF
 * @param franch     percent
 * @return format #FFFFFFFF
 */
@Synchronized
fun caculateColor(startColor : String , endColor : String , franch : Float) : String {
    var franch = franch
    if (franch > 1) {
        franch = 1f
    }
    if (franch < 0) {
        franch = 0f
    }
    val startAlpha = Integer.parseInt(startColor.substring(1 , 3) , 16)
    val startRed = Integer.parseInt(startColor.substring(3 , 5) , 16)
    val startGreen = Integer.parseInt(startColor.substring(5 , 7) , 16)
    val startBlue = Integer.parseInt(startColor.substring(7) , 16)

    val endAlpha = Integer.parseInt(endColor.substring(1 , 3) , 16)
    val endRed = Integer.parseInt(endColor.substring(3 , 5) , 16)
    val endGreen = Integer.parseInt(endColor.substring(5 , 7) , 16)
    val endBlue = Integer.parseInt(endColor.substring(7) , 16)

    val currentAlpha = ((endAlpha - startAlpha) * franch + startAlpha).toInt()
    val currentRed = ((endRed - startRed) * franch + startRed).toInt()
    val currentGreen = ((endGreen - startGreen) * franch + startGreen).toInt()
    val currentBlue = ((endBlue - startBlue) * franch + startBlue).toInt()

    return "#" + getHexString(currentAlpha) + getHexString(currentRed) + getHexString(currentGreen) + getHexString(currentBlue)

}

/**
 * covert decimal num to hex num
 */
fun getHexString(value : Int) : String {
    var hexString = Integer.toHexString(value)
    if (hexString.length == 1) {
        hexString = "0" + hexString
    }
    return hexString
}

/** Get_Color End */

//is nick name verified (2-15 Han / 4-30 characters, start with A-Z or a-z or Han, cannot include symbol)
val NICKNAME_NULL = 1
val NICKNAME_LENGTH_NOT_ENOUGH = 2
val NICKNAME_LENGTH_OVER = 3
val NICKNAME_INVALID_BEGIN = 4
val NICKNAME_CONTAINS_INVALID_CHAR = 5
val NICKNAME_VERIFIED = 0
fun isNickNameVerified(nickname : String) : Int {
    if (TextUtils.isEmpty(nickname)) return NICKNAME_NULL
    var sum = getStringLength(nickname)
    if (sum < 4) {
        return NICKNAME_LENGTH_NOT_ENOUGH
    }
    if (sum > 30) {
        return NICKNAME_LENGTH_OVER
    }
    if (!Pattern.matches("^[A-Za-z\u4e00-\u9fa5].*" , nickname)) {
        return NICKNAME_INVALID_BEGIN
    }
    return if (!Pattern.matches("[A-Za-z0-9_\\-\u4e00-\u9fa5]+" , nickname)) {
        NICKNAME_CONTAINS_INVALID_CHAR
    } else NICKNAME_VERIFIED
}

//1 Han = 2 characters
fun getStringLength(str : String) : Int {
    var sum = 0
    if (TextUtils.isEmpty(str)) return sum
    val chars = str.toCharArray()
    for (ch in chars) {
        if (Pattern.matches("[\u4e00-\u9fa5]" , ch.toString())) {
            sum += 2
        } else {
            sum += 1
        }
    }
    return sum
}

//sharedPreference utils
private var sharedPreferences : SharedPreferences? = null
private var spEditor : SharedPreferences.Editor? = null
val Context.sp : SharedPreferences
    get() {
        if (null == sharedPreferences) {
            initSP(this)
        }
        return sharedPreferences!!
    }

private var SP_NAME : String = ""
@Synchronized private fun initSP(ctx : Context) {
    if (null == sharedPreferences) {
        if (SP_NAME.isEmpty()) {
            SP_NAME = "${ctx.packageName}_config"
        }
        sharedPreferences = ctx.getSharedPreferences(SP_NAME , Context.MODE_PRIVATE);
    }
}

val Context.editor : SharedPreferences.Editor
    get() {
        if (null == spEditor) {
            initSPEditor(sp)
        }
        return spEditor!!
    }

@Synchronized private fun initSPEditor(sp : SharedPreferences) {
    if (null == spEditor) {
        spEditor = sp.edit()
    }
}

fun Context.putString(key : String , value : String) {
    editor.putString(key , value)
    editor.apply()
}

fun Context.putBoolean(key : String , value : Boolean?) {
    editor.putBoolean(key , value!!)
    editor.apply()
}

fun Context.putInt(key : String , value : Int?) {
    editor.putInt(key , value!!)
    editor.apply()
}

fun Context.putLong(key : String , value : Long?) {
    editor.putLong(key , value!!)
    editor.apply()
}

fun Context.putFloat(key : String , value : Float?) {
    editor.putFloat(key , value!!)
    editor.apply()
}

fun Context.getString(key : String , defaultValue : String) : String {
    return sp.getString(key , defaultValue)
}

fun Context.getBoolean(key : String , defaultValue : Boolean?) : Boolean {
    return sp.getBoolean(key , defaultValue!!)
}

fun Context.getInteger(key : String , defaultValue : Int?) : Int? {
    return sp.getInt(key , defaultValue!!)
}

fun Context.getLong(key : String , defaultValue : Long?) : Long? {
    return sp.getLong(key , defaultValue!!)
}

fun Context.getFloat(key : String , defaultValue : Float?) : Float? {
    return sp.getFloat(key , defaultValue!!)
}

fun Context.putAll(map : Map<String , *>) {
    for (key in map.keys) {
        val value = map[key]
        when (value) {
            is String -> editor.putString(key , value)
            is Long -> editor.putLong(key , value)
            is Int -> editor.putInt(key , value)
            is Boolean -> editor.putBoolean(key , value)
            is Float -> editor.putFloat(key , value)
            else -> editor.putString(key , value.toString())
        }
    }
    editor.apply()
}

//toast utils
private var toast : AppToast? = null

fun Context.showWarning(msg : String) {
    if (toast == null) {
        initToast()
    }
    toast?.setImageRes(R.drawable.svg_warning)
    toast?.setToastText(msg)
    toast?.setViewBg(this , R.color.color_warning)
    toast?.show()
}

fun Context.showError(msg : String) {
    if (toast == null) {
        initToast()
    }
    toast?.setImageRes(R.drawable.svg_error)
    toast?.setToastText(msg)
    toast?.setViewBg(this , R.color.color_error)
    toast?.show()
}

fun Context.showSuccess(msg : String) {
    if (toast == null) {
        initToast()
    }
    toast?.setImageRes(R.drawable.svg_success)
    toast?.setToastText(msg)
    toast?.setViewBg(this , R.color.color_success)
    toast!!.show()
}

private fun Context.initToast() {
    if (toast == null) {
        synchronized(String::class.java) {
            if (toast == null) {
                toast = AppToast(applicationContext)
            }
        }
    }
}

/** Get_Img_Path START */
fun Context.getAlbumPhotoPath(uri : Uri) : String {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(this , uri)) {
        if (isExternalStorageDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val type = split[0]
            if ("primary".equals(type , ignoreCase = true)) {
                return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
            }
        } else if (isDownloadsDocument(uri)) {
            val id = DocumentsContract.getDocumentId(uri)
            val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads") , java.lang.Long.valueOf(id)!!)
            return getDataColumn(this , contentUri , null , null)
        } else if (isMediaDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val type = split[0]
            var contentUri : Uri? = null
            if ("image" == type) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            } else if ("video" == type) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            } else if ("audio" == type) {
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }
            val selection = "_id=?"
            val selectionArgs = arrayOf(split[1])
            return getDataColumn(this , contentUri , selection , selectionArgs)
        }
    } else if ("content".equals(uri.scheme , ignoreCase = true)) {
        return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(this , uri , null , null)
    } else if ("file".equals(uri.scheme , ignoreCase = true)) {
        return uri.path
    }
    return ""
}

/**
 * Get the value of the data column for this Uri. This is useful for
 * MediaStore Uris, and other file-based ContentProviders.
 *
 * @param uri           The Uri to query.
 * @param selection     (Optional) Filter used in the query.
 * @param selectionArgs (Optional) Selection arguments used in the query.
 * @return The value of the _data column, which is typically a file path.
 */
fun getDataColumn(ctx : Context , uri : Uri? , selection : String? , selectionArgs : Array<String>?) : String {
    var cursor : Cursor? = null
    val column = "_data"
    val projection = arrayOf(column)

    try {
        cursor = ctx.contentResolver.query(uri , projection , selection , selectionArgs , null)
        if (cursor != null && cursor!!.moveToFirst()) {
            val index = cursor!!.getColumnIndexOrThrow(column)
            return cursor.getString(index)
        }
    } finally {
        if (cursor != null)
            cursor.close()
    }
    return ""
}


/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is ExternalStorageProvider.
 */
fun isExternalStorageDocument(uri : Uri) : Boolean {
    return "com.android.externalstorage.documents" == uri.authority
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is DownloadsProvider.
 */
fun isDownloadsDocument(uri : Uri) : Boolean {
    return "com.android.providers.downloads.documents" == uri.authority
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is MediaProvider.
 */
fun isMediaDocument(uri : Uri) : Boolean {
    return "com.android.providers.media.documents" == uri.authority
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is Google Photos.
 */
fun isGooglePhotosUri(uri : Uri) : Boolean {
    return "com.google.android.apps.photos.content" == uri.authority
}

/** Get_Img_Path END */

//mobile model
val DEVICE_NAME : String
    get() = Build.MODEL
//mobile os version
val OS_VERSION : String
    get() = Build.VERSION.RELEASE
//app version name
val Context.APP_VERSION_NAME : String
    get() {
        return try {
            val manager = packageManager
            val info = manager.getPackageInfo(packageName , 0)
            info.versionName
        } catch (e : Exception) {
            e.printStackTrace()
            ""
        }
    }
//app version num
val Context.APP_VERSION_CODE : Int
    get() {
        return try {
            val manager = packageManager
            val info = manager.getPackageInfo(packageName , 0)
            info.versionCode
        } catch (e : Exception) {
            e.printStackTrace()
            0
        }
    }

//compress img
/**
 * cannot run in main thread
 * @param srcImagePath source file path
 * @param outImagePath output file path
 * @param outWidth max img width pixels
 * @param outHeight max img height pixels
 * @param maxFileSize max file size（unit：kb）
 * @return return the compressed file(output file) bytes
 */
fun compressImg(srcImagePath : String , outImagePath : String , outWidth : Int , outHeight : Int , maxFileSize : Int) : ByteArray? {
    if (Looper.getMainLooper() == Looper.myLooper()) {
        log("cannot run in ui thread")
    }
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(srcImagePath , options)
    val srcWidth = options.outWidth.toFloat()
    val srcHeight = options.outHeight.toFloat()
    val maxWidth = (if (outWidth <= 0) 1024 else outWidth).toFloat()//期望输出的图片宽度
    val maxHeight = (if (outHeight <= 0) 1024 else outWidth).toFloat()//期望输出的图片高度
    val srcRatio = srcWidth / srcHeight
    val outRatio = maxWidth / maxHeight
    var actualOutWidth = srcWidth//最终输出的图片宽度
    var actualOutHeight = srcHeight//最终输出的图片高度
    if (srcWidth > maxWidth || srcHeight > maxHeight) {
        if (srcRatio < outRatio) {
            actualOutHeight = maxHeight
            actualOutWidth = actualOutHeight * srcRatio
        } else if (srcRatio > outRatio) {
            actualOutWidth = maxWidth
            actualOutHeight = actualOutWidth / srcRatio
        } else {
            actualOutWidth = maxWidth
            actualOutHeight = maxHeight
        }
    }
    //计算sampleSize
    options.inSampleSize = computeSampleSize(options , actualOutWidth , actualOutHeight)
    options.inJustDecodeBounds = false
    var scaledBitmap : Bitmap? = null
    try {
        scaledBitmap = BitmapFactory.decodeFile(srcImagePath , options)
    } catch (e : OutOfMemoryError) {
        e.printStackTrace()
    }

    if (scaledBitmap == null) {
        return null//压缩失败
    }
    //生成最终输出的bitmap
    val actualOutBitmap = Bitmap.createScaledBitmap(scaledBitmap , actualOutWidth.toInt() , actualOutHeight.toInt() , true)
    if (actualOutBitmap != scaledBitmap)
        scaledBitmap.recycle()

    //进行有损压缩
    val baos = ByteArrayOutputStream()
    var options_ = 100
    actualOutBitmap.compress(Bitmap.CompressFormat.JPEG , options_ , baos)//质量压缩方法，把压缩后的数据存放到baos中 (100表示不压缩，0表示压缩到最小)

    var baosLength = baos.toByteArray().size

    while (baosLength / 1024 > maxFileSize) {//循环判断如果压缩后图片是否大于maxMemmorrySize,大于继续压缩
        baos.reset()//重置baos即让下一次的写入覆盖之前的内容
        options_ = Math.max(0 , options_ - 10)//图片质量每次减少10
        actualOutBitmap.compress(Bitmap.CompressFormat.JPEG , options_ , baos)//将压缩后的图片保存到baos中
        baosLength = baos.toByteArray().size
        if (options_ == 0)
        //如果图片的质量已降到最低则，不再进行压缩
            break
    }
    actualOutBitmap.recycle()

    //将bitmap保存到指定路径
    var fos : FileOutputStream? = null
    val bytes = baos.toByteArray()
    try {
        val file = File(outImagePath)
        if (!file.exists()) {
            file.createNewFile()
        }
        fos = FileOutputStream(file)
        val bufferedOutputStream = BufferedOutputStream(fos)
        bufferedOutputStream.write(baos.toByteArray())
        bufferedOutputStream.flush()
    } catch (e : FileNotFoundException) {
    } catch (e : IOException) {
    } finally {
        if (baos != null) {
            try {
                baos.close()
            } catch (e : IOException) {
                e.printStackTrace()
            }
        }
        if (fos != null) {
            try {
                fos.close()
            } catch (e : IOException) {
                e.printStackTrace()
            }

        }
    }
    return bytes
}

fun computeSampleSize(options : BitmapFactory.Options , reqWidth : Float , reqHeight : Float) : Int {
    val srcWidth = options.outWidth.toFloat()//20
    val srcHeight = options.outHeight.toFloat()//10
    var sampleSize = 1
    if (srcWidth > reqWidth || srcHeight > reqHeight) {
        val withRatio = Math.round(srcWidth / reqWidth)
        val heightRatio = Math.round(srcHeight / reqHeight)
        sampleSize = Math.min(withRatio , heightRatio)
    }
    return sampleSize
}

/**
 * cannot run in main thread
 * @param bitmap
 * @return
 */
fun bitmapToBytes(bitmap : Bitmap) : ByteArray {
    if (Looper.getMainLooper() == Looper.myLooper()) {
        log("cannot run in ui thread")
    }
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG , 100 , stream)
    return stream.toByteArray()
}

fun fileToBitmap(filePath : String) : Bitmap {
    val opts = BitmapFactory.Options()
    opts.inPreferredConfig = Bitmap.Config.RGB_565
    opts.inJustDecodeBounds = true
    BitmapFactory.decodeFile(filePath , opts)
    //设置位图缩放比例 width，hight设为原来的四分一（该参数请使用2的整数倍）
    // 这也减小了位图占用的内存大小；例如，一张分辨率为2048*1536px的图像使用inSampleSize值为4的设置来解码，产生的Bitmap大小约为512*384px。相较于完整图片占用12M的内存，这种方式只需0.75M内存(假设Bitmap配置为ARGB_8888)。
    opts.inSampleSize = computeSampleSize(opts , 1024f , 1024f)
    //设置解码位图的尺寸信息
    opts.inInputShareable = true
    opts.inJustDecodeBounds = false
    return BitmapFactory.decodeFile(filePath , opts)
}

/**
 * cannot run in main thread
 * convert bitmap to file
 */
fun bitmapToFile(bitmap : Bitmap , path : String) {
    if (Looper.getMainLooper() == Looper.myLooper()) {
        log("cannot run in ui thread")
    }
    if (path.isEmpty()) throw NullPointerException("invalid output path")
    val file = File(path)
    try {
        if (file.exists()) {
            file.delete()
        }
        if (!file.exists()) {
            val folder = File(path.substring(0 , path.lastIndexOf("/")))
            if (!folder.exists()) {
                folder.mkdirs()
            }
            file.createNewFile()
        }
        val bos = BufferedOutputStream(FileOutputStream(file))
        bitmap.compress(Bitmap.CompressFormat.JPEG , 100 , bos)
        bos.flush()
        bos.close()
    } catch (e : IOException) {
        e.printStackTrace()
        return
    }
}

/**
 * cannot run in main thread
 * convert view to bitmap
 */
fun viewToBitmap(view : View , width : Float , height : Float , scroll : Boolean , config : Bitmap.Config) : Bitmap {
    if (Looper.getMainLooper() == Looper.myLooper()) {
        log("cannot run in ui thread")
    }
    if (!view.isDrawingCacheEnabled) {
        view.isDrawingCacheEnabled = true
    }
    val bitmap = Bitmap.createBitmap(width.toInt() , height.toInt() , config)
    bitmap.eraseColor(Color.WHITE)
    val canvas = Canvas(bitmap)
    var left = view.left
    var top = view.top
    if (scroll) {
        left = view.scrollX
        top = view.scrollY
    }
    val status = canvas.save()
    canvas.translate((-left).toFloat() , (-top).toFloat())
    //        canvas.scale(1, 1, left, top);
    view.draw(canvas)
    canvas.restoreToCount(status)
    val alphaPaint = Paint()
    alphaPaint.color = Color.WHITE
    canvas.drawRect(0f , 0f , 1f , height , alphaPaint)
    canvas.drawRect(width - 1f , 0f , width , height , alphaPaint)
    canvas.drawRect(0f , 0f , width , 1f , alphaPaint)
    canvas.drawRect(0f , height - 1f , width , height , alphaPaint)
    canvas.setBitmap(null)
    return bitmap
}

/**
 * cannot run in main thread
 * merge two bitmap to one
 */
private fun add2Bitmap(first : Bitmap , second : Bitmap , isHorizontal : Boolean) : Bitmap {
    if (Looper.getMainLooper() == Looper.myLooper()) {
        log("cannot run in ui thread")
    }
    val width : Int
    val height : Int
    if (isHorizontal) {
        width = first.width + second.width
        height = Math.max(first.height , second.height)
    } else {
        width = first.width
        height = first.height + second.height
    }
    val result = Bitmap.createBitmap(width , height , Bitmap.Config.ARGB_8888)
    result.eraseColor(Color.WHITE)
    val canvas = Canvas(result)
    val paint = Paint()
    paint.color = Color.WHITE
    val count = canvas.save()
    if (isHorizontal) {
        canvas.drawBitmap(first , 0f , 0f , paint)
        canvas.drawBitmap(second , first.width.toFloat() , 0f , paint)
    } else {
        canvas.drawBitmap(first , 0f , 0f , paint)
        canvas.drawBitmap(second , 0f , first.height.toFloat() , paint)
    }
    canvas.restoreToCount(count)
    return result
}

/**
 *
 */
fun getFileMd5(file : File?) : String {
    var fis : FileInputStream? = null
    try {
        if (null == file) {
            return ""
        }
        val dexDigest = MessageDigest.getInstance("MD5")
        val bytes = ByteArray(1024)
        var byteCount = 0
        fis = FileInputStream(file) // read the apk file
        while (fis.read(bytes).apply { byteCount = this } != -1) {
            dexDigest.update(bytes , 0 , byteCount)
        }
        val bigInteger = BigInteger(1 , dexDigest.digest()) // calculate the apk hash
        return bigInteger.toString(16)
    } catch (e : Exception) {

    } finally {
        if (null != fis) {
            try {
                fis.close()
            } catch (e : IOException) {
                e.printStackTrace()
            }
        }
    }
    return ""
}

val Context.externalImgDir : String
    get() {
        return externalCacheDir.absolutePath + "/img"
    }

fun Context.getUri(file : File) : Uri? {
    try {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            FileProvider.getUriForFile(this , packageName , file)
        } else {
            Uri.fromFile(file)
        }
    } catch (e : Exception) {
        e.printStackTrace()
    }
    return null
}

//convert day to chinese
fun getDayInChinese(day : Int) : String {
    when (day) {
        0 -> return "零"
        1 -> return "壹"
        2 -> return "贰"
        3 -> return "叁"
        4 -> return "肆"
        5 -> return "伍"
        6 -> return "陆"
        7 -> return "柒"
        8 -> return "捌"
        9 -> return "玖"
        10 -> return "拾"
        11 -> return "拾壹"
        12 -> return "拾贰"
        13 -> return "拾叁"
        14 -> return "拾肆"
        15 -> return "拾伍"
        16 -> return "拾陆"
        17 -> return "拾柒"
        18 -> return "拾捌"
        19 -> return "拾玖"
        20 -> return "贰拾"
        21 -> return "贰拾壹"
        22 -> return "贰拾贰"
        23 -> return "贰拾叁"
        24 -> return "贰拾肆"
        25 -> return "贰拾伍"
        26 -> return "贰拾陆"
        27 -> return "贰拾柒"
        28 -> return "贰拾捌"
        29 -> return "贰拾玖"
        30 -> return "叁拾"
        31 -> return "叁拾壹"
        else -> return ""
    }
}

//convert day to simple chinese
fun getDayInSimpleChinese(day : Int) : String {
    when (day) {
        0 -> return "零"
        1 -> return "一"
        2 -> return "二"
        3 -> return "三"
        4 -> return "四"
        5 -> return "五"
        6 -> return "六"
        7 -> return "七"
        8 -> return "八"
        9 -> return "九"
        10 -> return "十"
        11 -> return "十一"
        12 -> return "十二"
        13 -> return "十三"
        14 -> return "十四"
        15 -> return "十五"
        16 -> return "十六"
        17 -> return "十七"
        18 -> return "十八"
        19 -> return "十九"
        20 -> return "二十"
        21 -> return "二十一"
        22 -> return "二十二"
        23 -> return "二十三"
        24 -> return "二十四"
        25 -> return "二十五"
        26 -> return "二十六"
        27 -> return "二十七"
        28 -> return "二十八"
        29 -> return "二十九"
        30 -> return "三十"
        31 -> return "三十一"
        else -> return ""
    }
}

fun Activity.setStatusBarLightMode(@ColorInt color : Int) {
    if (color == Color.WHITE || color == Color.TRANSPARENT) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = color
        }
        if (isMIUI()) {
            val clazz = window.javaClass
            try {
                var darkModeFlag = 0
                val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
                val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
                darkModeFlag = field.getInt(layoutParams)
                val extraFlagField = clazz.getMethod("setExtraFlags" , kotlin.Int::class.javaPrimitiveType , kotlin.Int::class.javaPrimitiveType)
                extraFlagField.invoke(window , darkModeFlag , darkModeFlag)
            } catch (e : Exception) {
                e.printStackTrace()
            }
        } else if (isFlyme()) {
            val window = window
            if (window != null) {
                try {
                    val lp = window!!.attributes
                    val darkFlag = WindowManager.LayoutParams::class.java!!.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON")
                    val meizuFlags = WindowManager.LayoutParams::class.java!!.getDeclaredField("meizuFlags")
                    darkFlag.isAccessible = true
                    meizuFlags.isAccessible = true
                    val bit = darkFlag.getInt(null)
                    var value = meizuFlags.getInt(lp)
                    value = value or bit
                    meizuFlags.setInt(lp , value)
                    window!!.attributes = lp
                } catch (e : Exception) {
                }
            }
        }
    }
}

private object Config {
    val SMART = "ro.smartisan.version"
    val MIUI_VERSION_NAME = "ro.miui.ui.version.name"
    val EMUI_VERSION_CODE = "ro.build.hw_emui_api_level"
}

/**
 * @return
 */
fun isMIUI() : Boolean {
    try {
        return !TextUtils.isEmpty(getSystemProperty(Config.MIUI_VERSION_NAME))
    } catch (e : Exception) {
        return false
    }
}

/**
 * @return
 */
fun isEMUI() : Boolean {
    return try {
        !TextUtils.isEmpty(getSystemProperty(Config.EMUI_VERSION_CODE))
    } catch (e : Exception) {
        false
    }

}

/**
 * @return
 */
fun isSmartisan() : Boolean {
    return try {
        !TextUtils.isEmpty(getSystemProperty(Config.SMART))
    } catch (e : Exception) {
        false
    }

}

/**
 * @return
 */
fun isFlyme() : Boolean {
    return try {
        val method = Build::class.java.getMethod("hasSmartBar")
        method != null
    } catch (e : Exception) {
        false
    }
}

private fun getSystemProperty(propName : String) : String? {
    val line : String
    var input : BufferedReader? = null
    try {
        val p = Runtime.getRuntime().exec("getprop " + propName)
        input = BufferedReader(InputStreamReader(p.inputStream) , 1024)
        line = input.readLine()
        input.close()
    } catch (ex : IOException) {
        return null
    } finally {
        if (input != null) {
            try {
                input.close()
            } catch (e : IOException) {
            }
        }
    }
    return line
}

fun Context.getMetaString(key : String) : String {
    if (TextUtils.isEmpty(key)) {
        return ""
    }
    var resultData = ""
    try {
        val packageManager = packageManager
        if (packageManager != null) {
            val applicationInfo = packageManager!!.getApplicationInfo(packageName , PackageManager.GET_META_DATA)
            if (applicationInfo != null) {
                if (applicationInfo!!.metaData != null) {
                    resultData = applicationInfo!!.metaData.getString(key) ?: ""
                }
            }
        }
    } catch (e : PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return resultData
}

private val TAG = "Utils"
fun log(s : String) {
    Log.e(TAG , s)
}

private class AppToast(context : Context) : Toast(context) {
    private lateinit var imageView : ImageView
    private lateinit var textView : TextView
    private lateinit var bg : View
    private val sr = SoftReference(context.applicationContext)

    init {
        init()
    }

    private fun init() {
        val v : View = LayoutInflater.from(sr.get()).inflate(R.layout.widget_toast , null , false)
        view = v
        imageView = v.findViewById<ImageView>(R.id.image_view) as ImageView
        textView = v.findViewById<ImageView>(R.id.text_view) as TextView
        bg = v.findViewById(R.id.root_view)
        setGravity(Gravity.FILL_HORIZONTAL or Gravity.TOP , 0 , 0)
    }

    fun setToastText(text : String) {
        var text = text
        if (TextUtils.isEmpty(text)) {
            text = ""
        }
        textView!!.text = text
        if (textView!!.lineCount == 1) {
            textView!!.gravity = Gravity.CENTER_VERTICAL
        }
        duration = if (text.length > 10) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
    }

    fun setViewBg(ctx : Context , @ColorRes color : Int) {
        val up = bg.background
        val drawableUp = DrawableCompat.wrap(up)
        DrawableCompat.setTint(drawableUp , ContextCompat.getColor(ctx , color))
    }

    fun setImageRes(@DrawableRes id : Int) {
        imageView!!.setImageResource(id)
    }
}
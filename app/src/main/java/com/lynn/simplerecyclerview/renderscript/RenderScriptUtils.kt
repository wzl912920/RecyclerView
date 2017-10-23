package com.lynn.simplerecyclerview.renderscript

import android.content.*
import android.graphics.*
import android.os.*
import android.renderscript.*

/**
 * Created by Lynn.
 */

class RenderScriptUtils {
    /**
     * @param radius (0-25]f
     *
     */
//    private fun getRadiusBmp(context : Context , bitmap : Bitmap , radius : Float = 10f) : Bitmap {
//        val defaultConfig = Bitmap.Config.ARGB_8888//if (bitmap.config == null) Bitmap.Config.ARGB_8888 else bitmap.config
//        var output = Bitmap.createBitmap(bitmap).copy(defaultConfig , true)
//        val rs = RenderScript.create(context)
//        val gaussianBlue = ScriptIntrinsicBlur.create(rs , Element.U8_4(rs))
//        var bmp = if (bitmap.isMutable) {
//            bitmap
//        } else {
//            bitmap.copy(defaultConfig , true)
//        }
//        val config = bmp.config
//        if (null == config) {
//            bmp.config = defaultConfig
//        }
//        bmp.config = defaultConfig
//        val allIn = Allocation.createFromBitmap(rs , bmp) // 开辟输入内存
//        val allOut = Allocation.createFromBitmap(rs , output) // 开辟输出内存
//        gaussianBlue.setRadius(radius) // 设置模糊半径，范围0f<radius<=25f
//        gaussianBlue.setInput(allIn) // 设置输入内存
//        gaussianBlue.forEach(allOut) // 模糊编码，并将内存填入输出内存
//        allOut.copyTo(output) // 将输出内存编码为Bitmap，图片大小必须注意
//        rs.destroy() // 关闭RenderScript对象，API>=23则使用rs.releaseAllContexts()
//        return output
//    }
}

package com.jtsportech.visport.android.utils

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat

import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter

import java.util.Hashtable


/**
 * Author: BenChen
 * Date: 2024/02/26 11:18
 * Email:chenxiaobin@cloudhearing.cn
 */
object QRCodeGenerator {

    fun createQRImage(content: String?, width: Int, height: Int): Bitmap? {
        try {
            // 判断URL合法性
            if (content == null || "" == content || content.isEmpty()) {
                return null
            }
            val hints: Hashtable<EncodeHintType, String> = Hashtable<EncodeHintType, String>()
            hints[EncodeHintType.CHARACTER_SET] = "utf-8"
            // 图像数据转换，使用了矩阵转换
            val bitMatrix: BitMatrix = QRCodeWriter().encode(
                content,
                BarcodeFormat.QR_CODE, width, height, hints
            )
            val pixels = IntArray(width * height)
            // 下面这里按照二维码的算法，逐个生成二维码的图片，
            // 两个for循环是图片横列扫描的结果
            for (y in 0 until height) {
                for (x in 0 until width) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * width + x] = -0x1000000
                    } else {
                        pixels[y * width + x] = -0x1
                    }
                }
            }
            // 生成二维码图片的格式，使用ARGB_8888
            val bitmap = Bitmap.createBitmap(
                width, height,
                Bitmap.Config.ARGB_4444
            )
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            return bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
        }
        return null
    }
}
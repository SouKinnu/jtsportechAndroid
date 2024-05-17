package com.jtsportech.visport.android.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.media.ExifInterface
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ImageUtils.calculateInSampleSize
import com.cloudhearing.android.lib_base.utils.toDp
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


/**
 * Author: BenChen
 * Date: 2024/02/27 14:06
 * Email:chenxiaobin@cloudhearing.cn
 */
object BitmapUtils {

    fun textToBitmap(text: String?, textSize: Int, textColor: Int, typeface: Typeface?): Bitmap? {
        val paint = Paint()
        paint.textSize = textSize.toFloat()
        paint.color = textColor
        paint.setTypeface(typeface)
        paint.isAntiAlias = true
        val baseline = -paint.ascent() // 获取基线偏移
        val width = (paint.measureText(text) + 0.5f).toInt() // 获取文字宽度
        val height = (baseline + paint.descent() + 0.5f).toInt() // 获取文字高度
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.TRANSPARENT) // 设置透明背景
        canvas.drawText(text!!, 0f, baseline, paint) // 绘制文字
        return bitmap
    }

    fun shapeXmlToBitmap(context: Context, shapeResId: Int, width: Int, height: Int): Bitmap {
        // 创建一个画布
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 根据 shape XML 资源创建 ShapeDrawable
        val shapeDrawable = context.getDrawable(shapeResId) as? ShapeDrawable
            ?: throw IllegalArgumentException("Invalid shape XML resource")

        // 设置 ShapeDrawable 的尺寸和边界
        shapeDrawable.setBounds(0, 0, width, height)

        // 绘制 ShapeDrawable 到画布上
        shapeDrawable.draw(canvas)

        return bitmap
    }

    fun drawableToBitmap(drawable: Drawable): Bitmap? {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val width = drawable.intrinsicWidth
        val height = drawable.intrinsicHeight
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    fun mergeBitmap(firstBitmap: Bitmap, secondBitmap: Bitmap): Bitmap {
        val w1 = firstBitmap.width
        val h1 = firstBitmap.height
        val w2 = secondBitmap.width
        val h2 = secondBitmap.height
        val space = 20
        val bitmap = Bitmap.createBitmap(
            w1, h1 + h2 + 40,
            firstBitmap.config
        )
        val canvas = Canvas(bitmap)
        canvas.drawRGB(255, 255, 255)
        canvas.drawBitmap(firstBitmap, Matrix(), null)
        val left = if (w1 - w2 > 0) (w1 - w2) / 2 else 0
        canvas.drawBitmap(secondBitmap, left.toFloat(), (h1 + 20).toFloat(), null)
        return bitmap
    }

    fun mergeQrCodeBitmap(
        bgBitmap: Bitmap,
        qrCodeBitmap: Bitmap,
        codeBitmap: Bitmap
    ): Bitmap {
        val bgW = bgBitmap.width
        val bgH = bgBitmap.height
        val qrW = qrCodeBitmap.width
        val qrH = qrCodeBitmap.height
        val codeW = codeBitmap.width
        val codeH = codeBitmap.height

        val bitmap = Bitmap.createBitmap(
            bgW, bgH,
            bgBitmap.config
        )
        // 设置画布的宽度和高度
        val canvas = Canvas(bitmap)
        canvas.drawRGB(255, 255, 255)
        canvas.drawBitmap(bgBitmap, Matrix(), null)

        // 邀请码
        val codeLeft = (bgW / 2 - codeW / 2).toFloat()
        val codeTop = 30.toDp
        canvas.drawBitmap(codeBitmap, codeLeft, codeTop, null)

        // 二维码
        val qrCodeLeft = (bgW / 2 - qrW / 2).toFloat()
        val qrCodeTop = (bgH / 2 - qrH / 2).toFloat() + 10.toDp
        canvas.drawBitmap(qrCodeBitmap, qrCodeLeft, qrCodeTop - 40, null)

        return bitmap
    }

    fun saveBitmap(name: String, bm: Bitmap, mContext: Context) {
        Timber.d("Ready to save picture")
        //指定我们想要存储文件的地址
        val targetPath = mContext.filesDir.toString() + "/images/"
        Timber.d("Save Path=$targetPath")
        //判断指定文件夹的路径是否存在
        if (!FileUtils.isFileExists(targetPath)) {
            FileUtils.createOrExistsDir(targetPath)
        }

        //如果指定文件夹创建成功，那么我们则需要进行图片存储操作
        val saveFile = File(targetPath, name)
        try {
            val saveImgOut = FileOutputStream(saveFile)
            // compress - 压缩的意思
            bm.compress(Bitmap.CompressFormat.JPEG, 80, saveImgOut)
            //存储完成后需要清除相关的进程
            saveImgOut.flush()
            saveImgOut.close()
            Timber.d("The picture is save to your phone!")
        } catch (ex: IOException) {
            ex.printStackTrace()
        }

    }

    /**
     * 图片压缩-质量压缩
     *
     * @param filePath 源图片路径
     * @return 压缩后的路径
     */
    fun compressImage(filePath: String): String {
        //原文件
        val oldFile = File(filePath)
        //压缩文件路径 照片路径/
        val targetPath = oldFile.path
        val quality = 50 //压缩比例0-100
        var bm: Bitmap = getSmallBitmap(filePath) //获取一定尺寸的图片
        val degree: Int = getRotateAngle(filePath) //获取相片拍摄角度
        if (degree != 0) { //旋转照片角度，防止头像横着显示
            bm = setRotateAngle(degree, bm)!!
        }
        val outputFile = File(targetPath)
        try {
            if (!outputFile.exists()) {
                outputFile.getParentFile().mkdirs()
                //outputFile.createNewFile();
            } else {
                outputFile.delete()
            }
            val out = FileOutputStream(outputFile)
            bm.compress(Bitmap.CompressFormat.JPEG, quality, out)
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
            return filePath
        }
        return outputFile.path
    }

    /**
     * 根据路径获得图片信息并按比例压缩，返回bitmap
     */
    fun getSmallBitmap(filePath: String?): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true //只解析图片边沿，获取宽高
        BitmapFactory.decodeFile(filePath, options)
        // 计算缩放比
        options.inSampleSize = calculateInSampleSize(options, 480, 800)
        // 完整解析图片返回bitmap
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(filePath, options)
    }

    /**
     * 获取图片的旋转角度
     *
     * @param filePath
     * @return
     */
    fun getRotateAngle(filePath: String?): Int {
        var rotate_angle = 0
        try {
            val exifInterface = filePath?.let { ExifInterface(it) }
            val orientation: Int = exifInterface!!.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotate_angle = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> rotate_angle = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> rotate_angle = 270
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return rotate_angle
    }

    /**
     * 旋转图片角度
     *
     * @param angle
     * @param bitmap
     * @return
     */
    fun setRotateAngle(angle: Int, bitmap: Bitmap?): Bitmap? {
        var bitmap = bitmap
        if (bitmap != null) {
            val m = Matrix()
            m.postRotate(angle.toFloat())
            bitmap = Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), m, true
            )
            return bitmap
        }
        return bitmap
    }

}
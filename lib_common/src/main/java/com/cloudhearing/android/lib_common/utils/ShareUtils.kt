
package com.cloudhearing.android.lib_common.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.FileProvider
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.util.ArrayList

/**
 * @author Zhonghua
 * @Date 2019/05/08
 */
object ShareUtils {
    private val TAG = ShareUtils::class.java.simpleName

    @Throws(NullPointerException::class)
    fun bitmap2Uri(
        context: Context,
        bitmap: Bitmap
    ): Uri {
        return Uri.parse(
            MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, null, null)
        )
    }

    fun uri2Bitmap(
        context: Context,
        uri: Uri
    ): Bitmap? {
        try {
            return MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    fun shareMsg(
        context: Context,
        activityTitle: String,
        msgTitle: String,
        msgText: String,
        bitmap: Bitmap
    ) {
        shareMsg(context, activityTitle, msgTitle, msgText, bitmap2Uri(context, bitmap))
    }

    fun shareMsg(
        context: Context,
        activityTitle: String,
        msgTitle: String,
        msgText: String,
        imgUri: Uri? = null
    ) {
        val intent = Intent(Intent.ACTION_SEND)
        if (imgUri == null) {
            // 纯文本
            intent.type = "text/plain"
        } else {
            intent.type = "image/jpg"
            intent.putExtra(Intent.EXTRA_STREAM, imgUri)
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle)
        intent.putExtra(Intent.EXTRA_TEXT, msgText)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        try {
            context.startActivity(Intent.createChooser(intent, activityTitle))
        } catch (e: Exception) {
            Timber.e("shareMsg", e)
            throw e
        }
    }

    fun file2Uri(
        context: Context,
        file: File
    ): Uri {
        return FileProvider.getUriForFile(context, context.packageName + ".cloudhearing.provider", file)
    }

    fun shareSimpleFile(
        context: Context,
        path: String,
        msgTitle: String,
        msgText: String,
        showShareTitle: String
    ) {
        val file = File(path)
        if (!file.exists()) {
            throw NullPointerException()
        }
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, msgTitle)
        shareIntent.putExtra(Intent.EXTRA_TEXT, msgText)
        shareIntent.putExtra(Intent.EXTRA_STREAM, file2Uri(context, file))
        shareIntent.type = "*/*"
        try {
            context.startActivity(Intent.createChooser(shareIntent, showShareTitle))
        } catch (e: Exception) {
            Timber.e("shareSimpleFile", e)
            throw e
        }
    }

    fun shareMultipleFile(
        context: Context,
        path: String,
        msgTitle: String,
        msgText: String,
        showShareTitle: String
    ) {
        val file = File(path)
        if (!file.exists()) {
            throw NullPointerException()
        }
        val uriList = ArrayList<Uri>()
        if (file.isDirectory) {
            for (file1 in file.listFiles()) {
                uriList.add(file2Uri(context, file1))
            }
        } else {
            uriList.add(file2Uri(context, file))
        }
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND_MULTIPLE
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, msgTitle)
        shareIntent.putExtra(Intent.EXTRA_TEXT, msgText)
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList)
        shareIntent.type = "*/*"
        try {
            context.startActivity(Intent.createChooser(shareIntent, showShareTitle))
        } catch (e: Exception) {
            Timber.e("shareMultipleFile", e)
            throw e
        }
    }

    fun shareImage(
        context: Context,
        imagePath: String,
        msgTitle: String,
        msgText: String,
        showShareTitle: String
    ) {
        val imageFile = File(imagePath)
        if (!imageFile.exists()) {
            throw NullPointerException("Image file does not exist")
        }

        val imageUri = file2Uri(context, imageFile)

        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, msgTitle)
        shareIntent.putExtra(Intent.EXTRA_TEXT, msgText)
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
        shareIntent.type = "image/*"

        try {
            context.startActivity(Intent.createChooser(shareIntent, showShareTitle))
        } catch (e: Exception) {
            Timber.e("shareImage", e)
            throw e
        }
    }
}

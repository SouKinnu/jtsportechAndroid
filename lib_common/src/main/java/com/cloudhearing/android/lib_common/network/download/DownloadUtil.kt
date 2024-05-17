package com.cloudhearing.android.lib_common.network.download

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import java.io.File
import java.io.InputStream
import java.io.OutputStream

/**
 * Author: BenChen
 * Date: 2023/08/25 17:41
 * Email:chenxiaobin@cloudhearing.cn
 */
object DownloadUtil {
    private lateinit var newCall: Call

    fun ViewModel.downLoadFile(
        downLoadUrl: String,
        dirPath: String,
        fileName: String,
        progress: ((Int) -> Unit)?,
        success: (File) -> Unit,
        failed: (String) -> Unit,
    ) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val fileDir = File(dirPath)
            if (!fileDir.exists()) {
                fileDir.mkdirs()
            }
            val downLoadFile = File(fileDir, fileName)
            val request = Request.Builder().url(downLoadUrl).get().build()
            newCall = OkHttpClient.Builder().build().newCall(request)
            val response = newCall.execute()
            if (response.isSuccessful) {
                response.body?.let {
                    val totalLength = it.contentLength().toDouble()
                    val stream = it.byteStream()
                    stream.copyTo(downLoadFile.outputStream()) { currentLength ->
                        // 当前下载进度
                        val process = currentLength / totalLength * 100
                        progress?.invoke(process.toInt())
                    }
                    success.invoke(downLoadFile)
                } ?: failed.invoke("response body is null")
            } else failed.invoke("download failed：$response")
        } catch (ex: Exception) {
            failed.invoke("download failed：$ex")
            Timber.tag("download failed：$ex")
        }
    }

    fun ViewModel.cancelDownLoadFile() {
        newCall.cancel()
    }

    // InputStream 添加扩展函数，实现字节拷贝。
    private fun InputStream.copyTo(
        out: OutputStream,
        bufferSize: Int = DEFAULT_BUFFER_SIZE,
        progress: (Long) -> Unit,
    ): Long {
        var bytesCopied: Long = 0
        val buffer = ByteArray(bufferSize)
        var bytes = read(buffer)
        while (bytes >= 0) {
            out.write(buffer, 0, bytes)
            bytesCopied += bytes
            bytes = read(buffer)
            progress(bytesCopied)
        }
        return bytesCopied
    }

}
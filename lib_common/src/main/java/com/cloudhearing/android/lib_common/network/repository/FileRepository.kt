package com.cloudhearing.android.lib_common.network.repository


import com.blankj.utilcode.util.FileUtils
import com.cloudhearing.android.lib_common.network.upload.UploadProgressListener
import com.cloudhearing.android.lib_common.network.upload.UploadRequestBody
import com.cloudhearing.android.lib_network.utils.ApiResponse
import com.google.android.material.progressindicator.AnimatorDurationScaleProvider
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import timber.log.Timber

/**
 * Author: BenChen
 * Date: 2024/02/29 15:15
 * Email:chenxiaobin@cloudhearing.cn
 */
class FileRepository : BaseRepository() {

    suspend fun uploadImage(path: String): ApiResponse<String> {
        return executeHttp {
            mJTSportechService.uploadImage(
                assemblyRequestBody(path)
            )
        }
    }

    suspend fun uploadAudio(path: String,duration: Int): ApiResponse<String> {
        return executeHttp {
            mJTSportechService.getAudio(
                assemblyAudioRequestBody(path,duration)
            )
        }
    }
    private fun assemblyRequestBody(path: String): UploadRequestBody {
        var lastProgress = -1
        var currentProgress = 0L
        var totalProgress = -1L
        val multipartBody = MultipartBody.Builder()

        val file = FileUtils.getFileByPath(path)
        val fileName = FileUtils.getFileName(file)

        multipartBody.addFormDataPart(
            "file", fileName, RequestBody.create(
                "multipart/form-data".toMediaTypeOrNull(),
                file
            )
        )
        val body = multipartBody.build()

        return UploadRequestBody(body, object : UploadProgressListener {
            override fun update(written: Long, length: Long, done: Boolean, count: Long) {
                currentProgress += count
                val progress = (currentProgress * 100 / totalProgress).toInt()
                if (lastProgress != progress) {
                    lastProgress = progress
                    Timber.d("上传进度 $progress 是否完成 $done")
                }
            }
        })
    }
    private fun assemblyAudioRequestBody(path: String,duration: Int): UploadRequestBody {
        var lastProgress = -1
        var currentProgress = 0L
        var totalProgress = -1L
        val multipartBody = MultipartBody.Builder()

        val file = FileUtils.getFileByPath(path)
        val fileName = FileUtils.getFileName(file)
        multipartBody.addFormDataPart("duration","$duration")
        multipartBody.addFormDataPart(
            "file", fileName, RequestBody.create(
                "multipart/form-data".toMediaTypeOrNull(),
                file
            )
        )
        val body = multipartBody.build()

        return UploadRequestBody(body, object : UploadProgressListener {
            override fun update(written: Long, length: Long, done: Boolean, count: Long) {
                currentProgress += count
                val progress = (currentProgress * 100 / totalProgress).toInt()
                if (lastProgress != progress) {
                    lastProgress = progress
                    Timber.d("上传进度 $progress 是否完成 $done")
                }
            }
        })
    }
}
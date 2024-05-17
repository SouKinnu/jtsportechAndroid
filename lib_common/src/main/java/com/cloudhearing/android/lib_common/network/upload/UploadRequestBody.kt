package com.cloudhearing.android.lib_common.network.upload

import com.cloudhearing.android.lib_common.network.upload.UploadProgressListener
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.*



/**
 * 上传进度处理,并将进度下发给进度回调接口
 *
 * @property requestBody
 * @property progressListener
 */
class UploadRequestBody(
    private val requestBody: RequestBody,
    private val progressListener: UploadProgressListener
) : RequestBody() {
    //包装完成的BufferedSink
    private var bufferedSink: BufferedSink? = null

    /**
     * 重写调用实际的响应体的contentType
     *
     * @return  MediaType
     */
    override fun contentType(): MediaType? =
        requestBody.contentType()

    /**
     * 重写调用实际的响应体的contentLength
     *
     * @return   contentLength
     */
    override fun contentLength(): Long =
        requestBody.contentLength()

    override fun writeTo(sink: BufferedSink) {
        bufferedSink = sink(sink).buffer()
        //写入
        requestBody.writeTo(bufferedSink!!)
        //必须调用flush，否则最后一部分数据可能不会被写入
        bufferedSink!!.flush()
    }

    private fun sink(sink: Sink): ForwardingSink =
        object : ForwardingSink(sink) {
            //当前写入字节数
            var bytesWritten: Long = 0L

            //总字节长度，避免多次调用contentLength()方法
            var contentLength: Long = 0L

            override fun write(source: Buffer, byteCount: Long) {
                super.write(source, byteCount)
                if (contentLength == 0L) {
                    //获得contentLength的值，后续不再调用
                    contentLength = contentLength()
                }
                //增加当前写入的字节数
                bytesWritten += byteCount
                progressListener.update(
                    bytesWritten,
                    contentLength,
                    bytesWritten == contentLength,
                    byteCount
                )
            }
        }
}
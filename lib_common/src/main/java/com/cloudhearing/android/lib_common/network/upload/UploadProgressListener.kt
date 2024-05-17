package com.cloudhearing.android.lib_common.network.upload



/**
 * 上传进度回调接口
 *
 */
interface UploadProgressListener {

    /**
     * 上传进度
     *
     * @param written   当前读取数据值
     * @param length    请求体的总长度
     * @param done      是否完成
     * @param count     当前写入的总大小
     */
    fun update(written: Long, length: Long, done: Boolean, count: Long)
}
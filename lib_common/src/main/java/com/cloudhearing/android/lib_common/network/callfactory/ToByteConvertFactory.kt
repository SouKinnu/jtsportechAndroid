package com.cloudhearing.android.lib_network.callfactory

import android.os.Build
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class ToByteConvertFactory : Converter.Factory() {
    override fun responseBodyConverter(type: Type, annotations: Array<Annotation>, retrofit: Retrofit): Converter<ResponseBody, *>? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if ("byte[]" == type.typeName) {
                return Converter { value ->
                    value.bytes()
                }
            }
        } else {
            if ("byte[]" == type.toString()) {
                return Converter { value ->
                    value.bytes()
                }
            }
        }
        return super.responseBodyConverter(type, annotations, retrofit)
    }

    override fun requestBodyConverter(type: Type, parameterAnnotations: Array<Annotation>, methodAnnotations: Array<Annotation>, retrofit: Retrofit): Converter<*, RequestBody>? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if ("byte[]" == type.typeName) {
                return Converter<ByteArray?, RequestBody> {
                    it.toRequestBody(MEDIA_TYPE)
                }
            }
        } else {
            if ("byte[]" == type.toString()) {
                return Converter<ByteArray?, RequestBody> {
                    it.toRequestBody(MEDIA_TYPE)
                }
            }
        }
        return super.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit)
    }

    companion object {
        private val MEDIA_TYPE: MediaType = "application/octet-stream".toMediaTypeOrNull()!!
        private const val TAG = "ToByteConvertFactory"
    }
}
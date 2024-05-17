package com.cloudhearing.android.lib_common.network.interceptor

import com.cloudhearing.android.lib_common.network.client.BaseRetrofitClient
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber


class HandleDataDefinitionsInterceptor : ResponseBodyInterceptor() {

    override fun intercept(response: Response, url: String, path: String, body: String): Response {
        var jsonObject: JSONObject? = null
        var jsonArray: JSONArray? = null
        var newJsonObject: JSONObject? = null
        try {
            if (BaseRetrofitClient.getDataDefinitionsPath().contains(path)) {
                if (isJsonObject(body)) {
                    jsonObject = JSONObject(body)
                    newJsonObject = JSONObject()
                    if (response.isSuccessful) {
                        newJsonObject.put("data", jsonObject)
                        newJsonObject.put("code", "${response.code}")
                    } else {
                        // 接口報錯，後臺有返回狀態碼，就用後臺的，沒有用 HTTP 狀態碼
                        if (jsonObject.has("code")) {
                            newJsonObject.put("code", jsonObject.get("code"))
                            // 有返回錯誤信息，就用後臺的
                            if (jsonObject.has("msg")) {
                                newJsonObject.put("msg", jsonObject.get("msg"))
                            }

                        } else {
                            newJsonObject.put("code", "${response.code}")
                        }
                    }
                } else {
                    jsonArray = JSONArray(body)
                    newJsonObject = JSONObject()
                    newJsonObject.put("data", jsonArray)
                    newJsonObject.put("code", "${response.code}")
                }
            }else{
                jsonObject = JSONObject(body)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val contentType = response.body?.contentType()
        val responseBody: ResponseBody?
        responseBody = newJsonObject?.toString()?.toResponseBody(contentType)
            ?: jsonObject.toString().toResponseBody(contentType)
        return response.newBuilder().body(responseBody).build() // 重新生成响应对象
    }

    /**
     * JSON 是数组还是对象
     *
     * @param jsonString
     * @return
     */
    private fun isJsonArrayOrObject(jsonString: String): Boolean {
        val jsonElement = JsonParser.parseString(jsonString)
        return jsonElement.isJsonArray || jsonElement.isJsonObject
    }

    /**
     * JSON 是不是 数组
     *
     * @param jsonString
     * @return
     */
    private fun isJsonArray(jsonString: String): Boolean {
        val jsonElement = JsonParser.parseString(jsonString)
        return jsonElement.isJsonArray
    }

    /**
     * JSON 是不是 对象
     *
     * @param jsonString
     * @return
     */
    private fun isJsonObject(jsonString: String): Boolean {
        val jsonElement = JsonParser.parseString(jsonString)
        return jsonElement.isJsonObject
    }
}
package com.cloudhearing.android.lib_common.network.client

import com.cloudhearing.android.lib_network.callfactory.ToByteConvertFactory
import com.cloudhearing.android.lib_common.network.callfactory.CallFactoryProxy
import com.cloudhearing.android.lib_common.network.interceptor.JTSportechAuthenticatorInterceptor
import com.cloudhearing.android.lib_common.network.interceptor.HandleDataDefinitionsInterceptor
import com.cloudhearing.android.lib_common.network.interceptor.HttpLoggingInterceptor
import com.cloudhearing.android.lib_common.network.interceptor.ManagerDataDefinitionsIntercepter
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager


abstract class BaseRetrofitClient {

    companion object CLIENT {
        const val TIME_OUT = 30L
        const val X_Auth_Token = "X-Auth-Token"
        const val is_Data_Definitions = "is-Data-Definitions"

        private val dataDefinitionsPathSet = HashSet<String>()

        fun addDataDefinitionsPath(path: String) {
            dataDefinitionsPathSet.add(path)
        }

        fun getDataDefinitionsPath(): HashSet<String> {
            return dataDefinitionsPathSet
        }
    }

    /**
     * 创建自定义的 TrustManager
     */
    private val trustManager = object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            // 不做任何处理
        }

        override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            // 不做任何处理
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return emptyArray()
        }

    }

    /**
     * 创建 SSLContext 对象
     */
    private val sslContext = SSLContext.getInstance("TLS").apply {
        init(null, arrayOf(trustManager), SecureRandom())
    }

    private val client: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder()
            .callTimeout(TIME_OUT, TimeUnit.SECONDS)
            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
            .addInterceptor(getHttpLoggingInterceptor())
            .addInterceptor(HandleDataDefinitionsInterceptor())
            .addInterceptor(JTSportechAuthenticatorInterceptor())
            .addInterceptor(ManagerDataDefinitionsIntercepter())

        if (!isCertificateAuthentication) {
            builder.apply {
                sslSocketFactory(sslContext.socketFactory, trustManager)
                hostnameVerifier { _, _ -> true }
            }
        }

        handleBuilder(builder)
        builder.build()
    }

    private val client2: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder()
            .callTimeout(TIME_OUT, TimeUnit.SECONDS)
            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
            .addInterceptor(HandleDataDefinitionsInterceptor())
//            .addInterceptor(getHttpLoggingInterceptor())
//            .addInterceptor(createHeadInterceptor())
            .addInterceptor(JTSportechAuthenticatorInterceptor())
        handleBuilder(builder)
        builder.build()
    }

    private var createHeadInterceptor: () -> Interceptor = {
        Interceptor { chain ->
            val builder = chain.request().newBuilder()
            return@Interceptor chain.proceed(builder.build())
        }
    }

    private fun getHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor("HttpsLog- ").apply {
            //log打印级别，决定了log显示的详细程度
            setPrintLevel(HttpLoggingInterceptor.Level.BODY)
            //log颜色级别，决定了log在控制台显示的颜色
            setColorLevel(Level.INFO)
        }
        return logging
    }

    /**
     * 是否去掉 https 证书认证
     */
    abstract val isCertificateAuthentication: Boolean

    abstract fun handleBuilder(builder: OkHttpClient.Builder)

    open fun <Service> getService(serviceClass: Class<Service>, baseUrl: String): Service {
        return Retrofit.Builder()
            .client(client)
//            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .setLenient()
                        .create()
                )
            )
            .callFactory(CallFactoryProxy(client))
            .baseUrl(baseUrl)
            .build()
            .create(serviceClass)
    }

    fun <Service> getService2(serviceClass: Class<Service>, baseUrl: String): Service {
        return Retrofit.Builder()
            .client(client2)
//            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(
                ToByteConvertFactory()
            )
//            .callFactory(CallFactoryProxy(client2))
            .baseUrl(baseUrl)
            .build()
            .create(serviceClass)
    }
}
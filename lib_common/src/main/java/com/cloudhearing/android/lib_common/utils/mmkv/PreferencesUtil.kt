package com.cloudhearing.android.lib_common.utils.mmkv


import com.cloudhearing.android.lib_base.utils.context.AppProvider
import com.tencent.mmkv.MMKV

class PreferencesUtil private constructor() {


    companion object {
        val INSTANCE: PreferencesUtil by lazy {
            PreferencesUtil()
        }

//        fun setINIT(context: Context) {
//            MMKV.initialize(context)
//        }
//
//        private val kv: MMKV by lazy {
//            MMKV.defaultMMKV()
//        }
    }

    init {
        MMKV.initialize(AppProvider.get())
    }

    var kv: MMKV = MMKV.defaultMMKV()

    fun encodeInt(@PreferencesWrapper.PreferencesKey key: String, value: Int) {
        kv.encode(key, value)
    }

    fun decodeInt(@PreferencesWrapper.PreferencesKey key: String): Int =
        kv.decodeInt(key)

    fun encodeString(@PreferencesWrapper.PreferencesKey key: String, value: String) {
        kv.encode(key, value)
    }

    fun decodeString(@PreferencesWrapper.PreferencesKey key: String): String =
        kv.decodeString(key).orEmpty()

    fun encodeLong(@PreferencesWrapper.PreferencesKey key: String, value: Long) {
        kv.encode(key, value)
    }

    fun decodeLong(@PreferencesWrapper.PreferencesKey key: String): Long =
        kv.decodeLong(key)

    fun encodeDouble(@PreferencesWrapper.PreferencesKey key: String, value: Double) {
        kv.encode(key, value)
    }

    fun decodeDouble(@PreferencesWrapper.PreferencesKey key: String) =
        kv.decodeDouble(key)

    fun encodeBoolean(@PreferencesWrapper.PreferencesKey key: String, value: Boolean) {
        kv.encode(key, value)
    }

    fun decodeBoolean(@PreferencesWrapper.PreferencesKey key: String) =
        kv.decodeBool(key)

    fun removeValueForKey(@PreferencesWrapper.PreferencesKey key: String) {
        kv.removeValueForKey(key)
    }

    fun removeValueForKeys(@PreferencesWrapper.PreferencesKey vararg key: String) {
        kv.removeValuesForKeys(key)
    }
}
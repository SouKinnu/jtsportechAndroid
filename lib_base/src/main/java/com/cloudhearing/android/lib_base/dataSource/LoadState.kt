package com.cloudhearing.android.lib_base.dataSource

/**
 * Author: BenChen
 * Date: 2023/05/24 17:49
 * Email:chenxiaobin@cloudhearing.cn
 */
sealed class LoadState {
    /**
     * 加载中
     */
    class Start(var tip: String = "正在加载中...") : LoadState()

    /**
     * 成功
     */
    class Success<T>(t: T) : LoadState()

    /**
     * 失败
     */
    class Error(val code: String, val msg: String) : LoadState()

    /**
     * 完成
     */
    object Finish : LoadState()
}

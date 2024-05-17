package com.cloudhearing.android.lib_base.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.cloudhearing.android.lib_base.concurrency.CoroutineDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

/**
 * Author: BenChen
 * Date: 2023/12/20 11:02
 * Email:chenxiaobin@cloudhearing.cn
 */
open class BaseCoroutineFragment<VB : ViewBinding>(
    private val inflate: (LayoutInflater, ViewGroup?, Boolean) -> VB
)  : BaseFragment(), CoroutineScope {

    private var mJob = SupervisorJob()
    protected var mMainScope = MainScope()

    var mDispatchers = CoroutineDispatchers()

    override val coroutineContext: CoroutineContext
        get() = mDispatchers.ui + mJob

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mMainScope = MainScope()
    }

    override fun onStart() {
        super.onStart()
        mJob = SupervisorJob()
        mMainScope = MainScope()
    }

    override fun onStop() {
        super.onStop()
        mJob.cancel()
        mMainScope.cancel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mMainScope.cancel()
    }
}
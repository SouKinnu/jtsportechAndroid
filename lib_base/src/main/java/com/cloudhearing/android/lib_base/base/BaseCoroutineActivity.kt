package com.cloudhearing.android.lib_base.base

import com.cloudhearing.android.lib_base.concurrency.CoroutineDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

/**
 * Author: BenChen
 * Date: 2023/12/19 18:10
 * Email:chenxiaobin@cloudhearing.cn
 */
abstract class BaseCoroutineActivity : BaseActivity(), CoroutineScope {

    private var mJob = SupervisorJob()

    private val mDispatchers = CoroutineDispatchers()

    protected var mMainScope = MainScope()

    override val coroutineContext: CoroutineContext
        get() = mDispatchers.ui + mJob

    override fun onStart() {
        super.onStart()
        mJob = SupervisorJob()
        mMainScope = MainScope()
    }


    override fun onStop() {
        super.onStop()
        clearJobs()
    }

    override fun onDestroy() {
        super.onDestroy()
        clearJobs()
    }

    /**
     * Cancels all active background jobs.
     */
    private fun clearJobs() {
        mJob.cancel()
        mMainScope.cancel()
    }
}
package com.cloudhearing.android.lib_base.base.delegate.impl

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.cloudhearing.android.lib_base.base.delegate.listener.CoroutineDelegate
import com.cloudhearing.android.lib_base.concurrency.CoroutineDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

/**
 * Author: BenChen
 * Date: 2023/12/19 18:00
 * Email:chenxiaobin@cloudhearing.cn
 */
class CoroutineDelegateImpl : CoroutineDelegate, CoroutineScope, DefaultLifecycleObserver {

    private var mJob = SupervisorJob()

    private val mDispatchers = CoroutineDispatchers()

    protected var mMainScope = MainScope()

    override val coroutineContext: CoroutineContext
        get() = mDispatchers.ui + mJob

    override val job: Job
        get() = mJob

    override val dispatchers: CoroutineDispatchers
        get() = mDispatchers

    override val mainScope: CoroutineScope
        get() = mMainScope

    override fun registerCoroutine(lifecycle: Lifecycle) {
        lifecycle.addObserver(this)
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        clearJobs()
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        mJob = SupervisorJob()
        mMainScope = MainScope()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
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
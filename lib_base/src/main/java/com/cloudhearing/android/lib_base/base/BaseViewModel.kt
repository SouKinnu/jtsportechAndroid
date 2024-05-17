package com.cloudhearing.android.lib_base.base

import androidx.lifecycle.*
import com.cloudhearing.android.lib_base.BuildConfig
import com.cloudhearing.android.lib_base.concurrency.CoroutineDispatchers
import com.cloudhearing.android.lib_base.dataSource.LoadState
import com.cloudhearing.android.lib_base.utils.SharedFlowEvents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.coroutines.CoroutineContext


open class BaseViewModel :
    ViewModel(),
    CoroutineScope, IBaseViewModel {

    private val mJob = SupervisorJob()

    val coroutineDispatchers = CoroutineDispatchers()

    override val coroutineContext: CoroutineContext
        get() = coroutineDispatchers.ui + mJob

    // 加载状态
    val loadState = SharedFlowEvents<LoadState>()
//    val loadState = MutableLiveData<LoadState>()

    override fun onCleared() {
        super.onCleared()
        mJob.cancel()
    }

    override fun onAny(owner: LifecycleOwner?, event: Lifecycle.Event?) {
    }

    override fun onCreate() {
        Timber.d("onCreate")
    }

    override fun onStart() {
        Timber.d("onStart")
    }

    override fun onResume() {
        Timber.d("onResume")
    }

    override fun onPause() {
        Timber.d("onPause")
    }

    override fun onStop() {
        Timber.d("onStop")
    }

    override fun onDestroy() {
        Timber.d("onDestroy")
    }

    /**
     * 切换 Ui 线程
     *
     * @param T
     * @param block
     */
    fun <T> launchUi(block: suspend () -> T) {
        viewModelScope.launch(coroutineDispatchers.ui) {
            runCatching {
                block()
            }.onFailure {
                if (BuildConfig.DEBUG)
                    it.printStackTrace()
            }
        }
    }

    /**
     * 切换 Io 线程
     *
     * @param T
     * @param block
     */
    fun <T> launchIo(block: suspend () -> T) {
        viewModelScope.launch(coroutineDispatchers.io) {
            runCatching {
                block()
            }.onFailure {
                if (BuildConfig.DEBUG)
                    it.printStackTrace()
            }
        }
    }


}
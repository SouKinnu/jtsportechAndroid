package com.jtsportech.visport.android.mine.setup

import androidx.lifecycle.ViewModel
import com.cloudhearing.android.lib_base.base.BaseViewModel
import com.cloudhearing.android.lib_base.utils.context.AppProvider
import com.cloudhearing.android.lib_base.utils.setEvent
import com.cloudhearing.android.lib_common.utils.CacheDataManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class SetupViewModel : BaseViewModel() {

    private val _cacheSizeFlow = MutableStateFlow("")

    val cacheSizeFlow = _cacheSizeFlow.asStateFlow()

    override fun onStart() {
        super.onStart()
        getCacheSize()
    }

    private fun getCacheSize() = launchUi {
        val cacheSizeWithUnit = withContext(coroutineDispatchers.io) {
            CacheDataManager.getTotalCacheSizeWithUnit(AppProvider.get())
        }

        _cacheSizeFlow.setEvent(cacheSizeWithUnit)
    }

    fun clearCacheSize() = launchUi {
        withContext(coroutineDispatchers.io){
            CacheDataManager.clearAllCache(AppProvider.get())
        }

        getCacheSize()
    }

}
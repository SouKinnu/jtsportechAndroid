package com.jtsportech.visport.android.message.application

import androidx.lifecycle.ViewModel
import com.cloudhearing.android.lib_base.base.BaseViewModel
import com.cloudhearing.android.lib_base.utils.SharedFlowEvents
import com.cloudhearing.android.lib_base.utils.setEvent
import com.cloudhearing.android.lib_common.network.dataSource.messages.Version
import com.cloudhearing.android.lib_common.network.repository.MessageRepository
import com.cloudhearing.android.lib_common.network.repository.WeChatRepository
import com.cloudhearing.android.lib_common.utils.launchRequest
import com.jtsportech.visport.android.utils.helper.WechatHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class ApplicationMessageViewModel : BaseViewModel() {

    private val mMessageRepository: MessageRepository by lazy {
        MessageRepository()
    }

    private val _appVersionStateFlow = MutableStateFlow(Version())

    val appVersionStateFlow = _appVersionStateFlow.asStateFlow()

    private val _toastFlowEvents = SharedFlowEvents<String>()

    val toastFlowEvents = _toastFlowEvents.asSharedFlow()

    override fun onStart() {
        super.onStart()
        getAppVersion()
    }

    override fun onStop() {
        super.onStop()
    }

    private fun getAppVersion() {
        launchRequest(
            isLoading = false, {
                mMessageRepository.getAppVersion()
            }, {
                if (it != null) {
                    _appVersionStateFlow.value = it
                }
            }, { errorCode, errorMsg ->
                _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
            }, {
                _toastFlowEvents.setEvent("${it.message}")
            }
        )
    }
}
package com.jtsportech.visport.android.dialog.viewmodel

import com.cloudhearing.android.lib_base.base.BaseViewModel
import com.cloudhearing.android.lib_base.utils.SharedFlowEvents
import com.cloudhearing.android.lib_base.utils.setEvent
import com.cloudhearing.android.lib_common.network.dataSource.messages.MessageNotice
import com.cloudhearing.android.lib_common.network.repository.MessageRepository
import com.cloudhearing.android.lib_common.utils.launchRequest
import kotlinx.coroutines.flow.MutableStateFlow

class DialogViewModel : BaseViewModel() {
    private val messageRepository: MessageRepository by lazy {
        MessageRepository()
    }
    var messageNoticeStateFlow = MutableStateFlow(emptyList<MessageNotice>())
    private val _toastFlowEvents = SharedFlowEvents<String>()
    fun getMessageNotice() {
        launchRequest(
            isLoading = false, {
                messageRepository.getMessageNotice("", "MATCH_NOTICE")
            }, {
                if (it != null) {
                    messageNoticeStateFlow.value = it
                }
            }, { errorCode, errorMsg ->
                _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
            }, {
                _toastFlowEvents.setEvent("${it.message}")
            }
        )
    }
}
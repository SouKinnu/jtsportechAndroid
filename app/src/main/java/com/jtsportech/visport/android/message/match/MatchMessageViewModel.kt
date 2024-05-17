package com.jtsportech.visport.android.message.match

import androidx.lifecycle.ViewModel
import com.cloudhearing.android.lib_base.base.BaseViewModel
import com.cloudhearing.android.lib_base.utils.SharedFlowEvents
import com.cloudhearing.android.lib_base.utils.setEvent
import com.cloudhearing.android.lib_common.network.dataSource.messages.MessageNotice
import com.cloudhearing.android.lib_common.network.repository.MessageRepository
import com.cloudhearing.android.lib_common.utils.launchRequest
import com.cloudhearing.android.lib_common.utils.mmkv.PreferencesWrapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class MatchMessageViewModel : BaseViewModel() {

    private val mMessageRepository: MessageRepository by lazy {
        MessageRepository()
    }

    private val _matchMessageListStateFlow = MutableStateFlow(emptyList<MessageNotice>())

    val matchMessageListStateFlow = _matchMessageListStateFlow.asStateFlow()

    private val _toastFlowEvents = SharedFlowEvents<String>()

    val toastFlowEvents = _toastFlowEvents.asSharedFlow()

    override fun onStart() {
        super.onStart()
        getMessageNotice()
    }

    private fun getMessageNotice() {
        launchRequest(isLoading = false, {
            mMessageRepository.getMessageNotice("", "")
        }, {
            if (it != null) {
                getMatchMessageList(it)
            }
        }, { errorCode, errorMsg ->
            _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
        }, {
            _toastFlowEvents.setEvent("${it.message}")
        })
    }

    private fun getMatchMessageList(list: List<MessageNotice>) = launchUi {
        withContext(coroutineDispatchers.io) {
            list.filter {
                it.msgType == "MATCH_NOTICE"
            }
        }.let {
            updateMessageStatus(it)
            _matchMessageListStateFlow.value = it
        }
    }

    private fun updateMessageStatus(list: List<MessageNotice>) {
        launchRequest(isLoading = false, {
            val ids = list.map {
                it.id
            }.joinToString(",")

            mMessageRepository.updateMsgNotice(ids, "READ", "")
        }, {

        }, { errorCode, errorMsg ->
            _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
        }, {
            _toastFlowEvents.setEvent("${it.message}")
        })
    }
}
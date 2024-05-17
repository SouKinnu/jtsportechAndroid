package com.jtsportech.visport.android.dashboard.message

import com.cloudhearing.android.lib_base.base.BaseViewModel
import com.cloudhearing.android.lib_base.utils.SharedFlowEvents
import com.cloudhearing.android.lib_base.utils.setEvent
import com.cloudhearing.android.lib_common.network.dataSource.messages.MessageNotice
import com.cloudhearing.android.lib_common.network.repository.MessageRepository
import com.cloudhearing.android.lib_common.utils.launchRequest
import com.cloudhearing.android.lib_network.utils.ApiEmptyResponse
import com.cloudhearing.android.lib_network.utils.ApiSuccessApiResponse
import com.jtsportech.visport.android.dataSource.message.MessageEntity
import com.jtsportech.visport.android.utils.getDefaultAppVersionMessage
import com.jtsportech.visport.android.utils.getDefaultContestsAndInteractiveMessages
import com.jtsportech.visport.android.utils.handleAppVersionToMessageEntity
import com.jtsportech.visport.android.utils.handleMessageNoticeToMessageEntityList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class MessageViewModel : BaseViewModel() {

    private val mMessageRepository: MessageRepository by lazy {
        MessageRepository()
    }

    private val _messageListFlow = MutableStateFlow(emptyList<MessageEntity>())

    val messageListFlow = _messageListFlow.asStateFlow()

    private val _numberOfMessagesFlow = MutableStateFlow(0)

    val numberOfMessagesFlow = _numberOfMessagesFlow.asStateFlow()

    private val _toastFlowEvents = SharedFlowEvents<String>()

    val toastFlowEvents = _toastFlowEvents.asSharedFlow()

    private var mOriginalMessageList: List<MessageNotice> = emptyList()

    override fun onStart() {
        super.onStart()
        getMessageNotice()
    }

    override fun onStop() {
        super.onStop()
    }

    fun getMessageNotice() = launchUi {
        var numberOfMessages = 0

        val messageResult = withContext(coroutineDispatchers.io) {
            mMessageRepository.getMessageNotice("", "")
        }

        val appVersionResult = withContext(coroutineDispatchers.io) {
            mMessageRepository.getAppVersion()
        }

        val messageList = arrayListOf<MessageEntity>()

        when (messageResult) {
            is ApiSuccessApiResponse, is ApiEmptyResponse -> {
                val originalMessageList = messageResult.data

                mOriginalMessageList = originalMessageList.orEmpty()

                if (!originalMessageList.isNullOrEmpty()) {
                    withContext(coroutineDispatchers.io) {
                        handleMessageNoticeToMessageEntityList(originalMessageList)
                    }.let {
                        messageList.addAll(it)
                    }
                } else {
                    messageList.addAll(getDefaultContestsAndInteractiveMessages())
                }

                numberOfMessages += mOriginalMessageList.size
            }

            else -> {
                mOriginalMessageList = emptyList()

                messageList.addAll(getDefaultContestsAndInteractiveMessages())
            }
        }


        when (appVersionResult) {
            is ApiSuccessApiResponse, is ApiEmptyResponse -> {
                val version = appVersionResult.data

                if (version != null) {
                    messageList.add(handleAppVersionToMessageEntity(version))
                } else {
                    messageList.add(getDefaultAppVersionMessage())
                }

                numberOfMessages++
            }

            else -> {
                messageList.add(getDefaultAppVersionMessage())
            }
        }

        withContext(coroutineDispatchers.io) {
            messageList.sortedBy {
                it.type
            }
        }.let {
            _messageListFlow.value = it
        }

        _numberOfMessagesFlow.setEvent(numberOfMessages)
    }

    fun updateMessageStatus() {
        launchRequest(isLoading = false, {
            val ids = mOriginalMessageList.map {
                it.id
            }.joinToString(",")

            mMessageRepository.updateMsgNotice(ids, "READ", "")
        }, {
            getMessageNotice()
        }, { errorCode, errorMsg ->
            _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
        }, {
            _toastFlowEvents.setEvent("${it.message}")
        })
    }
}
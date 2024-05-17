package com.jtsportech.visport.android.message.interactive

import com.cloudhearing.android.lib_base.base.BaseViewModel
import com.cloudhearing.android.lib_base.utils.SharedFlowEvents
import com.cloudhearing.android.lib_base.utils.context.AppProvider
import com.cloudhearing.android.lib_base.utils.setEvent
import com.cloudhearing.android.lib_common.network.dataSource.messages.MessageNotice
import com.cloudhearing.android.lib_common.network.repository.MessageRepository
import com.cloudhearing.android.lib_common.utils.launchRequest
import com.jtsportech.visport.android.utils.audio
import com.jtsportech.visport.android.utils.player.AudioPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import timber.log.Timber

class InteractiveMessageViewModel : BaseViewModel() {

    private val mMessageRepository: MessageRepository by lazy {
        MessageRepository()
    }


    private val _messageNoticeListStateFlow = MutableStateFlow(emptyList<MessageNotice>())

    val messageNoticeListStateFlow = _messageNoticeListStateFlow.asStateFlow()

    private val _toastFlowEvents = SharedFlowEvents<String>()

    val toastFlowEvents = _toastFlowEvents.asSharedFlow()

    private var mCurrentMessageNotice: MessageNotice? = null

    override fun onStart() {
        super.onStart()
        AudioPlayer.getInstance(AppProvider.get()).addListener(mAudioPlayerListener)
        getMessageNotice()
    }

    override fun onStop() {
        super.onStop()
        AudioPlayer.getInstance(AppProvider.get()).removeListener(mAudioPlayerListener)
    }

    private val mAudioPlayerListener = object : AudioPlayer.AudioPlayerListener {
        override fun onAudioPlayerStart() {

        }

        override fun onAudioPlayerStop(url: String) {
            updateStopPlayerStatusLogic(url)
        }
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

    private fun getMatchMessageList(list: List<MessageNotice>) = launchUi {
        withContext(coroutineDispatchers.io) {
            list.filter {
                it.msgType == "INTERACT_MSG"
            }
        }.let {
            _messageNoticeListStateFlow.value = it
            updateMessageStatus(it)
        }
    }

    fun togglePlayingStatus(message: MessageNotice) = launchUi {
        mCurrentMessageNotice = message

        val messageNoticeList = _messageNoticeListStateFlow.value

        val isPlaying = message.msgTargetInfo?.isPlaying ?: false
        val audioFilePath = message.msgTargetInfo?.audioFilePath.orEmpty()

        adjustPlayerStatus(!isPlaying, audioFilePath)

        withContext(coroutineDispatchers.io) {
            messageNoticeList.map {
                if (it.msgTargetInfo?.contentType == InteractiveMessageAdapter.CONTENT_AUDIO) {
                    if (it.id == message.id) {
                        it.copy(
                            msgTargetInfo = it.msgTargetInfo?.copy(
                                isPlaying = !isPlaying
                            )
                        )
                    } else {
                        it.copy(
                            msgTargetInfo = it.msgTargetInfo?.copy(
                                isPlaying = false
                            )
                        )
                    }
                } else {
                    it
                }
            }
        }.let {
            _messageNoticeListStateFlow.value = it
        }
    }

    private fun adjustPlayerStatus(isPlaying: Boolean, audioFilePath: String) {
        if (isPlaying) {
            // 有正在播放的音频需要停止掉
            AudioPlayer.getInstance(AppProvider.get()).stop()
            Timber.d("播放音乐")
            AudioPlayer.getInstance(AppProvider.get()).play(audioFilePath.audio())
        } else {
            Timber.d("停止播放音乐")
            AudioPlayer.getInstance(AppProvider.get()).stop()
        }
    }

    private fun updateStopPlayerStatusLogic(audioFilePath: String) = launchUi {
        val messageNoticeList = _messageNoticeListStateFlow.value

        Timber.d("save audioFilePath ${mCurrentMessageNotice?.msgTargetInfo?.audioFilePath} player ${audioFilePath}")
        // 自然播放结束
        if (audioFilePath.endsWith(mCurrentMessageNotice?.msgTargetInfo?.audioFilePath.orEmpty())) {
            Timber.d("自然播放结束")
            withContext(coroutineDispatchers.io) {
                messageNoticeList.map {
                    if (it.msgTargetInfo?.contentType == InteractiveMessageAdapter.CONTENT_AUDIO && audioFilePath.endsWith(
                            it.msgTargetInfo?.audioFilePath.orEmpty()
                        )
                    ) {
                        Timber.d("刷新了")
                        it.copy(
                            msgTargetInfo = it.msgTargetInfo?.copy(
                                isPlaying = false
                            )
                        )
                    } else {
                        it
                    }
                }
            }.let {
                _messageNoticeListStateFlow.value = it
            }
        }
        mCurrentMessageNotice = null
    }
}
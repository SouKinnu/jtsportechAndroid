package com.jtsportech.visport.android.message.search

import androidx.lifecycle.ViewModel
import com.cloudhearing.android.lib_base.base.BaseViewModel
import com.cloudhearing.android.lib_base.utils.SharedFlowEvents
import com.cloudhearing.android.lib_base.utils.context.AppProvider
import com.cloudhearing.android.lib_base.utils.setEvent
import com.cloudhearing.android.lib_common.network.dataSource.messages.MessageNotice
import com.cloudhearing.android.lib_common.network.repository.MessageRepository
import com.cloudhearing.android.lib_common.utils.launchRequest
import com.jtsportech.visport.android.message.interactive.InteractiveMessageAdapter
import com.jtsportech.visport.android.utils.audio
import com.jtsportech.visport.android.utils.player.AudioPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import timber.log.Timber

class SearchMessageViewModel : BaseViewModel() {

    private val mMessageRepository: MessageRepository by lazy {
        MessageRepository()
    }

    private val _competitionMessageListFlow = MutableStateFlow(emptyList<MessageNotice>())

    val competitionMessageListFlow = _competitionMessageListFlow.asStateFlow()

    private val _interactionMessageListFlow = MutableStateFlow(emptyList<MessageNotice>())

    val interactionMessageListFlow = _interactionMessageListFlow.asStateFlow()

    private val _toastFlowEvents = SharedFlowEvents<String>()

    val toastFlowEvents = _toastFlowEvents.asSharedFlow()


    private var mCurrentMessageNotice: MessageNotice? = null

    override fun onStart() {
        super.onStart()
        AudioPlayer.getInstance(AppProvider.get()).addListener(mAudioPlayerListener)
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

    fun search(keyword: String) {
        launchRequest(isLoading = false, {
            mMessageRepository.getMessageNotice(keyword, "")
        }, {
            if (it != null) {
                handleCompetitionMessageList(it)
                handleInteractionMessageList(it)
            }
        }, { errorCode, errorMsg ->
            _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
        }, {
            _toastFlowEvents.setEvent("${it.message}")
        })
    }

    private suspend fun handleCompetitionMessageList(list: List<MessageNotice>) {
        withContext(coroutineDispatchers.io) {
            list.filter {
                it.msgType == "MATCH_NOTICE"
            }
        }.let {
            _competitionMessageListFlow.value = it
        }
    }

    private suspend fun handleInteractionMessageList(list: List<MessageNotice>) {
        withContext(coroutineDispatchers.io) {
            list.filter {
                it.msgType == "INTERACT_MSG"
            }
        }.let {
            _interactionMessageListFlow.value = it
        }
    }

    fun togglePlayingStatus(message: MessageNotice) = launchUi {
        mCurrentMessageNotice = message

        val messageNoticeList = _interactionMessageListFlow.value

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
            _interactionMessageListFlow.value = it
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
        val messageNoticeList = _interactionMessageListFlow.value

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
                _interactionMessageListFlow.value = it
            }
        }

        mCurrentMessageNotice = null
    }
}
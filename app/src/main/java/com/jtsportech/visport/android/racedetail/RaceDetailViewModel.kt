package com.jtsportech.visport.android.racedetail

import androidx.lifecycle.MutableLiveData
import com.cloudhearing.android.lib_base.base.BaseViewModel
import com.cloudhearing.android.lib_base.utils.SharedFlowEvents
import com.cloudhearing.android.lib_base.utils.context.AppProvider
import com.cloudhearing.android.lib_base.utils.setEvent
import com.cloudhearing.android.lib_common.network.dataSource.messages.Reply
import com.cloudhearing.android.lib_common.network.dataSource.player.VideoPlayUrlEntity
import com.cloudhearing.android.lib_common.network.dataSource.racedetail.RaceDetailEntity
import com.cloudhearing.android.lib_common.network.repository.CompetitionRepository
import com.cloudhearing.android.lib_common.network.repository.FileRepository
import com.cloudhearing.android.lib_common.utils.launchRequest
import com.jtsportech.visport.android.message.interactive.InteractiveMessageAdapter
import com.jtsportech.visport.android.racedetail.messages.MessagesAdapter
import com.jtsportech.visport.android.utils.audio
import com.jtsportech.visport.android.utils.player.AudioPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File

class RaceDetailViewModel : BaseViewModel() {
    private val mCompetitionRepository: CompetitionRepository by lazy {
        CompetitionRepository()
    }
    private val mFileRepository: FileRepository by lazy {
        FileRepository()
    }
    private var mCurrentMessageNotice: Reply? = null
    private val _interactionMessageListFlow = MutableStateFlow(ArrayList<Reply>())
    val interactionMessageListFlow = _interactionMessageListFlow.asStateFlow()
    val leagueFavoritesListStateFlowVideoPlayUrl = MutableStateFlow(emptyList<VideoPlayUrlEntity>())
    val leagueFavoritesListStateFlowMessages = MutableStateFlow(emptyList<Int>())
    val leagueFavoritesListLiveDataRaceDetail = MutableLiveData<RaceDetailEntity>()

    //    val leagueFavoritesListStateFlowMatchInfo = MutableStateFlow(emptyList<MatchInfo>())
    val leagueFavoritesListStateFlowAudio = MutableStateFlow(emptyList<String>())
    val leagueFavoritesListLiveDataCriticizeVoice = MutableLiveData<String>()
    val leagueFavoritesListLiveDataEvaluate = MutableLiveData<VideoPlayUrlEntity>()
    val leagueFavoritesListLiveDataWatchRecord = MutableLiveData<VideoPlayUrlEntity>()

    //    val leagueFavoritesListStateFlow = MutableStateFlow(emptyList<EventName>())
    val leagueFavoritesListLiveDataAddFavorite = MutableLiveData<VideoPlayUrlEntity>()
//    val  = MutableStateFlow(emptyList<VideoPlayUrlEntity>())

    val leagueFavoritesListLiveDataCancelFavorite = MutableLiveData<VideoPlayUrlEntity>()
    val leagueFavoritesLiveDataVideoDownLoadUrl = MutableLiveData<VideoPlayUrlEntity>()

    val leagueFavoritesLiveDataAudioIsPlay = MutableLiveData(2)

    private val _toastFlowEvents = SharedFlowEvents<String>()
    fun getVideoPlayUrl(videoSourceId: String, videoSourceType: String) {
        launchRequest(
            isLoading = false, {
                mCompetitionRepository.getVideoPlayUrl(videoSourceId, videoSourceType)
            }, {
                if (it != null) {
                    leagueFavoritesListStateFlowVideoPlayUrl.value = listOf(it)
                }
            }, { errorCode, errorMsg ->
                _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
            }, {
                _toastFlowEvents.setEvent("${it.message}")
            }
        )
    }

    fun getMessages(matchInfoId: String, sortType: Int) {
        launchRequest(
            isLoading = false, {
                mCompetitionRepository.getMessages(matchInfoId, sortType)
            }, {
                if (it != null) {
                    _interactionMessageListFlow.value.clear()
                    _interactionMessageListFlow.value = it.criticizeList as ArrayList<Reply>
                    AudioPlayer.getInstance(AppProvider.get()).addListener(mAudioPlayerListener)
                }
            }, { errorCode, errorMsg ->
                _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
            }, {
                _toastFlowEvents.setEvent("${it.message}")
            }
        )
    }

    fun getCriticize(
        audioDuration: Int,
        audioFileId: String,
        contentText: String,
        contentType: String,
        criticizeType: String,
        matchInfoId: String,
        replyCriticizeId: String
    ) {
        launchRequest(
            isLoading = false, {
                mCompetitionRepository.getCriticize(
                    audioDuration,
                    audioFileId,
                    contentText,
                    contentType,
                    criticizeType,
                    matchInfoId,
                    replyCriticizeId
                )
            }, {
                if (it != null) {
                    leagueFavoritesListLiveDataCriticizeVoice.postValue(it.data)
                }
            }, { errorCode, errorMsg ->
                _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
            }, {
                _toastFlowEvents.setEvent("${it.message}")
            }
        )
    }

    fun getRaceDetail(matchInfoId: String) {
        launchRequest(
            isLoading = false, {
                mCompetitionRepository.getRaceDetail(
                    matchInfoId,
                )
            }, {
                if (it != null) {
                    leagueFavoritesListLiveDataRaceDetail.value = it
//                    leagueFavoritesListStateFlowMatchInfo.value = listOf(it.matchInfo)
//                    leagueFavoritesListStateFlow.value = it.eventNameList
                }
            }, { errorCode, errorMsg ->
                _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
            }, {
                _toastFlowEvents.setEvent("${it.message}")
            }
        )
    }

    fun getVideoDownloadUrl(videoSourceId: String, videoSourceType: String) {
        launchRequest(
            isLoading = false, {
                mCompetitionRepository.getVideoDownLoadUrl(videoSourceId, videoSourceType)
            }, {
                if (it != null) {
                    leagueFavoritesLiveDataVideoDownLoadUrl.postValue(it)
                }
            }, { errorCode, errorMsg ->
                _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
            }, {
                _toastFlowEvents.setEvent("${it.message}")
            }
        )
    }

    fun getAudio(duration: Int, file: File) {
        launchRequest(
            isLoading = false, {
                mFileRepository.uploadAudio(file.path, duration)
            }, {
                if (it != null) {
                    leagueFavoritesListStateFlowAudio.value = listOf(it)
                }
            }, { errorCode, errorMsg ->
                _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
            }, {
                _toastFlowEvents.setEvent("${it.message}")
            }
        )
    }

    fun getEvaluate(
        matchEventId: String,
        evaluateResult: String
    ) {
        launchRequest(
            isLoading = false, {
                mCompetitionRepository.getEvaluate(matchEventId, evaluateResult)
            }, {
                if (it != null) {
                    leagueFavoritesListLiveDataEvaluate.postValue(it)
                }
            }, { errorCode, errorMsg ->
                _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
            }, {
                _toastFlowEvents.setEvent("${it.message}")
            }
        )
    }

    fun addWatchRecord(
        matchInfoId: String
    ) {
        launchRequest(
            isLoading = false, {
                mCompetitionRepository.addWatchRecord(matchInfoId)
            }, {
                if (it != null) {
                    leagueFavoritesListLiveDataWatchRecord.postValue(it)
                }
            }, { errorCode, errorMsg ->
                _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
            }, {
                _toastFlowEvents.setEvent("${it.message}")
            }
        )
    }

    fun getAddFavorite(
        eventName: String,
        favoriteType: String,
        matchInfoId: String
    ) {
        launchRequest(
            isLoading = false, {
                mCompetitionRepository.getAddFavorite(eventName, favoriteType, matchInfoId)
            }, {
                if (it != null) {
                    leagueFavoritesListLiveDataAddFavorite.postValue(it)
                }
            }, { errorCode, errorMsg ->
                _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
            }, {
                _toastFlowEvents.setEvent("${it.message}")
            }
        )
    }

    fun getCancelFavorite(
        favoriteIds: String
    ) {
        launchRequest(
            isLoading = false, {
                mCompetitionRepository.getCancelFavorite(favoriteIds)
            }, {
                if (it != null) {
                    leagueFavoritesListLiveDataCancelFavorite.postValue(it)
                }
            }, { errorCode, errorMsg ->
                _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
            }, {
                _toastFlowEvents.setEvent("${it.message}")
            }
        )
    }

    fun getMessagesNum(
        matchInfoId: String, sortType: Int
    ) {
        launchRequest(
            isLoading = false, {
                mCompetitionRepository.getMessages(matchInfoId, sortType)
            }, {
                if (it != null) {
                    leagueFavoritesListStateFlowMessages.value = listOf(it.totalCount)
                }
            }, { errorCode, errorMsg ->
                _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
            }, {
                _toastFlowEvents.setEvent("${it.message}")
            }
        )
    }

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

    fun togglePlayingStatus(reply: Reply) = launchUi {
        mCurrentMessageNotice = reply
        val messageNoticeList = _interactionMessageListFlow.value
        val isPlaying = reply.isPlaying
        val audioFilePath = reply.audioFilePath.orEmpty()
        adjustPlayerStatus(!isPlaying, audioFilePath)

        withContext(coroutineDispatchers.io) {
            messageNoticeList.map {
                if (it.contentType == MessagesAdapter.CONTENT_AUDIO) {
                    if (it.id == reply.id) {

                        it.copy(
                            isPlaying = !isPlaying
                        )
                    } else {
                        it.copy(
                            isPlaying = false
                        )
                    }
                } else {
                    it
                }
            }
        }.let {
            _interactionMessageListFlow.value = it as ArrayList<Reply>
        }
    }

    private fun adjustPlayerStatus(isPlaying: Boolean, audioFilePath: String) {
        if (isPlaying) {
            // 有正在播放的音频需要停止掉
            AudioPlayer.getInstance(AppProvider.get()).stop()
            Timber.d("播放音乐")
            AudioPlayer.getInstance(AppProvider.get()).play(audioFilePath.audio())
            leagueFavoritesLiveDataAudioIsPlay.postValue(1)
        } else {
            Timber.d("停止播放音乐")
            AudioPlayer.getInstance(AppProvider.get()).stop()
            leagueFavoritesLiveDataAudioIsPlay.postValue(0)
        }
    }

    fun updateStopPlayerStatusLogic(audioFilePath: String) = launchUi {
        val messageNoticeList = _interactionMessageListFlow.value

        // 自然播放结束
        if (audioFilePath.endsWith(mCurrentMessageNotice?.audioFilePath.orEmpty())) {
            Timber.d("自然播放结束")
            withContext(coroutineDispatchers.io) {
                Timber.d("自然播放结束$messageNoticeList")
                leagueFavoritesLiveDataAudioIsPlay.postValue(0)
                messageNoticeList.map {
                    if (it.contentType == InteractiveMessageAdapter.CONTENT_AUDIO && audioFilePath.endsWith(
                            it.audioFilePath.orEmpty()
                        )
                    ) {
                        Timber.d("刷新了")
                        it.copy(
                            isPlaying = false
                        )
                    } else {
                        it
                    }
                }
            }.let {
                _interactionMessageListFlow.value = it as ArrayList<Reply>
            }
        }
        mCurrentMessageNotice = null
    }
}
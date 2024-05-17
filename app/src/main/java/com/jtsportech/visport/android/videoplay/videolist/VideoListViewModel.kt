package com.jtsportech.visport.android.videoplay.videolist

import com.cloudhearing.android.lib_base.base.BaseViewModel
import com.cloudhearing.android.lib_base.utils.SharedFlowEvents
import com.cloudhearing.android.lib_base.utils.setEvent
import com.cloudhearing.android.lib_common.network.dataSource.player.VideoPlayUrlEntity
import com.cloudhearing.android.lib_common.network.dataSource.racedetail.Event
import com.cloudhearing.android.lib_common.network.repository.CompetitionRepository
import com.cloudhearing.android.lib_common.utils.launchRequest
import kotlinx.coroutines.flow.MutableStateFlow

class VideoListViewModel : BaseViewModel() {
    private val mCompetitionRepository: CompetitionRepository by lazy {
        CompetitionRepository()
    }
    val leagueFavoritesListStateFlow = MutableStateFlow(emptyList<Event>())
    val leagueFavoritesListStateFlowVideoPlayUrl = MutableStateFlow(emptyList<VideoPlayUrlEntity>())
    private val _toastFlowEvents = SharedFlowEvents<String>()
    fun getFavorite(matchInfoId: String) {
        launchRequest(
            isLoading = false, {
                mCompetitionRepository.getRaceDetail(matchInfoId)
            }, {
                if (it != null) {
                    leagueFavoritesListStateFlow.value = it.eventList
                }
            }, { errorCode, errorMsg ->
                _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
            }, {
                _toastFlowEvents.setEvent("${it.message}")
            }
        )
    }

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
}
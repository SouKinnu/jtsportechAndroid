package com.jtsportech.visport.android.videoplay

import com.cloudhearing.android.lib_base.base.BaseViewModel
import com.cloudhearing.android.lib_base.utils.SharedFlowEvents
import com.cloudhearing.android.lib_base.utils.setEvent
import com.cloudhearing.android.lib_common.network.dataSource.player.VideoPlayUrlEntity
import com.cloudhearing.android.lib_common.network.dataSource.racedetail.EventName
import com.cloudhearing.android.lib_common.network.repository.CompetitionRepository
import com.cloudhearing.android.lib_common.utils.launchRequest
import kotlinx.coroutines.flow.MutableStateFlow

class VideoPlayViewModel : BaseViewModel() {
    private lateinit var mlist: ArrayList<VideoPlayUrlEntity>
    private val mCompetitionRepository: CompetitionRepository by lazy {
        CompetitionRepository()
    }
    val leagueFavoritesListStateFlowEventName = MutableStateFlow(emptyList<EventName>())
    val leagueFavoritesListStateFlowAddFavorite = MutableStateFlow(emptyList<VideoPlayUrlEntity>())
    val leagueFavoritesListStateFlowCancelFavorite =
        MutableStateFlow(emptyList<VideoPlayUrlEntity>())

    private val _toastFlowEvents = SharedFlowEvents<String>()
    fun getRaceDetail(matchInfoId: String) {
        launchRequest(
            isLoading = false, {
                mCompetitionRepository.getRaceDetail(matchInfoId)
            }, {
                if (it != null) {
                    leagueFavoritesListStateFlowEventName.value = it.eventNameList
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
        mlist = ArrayList()
        launchRequest(
            isLoading = false, {
                mCompetitionRepository.getAddFavorite(eventName, favoriteType, matchInfoId)
            }, {
                if (it != null) {
                    mlist.addAll(listOf(it))
                    leagueFavoritesListStateFlowAddFavorite.value = mlist
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
                    mlist.addAll(listOf(it))
                    leagueFavoritesListStateFlowCancelFavorite.value = mlist
                }
            }, { errorCode, errorMsg ->
                _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
            }, {
                _toastFlowEvents.setEvent("${it.message}")
            }
        )
    }
}
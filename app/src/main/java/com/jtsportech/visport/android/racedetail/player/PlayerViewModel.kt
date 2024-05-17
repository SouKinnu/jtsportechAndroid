package com.jtsportech.visport.android.racedetail.player

import com.cloudhearing.android.lib_base.base.BaseViewModel
import com.cloudhearing.android.lib_base.utils.SharedFlowEvents
import com.cloudhearing.android.lib_base.utils.setEvent
import com.cloudhearing.android.lib_common.network.dataSource.racedetail.MatchInfo
import com.cloudhearing.android.lib_common.network.dataSource.racedetail.Team1Player
import com.cloudhearing.android.lib_common.network.repository.CompetitionRepository
import com.cloudhearing.android.lib_common.utils.launchRequest
import kotlinx.coroutines.flow.MutableStateFlow

class PlayerViewModel : BaseViewModel() {
    private val mCompetitionRepository: CompetitionRepository by lazy {
        CompetitionRepository()
    }
    val leagueFavoritesListStateFlow = MutableStateFlow(emptyList<Team1Player>())
    val leagueFavoritesListStateFlow2 = MutableStateFlow(emptyList<Team1Player>())
    val leagueFavoritesListStateFlowMatchInfo = MutableStateFlow(emptyList<MatchInfo>())
    private val _toastFlowEvents = SharedFlowEvents<String>()
    fun getRaceDetail(matchInfoId: String) {
        launchRequest(
            isLoading = false, {
                mCompetitionRepository.getRaceDetail(matchInfoId)
            }, {
                if (it != null) {
                    leagueFavoritesListStateFlow.value = it.team1PlayerList
                    leagueFavoritesListStateFlow2.value = it.team2PlayerList
                }
            }, { errorCode, errorMsg ->
                _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
            }, {
                _toastFlowEvents.setEvent("${it.message}")
            }
        )
    }

    fun getRaceDetailMatchInfo(matchInfoId: String) {
        launchRequest(
            isLoading = false, {
                mCompetitionRepository.getRaceDetail(matchInfoId)
            }, {
                if (it != null) {
                    leagueFavoritesListStateFlowMatchInfo.value = listOf(it.matchInfo)
                }
            }, { errorCode, errorMsg ->
                _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
            }, {
                _toastFlowEvents.setEvent("${it.message}")
            }
        )
    }
}
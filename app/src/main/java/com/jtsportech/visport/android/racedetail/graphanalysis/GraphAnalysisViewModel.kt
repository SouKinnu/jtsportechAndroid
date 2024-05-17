package com.jtsportech.visport.android.racedetail.graphanalysis

import com.cloudhearing.android.lib_base.base.BaseViewModel
import com.cloudhearing.android.lib_base.utils.SharedFlowEvents
import com.cloudhearing.android.lib_base.utils.setEvent
import com.cloudhearing.android.lib_common.network.dataSource.racedetail.ChartData
import com.cloudhearing.android.lib_common.network.dataSource.racedetail.MatchInfo
import com.cloudhearing.android.lib_common.network.dataSource.racedetail.RaceDetailEntity
import com.cloudhearing.android.lib_common.network.repository.CompetitionRepository
import com.cloudhearing.android.lib_common.utils.launchRequest
import com.jtsportech.visport.android.racedetail.RaceDetailActivity
import kotlinx.coroutines.flow.MutableStateFlow

class GraphAnalysisViewModel : BaseViewModel() {
    private val mCompetitionRepository: CompetitionRepository by lazy {
        CompetitionRepository()
    }
    val leagueFavoritesListStateFlow = MutableStateFlow(emptyList<RaceDetailEntity>())
    val leagueFavoritesListStateMatchInfo = MutableStateFlow(emptyList<String>())
    private val _toastFlowEvents = SharedFlowEvents<String>()
    fun getRaceDetail(matchInfoId: String) {
        launchRequest(
            isLoading = false, {
                mCompetitionRepository.getRaceDetail(matchInfoId)
            }, {
                if (it != null) {
                    leagueFavoritesListStateFlow.value = listOf(it)
                    leagueFavoritesListStateMatchInfo.value =
                        listOf(it.matchInfo.previewImageFilePath)
                }
            }, { errorCode, errorMsg ->
                _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
            }, {
                _toastFlowEvents.setEvent("${it.message}")
            }
        )
    }
}
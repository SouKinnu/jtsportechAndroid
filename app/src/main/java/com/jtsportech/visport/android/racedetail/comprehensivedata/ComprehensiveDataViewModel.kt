package com.jtsportech.visport.android.racedetail.comprehensivedata
import com.cloudhearing.android.lib_base.base.BaseViewModel
import com.cloudhearing.android.lib_base.utils.SharedFlowEvents
import com.cloudhearing.android.lib_base.utils.setEvent
import com.cloudhearing.android.lib_common.network.dataSource.racedetail.EventData
import com.cloudhearing.android.lib_common.network.repository.CompetitionRepository
import com.cloudhearing.android.lib_common.utils.launchRequest
import kotlinx.coroutines.flow.MutableStateFlow

class ComprehensiveDataViewModel :BaseViewModel(){
    private val mCompetitionRepository: CompetitionRepository by lazy {
        CompetitionRepository()
    }
    val leagueFavoritesListStateFlow = MutableStateFlow(emptyList<EventData>())
    private val _toastFlowEvents = SharedFlowEvents<String>()
    fun getRaceDetail(matchInfoId:String) {
        launchRequest(
            isLoading = false, {
                mCompetitionRepository.getRaceDetail(matchInfoId)
            }, {
                if (it != null) {
                    leagueFavoritesListStateFlow.value =it.eventDataList
                }
            }, { errorCode, errorMsg ->
                _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
            }, {
                _toastFlowEvents.setEvent("${it.message}")
            }
        )
    }
}
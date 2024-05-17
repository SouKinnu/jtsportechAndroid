package com.jtsportech.visport.android.mine.recentlyWatched.train

import androidx.lifecycle.ViewModel
import com.cloudhearing.android.lib_base.base.BaseViewModel
import com.cloudhearing.android.lib_base.utils.SharedFlowEvents
import com.cloudhearing.android.lib_base.utils.setEvent
import com.cloudhearing.android.lib_common.network.dataSource.home.competition.Competition
import com.cloudhearing.android.lib_common.network.dataSource.home.competition.CompetitionList
import com.cloudhearing.android.lib_common.network.dataSource.mine.TeamMembers
import com.cloudhearing.android.lib_common.network.repository.CompetitionRepository
import com.cloudhearing.android.lib_common.utils.launchRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class TrainWatchHistoryViewModel : BaseViewModel() {

    private val mCompetitionRepository: CompetitionRepository by lazy {
        CompetitionRepository()
    }

    private val _trainWatchHistoryListStateFlow = MutableStateFlow(emptyList<Competition>())

    val trainWatchHistoryListStateFlow = _trainWatchHistoryListStateFlow.asStateFlow()

    private val _toastFlowEvents = SharedFlowEvents<String>()

    val toastFlowEvents = _toastFlowEvents.asSharedFlow()

    override fun onStart() {
        super.onStart()
        getWatchHistory()
    }

    override fun onStop() {
        super.onStop()
    }

    private fun getWatchHistory() {
        launchRequest(
            isLoading = false, {
                mCompetitionRepository.getTrainWatchHistory(pageNum = 1, pageSize = 1000)
            }, {
                if (it != null) {
                    _trainWatchHistoryListStateFlow.value = it.list
                }

            }, { errorCode, errorMsg ->
                _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
            }, {
                _toastFlowEvents.setEvent("${it.message}")
            }
        )
    }

}
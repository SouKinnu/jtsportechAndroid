package com.jtsportech.visport.android.playerdetail.playerevents

import com.cloudhearing.android.lib_base.base.BaseViewModel
import com.cloudhearing.android.lib_base.utils.SharedFlowEvents
import com.cloudhearing.android.lib_base.utils.setEvent
import com.cloudhearing.android.lib_common.network.dataSource.player.playerevents.PlayerEventsEntity
import com.cloudhearing.android.lib_common.network.repository.CompetitionRepository
import com.cloudhearing.android.lib_common.utils.launchRequest
import kotlinx.coroutines.flow.MutableStateFlow

class PlayerEventsViewModel : BaseViewModel() {
    private val mCompetitionRepository: CompetitionRepository by lazy {
        CompetitionRepository()
    }
    val leagueFavoritesListStateFlow = MutableStateFlow(emptyList<PlayerEventsEntity>())
    private val _toastFlowEvents = SharedFlowEvents<String>()
    fun getPlayerEvents(frontUserId: String, matchType: String) {
        launchRequest(
            isLoading = false, {
                mCompetitionRepository.getPlayerEvents(frontUserId, matchType)
            }, {
                if (it != null) {
                    leagueFavoritesListStateFlow.value = listOf(it)
                }
            }, { errorCode, errorMsg ->
                _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
            }, {
                _toastFlowEvents.setEvent("${it.message}")
            }
        )
    }
}
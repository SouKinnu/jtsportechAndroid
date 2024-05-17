package com.jtsportech.visport.android.playerdetail.playervideo

import com.cloudhearing.android.lib_base.base.BaseViewModel
import com.cloudhearing.android.lib_base.utils.SharedFlowEvents
import com.cloudhearing.android.lib_base.utils.setEvent
import com.cloudhearing.android.lib_common.network.dataSource.player.PlayerEntity
import com.cloudhearing.android.lib_common.network.repository.CompetitionRepository
import com.cloudhearing.android.lib_common.utils.launchRequest
import kotlinx.coroutines.flow.MutableStateFlow

class PlayerVideoViewModel : BaseViewModel() {
    private val mCompetitionRepository: CompetitionRepository by lazy {
        CompetitionRepository()
    }
    val leagueFavoritesListStateFlow = MutableStateFlow(emptyList<PlayerEntity>())
    private val _toastFlowEvents = SharedFlowEvents<String>()
    fun getPlayerVideo(frontUserId: String) {
        launchRequest(
            isLoading = false, {
                mCompetitionRepository.getPlayerVideo(frontUserId)
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
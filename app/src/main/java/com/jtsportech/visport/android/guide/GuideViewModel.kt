package com.jtsportech.visport.android.guide

import com.cloudhearing.android.lib_base.base.BaseViewModel
import com.cloudhearing.android.lib_base.utils.SharedFlowEvents
import com.cloudhearing.android.lib_base.utils.setEvent
import com.cloudhearing.android.lib_common.network.dataSource.welcome.Welcome
import com.cloudhearing.android.lib_common.network.dataSource.welcome.WelcomeItem
import com.cloudhearing.android.lib_common.network.repository.CompetitionRepository
import com.cloudhearing.android.lib_common.utils.launchRequest
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Author: BenChen
 * Date: 2023/12/25 11:26
 * Email:chenxiaobin@cloudhearing.cn
 */
class GuideViewModel : BaseViewModel() {
    private val mCompetitionRepository: CompetitionRepository by lazy {
        CompetitionRepository()
    }
    val leagueFavoritesListStateFlow = MutableStateFlow(emptyList<WelcomeItem>())
    private val _toastFlowEvents = SharedFlowEvents<String>()
    fun getWelcomeImg() {
        launchRequest(
            isLoading = false, {
                mCompetitionRepository.getWelcomeImg()
            }, {
                if (it != null) {
                    leagueFavoritesListStateFlow.value = it
                }
            }, { errorCode, errorMsg ->
                _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
            }, {
                _toastFlowEvents.setEvent("${it.message}")
            }
        )
    }
}
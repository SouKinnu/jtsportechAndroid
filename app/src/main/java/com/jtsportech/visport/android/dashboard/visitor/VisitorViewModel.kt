package com.jtsportech.visport.android.dashboard.visitor

import com.cloudhearing.android.lib_base.base.BaseViewModel
import com.cloudhearing.android.lib_common.network.dataSource.home.competition.Competition
import com.cloudhearing.android.lib_common.network.dataSource.homebanner.EventsItem
import com.cloudhearing.android.lib_common.network.repository.CompetitionRepository
import com.cloudhearing.android.lib_common.utils.launchRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class VisitorViewModel : BaseViewModel() {

    private val mCompetitionRepository: CompetitionRepository by lazy {
        CompetitionRepository()
    }

    private val _trainListStateFlow =
        MutableStateFlow(emptyList<Competition>())

    val trainListStateFlow = _trainListStateFlow.asStateFlow()

    private val _bannerListStateFlow =
        MutableStateFlow(emptyList<EventsItem>())

    val bannerListStateFlow = _bannerListStateFlow.asStateFlow()


    override fun onStart() {
        super.onStart()
        getBannerList()
        getTrainList()
    }

    private fun getTrainList(pageNum: Int = 1, pageSize: Int = 20) {
        launchRequest(isLoading = false, {
            mCompetitionRepository.getTrainHomeList(pageNum, pageSize)
        }, {
            if (it != null) {
                _trainListStateFlow.value = it.list
            }
        }, { errorCode, errorMsg ->

        }, {

        })
    }

    private fun getBannerList() {
        launchRequest(isLoading = false, {
            mCompetitionRepository.getMatchBannerList()
        }, {
            if (it?.eventsItemList != null) {
                _bannerListStateFlow.value = it.eventsItemList ?: emptyList()
            }
        }, { errorCode, errorMsg ->

        }, {

        })
    }

}
package com.jtsportech.visport.android.dashboard.home.competition

import com.cloudhearing.android.lib_base.base.BaseViewModel
import com.cloudhearing.android.lib_common.network.dataSource.home.competition.Competition
import com.cloudhearing.android.lib_common.network.dataSource.homebanner.EventsItem
import com.cloudhearing.android.lib_common.network.repository.CompetitionRepository
import com.cloudhearing.android.lib_common.utils.launchRequest
import com.jtsportech.visport.android.utils.date.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

class CompetitionViewModel : BaseViewModel() {

    private val mCompetitionRepository: CompetitionRepository by lazy {
        CompetitionRepository()
    }

    private val _competitionListStateFlow =
        MutableStateFlow(emptyList<Competition>())

    val competitionListStateFlow = _competitionListStateFlow.asStateFlow()

    private val _bannerListStateFlow =
        MutableStateFlow(emptyList<EventsItem>())

    val bannerListStateFlow = _bannerListStateFlow.asStateFlow()

    override fun onStart() {
        super.onStart()
        getBannerList()
        getCompetitionList()
    }


    //    private fun getCompetitionList(pageNum: Int = 1, pageSize: Int = 20) {
//        launchRequest(isLoading = false, {
//            mCompetitionRepository.getMatchHomeList(pageNum, pageSize)
//        }, {
//            if (it != null) {
//                _competitionListStateFlow.value = it.list
//            }
//        }, { errorCode, errorMsg ->
//
//        }, {
//
//        })
//    }
    private fun getCompetitionList(
        pageNum: Int = 1,
        pageSize: Int = 20
    ) = launchUi {

        launchRequest(isLoading = false, {
            mCompetitionRepository.getLeagueHomeList(
                "",
                "MATCH",
                pageNum,
                pageSize,
                name = "",
                leagueId = ""
            )
        }, {
            if (it != null) {
                Timber.d("数据 ${it.list}")
                _competitionListStateFlow.value = it.list
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
                findBelongBannerList(it.eventsItemList, "MATCH")
            }
        }, { errorCode, errorMsg ->

        }, {

        })
    }

    private fun findBelongBannerList(list: List<EventsItem>?, matchType: String) = launchUi {
        if (list.isNullOrEmpty()) {
            return@launchUi
        }
        val banners = ArrayList<EventsItem>()
        list.forEach {
            if (it.matchType == matchType)
                banners.add(it)
        }
        _bannerListStateFlow.value = banners
    }

}
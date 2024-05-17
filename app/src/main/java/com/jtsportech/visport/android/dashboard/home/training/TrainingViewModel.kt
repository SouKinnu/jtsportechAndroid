package com.jtsportech.visport.android.dashboard.home.training

import com.cloudhearing.android.lib_base.base.BaseViewModel
import com.cloudhearing.android.lib_common.network.dataSource.home.competition.Competition
import com.cloudhearing.android.lib_common.network.dataSource.homebanner.EventsItem
import com.cloudhearing.android.lib_common.network.repository.CompetitionRepository
import com.cloudhearing.android.lib_common.utils.launchRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

class TrainingViewModel : BaseViewModel() {


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

//    private fun getTrainList(pageNum: Int = 1, pageSize: Int = 20) {
//        launchRequest(isLoading = false, {
//            mCompetitionRepository.getTrainHomeList(pageNum, pageSize)
//        }, {
//            if (it != null) {
//                _trainListStateFlow.value = it.list
//            }
//        }, { errorCode, errorMsg ->
//
//        }, {
//
//        })
//    }

    private fun getTrainList(
        pageNum: Int = 1,
        pageSize: Int = 20
    ) = launchUi {

        launchRequest(isLoading = false, {
            mCompetitionRepository.getLeagueHomeList(
                "",
                "TRAIN",
                pageNum,
                pageSize,
                name = "",
                leagueId = ""
            )
        }, {
            if (it != null) {
                Timber.d("数据 ${it.list}")
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
                findBelongBannerList(it.eventsItemList, "TRAIN")
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
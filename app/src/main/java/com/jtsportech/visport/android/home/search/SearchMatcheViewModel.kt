package com.jtsportech.visport.android.home.search

import com.cloudhearing.android.lib_base.base.BaseViewModel
import com.cloudhearing.android.lib_base.utils.SharedFlowEvents
import com.cloudhearing.android.lib_base.utils.context.AppProvider
import com.cloudhearing.android.lib_base.utils.setEvent
import com.cloudhearing.android.lib_common.network.dataSource.home.competition.Competition
import com.cloudhearing.android.lib_common.network.repository.CompetitionRepository
import com.cloudhearing.android.lib_common.utils.launchRequest
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.dataSource.home.search.SearchEntity
import com.jtsportech.visport.android.dataSource.home.search.SearchFilterEntity
import com.jtsportech.visport.android.utils.PreferencesBusinessHelper
import com.jtsportech.visport.android.utils.findSearchFilterOption
import com.jtsportech.visport.android.utils.getSearchFilterData
import com.jtsportech.visport.android.utils.handleUpdateSearchFilterData
import com.jtsportech.visport.android.utils.handleUpdateSearchFilterEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import timber.log.Timber

class SearchMatcheViewModel : BaseViewModel() {

    private val mCompetitionRepository: CompetitionRepository by lazy {
        CompetitionRepository()
    }


    private val _competitionSearchListStateFlow = MutableStateFlow(emptyList<SearchEntity>())

    val competitionSearchListStateFlow = _competitionSearchListStateFlow.asStateFlow()

    private val _competitionListStateFlow = MutableStateFlow(emptyList<Competition>())

    val competitionListStateFlow = _competitionListStateFlow.asStateFlow()

    private val _searchFilterOptionListStateFlow = MutableStateFlow(emptyList<SearchFilterEntity>())

    val searchFilterOptionListStateFlow = _searchFilterOptionListStateFlow.asStateFlow()

    private val _eventListDataFlow = MutableStateFlow(emptyList<Competition>())

    val eventListDataFlow = _eventListDataFlow.asStateFlow()

    private val _toastFlowEvents = SharedFlowEvents<String>()

    val toastFlowEvents = _toastFlowEvents.asSharedFlow()


    var misUnfold: Boolean = false
        private set

    var mSearchListSize: Int = 0
        private set

    override fun onCreate() {
        super.onCreate()
        getCompetitionSearchList()
        getHomeDialogData()
    }

    override fun onStart() {
        super.onStart()

    }


    /**
     * 获取比赛搜索记录
     *
     * @param isUnfold 是否全部显示，默认显示 5 条
     */
    fun getCompetitionSearchList(isUnfold: Boolean = misUnfold) = launchUi {
        withContext(coroutineDispatchers.io) {
            PreferencesBusinessHelper.getCompetitionSearchList()
        }.let {
            try {
                mSearchListSize = it.size
                val newList = if (isUnfold) it else {
                    if (it.size > 5) it.slice(IntRange(0, 4)) else it
                }
                Timber.d("列表数量 ${newList.size}")

                _competitionSearchListStateFlow.value = newList
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addCompetitionSearchRecord(content: String) = launchUi {
        search(content)

        withContext(coroutineDispatchers.io) {
            PreferencesBusinessHelper.addCompetitionSearchRecord(content)
        }.let {
            getCompetitionSearchList()
        }
    }

    fun deleteCompetitionSearchRecord(position: Int) = launchUi {
        try {
            val newList = _competitionSearchListStateFlow.value.toMutableList()
            newList.removeAt(position)

            Timber.d("删除后的大小 ${newList.size}")

            withContext(coroutineDispatchers.io) {
                PreferencesBusinessHelper.deleteCompetitionSearch(newList)
            }.let {
                if (misUnfold) {
                    if (newList.size < 5) {
                        misUnfold = false
                    }
                }

                getCompetitionSearchList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun changeCompetitionSearchRecord() = launchIo {
        // 展开情况下，需要清空搜索历史列表
        // 收起状态下，需要展开搜索历史列表
        if (misUnfold) {
            PreferencesBusinessHelper.deleteCompetitionSearchList()
        } else {
            misUnfold = true
        }

        getCompetitionSearchList()
    }

    fun search(content: String, matchType: String = "") {
        launchRequest(isLoading = true, {
            mCompetitionRepository.getSearchList(name = content, matchType = matchType)
        }, {
            if (it != null) {
                _competitionListStateFlow.value = it.list

                if (it.list.isEmpty()) {
                    _toastFlowEvents.setEvent(
                        "${
                            AppProvider.get()
                                .getString(R.string.search_no_relevant_content_was_found)
                        }"
                    )
                }
            }
        }, { errorCode, errorMsg ->
            _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
        }, {
            _toastFlowEvents.setEvent("${it.message}")
        })
    }

    fun filterSearch(content: String) {
        val searchFilterOptionList = _searchFilterOptionListStateFlow.value

        launchRequest(isLoading = true, {
            val option = findSearchFilterOption(searchFilterOptionList)

            val publishStartTime = option.first.first
            val publishEndTime = option.first.second
            val durationFrom = option.second.first
            val durationTo = option.second.second
            val matchType = option.third.first
            val leagueId = option.third.second


            mCompetitionRepository.getSearchList(
                name = content,
                publishStartTime = publishStartTime,
                publishEndTime = publishEndTime,
                durationFrom = durationFrom,
                durationTo = durationTo,
                matchType = matchType,
                leagueId = leagueId
            )
        }, {
            if (it != null) {
                _competitionListStateFlow.value = it.list

                if (it.list.isEmpty()) {
                    _toastFlowEvents.setEvent(
                        "${
                            AppProvider.get()
                                .getString(R.string.search_no_relevant_content_was_found)
                        }"
                    )
                }
            }
        }, { errorCode, errorMsg ->
            _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
        }, {
            _toastFlowEvents.setEvent("${it.message}")
        })
    }

    fun getSearchFilterOptionList(isEventMore: Boolean) = launchUi {
        withContext(coroutineDispatchers.io) {
            getSearchFilterData(isEventMore)
        }.let {
            _searchFilterOptionListStateFlow.value = it
        }
    }

    fun updateSearchFilterOptionList(entity: SearchFilterEntity) = launchUi {
        val searchFilterOptionList = _searchFilterOptionListStateFlow.value
        withContext(coroutineDispatchers.io) {
            handleUpdateSearchFilterData(searchFilterOptionList, entity)
        }.let {
            _searchFilterOptionListStateFlow.value = it
        }
    }

    fun getHomeDialogData() {
        launchRequest(isLoading = false, {
            mCompetitionRepository.getLeagueList()
        }, {
            if (!it.isNullOrEmpty()) {
                _eventListDataFlow.value = it
            }

            getSearchFilterOptionList(!it.isNullOrEmpty())
        }, { errorCode, errorMsg ->

        }, {

        })

    }

    fun updateSearchFilterEvent(leagueId: String, eventName: String) = launchUi {
        val searchFilterOptionList = _searchFilterOptionListStateFlow.value
        withContext(coroutineDispatchers.io) {
            handleUpdateSearchFilterEvent(searchFilterOptionList, leagueId, eventName)
        }.let {
            _searchFilterOptionListStateFlow.value = it
        }
    }
}
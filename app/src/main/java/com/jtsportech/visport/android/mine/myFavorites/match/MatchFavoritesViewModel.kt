package com.jtsportech.visport.android.mine.myFavorites.match

import com.cloudhearing.android.lib_base.base.BaseViewModel
import com.cloudhearing.android.lib_base.utils.SharedFlowEvents
import com.cloudhearing.android.lib_base.utils.setEvent
import com.cloudhearing.android.lib_common.network.dataSource.home.competition.Competition
import com.cloudhearing.android.lib_common.network.repository.CompetitionRepository
import com.cloudhearing.android.lib_common.utils.launchRequest
import com.jtsportech.visport.android.mine.myFavorites.IEditModeViewModel
import com.jtsportech.visport.android.utils.checkAllStateCompetitionList
import com.jtsportech.visport.android.utils.toggleEditModeCompetitionList
import com.jtsportech.visport.android.utils.updateSelectedStateCompetitionList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import timber.log.Timber

class MatchFavoritesViewModel : BaseViewModel(), IEditModeViewModel {

    private val mCompetitionRepository: CompetitionRepository by lazy {
        CompetitionRepository()
    }

    private val _matchFavoritesListStateFlow = MutableStateFlow(emptyList<Competition>())

    val matchFavoritesListStateFlow = _matchFavoritesListStateFlow.asStateFlow()

    private val _toastFlowEvents = SharedFlowEvents<String>()

    val toastFlowEvents = _toastFlowEvents.asSharedFlow()

    override fun onStart() {
        super.onStart()
        getFavorites()
    }

    override fun onStop() {
        super.onStop()
    }

     fun getFavorites() {
        launchRequest(
            isLoading = false, {
                mCompetitionRepository.getMatchFavorite(pageNum = 1, pageSize = 1000)
            }, {
                if (it != null) {
                    _matchFavoritesListStateFlow.value = it.list
                }

            }, { errorCode, errorMsg ->
                _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
            }, {
                _toastFlowEvents.setEvent("${it.message}")
            }
        )
    }

    override fun switchEditMode(isEditMode: Boolean) = launchUi {
        val competitions = _matchFavoritesListStateFlow.value

        withContext(coroutineDispatchers.io) {
            toggleEditModeCompetitionList(isEditMode, competitions)
        }.let {
            Timber.d("change list ${it}")
            _matchFavoritesListStateFlow.value = it
        }
    }

    override fun updateSelectedState(competition: Competition) = launchUi {
        val competitions = _matchFavoritesListStateFlow.value

        withContext(coroutineDispatchers.io) {
            updateSelectedStateCompetitionList(competition, competitions)
        }.let {
            _matchFavoritesListStateFlow.value = it
        }
    }

    override fun checkAll(isCheckAll: Boolean) = launchUi {
        val competitions = _matchFavoritesListStateFlow.value

        withContext(coroutineDispatchers.io) {
            checkAllStateCompetitionList(isCheckAll, competitions)
        }.let {
            _matchFavoritesListStateFlow.value = it
        }
    }


}
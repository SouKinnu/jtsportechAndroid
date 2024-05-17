package com.jtsportech.visport.android.mine.myFavorites

import androidx.lifecycle.ViewModel
import com.cloudhearing.android.lib_base.base.BaseViewModel
import com.cloudhearing.android.lib_base.utils.SharedFlowEvents
import com.cloudhearing.android.lib_base.utils.setEvent
import com.cloudhearing.android.lib_common.network.repository.CompetitionRepository
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext

class MyFavoritesViewModel : BaseViewModel() {


    private val mCompetitionRepository: CompetitionRepository by lazy {
        CompetitionRepository()
    }

    private val _updateFavoriteListFlowEvents = SharedFlowEvents<Unit>()

    val updateFavoriteListFlowEvents = _updateFavoriteListFlowEvents.asSharedFlow()

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    fun unfavorite(ids: List<String>) = launchUi {
        withContext(coroutineDispatchers.io) {
            mCompetitionRepository.deleteFavorite(ids.joinToString(","))
        }.let {
            _updateFavoriteListFlowEvents.setEvent(Unit)
        }
    }
}
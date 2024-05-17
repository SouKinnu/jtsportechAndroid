package com.jtsportech.visport.android.playerdetail.simpleplay

import androidx.lifecycle.MutableLiveData
import com.cloudhearing.android.lib_base.base.BaseViewModel
import com.cloudhearing.android.lib_base.utils.SharedFlowEvents
import com.cloudhearing.android.lib_base.utils.setEvent
import com.cloudhearing.android.lib_common.network.dataSource.player.VideoPlayUrlEntity
import com.cloudhearing.android.lib_common.network.repository.CompetitionRepository
import com.cloudhearing.android.lib_common.utils.launchRequest
import kotlinx.coroutines.flow.MutableStateFlow

class SimplePlayViewModel : BaseViewModel() {
    private val mCompetitionRepository: CompetitionRepository by lazy {
        CompetitionRepository()
    }
    val leagueFavoritesListLiveDataVideoPlayUrl = MutableLiveData<VideoPlayUrlEntity>()
    private val _toastFlowEvents = SharedFlowEvents<String>()
    fun getVideoPlayUrl(videoSourceId: String, videoSourceType: String) {
        launchRequest(
            isLoading = false, {
                mCompetitionRepository.getVideoPlayUrl(videoSourceId, videoSourceType)
            }, {
                if (it != null) {
                    leagueFavoritesListLiveDataVideoPlayUrl.postValue(it)
                }
            }, { errorCode, errorMsg ->
                _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
            }, {
                _toastFlowEvents.setEvent("${it.message}")
            }
        )
    }
}
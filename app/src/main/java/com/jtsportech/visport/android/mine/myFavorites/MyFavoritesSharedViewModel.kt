package com.jtsportech.visport.android.mine.myFavorites

import com.cloudhearing.android.lib_base.base.BaseViewModel
import com.cloudhearing.android.lib_base.utils.SharedFlowEvents
import com.cloudhearing.android.lib_base.utils.setEvent
import com.cloudhearing.android.lib_common.network.dataSource.home.competition.Competition
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Author: BenChen
 * Date: 2024/03/04 10:51
 * Email:chenxiaobin@cloudhearing.cn
 */
class MyFavoritesSharedViewModel : BaseViewModel() {

    private val _checkAllRadioFlowEvents = SharedFlowEvents<Boolean>()

    val checkAllRadioFlowEvents = _checkAllRadioFlowEvents.asSharedFlow()

    private val _selectedListFlowEvents = SharedFlowEvents<List<String>>()

    val selectedListFlowEvents = _selectedListFlowEvents.asSharedFlow()

    fun examineCheckAllRadioState(competitions: List<Competition>) = launchUi {
        withContext(coroutineDispatchers.io) {
            competitions.all {
                it.isSelected
            }
        }.let {
            Timber.d("共享发射")
            _checkAllRadioFlowEvents.setEvent(it)
        }
    }

    fun obtainUnFavorites(competitions: List<Competition>) = launchUi {
        withContext(coroutineDispatchers.io) {
            competitions.filter {
                it.isSelected
            }.mapNotNull {
                it.favoriteId
            }
        }.let {
            _selectedListFlowEvents.setEvent(it)
        }
    }
}
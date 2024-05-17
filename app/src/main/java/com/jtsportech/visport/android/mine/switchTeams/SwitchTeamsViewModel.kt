package com.jtsportech.visport.android.mine.switchTeams

import androidx.lifecycle.ViewModel
import com.cloudhearing.android.lib_base.base.BaseViewModel
import com.cloudhearing.android.lib_base.utils.SharedFlowEvents
import com.cloudhearing.android.lib_base.utils.context.AppProvider
import com.cloudhearing.android.lib_base.utils.setEvent
import com.cloudhearing.android.lib_common.network.dataSource.mine.Team
import com.cloudhearing.android.lib_common.network.dataSource.mine.TeamMembers
import com.cloudhearing.android.lib_common.network.repository.UserRepository
import com.cloudhearing.android.lib_common.utils.launchRequest
import com.cloudhearing.android.lib_common.utils.mmkv.PreferencesWrapper
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.dataSource.mine.switchTeams.SwitchTeamEntity
import com.jtsportech.visport.android.utils.PreferencesBusinessHelper
import com.jtsportech.visport.android.utils.getSwitchTeamByTeam
import com.jtsportech.visport.android.utils.setSelectedTeamList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class SwitchTeamsViewModel : BaseViewModel() {

    private val mUserRepository: UserRepository by lazy {
        UserRepository()
    }

    private val _teamListStateFlow = MutableStateFlow(emptyList<SwitchTeamEntity>())

    val teamListStateFlow = _teamListStateFlow.asStateFlow()

    private val _signOutNoticeEvents = SharedFlowEvents<Unit>()

    val signOutNoticeEvents = _signOutNoticeEvents.asSharedFlow()

    private val _toastFlowEvents = SharedFlowEvents<String>()

    val toastFlowEvents = _toastFlowEvents.asSharedFlow()

    val currentOrganizationId: String
        get() = PreferencesWrapper.get().getCurrentOrganizationId()

    val currentGroupId: String
        get() = PreferencesWrapper.get().getGroupId()

    override fun onStart() {
        super.onStart()
        getTeamList()
    }

    override fun onStop() {
        super.onStop()
    }

    private fun getTeamList() {
        launchRequest(
            isLoading = false, {
                mUserRepository.getTeam()
            }, {
                if (it != null) {
//                    _teamListStateFlow.value =
//                        setSelectedTeamList(it, PreferencesWrapper.get().getCurrentOrganizationId())
                    _teamListStateFlow.value =
                        getSwitchTeamByTeam(
                            it,
                            currentOrganizationId,
                            currentGroupId
                        )
                }

            }, { errorCode, errorMsg ->
                _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
            }, {
                _toastFlowEvents.setEvent("${it.message}")
            }
        )
    }

    fun changeOrganization(
        organizationId: String,
        groupId: String,
        isSameTeam: Boolean = false
    ) {
        launchRequest(
            isLoading = true, {
                mUserRepository.changeOrganization(organizationId, groupId)
            }, { token ->
                if (!token.isNullOrEmpty()) {
                    PreferencesWrapper.get().setAccessToken(token)
                }

                if (isSameTeam) {
                    _toastFlowEvents.setEvent(
                        AppProvider.get()
                            .getString(R.string.switchTeams_the_switchover_was_successful)
                    )
                    getUserInfo()
                } else {
                    _signOutNoticeEvents.setEvent(Unit)
                }
            }, { errorCode, errorMsg ->
                _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
            }, {
                _toastFlowEvents.setEvent("${it.message}")
            }
        )
    }

    private fun getUserInfo() {
        launchRequest(
            isLoading = false, {
                mUserRepository.getUserInfo()
            }, {
                if (it != null) {
                    PreferencesBusinessHelper.saveUserInfo(it)
                    getTeamList()
                }

            }, { errorCode, errorMsg ->
                _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
            }, {
                _toastFlowEvents.setEvent("${it.message}")
            }
        )
    }

//    private suspend fun updateSwitchTeamEntityList(switchTeamEntity: SwitchTeamEntity) {
//        val teamList = _teamListStateFlow.value
//        withContext(coroutineDispatchers.io) {
//
//        }
//    }

}
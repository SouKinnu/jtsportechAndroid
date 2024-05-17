package com.jtsportech.visport.android.dashboard.parents

import com.cloudhearing.android.lib_base.base.BaseViewModel
import com.cloudhearing.android.lib_common.network.dataSource.mine.User
import com.cloudhearing.android.lib_common.network.dataSource.player.PlayerEntityItem
import com.cloudhearing.android.lib_common.network.repository.CompetitionRepository
import com.cloudhearing.android.lib_common.network.repository.UserRepository
import com.cloudhearing.android.lib_common.utils.launchRequest
import com.cloudhearing.android.lib_common.utils.mmkv.PreferencesWrapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ParentsViewModel : BaseViewModel() {

    private val mCompetitionRepository: CompetitionRepository by lazy {
        CompetitionRepository()
    }

    private val mUserRepository: UserRepository by lazy {
        UserRepository()
    }

    private val _videoHightlightStateFlow =
        MutableStateFlow(emptyList<PlayerEntityItem>())

    val videoHightlightStateFlow = _videoHightlightStateFlow.asStateFlow()

    private val _userInfoStateFlow =
        MutableStateFlow(User())

    val userInfoStateFlow = _userInfoStateFlow.asStateFlow()

    private val inviterFrontUserId = PreferencesWrapper.get().getInviterFrontUserId()

    override fun onStart() {
        super.onStart()
        getUserInfo()
        getVideoHightlight()
    }

    private fun getUserInfo() {
        launchRequest(isLoading = false, {
            mUserRepository.getUserInfo(inviterFrontUserId)
        }, {
            if (it != null) {
//                PreferencesWrapper.get()
//                    .setOrganizationName(it.orgRoleList?.get(0)?.organizationName.orEmpty())
                it.orgRoleList?.forEach { it1 ->
                    if (it1.organizationId == it.lastUsedOrganizationId) {
                        it1.organizationName?.let { it2 ->
                            PreferencesWrapper.get()
                                .setOrganizationName(it2)
                            return@forEach
                        }
                    }
                }
                _userInfoStateFlow.value = it
            }
        }, { errorCode, errorMsg ->

        }, {

        })
    }

    private fun getVideoHightlight() {

        launchRequest(isLoading = false, {
            mCompetitionRepository.getPlayerVideo(inviterFrontUserId)
        }, {
            if (it != null) {
                _videoHightlightStateFlow.value = it
            }
        }, { errorCode, errorMsg ->

        }, {

        })
    }
}
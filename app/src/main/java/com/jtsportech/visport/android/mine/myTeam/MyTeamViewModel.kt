package com.jtsportech.visport.android.mine.myTeam

import com.cloudhearing.android.lib_base.base.BaseViewModel
import com.cloudhearing.android.lib_base.utils.SharedFlowEvents
import com.cloudhearing.android.lib_base.utils.setEvent
import com.cloudhearing.android.lib_common.network.dataSource.mine.TeamMembers
import com.cloudhearing.android.lib_common.network.dataSource.mine.User
import com.cloudhearing.android.lib_common.network.repository.UserRepository
import com.cloudhearing.android.lib_common.utils.launchRequest
import com.cloudhearing.android.lib_common.utils.mmkv.PreferencesWrapper
import com.jtsportech.visport.android.dataSource.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class MyTeamViewModel : BaseViewModel() {

    private val mUserRepository: UserRepository by lazy {
        UserRepository()
    }

    private val _temaMemberListStateFlow = MutableStateFlow(emptyList<TeamMembers>())

    val temaMemberListStateFlow = _temaMemberListStateFlow.asStateFlow()

    private val _userStateFlow = MutableStateFlow(User())

    val userStateFlow = _userStateFlow.asStateFlow()

    private val _hasTeamStateFlow = MutableStateFlow(true)

    val hasTeamStateFlow = _hasTeamStateFlow.asStateFlow()

    val _organizationNameFlow = MutableStateFlow("")

    val organizationNameFlow = _organizationNameFlow.asStateFlow()

    private val _groupNameFlow = MutableStateFlow("")

    val groupNameFlow = _groupNameFlow.asStateFlow()

    private val _logoImageFilePathFlow = MutableStateFlow("")

    val logoImageFilePathFlow = _logoImageFilePathFlow.asStateFlow()

    private val _toastFlowEvents = SharedFlowEvents<String>()

    val toastFlowEvents = _toastFlowEvents.asSharedFlow()


    /**
     * 是否有人邀请
     */
    val hasInviterFrontUserId: Boolean
        get() = PreferencesWrapper.get().getInviterFrontUserId().isNotEmpty()

    /**
     * 角色
     */
    val role: String
        get() = PreferencesWrapper.get().getRole()

    override fun onStart() {
        super.onStart()
        getProfile()
        getTeamMembers()
    }

    override fun onStop() {
        super.onStop()

    }

    private fun getTeamMembers() {
        when (role) {
            UserRole.HEAD_COACH, UserRole.COACH, UserRole.MEMBER -> {
                launchRequest(
                    isLoading = false, {
                        val groupId = PreferencesWrapper.get().getGroupId()

                        if (groupId.isNotEmpty()) {
                            mUserRepository.getGroupMemberList(groupId)
                        } else {
                            mUserRepository.getTeamMembers()
                        }

                    }, {
                        if (it != null) {
                            _temaMemberListStateFlow.value = it
                        }

                    }, { errorCode, errorMsg ->
                        _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
                    }, {
                        _toastFlowEvents.setEvent("${it.message}")
                    }
                )
            }

            else -> {
                if (hasInviterFrontUserId) {
                    launchRequest(
                        isLoading = false, {
                            mUserRepository.getUserInfo(
                                PreferencesWrapper.get().getInviterFrontUserId()
                            )
                        }, {
                            if (it != null) {
                                _userStateFlow.value = it
                            }

                        }, { errorCode, errorMsg ->
                            _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
                        }, {
                            _toastFlowEvents.setEvent("${it.message}")
                        }
                    )
                } else {
                    _hasTeamStateFlow.value = false
                }
            }
        }
    }

    private fun getProfile() {
        PreferencesWrapper.get().apply {
            _logoImageFilePathFlow.value = getOrganizationLogoImageFilePath()
            _organizationNameFlow.value = getOrganizationName()
            _groupNameFlow.value = getGroupName()
        }
    }
}
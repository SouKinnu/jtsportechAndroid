package com.jtsportech.visport.android.dashboard.mine

import com.cloudhearing.android.lib_base.base.BaseViewModel
import com.cloudhearing.android.lib_base.utils.SharedFlowEvents
import com.cloudhearing.android.lib_base.utils.setEvent
import com.cloudhearing.android.lib_base.utils.setState
import com.cloudhearing.android.lib_common.network.dataSource.mine.UserInfo
import com.cloudhearing.android.lib_common.network.repository.UserRepository
import com.cloudhearing.android.lib_common.utils.launchRequest
import com.cloudhearing.android.lib_common.utils.mmkv.PreferencesWrapper
import com.jtsportech.visport.android.utils.PreferencesBusinessHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class MineViewModel : BaseViewModel() {

    private val mUserRepository: UserRepository by lazy {
        UserRepository()
    }

    private val _avatarImageFilePathFlow = MutableStateFlow("")

    val avatarImageFilePathFlow = _avatarImageFilePathFlow.asStateFlow()

    private val _nicknameFlow = MutableStateFlow("")

    val nicknameFlow = _nicknameFlow.asStateFlow()

    private val _organizationNameFlow = MutableStateFlow("")

    val organizationNameFlow = _organizationNameFlow.asStateFlow()

    private val _headCoachFrontUserNameFlow = MutableStateFlow("")

    val headCoachFrontUserNameFlow = _headCoachFrontUserNameFlow.asStateFlow()

    private val _logoImageFilePathFlow = MutableStateFlow("")

    val logoImageFilePathFlow = _logoImageFilePathFlow.asStateFlow()

    private val _headCoachAvatarImageFilePathFlow = MutableStateFlow("")

    val headCoachAvatarImageFilePathFlow = _headCoachAvatarImageFilePathFlow.asStateFlow()

    private val _toastFlowEvents = SharedFlowEvents<String>()

    val toastFlowEvents = _toastFlowEvents.asSharedFlow()

    private val _invitecodeFlowEvents = SharedFlowEvents<String>()

    val invitecodeFlowEvents = _invitecodeFlowEvents.asSharedFlow()

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
        getUserInfo()
    }

    override fun onStop() {
        super.onStop()

    }

    fun getInvitecode() {
        launchRequest(
            isLoading = true, {
                mUserRepository.getInvitecode()
            }, {
                if (it != null) {
                    _invitecodeFlowEvents.setEvent(it)
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
                    getProfile()
                }

            }, { errorCode, errorMsg ->
                _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
            }, {
                _toastFlowEvents.setEvent("${it.message}")
            }
        )
    }


    private fun getProfile() {
        PreferencesWrapper.get().apply {
            _avatarImageFilePathFlow.value = getAvatarImageFilePath()
            _nicknameFlow.value = getUsername()
            _logoImageFilePathFlow.value = getOrganizationLogoImageFilePath()
            _organizationNameFlow.value = getOrganizationName()
            _headCoachFrontUserNameFlow.value = getHeadCoachFrontUserName()
            _headCoachAvatarImageFilePathFlow.value = getHeadCoachAvatarImageFilePath()
        }
    }
}
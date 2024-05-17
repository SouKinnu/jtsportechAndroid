package com.jtsportech.visport.android.mine.profile

import com.cloudhearing.android.lib_base.base.BaseViewModel
import com.cloudhearing.android.lib_base.utils.SharedFlowEvents
import com.cloudhearing.android.lib_base.utils.setEvent
import com.cloudhearing.android.lib_common.network.dataSource.mine.UserInfo
import com.cloudhearing.android.lib_common.network.repository.FileRepository
import com.cloudhearing.android.lib_common.network.repository.UserRepository
import com.cloudhearing.android.lib_common.utils.launchRequest
import com.cloudhearing.android.lib_common.utils.mmkv.PreferencesWrapper
import com.jtsportech.visport.android.utils.PreferencesBusinessHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel : BaseViewModel() {

    private val mUserRepository: UserRepository by lazy {
        UserRepository()
    }

    private val mFileRepository: FileRepository by lazy {
        FileRepository()
    }

    private val _avatarImageFilePathFlow = MutableStateFlow("")

    val avatarImageFilePathFlow = _avatarImageFilePathFlow.asStateFlow()

    private val _nicknameFlow = MutableStateFlow("")

    val nicknameFlow = _nicknameFlow.asStateFlow()

    private val _phoneNumberFlow = MutableStateFlow("")

    val phoneNumberFlow = _phoneNumberFlow.asStateFlow()

    private val _organizationNameFlow = MutableStateFlow("")

    val organizationNameFlow = _organizationNameFlow.asStateFlow()

    private val _userRoleFlow = MutableStateFlow("")

    val userRoleFlow = _userRoleFlow.asStateFlow()

    private val _toastFlowEvents = SharedFlowEvents<String>()

    val toastFlowEvents = _toastFlowEvents.asSharedFlow()

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
    }

    override fun onStop() {
        super.onStop()
    }

    fun uploadAvatarImage(path: String) {
        launchRequest(
            isLoading = true, {
                mFileRepository.uploadImage(path)
            }, {
                if (it != null) {
                    modifyUserInfo(it)
                }

            }, { errorCode, errorMsg ->
                _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
            }, {
                _toastFlowEvents.setEvent("${it.message}")
            }
        )
    }

    private fun modifyUserInfo(avatarImageFileId: String) {
        launchRequest(
            isLoading = true, {
                mUserRepository.modifyUserInfo(avatarImageFileId)
            }, {
                getUserInfo()

            }, { errorCode, errorMsg ->
                _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
            }, {
                _toastFlowEvents.setEvent("${it.message}")
            }
        )
    }

    private fun getUserInfo() {
        launchRequest(
            isLoading = true, {
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
            _phoneNumberFlow.value = getPhoneNumber()
            _organizationNameFlow.value = getOrganizationName()
            _userRoleFlow.value = getRole()
        }
    }
}
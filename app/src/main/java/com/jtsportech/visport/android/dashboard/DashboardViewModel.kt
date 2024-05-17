package com.jtsportech.visport.android.dashboard

import com.cloudhearing.android.lib_base.base.BaseViewModel
import com.cloudhearing.android.lib_base.utils.SharedFlowEvents
import com.cloudhearing.android.lib_base.utils.context.AppProvider
import com.cloudhearing.android.lib_base.utils.setEvent
import com.cloudhearing.android.lib_common.network.repository.UserRepository
import com.cloudhearing.android.lib_common.utils.launchRequest
import com.cloudhearing.android.lib_common.utils.mmkv.PreferencesWrapper
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.dataSource.UserRole
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Author: BenChen
 * Date: 2023/12/29 16:44
 * Email:chenxiaobin@cloudhearing.cn
 */
class DashboardViewModel : BaseViewModel() {


    private val mUserRepository: UserRepository by lazy {
        UserRepository()
    }

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


    fun alterQrcode(id: String) {
        launchRequest(
            isLoading = false, {
                mUserRepository.alterQrcode(id)
            }, {
                _toastFlowEvents.setEvent(
                    AppProvider.get()
                        .getString(R.string.dashboard_scan_the_qr_code_to_log_in_successfully)
                )
            }, { errorCode, errorMsg ->
                _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
            }, {
                _toastFlowEvents.setEvent("${it.message}")
            }
        )
    }
}
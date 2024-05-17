package com.cloudhearing.android.lib_common.network.repository

import com.cloudhearing.android.lib_common.network.dataSource.login.OpenLogin
import com.cloudhearing.android.lib_common.network.dataSource.mine.Team
import com.cloudhearing.android.lib_common.network.dataSource.mine.TeamMembers
import com.cloudhearing.android.lib_common.network.dataSource.mine.User
import com.cloudhearing.android.lib_common.network.dataSource.mine.UserInfo
import com.cloudhearing.android.lib_common.network.dataSource.welcome.Welcome
import com.cloudhearing.android.lib_network.utils.ApiResponse

/**
 * Author: BenChen
 * Date: 2024/01/05 14:56
 * Email:chenxiaobin@cloudhearing.cn
 */
class UserRepository : BaseRepository() {

    suspend fun accountLogin(username: String, password: String): ApiResponse<String> {
        return executeHttp {
            mJTSportechService.accountLogin(username, password)
        }
    }

    suspend fun smsLoginSendCode(phoneNumber: String): ApiResponse<String> {
        return executeHttp {
            mJTSportechService.smsLoginSendCode(phoneNumber)
        }
    }

    suspend fun smsLogin(
        inviteCode: String,
        smsToken: String,
        validCode: String
    ): ApiResponse<String> {
        return executeHttp {
            mJTSportechService.smsLogin(inviteCode, smsToken, validCode)
        }
    }

    suspend fun openLogin(openId: String, openType: String): ApiResponse<String> {
        return executeHttp {
            mJTSportechService.openLogin(openId, openType)
        }
    }

    suspend fun openBind(openId: String, openType: String): ApiResponse<String> {
        return executeHttp {
            mJTSportechService.openBind(openId, openType)
        }
    }

    suspend fun openUnbind(openId: String, openType: String): ApiResponse<String> {
        return executeHttp {
            mJTSportechService.openUnbind(openId, openType)
        }
    }

    suspend fun openBindList(): ApiResponse<List<OpenLogin>> {
        return executeHttp {
            mJTSportechService.openBindList()
        }
    }

    suspend fun mobileLogin(umToken: String, inviteCode: String): ApiResponse<String> {
        return executeHttp {
            mJTSportechService.mobileLogin(umToken, inviteCode)
        }
    }

    suspend fun getUserInfo(): ApiResponse<UserInfo> {
        return executeHttp {
            mJTSportechService.getUserInfo()
        }
    }

    suspend fun getUserInfo(id: String): ApiResponse<User> {
        return executeHttp {
            mJTSportechService.getUserInfo(id)
        }
    }

    suspend fun getInvitecode(): ApiResponse<String> {
        return executeHttp {
            mJTSportechService.getInvitecode()
        }
    }

    suspend fun changePasswordToSendcode(): ApiResponse<String> {
        return executeHttp {
            mJTSportechService.changePasswordToSendcode()
        }
    }

    suspend fun changePasswordStepOne(smsToken: String, validCode: String): ApiResponse<String> {
        return executeHttp {
            mJTSportechService.changePasswordStepOne(smsToken, validCode)
        }
    }

    suspend fun changePasswordStepTwo(
        checkToken: String,
        oldPasswd: String,
        newPasswd: String,
        confirmPasswd: String
    ): ApiResponse<Unit> {
        return executeHttp {
            mJTSportechService.changePasswordStepTwo(
                checkToken,
                oldPasswd,
                newPasswd,
                confirmPasswd
            )
        }
    }

    suspend fun modifyUserInfo(
        avatarImageFileId: String,
    ): ApiResponse<Unit> {
        return executeHttp {
            mJTSportechService.modifyUserInfo(
                avatarImageFileId = avatarImageFileId
            )
        }
    }

    suspend fun changePhoneNumberToStepOne(): ApiResponse<String> {
        return executeHttp {
            mJTSportechService.changePhoneNumberToStepOne()
        }
    }

    suspend fun changePhoneNumberToStepTwo(
        smsToken: String,
        validCode: String
    ): ApiResponse<String> {
        return executeHttp {
            mJTSportechService.changePhoneNumberToStepTwo(smsToken, validCode)
        }
    }

    suspend fun changePhoneNumberToStepThree(
        checkToken: String,
        phoneNoEnc: String
    ): ApiResponse<String> {
        return executeHttp {
            mJTSportechService.changePhoneNumberToStepThree(checkToken, phoneNoEnc)
        }
    }

    suspend fun changePhoneNumberToStepFour(
        smsToken: String,
        validCode: String
    ): ApiResponse<Unit> {
        return executeHttp {
            mJTSportechService.changePhoneNumberToStepFour(smsToken, validCode)
        }
    }

    suspend fun getTeamMembers(): ApiResponse<List<TeamMembers>> {
        return executeHttp {
            mJTSportechService.getTeamMembers()
        }
    }

    suspend fun getTeam(): ApiResponse<List<Team>> {
        return executeHttp {
            mJTSportechService.getTeam()
        }
    }

    suspend fun changeOrganization(organizationId: String, groupId: String): ApiResponse<String> {
        return executeHttp {
            mJTSportechService.changeOrganization(organizationId, groupId)
        }
    }

    suspend fun cancellAccount(): ApiResponse<Unit> {
        return executeHttp {
            mJTSportechService.cancellAccount()
        }
    }

    suspend fun alterQrcode(id: String): ApiResponse<Unit> {
        return executeHttp {
            mJTSportechService.alterQrcode(id, "SUCC")
        }
    }

    suspend fun getGroupMemberList(groupId: String): ApiResponse<List<TeamMembers>> {
        return executeHttp {
            mJTSportechService.getGroupMemberList(groupId)
        }
    }

}
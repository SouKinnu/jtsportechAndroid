package com.jtsportech.visport.android.utils

import com.cloudhearing.android.lib_common.network.dataSource.mine.UserInfo
import com.cloudhearing.android.lib_common.utils.GsonUtil
import com.cloudhearing.android.lib_common.utils.mmkv.PreferencesUtil
import com.cloudhearing.android.lib_common.utils.mmkv.PreferencesWrapper
import com.jtsportech.visport.android.dataSource.home.search.SearchEntity

/**
 * Author: BenChen
 * Date: 2024/02/29 10:17
 * Email:chenxiaobin@cloudhearing.cn
 */
object PreferencesBusinessHelper {

    fun onSignOut() {
        PreferencesUtil.INSTANCE.removeValueForKeys(
            PreferencesWrapper.ACCESSTOKEN_KEY,
            PreferencesWrapper.USERNAME_KEY,
            PreferencesWrapper.PHONE_NUMBER_KEY,
            PreferencesWrapper.ROLE_KEY,
            PreferencesWrapper.ORGANIZATION_NAME_KEY,
            PreferencesWrapper.USER_HEIGHT_KEY,
            PreferencesWrapper.USER_WEIGHT_KEY,
            PreferencesWrapper.GROUP_NAME_KEY,
            PreferencesWrapper.AVATAR_IMAGE_FILE_PATH_KEY,
            PreferencesWrapper.AVATAR_IMAGE_FILE_ID_KEY,
            PreferencesWrapper.HEAD_COACH_AVATAR_IMAGE_FILE_PATH_KEY,
            PreferencesWrapper.HEAD_COACH_FRONT_USER_NAME_KEY,
            PreferencesWrapper.LOGO_IMAGE_FILE_PATH_KEY,
            PreferencesWrapper.CURRENT_ORGANIZATION_ID_KEY,
            PreferencesWrapper.INVITER_FRONT_USER_ID_KEY,
            PreferencesWrapper.COMPETITION_SEARCH_LIST_KEY,
        )
    }

    fun onDeleteAccount() {
        PreferencesUtil.INSTANCE.removeValueForKeys(
            PreferencesWrapper.ACCESSTOKEN_KEY,
            PreferencesWrapper.USERNAME_KEY,
            PreferencesWrapper.PHONE_NUMBER_KEY,
            PreferencesWrapper.ROLE_KEY,
            PreferencesWrapper.ORGANIZATION_NAME_KEY,
            PreferencesWrapper.USER_HEIGHT_KEY,
            PreferencesWrapper.USER_WEIGHT_KEY,
            PreferencesWrapper.GROUP_NAME_KEY,
            PreferencesWrapper.AVATAR_IMAGE_FILE_PATH_KEY,
            PreferencesWrapper.AVATAR_IMAGE_FILE_ID_KEY,
            PreferencesWrapper.HEAD_COACH_AVATAR_IMAGE_FILE_PATH_KEY,
            PreferencesWrapper.HEAD_COACH_FRONT_USER_NAME_KEY,
            PreferencesWrapper.LOGO_IMAGE_FILE_PATH_KEY,
            PreferencesWrapper.CURRENT_ORGANIZATION_ID_KEY,
            PreferencesWrapper.INVITER_FRONT_USER_ID_KEY,
            PreferencesWrapper.COMPETITION_SEARCH_LIST_KEY,
        )
    }

    fun saveUserInfo(userInfo: UserInfo) {
        PreferencesWrapper.get().apply {
            setUsername(userInfo.user?.name.orEmpty())
            setPhoneNumber(userInfo.user?.phoneNo.orEmpty())
            setRole(userInfo.role.orEmpty())
            if (userInfo.organization != null) {
//                val orgRole = userInfo.user!!.orgRoleList?.get(0)!!
                setOrganizationName(userInfo.organization!!.name!!)
                setOrganizationLogoImageFilePath(userInfo.organization!!.logoImageFilePath.orEmpty())
            } else if (userInfo.user?.lastUsedOrganizationId != null) {
                userInfo.user?.orgRoleList?.forEach {
                    if (it.organizationId == userInfo.user?.lastUsedOrganizationId) {
                        setOrganizationLogoImageFilePath(it.avatarImageFilePath.orEmpty())
                        it.organizationName?.let { it1 ->
                            setOrganizationName(it1)
                            return@forEach
                        }
                    }
                }
            }
//            if (userInfo.organization != null) {
//                setOrganizationLogoImageFilePath(userInfo.organization!!.logoImageFilePath.orEmpty())
//            } else {
//                setOrganizationLogoImageFilePath("")
//            }
            if (userInfo.userOrgGroup != null) {
                setPitchPosition(userInfo.userOrgGroup!!.pitchPosition.orEmpty())
                setPitchPositionName(userInfo.userOrgGroup!!.pitchPositionName.orEmpty())
                setUniformNo(userInfo.userOrgGroup!!.uniformNo ?: 0)
                setGroupName(userInfo.userOrgGroup!!.groupName.orEmpty())
                setGroupId(userInfo.userOrgGroup!!.groupId.orEmpty())
            } else {
                setPitchPosition("")
                setPitchPositionName("")
                setUniformNo(0)
                setGroupName("")
                setGroupId("")
            }
            setUserWeight(userInfo.user?.userWeight ?: 0.0)
            setUserHeight(userInfo.user?.userHeight ?: 0.0)
            setAvatarImageFilePath(userInfo.user?.avatarImageFilePath.orEmpty())
            setAvatarImageFileId(userInfo.user?.avatarImageFileId.orEmpty())
            setHeadCoachAvatarImageFilePath(userInfo.organization?.headCoachAvatarImageFilePath.orEmpty())
            setHeadCoachFrontUserName(userInfo.organization?.headCoachFrontUserName.orEmpty())
            setLogoImageFilePath(userInfo.organization?.logoImageFilePath.orEmpty())
            setCurrentOrganizationId(userInfo.organization?.id.orEmpty())
            setInviterFrontUserId(userInfo.user?.inviterFrontUserId.orEmpty())
        }
    }

    suspend fun getCompetitionSearchList(): List<SearchEntity> {
        return PreferencesWrapper.get().getCompetitionSearchList().let {
            if (it.isEmpty()) {
                emptyList()
            } else {
                GsonUtil.jsonToList(it, SearchEntity::class.java)
            }
        }
    }

    suspend fun deleteCompetitionSearchList() {
        PreferencesUtil.INSTANCE.removeValueForKey(PreferencesWrapper.COMPETITION_SEARCH_LIST_KEY)
    }

    suspend fun addCompetitionSearchRecord(content: String) {
        val competitionSearchList = getCompetitionSearchList().toMutableList()

        competitionSearchList.add(0, SearchEntity(content))

        PreferencesWrapper.get()
            .setCompetitionSearchList(GsonUtil.gsonString(competitionSearchList))
    }

    suspend fun deleteCompetitionSearch(list: List<SearchEntity>) {
        deleteCompetitionSearchList()

        PreferencesWrapper.get().setCompetitionSearchList(GsonUtil.gsonString(list))
    }


}
package com.cloudhearing.android.lib_common.network.dataSource.mine

data class Team(
    val adminUserEmail: String?,
    val adminUserId: String?,
    val adminUserName: String?,
    val adminUserPhoneNum: String?,
    val backgroundImageFileId: String?,
    val backgroundImageFilePath: String?,
    val createBy: String?,
    val createByName: String?,
    val createTime: String?,
    val headCoachAvatarImageFilePath: String?,
    val headCoachFrontUserId: String?,
    val headCoachFrontUserName: String?,
    val id: String?,
    val logoImageFileId: String?,
    val logoImageFilePath: String?,
    val name: String?,
    val orgStatus: String?,
    val orgType: String?,
    val sportType: String?,
    val uid: String?,
    val updateBy: String?,
    val updateTime: String?,
    val orgGroupList: List<OrgGroup>? = null
) {

    var isSelected: Boolean = false
}
package com.cloudhearing.android.lib_common.network.dataSource.mine

data class UserInfo(
    val organization: Organization? = null,
    val role: String? = null,
    val user: User? = null,
    val userOrgGroup: UserOrgGroup? = null,
    val orgGroupList: List<OrgGroup>? = null
)

data class Organization(
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
    val updateTime: String?
)

data class User(
    val avatarImageFileId: String? = null,
    val avatarImageFilePath: String? = null,
    val lastUsedOrganizationId: String? = null,
    val createBy: String? = null,
    val createTime: String? = null,
    val enable: String? = null,
    val id: String? = null,
    val name: String? = null,
    val orgRoleList: List<OrgRole>? = null,
    val phoneNo: String? = null,
    val uid: String? = null,
    val updateBy: String? = null,
    val updateTime: String? = null,
    val userHeight: Double? = null,
    val userWeight: Double? = null,
    val inviterFrontUserId: String? = null
)

data class OrgRole(
    val avatarImageFilePath: String?,
    val createBy: String?,
    val createTime: String?,
    val frontRoleType: String?,
    val frontUserId: String?,
    val id: String?,
    val name: String?,
    val orgType: String?,
    val organizationId: String?,
    val organizationName: String?,
    val phoneNo: String?,
    val uid: String?,
    val updateBy: String?,
    val updateTime: String?,
    val userHeight: Double?,
)

data class UserOrgGroup(
    val createBy: String? = "",
    val createByName: String? = "",
    val createTime: String? = "",
    val frontUserId: String? = "",
    val groupId: String? = "",
    val groupName: String? = "",
    val id: String? = "",
    val name: String? = "",
    val organizationId: String? = "",
    val organizationName: String? = "",
    val phoneNo: String? = "",
    val pitchPosition: String? = "",
    val pitchPositionName: String? = "",
    val uniformNo: Int? = 0,
    val updateBy: String? = "",
    val updateTime: String? = "",
    val userHeight: Double? = 0.0,
    val userWeight: Double? = 0.0
)

data class OrgGroup(
    val createBy: String? = "",
    val createByName: String? = "",
    val createTime: String? = "",
    val frontUserId: String? = "",
    val groupId: String? = "",
    val groupName: String? = "",
    val id: String? = "",
    val name: String? = "",
    val organizationId: String? = "",
    val organizationName: String? = "",
    val phoneNo: String? = "",
    val pitchPosition: String? = "",
    val pitchPositionName: String? = "",
    val uniformNo: Int? = 0,
    val updateBy: String? = "",
    val updateTime: String? = "",
    val userHeight: Double? = 0.0,
    val userWeight: Double? = 0.0
)
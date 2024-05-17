package com.cloudhearing.android.lib_common.network.dataSource.mine

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TeamMembers(
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
    val roleTypeName: String?,
    val uid: String?,
    val updateBy: String?,
    val updateTime: String?,
    val userHeight: Double?,
    val userWeight: Double?,
    val groupName: String?,
    val groupId: String?,
    val uniformNo: Int?,
    val pitchPosition: String?,
    val pitchPositionName: String?,
) : Parcelable
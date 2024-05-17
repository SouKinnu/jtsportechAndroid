package com.cloudhearing.android.lib_common.network.dataSource.messages

/**
 * Author: BenChen
 * Date: 2024/03/29 14:38
 * Email:chenxiaobin@cloudhearing.cn
 */
data class MessageNotice(
    val createTime: String? = "",
    val id: String? = "",
    val msgContent: String? = "",
    val msgStatus: String? = "",
    val msgTargetId: String? = "",
    val msgTargetInfo: MsgTargetInfo? = MsgTargetInfo(),
    val msgTitle: String? = "",
    val msgType: String? = ""
)

data class MsgTargetInfo(
    val contentText: String? = "",
    val contentType: String? = "",
    val createTime: String? = "",
    val criticizeType: String? = "",
    val frontRoleType: String? = "",
    val frontUserAvatarPath: String? = "",
    val frontUserName: String? = "",
    val id: String? = "",
    val matchInfoId: String? = "",
    val parentId: String? = "",
    val replyCriticizeId: String? = "",
    val toUserId: String? = "",
    val toUserName: String? = "",
    val analystAdminUserId: String? = "",
    val analystAdminUserName: String? = "",
    val auditStatus: String? = "",
    val createBy: String? = "",
    val creatorName: String? = "",
    val dealStatus: String? = "",
    val leagueId: String? = "",
    val leagueName: String? = "",
    val matchDuration: Int? = 0,
    val matchStatusName: String? = "",
    val matchTime: String? = "",
    val matchType: String? = "",
    val matchTypeName: String? = "",
    val name: String? = "",
    val playingField: String? = "",
    val previewImageFileId: String? = "",
    val previewImageFilePath: String? = "",
    val team1BackgroundImagePath: String? = "",
    val team1CoachFrontUserId: String? = "",
    val team1CoachFrontUserName: String? = "",
    val team1GroupId: String? = "",
    val team1GroupName: String? = "",
    val team1OrgLogoImagePath: String? = "",
    val team1OrganizationId: String? = "",
    val team1OrganizationName: String? = "",
    val team1Score: Int? = 0,
    val team2BackgroundImagePath: String? = "",
    val team2CoachFrontUserId: String? = "",
    val team2CoachFrontUserName: String? = "",
    val team2GroupId: String? = "",
    val team2GroupName: String? = "",
    val team2OrgLogoImagePath: String? = "",
    val team2OrganizationId: String? = "",
    val team2OrganizationName: String? = "",
    val team2Score: Int? = 0,
    val uid: String? = "",
    val updateBy: String? = "",
    val updateTime: String? = "",
    val usableStatus: String? = "",
    val audioDuration: Int = 0,
    val audioFilePath: String = "",
    val isPlaying: Boolean = false,
)
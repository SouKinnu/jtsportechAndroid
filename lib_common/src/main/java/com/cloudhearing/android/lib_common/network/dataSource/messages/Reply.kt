package com.cloudhearing.android.lib_common.network.dataSource.messages

data class Reply(
    val audioDuration: Int?,
    val audioFilePath: String?,
    val contentText: String?,
    val contentType: String?,
    val createTime: String?,
    val criticizeType: String?,
    val frontRoleType: String?,
    val frontUserAvatarPath: String?,
    val frontUserName: String?,
    val id: String?,
    val matchInfoId: String?,
    val replyCriticizeId: String?,
    val replyList: List<Reply>?,
    val toUserName: String?,
    val isPlaying: Boolean = false,
    var isReply: Boolean = false,
)
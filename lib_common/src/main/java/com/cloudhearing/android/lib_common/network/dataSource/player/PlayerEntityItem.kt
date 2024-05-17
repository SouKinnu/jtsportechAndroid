package com.cloudhearing.android.lib_common.network.dataSource.player

data class PlayerEntityItem(
    val createTime: String,
    val id: String,
    val matchInfoId: String,
    val matchInfoName: String,
    val playerFrontUserId: String,
    val playerFrontUserName: String,
    val thumbUrl: String,
    val updateTime: String,
    val videoFileId: String,
    val videoFilePath: String
)
package com.cloudhearing.android.lib_common.network.dataSource.racedetail

data class MatchPlayerVideo(
    val createTime: String,
    val id: String,
    val matchInfoId: String,
    val playerFrontUserId: String,
    val playerFrontUserName: String,
    val thumbUrl: String,
    val updateTime: String,
    val videoFileId: String,
    val videoFilePath: String
)
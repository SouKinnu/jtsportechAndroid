package com.cloudhearing.android.lib_common.network.dataSource.playerevents

data class EventPlayer(
    val createTime: String,
    val id: String,
    val matchVideoEventId: String,
    val playerFrontUserId: String,
    val playerFrontUserName: String,
    val playerNum: String,
    val updateTime: String
)
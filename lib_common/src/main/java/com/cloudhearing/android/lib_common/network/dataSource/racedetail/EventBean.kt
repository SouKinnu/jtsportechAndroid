package com.cloudhearing.android.lib_common.network.dataSource.racedetail
data class Event(
    val eventDuration: Int,
    val eventFrom: Int,
    val eventName: String,
    val eventNum: Int,
    val eventPlayerList: List<EventPlayer>,
    val eventPriority: Int,
    val eventResult: String,
    val id: String,
    val organizationName: String,
    val thumbUrl: String,
    val videoFilePath: String
)

data class EventPlayer(
    val createTime: String,
    val id: String,
    val matchVideoEventId: String,
    val playerFrontUserId: String,
    val playerFrontUserName: String,
    val playerNum: String,
    val updateTime: String
)
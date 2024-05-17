package com.cloudhearing.android.lib_common.network.dataSource.playerevents

data class PlayerEventEntityItem(
    val endArea: Int,
    val eventDuration: Int,
    val eventEvaluate: String,
    val eventFrom: Int,
    val eventName: String,
    val eventNum: Int,
    val eventPlayerList: List<EventPlayer>,
    val eventPriority: Int,
    val eventResult: String,
    val id: String,
    val organizationName: String,
    val startArea: Int,
    val thumbUrl: String
)
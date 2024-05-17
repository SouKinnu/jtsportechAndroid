package com.cloudhearing.android.lib_common.network.dataSource.racedetail

import java.io.Serializable

data class RaceDetailEntity(
    val chartDataList: List<ChartData>,
    val dataList: List<DataX>,
    val eventDataList: List<EventData>,
    val eventList: List<Event>,
    val eventNameList: List<EventName>,
    val matchInfo: MatchInfo,
    val matchPlayerVideoList: List<MatchPlayerVideo>,
    val team1PlayerList: List<Team1Player>,
    val team2PlayerList: List<Team1Player>,
    val videoList: List<Video>
):Serializable
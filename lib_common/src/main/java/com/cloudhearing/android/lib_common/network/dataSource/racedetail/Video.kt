package com.cloudhearing.android.lib_common.network.dataSource.racedetail

data class Video(
    val clipFrom: Int,
    val duration: Int,
    val id: String,
    val name: String,
    val perspective: String,
    val videoType: String,
    val videoFilePath: String,
    val thumbUrl: String
)
package com.cloudhearing.android.lib_common.network.dataSource.racedetail

import java.io.Serializable

data class Team1Player(
    val id: String,
    val organizationId: String,
    val organizationName: String,
    val pitchPosition: String,
    val playerFrontUserId: String,
    val playerFrontUserName: String,
    val playerUserAvatarPath: String?,
    val playerUserHeight: Int = 0,
    val playerUserWeight: Int = 0,
    val uniformNo: Int
) :Serializable
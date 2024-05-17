package com.cloudhearing.android.lib_common.network.dataSource.search

data class Search(
    val analystAdminUserName: String?,
    val dealStatus: String?,
    val hasBooking: Boolean?,
    val hasFavorite: Boolean?,
    val id: String?,
    val leagueId: String?,
    val leagueName: String?,
    val matchDuration: Int?,
    val matchTime: String?,
    val matchType: String?,
    val name: String?,
    val playingField: String?,
    val previewImageFilePath: String?,
    val team1CoachFrontUserName: String?,
    val team1GroupName: String?,
    val team1OrgLogoImagePath: String?,
    val team1OrganizationName: String?,
    val team1Score: Int?,
    val team2CoachFrontUserName: String?,
    val team2GroupName: String?,
    val team2OrgLogoImagePath: String?,
    val team2OrganizationName: String?,
    val team2Score: Int?
)
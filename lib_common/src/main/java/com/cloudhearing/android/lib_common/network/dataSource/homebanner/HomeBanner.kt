package com.cloudhearing.android.lib_common.network.dataSource.homebanner

data class HomeBanner(
    val adminGroupId: String,
    val auditStatus: String,
    val createBy: String,
    val createTime: String,
    val creator: String,
    val eventsItemList: List<EventsItem>,
    val eventsStatus: String,
    val eventsType: String,
    val headAdminUserId: String,
    val headAdminUserName: String,
    val headPhoneNum: String,
    val id: String,
    val name: String,
    val showType: String,
    val uid: String,
    val updateBy: String,
    val updateTime: String,
    val viewRanges: List<Any>
)
package com.jtsportech.visport.android.utils

import android.content.Context
import com.cloudhearing.android.lib_base.utils.context.AppProvider
import com.cloudhearing.android.lib_common.network.dataSource.home.competition.Competition
import com.cloudhearing.android.lib_common.network.dataSource.messages.MessageNotice
import com.cloudhearing.android.lib_common.network.dataSource.messages.Version
import com.cloudhearing.android.lib_common.network.dataSource.mine.Team
import com.cloudhearing.android.lib_common.network.dataSource.search.Search
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.dataSource.UserRole
import com.jtsportech.visport.android.dataSource.home.event.CalendarPanelEntity
import com.jtsportech.visport.android.dataSource.home.search.SearchFilterEntity
import com.jtsportech.visport.android.dataSource.home.search.SearchFilterLayoutType
import com.jtsportech.visport.android.dataSource.home.search.SearchFilterTitleType
import com.jtsportech.visport.android.dataSource.message.MessageEntity
import com.jtsportech.visport.android.dataSource.message.MessageType
import com.jtsportech.visport.android.dataSource.mine.switchTeams.SwitchTeamEntity
import com.jtsportech.visport.android.dataSource.mine.switchTeams.SwitchTeamLayoutType
import com.jtsportech.visport.android.utils.date.CalendarUtil
import com.jtsportech.visport.android.utils.date.DateUtils
import timber.log.Timber
import java.util.Calendar

/**
 * Author: BenChen
 * Date: 2024/01/12 13:52
 * Email:chenxiaobin@cloudhearing.cn
 */
/**
 * 组装日历面板数据
 *
 * @return
 */
fun generateCalendarPanelDataList(): List<List<CalendarPanelEntity>> {
    val list = arrayListOf<List<CalendarPanelEntity>>()

    val currentWeekList = mutableListOf<CalendarPanelEntity>()

    val todayCalendar = Calendar.getInstance()
    val weekCalendar = Calendar.getInstance()

    //设定从周日开始
    weekCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)

    for (i in 0 until 7) {
        currentWeekList.add(
            CalendarPanelEntity(
                date = weekCalendar.time,
                topTime = AppProvider.get().resources.getStringArray(R.array.week)[i],
                bottomTime = "${weekCalendar.get(Calendar.DAY_OF_MONTH)}",
                isToday = CalendarUtil.get().isSameDay(todayCalendar, weekCalendar),
                selected = CalendarUtil.get().isSameDay(todayCalendar, weekCalendar)
            )
        )

        weekCalendar.add(Calendar.DATE, 1)
    }

    list.addAll(increaseWeekBeforeCalendarPanelDataList(arrayListOf(currentWeekList), false))
    list.add(currentWeekList)
    list.addAll(increaseWeekNextCalendarPanelDataList(arrayListOf(currentWeekList), false))

    return list
}

/**
 * 增加上一周的数据
 *
 * @param originCalendarPanelDataList
 * @return
 */
fun increaseWeekBeforeCalendarPanelDataList(
    originCalendarPanelDataList: List<List<CalendarPanelEntity>>,
    isAddOriginDataList: Boolean = true
): List<List<CalendarPanelEntity>> {
    val list = arrayListOf<List<CalendarPanelEntity>>()

    val weekBeforeList = mutableListOf<CalendarPanelEntity>()

    // 第一周的第一天
    val calendar = Calendar.getInstance().apply {
        time = originCalendarPanelDataList.first().first().date
    }
    Timber.d(
        "originCalendarPanelDataList.first().first() ${
            originCalendarPanelDataList.first().first()
        }"
    )
    Timber.d("before ${calendar.get(Calendar.DAY_OF_MONTH)}")
    // 上一周的日期
    calendar.add(Calendar.DATE, -7)
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)

    Timber.d("No ${calendar.get(Calendar.DAY_OF_WEEK)} week , day: ${calendar.get(Calendar.DAY_OF_MONTH)}}")

    for (i in 0 until 7) {
        weekBeforeList.add(
            CalendarPanelEntity(
                date = calendar.time,
                topTime = AppProvider.get().resources.getStringArray(R.array.week)[i],
                bottomTime = "${calendar.get(Calendar.DAY_OF_MONTH)}",
                isToday = false,
                selected = false
            )
        )

        calendar.add(Calendar.DATE, 1)
    }

    // 在第一项增加原本周的上一周
    list.add(weekBeforeList)
    if (isAddOriginDataList && originCalendarPanelDataList.isNotEmpty()) {
        list.addAll(originCalendarPanelDataList)
    }

    Timber.d("list size: ${list.size}")

    return list
}

/**
 * 增加下一周的数据
 *
 * @param originCalendarPanelDataList
 * @return
 */
fun increaseWeekNextCalendarPanelDataList(
    originCalendarPanelDataList: List<List<CalendarPanelEntity>>,
    isAddOriginDataList: Boolean = true
): List<List<CalendarPanelEntity>> {
    val list = arrayListOf<List<CalendarPanelEntity>>()

    val weekBeforeList = mutableListOf<CalendarPanelEntity>()

    // 最后一周的第一天
    val calendar = Calendar.getInstance().apply {
        time = originCalendarPanelDataList.last().first().date
    }
    // 上一周的日期
    calendar.add(Calendar.DATE, 7)
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)

    for (i in 0 until 7) {
        weekBeforeList.add(
            CalendarPanelEntity(
                date = calendar.time,
                topTime = AppProvider.get().resources.getStringArray(R.array.week)[i],
                bottomTime = "${calendar.get(Calendar.DAY_OF_MONTH)}",
                isToday = false,
                selected = false
            )
        )

        calendar.add(Calendar.DATE, 1)
    }

    // 在第一项增加原本周的下一周
    if (isAddOriginDataList && originCalendarPanelDataList.isNotEmpty()) {
        list.addAll(originCalendarPanelDataList)
    }
    list.add(weekBeforeList)

    Timber.d("size: ${list.size}")

    return list
}

fun List<Search>.toCompetition(): List<Competition> {
    return try {
        this.map {
            Competition(
                analystAdminUserName = it.analystAdminUserName,
                dealStatus = it.dealStatus,
                id = it.id,
                matchDuration = it.matchDuration,
                matchTime = it.matchTime,
                matchType = it.matchType,
                name = it.name,
                playingField = it.playingField,
                previewImageFilePath = it.previewImageFilePath,
                team1CoachFrontUserName = it.team1CoachFrontUserName,
                team1GroupName = it.team1GroupName,
                team1OrgLogoImagePath = it.team1OrgLogoImagePath,
                team1OrganizationName = it.team1OrganizationName,
                team1Score = it.team1Score,
                team2CoachFrontUserName = it.team2CoachFrontUserName,
                team2GroupName = it.team2GroupName,
                team2OrgLogoImagePath = it.team2OrgLogoImagePath,
                team2OrganizationName = it.team2OrganizationName,
                team2Score = it.team2Score

            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList<Competition>()
    }

}

fun findSelectedPositions(list: List<List<CalendarPanelEntity>>): Pair<Int, Int>? {
    for (i in list.indices) {
        val sublist = list[i]
        for (j in sublist.indices) {
            if (sublist[j].selected) {
                return Pair(i, j)
            }
        }
    }
    return null
}

fun String.getDesByUserRole(context: Context, hasInviterFrontUserId: Boolean): String {
    return when (this) {
        UserRole.HEAD_COACH, UserRole.COACH -> context.getString(R.string.my_team_coach)
        UserRole.MEMBER -> context.getString(R.string.profile_member)
        UserRole.VISITOR -> context.getString(R.string.profile_visitor)
        UserRole.GUARDER -> context.getString(R.string.profile_parents)

        else -> this
    }
}

/**
 * 获取搜索的筛选数据
 *
 * @return
 */
fun getSearchFilterData(isEventMore: Boolean): List<SearchFilterEntity> {
    val context = AppProvider.get()
    val list = mutableListOf<SearchFilterEntity>()

    val calendar = Calendar.getInstance()
    val todayDate = calendar.time

    // * 发布时间
    list.add(
        SearchFilterEntity(
            type = SearchFilterLayoutType.TITLE,
            titleType = SearchFilterTitleType.PUBLISH_TIME,
            title = context.getString(R.string.search_released),
            subTitle = ""
        )
    )

    // 不限
    list.add(
        SearchFilterEntity(
            type = SearchFilterLayoutType.CONTENT,
            titleType = SearchFilterTitleType.PUBLISH_TIME,
            title = context.getString(R.string.search_released),
            subTitle = context.getString(R.string.search_unlimited),
            isSelected = true,
        )
    )

    // 一天内
    list.add(
        SearchFilterEntity(
            type = SearchFilterLayoutType.CONTENT,
            titleType = SearchFilterTitleType.PUBLISH_TIME,
            title = context.getString(R.string.search_released),
            subTitle = context.getString(R.string.search_within_a_day),
            isSelected = false,
            publishStartTime = DateUtils.convertToFormat2(todayDate)
        )
    )

    // 一周内
    //调整为前一周
    com.cloudhearing.android.lib_common.utils.date.CalendarUtil.get()
        .currentDateReduceOneWeek(calendar)
    list.add(
        SearchFilterEntity(
            type = SearchFilterLayoutType.CONTENT,
            titleType = SearchFilterTitleType.PUBLISH_TIME,
            title = context.getString(R.string.search_released),
            subTitle = context.getString(R.string.search_within_a_week),
            isSelected = false,
            publishStartTime = DateUtils.convertToFormat2(calendar.time),
            publishEndTime = DateUtils.convertToFormat2(todayDate)
        )
    )
    //恢复成今天
    calendar.time = todayDate

    // 一月内
    //调整为前一月
    com.cloudhearing.android.lib_common.utils.date.CalendarUtil.get()
        .currentDateReduceOneMonth(calendar)
    list.add(
        SearchFilterEntity(
            type = SearchFilterLayoutType.CONTENT,
            titleType = SearchFilterTitleType.PUBLISH_TIME,
            title = context.getString(R.string.search_released),
            subTitle = context.getString(R.string.search_within_a_month),
            isSelected = false,
            publishStartTime = DateUtils.convertToFormat2(calendar.time),
            publishEndTime = DateUtils.convertToFormat2(todayDate)
        )
    )
    //恢复成今天
    calendar.time = todayDate


    // * 视频时长
    list.add(
        SearchFilterEntity(
            type = SearchFilterLayoutType.TITLE,
            titleType = SearchFilterTitleType.DURATION_TIME,
            title = context.getString(R.string.search_the_length_of_the_video),
            subTitle = ""
        )
    )

    // 不限
    list.add(
        SearchFilterEntity(
            type = SearchFilterLayoutType.CONTENT,
            titleType = SearchFilterTitleType.DURATION_TIME,
            title = context.getString(R.string.search_the_length_of_the_video),
            subTitle = context.getString(R.string.search_unlimited),
            isSelected = true,
        )
    )

    // 10分钟以上
    list.add(
        SearchFilterEntity(
            type = SearchFilterLayoutType.CONTENT,
            titleType = SearchFilterTitleType.DURATION_TIME,
            title = context.getString(R.string.search_the_length_of_the_video),
            subTitle = context.getString(R.string.search_more_than_10_minutes),
            isSelected = false,
            durationFrom = 10,
        )
    )

    // 30 ~ 60 分钟
    list.add(
        SearchFilterEntity(
            type = SearchFilterLayoutType.CONTENT,
            titleType = SearchFilterTitleType.DURATION_TIME,
            title = context.getString(R.string.search_the_length_of_the_video),
            subTitle = context.getString(R.string.search_30_60_minutes),
            isSelected = false,
            durationFrom = 30,
            durationTo = 60
        )
    )

    // 60 ~ 90 分钟
    list.add(
        SearchFilterEntity(
            type = SearchFilterLayoutType.CONTENT,
            titleType = SearchFilterTitleType.DURATION_TIME,
            title = context.getString(R.string.search_the_length_of_the_video),
            subTitle = context.getString(R.string.search_60_90_minutes),
            isSelected = false,
            durationFrom = 60,
            durationTo = 90
        )
    )

    // 90 分钟
    list.add(
        SearchFilterEntity(
            type = SearchFilterLayoutType.CONTENT,
            titleType = SearchFilterTitleType.DURATION_TIME,
            title = context.getString(R.string.search_the_length_of_the_video),
            subTitle = context.getString(R.string.search_more_than_90_minutes),
            isSelected = false,
            durationFrom = 90,
        )
    )


    // * 检索范围
    list.add(
        SearchFilterEntity(
            type = SearchFilterLayoutType.TITLE,
            titleType = SearchFilterTitleType.MATCH_TYPE,
            title = context.getString(R.string.search_search_scope),
            subTitle = ""
        )
    )

    // 全部
    list.add(
        SearchFilterEntity(
            type = SearchFilterLayoutType.CONTENT,
            titleType = SearchFilterTitleType.MATCH_TYPE,
            title = context.getString(R.string.search_search_scope),
            subTitle = context.getString(R.string.dashboard_all),
            isSelected = true
        )
    )

    // 比赛
    list.add(
        SearchFilterEntity(
            type = SearchFilterLayoutType.CONTENT,
            titleType = SearchFilterTitleType.MATCH_TYPE,
            title = context.getString(R.string.search_search_scope),
            subTitle = context.getString(R.string.dashboard_race),
            isSelected = false,
            matchType = "MATCH"
        )
    )

    // 训练
    list.add(
        SearchFilterEntity(
            type = SearchFilterLayoutType.CONTENT,
            titleType = SearchFilterTitleType.MATCH_TYPE,
            title = context.getString(R.string.search_search_scope),
            subTitle = context.getString(R.string.dashboard_training),
            isSelected = false,
            matchType = "TRAIN"
        )
    )

    // 赛事
    list.add(
        SearchFilterEntity(
            type = SearchFilterLayoutType.CONTENT,
            titleType = SearchFilterTitleType.MATCH_TYPE,
            title = context.getString(R.string.search_search_scope),
            subTitle = context.getString(R.string.dashboard_events),
            isSelected = false,
            matchType = "LEAGUE",
            isMore = isEventMore,
            hasMoreView = true
        )
    )

    return list
}

fun handleUpdateSearchFilterData(
    list: List<SearchFilterEntity>,
    selectSearchFilterEntity: SearchFilterEntity
): List<SearchFilterEntity> {
    return list.map {
        if (it.title == selectSearchFilterEntity.title) {
            if (it.subTitle == selectSearchFilterEntity.subTitle) {
                it.copy(
                    isSelected = true
                )
            } else {
                it.copy(
                    isSelected = false
                )
            }
        } else {
            it
        }
    }
}

/**
 * 找到对应的筛选值
 *
 * @param list
 * @return
 */
fun findSearchFilterOption(list: List<SearchFilterEntity>): Triple<Pair<String, String>, Pair<Long, Long>, Pair<String, String>> {

    var publishStartTime = ""
    var publishEndTime = ""
    var durationFrom = -1L
    var durationTo = -1L
    var matchType = ""
    var leagueId = ""

    try {
        // 发布时间
        val publishTimeId = SearchFilterTitleType.PUBLISH_TIME
        // 视频时长
        val videoTimeId = SearchFilterTitleType.DURATION_TIME
        // 搜索范围
        val searchScopeId = SearchFilterTitleType.MATCH_TYPE

        list.forEach {
            if (publishTimeId == it.titleType && it.isSelected == true) {
                publishStartTime = it.publishStartTime.orEmpty()
                publishEndTime = it.publishEndTime.orEmpty()
            } else if (videoTimeId == it.titleType && it.isSelected == true) {
                durationFrom = it.durationFrom ?: -1L
                durationTo = it.durationTo ?: -1L
            } else if (searchScopeId == it.titleType && it.isSelected == true) {
                matchType = it.matchType.orEmpty()
                leagueId = it.leagueId.orEmpty()
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return Triple(
        Pair(publishStartTime, publishEndTime),
        Pair(durationFrom, durationTo),
        Pair(matchType, leagueId)
    )
}

/**
 * 更新赛事名称
 *
 * @param list
 * @param leagueId
 * @param eventName
 * @return
 */
fun handleUpdateSearchFilterEvent(
    list: List<SearchFilterEntity>,
    leagueId: String,
    eventName: String
): List<SearchFilterEntity> {
//    val newList = list.toMutableList()
//    val lastPosition = newList.size - 1
    val lastPosition = list.size - 1
//
//    newList[lastPosition] = newList[lastPosition].copy(
//        leagueId = leagueId,
//        subTitle = eventName
//    )

    return list.mapIndexed { index, entity ->
        if (entity.titleType == SearchFilterTitleType.MATCH_TYPE) {
            if (lastPosition == index) {
                entity.copy(
                    isSelected = true,
                    leagueId = leagueId,
                    subTitle = eventName
                )
            } else {
                entity.copy(
                    isSelected = false
                )
            }
        } else {
            entity
        }
    }
}

fun handleMessageNoticeToMessageEntityList(list: List<MessageNotice>): List<MessageEntity> {
    val context = AppProvider.get()
    val messageList = arrayListOf<MessageEntity>()

    list.find {
        it.msgType == "INTERACT_MSG"
    }.let {
        if (it != null) {
            messageList.add(
                MessageEntity(
                    type = MessageType.INTERACTIVE_MESSAGES,
                    iconRes = R.drawable.icon_interaction_message,
                    title = context.getString(R.string.message_interactive_messages),
                    subtitle = it.msgTitle.orEmpty(),
                    time = it.createTime.orEmpty(),
                    hasNewMessage = it.msgStatus == "UNREAD"
                )
            )
        } else {
            messageList.add(
                MessageEntity(
                    type = MessageType.INTERACTIVE_MESSAGES,
                    iconRes = R.drawable.icon_interaction_message,
                    title = context.getString(R.string.message_interactive_messages),
                    subtitle = context.getString(R.string.message_no_news_yet),
                    time = "",
                    hasNewMessage = false
                )
            )
        }
    }

    list.find {
        it.msgType == "MATCH_NOTICE"
    }.let {
        if (it != null) {
            messageList.add(
                MessageEntity(
                    type = MessageType.TOURNAMENT_NOTIFICATIONS,
                    iconRes = R.drawable.icon_competition_message,
                    title = context.getString(R.string.message_tournament_notifications),
                    subtitle = it.msgTitle.orEmpty(),
                    time = it.createTime.orEmpty(),
                    hasNewMessage = it.msgStatus == "UNREAD"
                )
            )
        } else {
            messageList.add(
                MessageEntity(
                    type = MessageType.TOURNAMENT_NOTIFICATIONS,
                    iconRes = R.drawable.icon_competition_message,
                    title = context.getString(R.string.message_tournament_notifications),
                    subtitle = context.getString(R.string.message_no_news_yet),
                    time = "",
                    hasNewMessage = false
                )
            )
        }
    }

    return messageList
}

fun getDefaultContestsAndInteractiveMessages(): List<MessageEntity> {
    val context = AppProvider.get()
    val messageList = arrayListOf<MessageEntity>()

    messageList.add(
        MessageEntity(
            type = MessageType.INTERACTIVE_MESSAGES,
            iconRes = R.drawable.icon_interaction_message,
            title = context.getString(R.string.message_interactive_messages),
            subtitle = context.getString(R.string.message_no_news_yet),
            time = "",
            hasNewMessage = false
        )
    )

    messageList.add(
        MessageEntity(
            type = MessageType.TOURNAMENT_NOTIFICATIONS,
            iconRes = R.drawable.icon_competition_message,
            title = context.getString(R.string.message_tournament_notifications),
            subtitle = "",
            time = "",
            hasNewMessage = false
        )
    )

    return messageList
}

fun handleAppVersionToMessageEntity(verison: Version): MessageEntity {
    val context = AppProvider.get()

    return MessageEntity(
        type = MessageType.APP_MESSAGES,
        iconRes = R.drawable.icon_apply_message,
        title = context.getString(R.string.message_app_messages),
        subtitle = verison.desc.orEmpty(),
        time = "",
        hasNewMessage = false
    )
}

fun getDefaultAppVersionMessage(): MessageEntity {
    val context = AppProvider.get()

    return MessageEntity(
        type = MessageType.APP_MESSAGES,
        iconRes = R.drawable.icon_apply_message,
        title = context.getString(R.string.message_app_messages),
        subtitle = context.getString(R.string.message_no_news_yet),
        time = "",
        hasNewMessage = false
    )
}

fun getSwitchTeamByTeam(
    teamList: List<Team>,
    organizationId: String,
    groupId: String
): List<SwitchTeamEntity> {
    val switchTeamList = mutableListOf<SwitchTeamEntity>()

    teamList.forEach { team ->
        // 队伍
        val entity = SwitchTeamEntity(
            layoutType = SwitchTeamLayoutType.TEAM_LAYOUT,
            teamLogoImageFilePath = team.logoImageFilePath,
            teamId = team.id,
            teamName = team.name,
            isTeamSelected = team.id == organizationId,
        )

        switchTeamList.add(entity)

        team.orgGroupList?.forEach { orgGroup ->
            // 小组
            val entity = SwitchTeamEntity(
                layoutType = SwitchTeamLayoutType.GROUP_LAYOUT,
                teamLogoImageFilePath = team.logoImageFilePath,
                teamId = team.id,
                teamName = team.name,
                isTeamSelected = team.id == organizationId,
                groupId = orgGroup.groupId,
                groupName = orgGroup.groupName,
                isGroupSelected = orgGroup.groupId == groupId
            )

            switchTeamList.add(entity)
        }
    }

    return switchTeamList
}
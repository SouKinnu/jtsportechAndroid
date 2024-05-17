package com.jtsportech.visport.android.utils

import android.content.Context
import com.cloudhearing.android.lib_common.network.client.RetrofitClient.JTSPORTECH_AUDIO_PRO_BASE_URL
import com.cloudhearing.android.lib_common.network.client.RetrofitClient.JTSPORTECH_IMAGE_PRO_BASE_URL
import com.cloudhearing.android.lib_common.network.client.RetrofitClient.JTSPORTECH_VIDEO_PRO_BASE_URL
import com.cloudhearing.android.lib_common.network.dataSource.home.competition.Competition
import com.cloudhearing.android.lib_common.network.dataSource.mine.Team
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.dataSource.home.HomeTabEntity
import com.jtsportech.visport.android.dataSource.home.event.CalendarPanelEntity
import java.util.Calendar

/**
 * Author: BenChen
 * Date: 2024/01/02 11:31
 * Email:chenxiaobin@cloudhearing.cn
 */

/**
 * 获取 问候语
 *
 * @return
 */
fun getGreeting(): String {
    val calendar = Calendar.getInstance()
    val currentHour = calendar.get(Calendar.HOUR_OF_DAY)

    return when (currentHour) {
        in 0..7 -> "上午好"
        in 8..11 -> "上午好"
        in 12..19 -> "下午好"
        else -> "晚上好"
    }
}

/**
 * 获取首页 tab 数据
 *
 * @param context
 * @return
 */
fun getHomeTabData(
    context: Context,
    position: Int = 1,
    isEventMore: Boolean = false,
    isEventMorePop: Boolean = false
): List<HomeTabEntity> {
    val list = mutableListOf<HomeTabEntity>()

    list.add(HomeTabEntity(context.getString(R.string.home_training), position == 0, false, false))
    list.add(HomeTabEntity(context.getString(R.string.home_race), position == 1, false, false))
    list.add(
        HomeTabEntity(
            context.getString(R.string.home_events),
            position == 2,
            isEventMore,
            isEventMorePop
        )
    )

    return list
}

fun getRecentlyWatchedTabData(context: Context, position: Int = 1): List<HomeTabEntity> {
    val list = mutableListOf<HomeTabEntity>()

    list.add(HomeTabEntity(context.getString(R.string.home_training), position == 0, false, false))
    list.add(HomeTabEntity(context.getString(R.string.home_race), position == 1, false, false))
    list.add(HomeTabEntity(context.getString(R.string.home_events), position == 2, false, false))

    return list
}

//fun backTodaySelectedList(list: List<MutableList<CalendarPanelEntity>>): List<List<CalendarPanelEntity>> {
////    list.forEach {
////        it.forEach {
////            if (it.selected && !it.isToday) {
////                it = it.copy(
////                    selected = false
////                )
////            }
////        }
////    }
//
////    list.map {
////        it.map {
////            if (it.selected && !it.isToday) {
////                it.copy(
////                    selected = false
////                )
////            }
////        }
////    }
//
//}

fun String.img() = "${JTSPORTECH_IMAGE_PRO_BASE_URL}/$this"

fun String.audio() = "${JTSPORTECH_AUDIO_PRO_BASE_URL}/$this"

fun String.video() = "${JTSPORTECH_VIDEO_PRO_BASE_URL}$this"

/**
 * 手机号脱敏
 * 原始数据是 13692123111,脱敏后市 136*****111
 *
 * @return
 */
fun String.maskPhoneNumber(): String {
    val prefix = this.substring(0, 3)
    val suffix = this.substring(8)

    return "$prefix*****$suffix"
}

fun setSelectedTeamList(teamList: List<Team>, id: String): List<Team> {
    return teamList.map {
        it.isSelected = it.id == id

        it
    }
}

fun toggleEditModeCompetitionList(
    isEditMode: Boolean,
    competitionList: List<Competition>
): List<Competition> {
    return competitionList.map {
        it.copy(
            isEditMode = isEditMode
        )
    }
}

fun updateSelectedStateCompetitionList(
    competition: Competition,
    competitionList: List<Competition>
): List<Competition> {
    val id = competition.favoriteId
    val isSelected = !competition.isSelected
    return competitionList.map {
        if (it.favoriteId == id) {
            it.copy(
                isEditMode = true,
                isSelected = isSelected
            )
        } else {
            it
        }
    }
}

fun checkAllStateCompetitionList(
    isCheckAll: Boolean,
    competitionList: List<Competition>
): List<Competition> {
    return competitionList.map {
        it.copy(
            isEditMode = true,
            isSelected = isCheckAll
        )
    }
}

fun updateCalendarDataListSelected(
    list: List<List<CalendarPanelEntity>>,
    currentListPosition: Int,
    selectPosition: Int
): List<List<CalendarPanelEntity>> {
    val newRootList = mutableListOf<List<CalendarPanelEntity>>()
    list.forEachIndexed { index, calendarPanelEntities ->
        val newChildList = mutableListOf<CalendarPanelEntity>()
        if (index == currentListPosition) {
            calendarPanelEntities.forEachIndexed { indexx, calendarPanelEntity ->
                val entity = calendarPanelEntity.copy(
                    selected = indexx == selectPosition
                )
                newChildList.add(entity)
            }
        } else {
            calendarPanelEntities.forEach { calendarPanelEntity ->
                newChildList.add(
                    calendarPanelEntity.copy(
                        selected = false
                    )
                )
            }
        }

        newRootList.add(newChildList)
    }

    return newRootList
}

fun updateTargetedTodayList(list: List<List<CalendarPanelEntity>>): Pair<Int, List<List<CalendarPanelEntity>>> {
    var position = 0
    val newRootList = mutableListOf<List<CalendarPanelEntity>>()

    list.forEachIndexed { index, calendarPanelEntities ->
        val newChildList = mutableListOf<CalendarPanelEntity>()
        calendarPanelEntities.forEachIndexed { indexx, calendarPanelEntitiess ->
            if (calendarPanelEntitiess.isToday) {
                position = index
            }

            newChildList.add(
                calendarPanelEntitiess.copy(
                    selected = calendarPanelEntitiess.isToday
                )
            )
        }

        newRootList.add(newChildList)
    }

    return Pair(position, newRootList)
}

fun judgetToday(list: List<List<CalendarPanelEntity>>): Boolean? {
    var entity: CalendarPanelEntity? = null

    list.forEach {
        it.forEach {
            if (it.selected) {
                entity = it
                return@forEach
            }
        }
    }

    return entity?.isToday
}
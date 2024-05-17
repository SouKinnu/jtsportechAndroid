package com.jtsportech.visport.android.dataSource.mine.switchTeams

import androidx.annotation.IntDef

/**
 * Author: BenChen
 * Date: 2024/04/02 16:18
 * Email:chenxiaobin@cloudhearing.cn
 */
data class SwitchTeamEntity(
    @SwitchTeamLayoutType
    val layoutType: Int,
    val teamLogoImageFilePath: String?,
    val teamId: String?,
    val teamName: String?,
    val isTeamSelected: Boolean,
    val groupId: String? = null,
    val groupName: String? = null,
    val isGroupSelected: Boolean? = null,
)

@IntDef(
    SwitchTeamLayoutType.TEAM_LAYOUT,
    SwitchTeamLayoutType.GROUP_LAYOUT
)
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class SwitchTeamLayoutType {
    companion object {
        /**
         * 队伍
         */
        const val TEAM_LAYOUT = 0

        /**
         * 小组
         */
        const val GROUP_LAYOUT = 1
    }
}

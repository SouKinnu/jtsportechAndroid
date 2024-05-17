package com.jtsportech.visport.android.mine.switchTeams

import androidx.recyclerview.widget.DiffUtil
import com.cloudhearing.android.lib_base.base.DelegateAdapter
import com.jtsportech.visport.android.dataSource.mine.switchTeams.SwitchTeamEntity

/**
 * Author: BenChen
 * Date: 2024/04/02 16:34
 * Email:chenxiaobin@cloudhearing.cn
 */
class SwitchTeamsAdapter :
    DelegateAdapter<SwitchTeamEntity, SwitchTeamsAdapter.SwitchTeamsDiffCallback>(
        SwitchTeamsDiffCallback()
    ) {

    class SwitchTeamsDiffCallback : DiffUtil.ItemCallback<SwitchTeamEntity>() {
        override fun areItemsTheSame(
            oldItem: SwitchTeamEntity,
            newItem: SwitchTeamEntity
        ): Boolean {
            return oldItem.layoutType == newItem.layoutType
        }

        override fun areContentsTheSame(
            oldItem: SwitchTeamEntity,
            newItem: SwitchTeamEntity
        ): Boolean {
            return oldItem.isTeamSelected == newItem.isTeamSelected && oldItem.isGroupSelected == newItem.isGroupSelected
        }

    }
}
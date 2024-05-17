package com.jtsportech.visport.android.mine.myTeam

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.cloudhearing.android.lib_base.base.BaseListAdapter
import com.cloudhearing.android.lib_base.utils.toDp
import com.cloudhearing.android.lib_common.image.loadRoundCornerImage
import com.cloudhearing.android.lib_common.network.dataSource.mine.TeamMembers
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.databinding.ItemTeamMenberBinding
import com.jtsportech.visport.android.utils.img

/**
 * Author: BenChen
 * Date: 2024/03/01 14:08
 * Email:chenxiaobin@cloudhearing.cn
 */
class TeamMemberAdapter :
    BaseListAdapter<TeamMembers, ItemTeamMenberBinding>(TeamMemberDiffCallback()) {


    private var mClickListener: ((TeamMembers) -> Unit)? = null

    fun setClickListener(listener: (TeamMembers) -> Unit) {
        mClickListener = listener
    }

    class TeamMemberDiffCallback : DiffUtil.ItemCallback<TeamMembers>() {
        override fun areItemsTheSame(oldItem: TeamMembers, newItem: TeamMembers): Boolean {
            return false
        }

        override fun areContentsTheSame(oldItem: TeamMembers, newItem: TeamMembers): Boolean {
            return false
        }

    }

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup): ItemTeamMenberBinding {
        return ItemTeamMenberBinding.inflate(inflater, parent, false)
    }

    override fun bind(binding: ItemTeamMenberBinding, item: TeamMembers, position: Int) {
        val context = binding.root.context

        binding.ivAvatar.loadRoundCornerImage(
            url = item.avatarImageFilePath?.img(),
            radius = 6.toDp.toInt(),
            placeHolder = R.drawable.ic_mine_team_avatar_def
        )
        binding.tvName.text = item.name
        binding.tvRole.text = item.pitchPositionName.takeIf {
            it != null
        } ?: ""//item.roleTypeName

        binding.root.setOnClickListener {
            mClickListener?.invoke(item)
        }
    }
}
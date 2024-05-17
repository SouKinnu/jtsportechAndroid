package com.jtsportech.visport.android.mine.switchTeams

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import com.cloudhearing.android.lib_base.base.BaseListAdapter
import com.cloudhearing.android.lib_base.utils.toDp
import com.cloudhearing.android.lib_common.image.loadRoundCornerImage
import com.cloudhearing.android.lib_common.network.dataSource.mine.Team
import com.jtsportech.visport.android.databinding.ItemTeamBinding
import com.jtsportech.visport.android.utils.img

/**
 * Author: BenChen
 * Date: 2024/03/01 16:07
 * Email:chenxiaobin@cloudhearing.cn
 */
class TeamAdapter : BaseListAdapter<Team, ItemTeamBinding>(TeamDiffCallback()) {

    private var mOnClickListener: ((String) -> Unit)? = null

    fun setOnClickListener(listener: (String) -> Unit) {
        mOnClickListener = listener
    }

    class TeamDiffCallback : DiffUtil.ItemCallback<Team>() {
        override fun areItemsTheSame(oldItem: Team, newItem: Team): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Team, newItem: Team): Boolean {
            return oldItem.isSelected == newItem.isSelected
        }

    }

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup): ItemTeamBinding {
        return ItemTeamBinding.inflate(inflater, parent, false)
    }

    override fun bind(binding: ItemTeamBinding, item: Team, position: Int) {
        val context = binding.root.context

        binding.ivTeam.loadRoundCornerImage(
            url = item.logoImageFilePath?.img(),
            radius = 6.toDp.toInt()
        )

        binding.tvTeamName.text = item.name

        binding.ivChecked.isVisible = item.isSelected

        binding.root.setOnClickListener {
            val id = item.id
            if (id != null) {
                mOnClickListener?.invoke(id)
            }
        }
    }
}
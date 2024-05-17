package com.jtsportech.visport.android.dashboard.home.competition

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.cloudhearing.android.lib_base.base.BaseListAdapter
import com.cloudhearing.android.lib_base.utils.toDp
import com.cloudhearing.android.lib_common.image.loadRoundCornerImage
import com.cloudhearing.android.lib_common.network.dataSource.home.competition.Competition
import com.jtsportech.visport.android.databinding.ItemCompetitionBinding
import com.jtsportech.visport.android.utils.date.DateUtils
import com.jtsportech.visport.android.utils.img

/**
 * Author: BenChen
 * Date: 2024/01/11 15:13
 * Email:chenxiaobin@cloudhearing.cn
 */
class CompetitionAdapter :
    BaseListAdapter<Competition, ItemCompetitionBinding>(CompetitionDiffCallback()) {

    private var mOnClickListener: ((Competition) -> Unit)? = null

    fun setOnClickListener(listener: (Competition) -> Unit) {
        mOnClickListener = listener
    }

    class CompetitionDiffCallback : DiffUtil.ItemCallback<Competition>() {
        override fun areItemsTheSame(oldItem: Competition, newItem: Competition): Boolean {
            return false
        }

        override fun areContentsTheSame(oldItem: Competition, newItem: Competition): Boolean {
            return false
        }

    }

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemCompetitionBinding {
        return ItemCompetitionBinding.inflate(inflater, parent, false)
    }

    override fun bind(binding: ItemCompetitionBinding, item: Competition, position: Int) {
        val context = binding.root.context

        binding.apply {
            tvStadium.text = item.playingField
            tvTeamScore1.text = "${item.team1Score}"
            tvTeamScore2.text = "${item.team2Score}"
            tvCompetitionInfo.text = "${DateUtils.convertToFormat2(item.matchTime.orEmpty())}/${item.name}"
            tvTeamName1.text = item.team1OrganizationName
            tvTeamName2.text = item.team2OrganizationName
            ivTeamLogo1.loadRoundCornerImage(
                url = item.team1OrgLogoImagePath?.img(),
                radius = 6.toDp.toInt()
            )
            ivTeamLogo2.loadRoundCornerImage(
                url = item.team2OrgLogoImagePath?.img(),
                radius = 6.toDp.toInt()
            )

            root.setOnClickListener {
                mOnClickListener?.invoke(item)
            }
        }
    }
}
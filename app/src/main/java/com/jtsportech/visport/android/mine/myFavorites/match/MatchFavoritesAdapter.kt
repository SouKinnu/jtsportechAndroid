package com.jtsportech.visport.android.mine.myFavorites.match

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import com.cloudhearing.android.lib_base.base.BaseListAdapter
import com.cloudhearing.android.lib_base.utils.show
import com.cloudhearing.android.lib_base.utils.toDp
import com.cloudhearing.android.lib_common.image.loadRoundCornerImage
import com.cloudhearing.android.lib_common.network.dataSource.home.competition.Competition
import com.jtsportech.visport.android.databinding.ItemCompetitionBinding
import com.jtsportech.visport.android.utils.date.DateUtils
import com.jtsportech.visport.android.utils.img

/**
 * Author: BenChen
 * Date: 2024/03/02 14:19
 * Email:chenxiaobin@cloudhearing.cn
 */
class MatchFavoritesAdapter :
    BaseListAdapter<Competition, ItemCompetitionBinding>(MatchFavoritesDiffCallback()) {

    private var mOnClickListener: ((Competition) -> Unit)? = null

    fun setOnClickListener(listener: (Competition) -> Unit) {
        mOnClickListener = listener
    }

    class MatchFavoritesDiffCallback : DiffUtil.ItemCallback<Competition>() {
        override fun areItemsTheSame(oldItem: Competition, newItem: Competition): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Competition, newItem: Competition): Boolean {
            return oldItem.isEditMode == newItem.isEditMode && oldItem.isSelected == newItem.isSelected
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
            ivEdit.isVisible = item.isEditMode
            ivEdit.isSelected = item.isSelected
            tvStadium.text = item.playingField
            tvTeamScore1.text = "${item.team1Score}"
            tvTeamScore2.text = "${item.team2Score}"
            tvCompetitionInfo.text =
                "${DateUtils.convertToFormat2(item.matchTime.orEmpty())}/${item.name}"
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

            groupCompilation.isVisible = item.favoriteType == "EVENTS"

            root.setOnClickListener {
                mOnClickListener?.invoke(item)
            }
        }
    }
}
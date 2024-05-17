package com.jtsportech.visport.android.mine.myFavorites.league

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import com.cloudhearing.android.lib_base.base.BaseListAdapter
import com.cloudhearing.android.lib_base.utils.show
import com.cloudhearing.android.lib_base.utils.toDp
import com.cloudhearing.android.lib_common.image.loadRoundCornerImage
import com.cloudhearing.android.lib_common.network.dataSource.home.competition.Competition
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.databinding.ItemEventBinding
import com.jtsportech.visport.android.utils.date.DateUtils
import com.jtsportech.visport.android.utils.img

/**
 * Author: BenChen
 * Date: 2024/03/02 14:28
 * Email:chenxiaobin@cloudhearing.cn
 */
class LeagueFavoritesAdapter :
    BaseListAdapter<Competition, ItemEventBinding>(LeagueFavoritesDiffCallback()) {

    private var mOnClickListener: ((Competition) -> Unit)? = null

    fun setOnClickListener(listener: (Competition) -> Unit) {
        mOnClickListener = listener
    }

    class LeagueFavoritesDiffCallback : DiffUtil.ItemCallback<Competition>() {
        override fun areItemsTheSame(oldItem: Competition, newItem: Competition): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Competition, newItem: Competition): Boolean {
            return oldItem.isEditMode == newItem.isEditMode && oldItem.isSelected == newItem.isSelected
        }

    }

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup): ItemEventBinding {
        return ItemEventBinding.inflate(inflater, parent, false)
    }

    override fun bind(binding: ItemEventBinding, item: Competition, position: Int) {
        val context = binding.root.context

        binding.apply {
            ivEdit.isVisible = item.isEditMode
            ivEdit.isSelected = item.isSelected
            item.matchTime?.let {
                tvEventTime.text = DateUtils.convertToFormat3(it)
            }
            ivTeamLogo1.loadRoundCornerImage(
                url = item.team1OrgLogoImagePath?.img(),
                radius = 6.toDp.toInt()
            )
            ivTeamLogo2.loadRoundCornerImage(
                url = item.team2OrgLogoImagePath?.img(),
                radius = 6.toDp.toInt()
            )
            tvTeamName1.text = item.team1OrganizationName
            tvTeamName2.text = item.team2OrganizationName

            ivEventBg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.img_competition_f_orange))
            tvEventState.text = ContextCompat.getString(context,R.string.event_view)
            tvEventState.setTextColor(ContextCompat.getColor(context, R.color.ecstasy))

            groupCompilation.isVisible = item.favoriteType == "EVENTS"

            root.setOnClickListener {
                mOnClickListener?.invoke(item)
            }
        }
    }
}
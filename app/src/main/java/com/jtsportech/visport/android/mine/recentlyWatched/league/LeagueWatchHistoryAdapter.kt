package com.jtsportech.visport.android.mine.recentlyWatched.league

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import com.cloudhearing.android.lib_base.base.BaseListAdapter
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
class LeagueWatchHistoryAdapter :
    BaseListAdapter<Competition, ItemEventBinding>(LeagueWatchHistoryDiffCallback()) {

    private var mOnClickListener: ((Competition) -> Unit)? = null

    fun setOnClickListener(listener: (Competition) -> Unit) {
        mOnClickListener = listener
    }

    class LeagueWatchHistoryDiffCallback : DiffUtil.ItemCallback<Competition>() {
        override fun areItemsTheSame(oldItem: Competition, newItem: Competition): Boolean {
            return false
        }

        override fun areContentsTheSame(oldItem: Competition, newItem: Competition): Boolean {
            return false
        }

    }

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup): ItemEventBinding {
        return ItemEventBinding.inflate(inflater, parent, false)
    }

    override fun bind(binding: ItemEventBinding, item: Competition, position: Int) {
        val context = binding.root.context

        binding.apply {
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
            tvEventState.text = ContextCompat.getString(context, R.string.event_view)
            tvEventState.setTextColor(ContextCompat.getColor(context, R.color.ecstasy))

            root.setOnClickListener {
                mOnClickListener?.invoke(item)
            }
        }
    }
}
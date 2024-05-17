package com.jtsportech.visport.android.dashboard.home.event

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
 * Date: 2024/03/05 11:27
 * Email:chenxiaobin@cloudhearing.cn
 */
class EventAdapter : BaseListAdapter<Competition, ItemEventBinding>(EventDiffCallback()) {

    private var mOpenEventNotifyOnClickListener: ((Competition) -> Unit)? = null
    private var mCheckOnClickListener: ((Competition) -> Unit)? = null
    private var mAppointmentOnClickListener: ((Competition) -> Unit)? = null

    private val published = "PUBLISHED"

    fun setOpenEventNotifyOnClickListener(listener: (Competition) -> Unit) {
        mOpenEventNotifyOnClickListener = listener
    }

    fun setCheckOnClickListener(listener: (Competition) -> Unit) {
        mCheckOnClickListener = listener
    }

    fun setAppointmentOnClickListener(listener: (Competition) -> Unit) {
        mAppointmentOnClickListener = listener
    }


    class EventDiffCallback : DiffUtil.ItemCallback<Competition>() {
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

            if (item.dealStatus == published) {
                tvEventState.text = context.getString(R.string.event_view)
                tvEventState.setTextColor(ContextCompat.getColor(context, R.color.ecstasy))
                ivEventBg.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.img_competition_f_orange
                    )
                )
            } else {
                if (item.hasBooking!!) {
                    tvEventState.text = context.getString(R.string.event_appointment)
                    tvEventState.setTextColor(ContextCompat.getColor(context, R.color.dusty_gray))
                    ivEventBg.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.img_competition_f
                        )
                    )
                } else {
                    tvEventState.text = context.getString(R.string.event_notify_me_of_the_start)
                    tvEventState.setTextColor(ContextCompat.getColor(context, R.color.cerulean))
                    ivEventBg.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.img_competition_f_blue
                        )
                    )
                }
            }

            root.setOnClickListener {
                if (item.dealStatus == published) {
                    mCheckOnClickListener?.invoke(item)
                } else {
                    if (item.hasBooking!!) {
                        mOpenEventNotifyOnClickListener?.invoke(item)
                    } else {
                        mAppointmentOnClickListener?.invoke(item)
                    }
                }
            }
        }
    }
}
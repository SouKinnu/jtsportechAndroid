package com.jtsportech.visport.android.home.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding
import com.cloudhearing.android.lib_base.base.BaseListViewTypeAdapter
import com.cloudhearing.android.lib_base.utils.toDp
import com.cloudhearing.android.lib_common.image.loadImage
import com.cloudhearing.android.lib_common.image.loadRoundCornerImage
import com.cloudhearing.android.lib_common.network.dataSource.home.competition.Competition
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.databinding.ItemCompetitionBinding
import com.jtsportech.visport.android.databinding.ItemEventBinding
import com.jtsportech.visport.android.databinding.ItemTrainingBinding
import com.jtsportech.visport.android.utils.date.DateUtils
import com.jtsportech.visport.android.utils.img

/**
 * Author: BenChen
 * Date: 2024/03/28 13:35
 * Email:chenxiaobin@cloudhearing.cn
 */
class SearchMatcheAdapter :
    BaseListViewTypeAdapter<Competition, ViewBinding>(SearchMatcheDiffCallback()) {

    private var mOnTrainingClickListener: ((Competition) -> Unit)? = null
    private var mOnCompetitionClickListener: ((Competition) -> Unit)? = null
    private var mOnEventClickListener: ((Competition) -> Unit)? = null

    fun setOnTrainingClickListener(listener: (Competition) -> Unit) {
        mOnTrainingClickListener = listener
    }

    fun setOnCompetitionClickListener(listener: (Competition) -> Unit) {
        mOnCompetitionClickListener = listener
    }

    fun setOnEventClickListener(listener: (Competition) -> Unit) {
        mOnEventClickListener = listener
    }

    private val published = "PUBLISHED"

    class SearchMatcheDiffCallback : DiffUtil.ItemCallback<Competition>() {
        override fun areItemsTheSame(oldItem: Competition, newItem: Competition): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Competition, newItem: Competition): Boolean {
            return oldItem.name == newItem.name
        }

    }

    override fun getItemViewType(position: Int, item: Competition): Int {
        return when (item.matchType) {
            "TRAIN" -> 0
            "MATCH" -> 1
            "LEAGUE" -> 2
            else -> 0
        }
    }

    override fun onCreateBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewBinding {
        return when (viewType) {
            0 -> ItemTrainingBinding.inflate(inflater, parent, false)
            1 -> ItemCompetitionBinding.inflate(inflater, parent, false)
            2 -> ItemEventBinding.inflate(inflater, parent, false)
            else -> ItemTrainingBinding.inflate(inflater, parent, false)
        }
    }

    override fun onBind(binding: ViewBinding, item: Competition, position: Int) {
        val context = binding.root.context

        when (binding) {
            is ItemTrainingBinding -> {
                binding.apply {
                    ciLogo.loadImage(url = item.team1OrgLogoImagePath?.img())
                    tvTrainingName.text = item.name
                    item.matchTime?.let {
                        tvTime.text = DateUtils.convertToFormat1(it)
                    }
                    ivTrainingCover.loadRoundCornerImage(
                        url = item.previewImageFilePath?.img(),
                        radius = 6.toDp.toInt()
                    )

                    root.setOnClickListener {
                        mOnTrainingClickListener?.invoke(item)
                    }
                }
            }

            is ItemCompetitionBinding -> {
                binding.apply {
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

                    root.setOnClickListener {
                        mOnCompetitionClickListener?.invoke(item)
                    }
                }
            }

            is ItemEventBinding -> {
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
                            tvEventState.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.dusty_gray
                                )
                            )
                            ivEventBg.setImageDrawable(
                                ContextCompat.getDrawable(
                                    context,
                                    R.drawable.img_competition_f
                                )
                            )
                        } else {
                            tvEventState.text =
                                context.getString(R.string.event_notify_me_of_the_start)
                            tvEventState.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.cerulean
                                )
                            )
                            ivEventBg.setImageDrawable(
                                ContextCompat.getDrawable(
                                    context,
                                    R.drawable.img_competition_f_blue
                                )
                            )
                        }
                    }

                    root.setOnClickListener {
                        mOnEventClickListener?.invoke(item)
                    }
                }
            }
        }
    }
}

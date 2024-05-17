package com.jtsportech.visport.android.dashboard.home.training

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.cloudhearing.android.lib_base.base.BaseListAdapter
import com.cloudhearing.android.lib_base.utils.toDp
import com.cloudhearing.android.lib_common.image.loadImage
import com.cloudhearing.android.lib_common.image.loadRoundCornerImage
import com.cloudhearing.android.lib_common.network.dataSource.home.competition.Competition
import com.jtsportech.visport.android.dataSource.home.event.CalendarPanelEntity
import com.jtsportech.visport.android.databinding.ItemTrainingBinding
import com.jtsportech.visport.android.utils.date.DateUtils
import com.jtsportech.visport.android.utils.img

/**
 * Author: BenChen
 * Date: 2024/01/12 10:59
 * Email:chenxiaobin@cloudhearing.cn
 */
class TrainingAdapter : BaseListAdapter<Competition, ItemTrainingBinding>(TrainDiffCallback()) {

    private var mOnClickListener: ((Competition) -> Unit)? = null

    fun setOnClickListener(listener: (Competition) -> Unit) {
        mOnClickListener = listener
    }

    class TrainDiffCallback : DiffUtil.ItemCallback<Competition>() {
        override fun areItemsTheSame(oldItem: Competition, newItem: Competition): Boolean {
            return false
        }

        override fun areContentsTheSame(oldItem: Competition, newItem: Competition): Boolean {
            return false
        }

    }

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup): ItemTrainingBinding {
        return ItemTrainingBinding.inflate(inflater, parent, false)
    }

    override fun bind(binding: ItemTrainingBinding, item: Competition, position: Int) {
        val context = binding.root.context

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
                mOnClickListener?.invoke(item)
            }
        }
    }
}
package com.jtsportech.visport.android.mine.myFavorites.train

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import com.cloudhearing.android.lib_base.base.BaseListAdapter
import com.cloudhearing.android.lib_base.utils.show
import com.cloudhearing.android.lib_base.utils.toDp
import com.cloudhearing.android.lib_common.image.loadImage
import com.cloudhearing.android.lib_common.image.loadRoundCornerImage
import com.cloudhearing.android.lib_common.network.dataSource.home.competition.Competition
import com.jtsportech.visport.android.databinding.ItemTrainingBinding
import com.jtsportech.visport.android.utils.date.DateUtils
import com.jtsportech.visport.android.utils.img
import timber.log.Timber

/**
 * Author: BenChen
 * Date: 2024/03/02 15:40
 * Email:chenxiaobin@cloudhearing.cn
 */
class TrainFavoritesAdapter :
    BaseListAdapter<Competition, ItemTrainingBinding>(TrainFavoritesDiffCallback()) {

    private var mOnClickListener: ((Competition) -> Unit)? = null

    fun setOnClickListener(listener: (Competition) -> Unit) {
        mOnClickListener = listener
    }

    class TrainFavoritesDiffCallback : DiffUtil.ItemCallback<Competition>() {
        override fun areItemsTheSame(oldItem: Competition, newItem: Competition): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Competition, newItem: Competition): Boolean {
            return oldItem.isEditMode == newItem.isEditMode && oldItem.isSelected == newItem.isSelected
        }

    }

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup): ItemTrainingBinding {
        return ItemTrainingBinding.inflate(inflater, parent, false)
    }

    override fun bind(binding: ItemTrainingBinding, item: Competition, position: Int) {
        val context = binding.root.context
        Timber.d("走了2 TrainFavoritesAdapter isEditMode=${item.isEditMode} isSelected=${item.isSelected}")
        binding.apply {
            ivEdit.isVisible = item.isEditMode
            ivEdit.isSelected = item.isSelected
            ciLogo.loadImage(url = item.team1OrgLogoImagePath?.img())
            tvTrainingName.text = item.name
            item.matchTime?.let {
                tvTime.text = DateUtils.convertToFormat1(it)
            }
            ivTrainingCover.loadRoundCornerImage(
                url = item.previewImageFilePath?.img(),
                radius = 6.toDp.toInt()
            )

            groupCompilation.isVisible = item.favoriteType == "EVENTS"

            root.setOnClickListener {
                mOnClickListener?.invoke(item)
            }
        }
    }
}
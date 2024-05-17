package com.jtsportech.visport.android.dashboard.parents

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.cloudhearing.android.lib_base.base.BaseListAdapter
import com.cloudhearing.android.lib_base.utils.toDp
import com.cloudhearing.android.lib_common.image.loadRoundCornerImage
import com.cloudhearing.android.lib_common.network.dataSource.player.PlayerEntityItem
import com.jtsportech.visport.android.databinding.IteamVideoHighlightsBinding

/**
 * Author: BenChen
 * Date: 2024/03/25 19:32
 * Email:chenxiaobin@cloudhearing.cn
 */
class VideoHighlightAdapter :
    BaseListAdapter<PlayerEntityItem, IteamVideoHighlightsBinding>(VideoHighlightDiffCallback()) {

    private var mOnClickListener: ((PlayerEntityItem) -> Unit)? = null

    fun setOnClickListener(listener: (PlayerEntityItem) -> Unit) {
        mOnClickListener = listener
    }

    class VideoHighlightDiffCallback : DiffUtil.ItemCallback<PlayerEntityItem>() {
        override fun areItemsTheSame(
            oldItem: PlayerEntityItem,
            newItem: PlayerEntityItem
        ): Boolean {
            return false
        }

        override fun areContentsTheSame(
            oldItem: PlayerEntityItem,
            newItem: PlayerEntityItem
        ): Boolean {
            return false
        }

    }

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): IteamVideoHighlightsBinding {
        return IteamVideoHighlightsBinding.inflate(inflater, parent, false)
    }

    override fun bind(binding: IteamVideoHighlightsBinding, item: PlayerEntityItem, position: Int) {
        val context = binding.root.context

        binding.apply {
            ivThumb.loadRoundCornerImage(url = item.thumbUrl, radius = 6.toDp.toInt())
            tvTime.text = item.createTime
            tvVideoName.text = "${item.matchInfoName}-${item.playerFrontUserName}"


            root.setOnClickListener {
                mOnClickListener?.invoke(item)
            }
        }
    }
}
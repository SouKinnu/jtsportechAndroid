package com.jtsportech.visport.android.playerdetail.playervideo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.cloudhearing.android.lib_base.base.BaseListAdapter
import com.cloudhearing.android.lib_base.utils.toDp
import com.cloudhearing.android.lib_common.image.loadRoundCornerImage
import com.cloudhearing.android.lib_common.network.dataSource.player.PlayerEntityItem
import com.jtsportech.visport.android.databinding.ItemVideoList2Binding


class PlayerVideoAdapter :
    BaseListAdapter<PlayerEntityItem, ItemVideoList2Binding>(VideoDiffCallback()) {
    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemVideoList2Binding {
        return ItemVideoList2Binding.inflate(inflater, parent, false)
    }

    private lateinit var onItemClickListener: OnItemClickListener

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }
    override fun bind(binding: ItemVideoList2Binding, item: PlayerEntityItem, position: Int) {
        val s = item.matchInfoName + "-" + item.playerFrontUserName
        binding.name.text = s
        binding.image.loadRoundCornerImage(
            url = item.thumbUrl,
            radius = 6.toDp.toInt()
        )
        binding.root.setOnClickListener {
            if (::onItemClickListener.isInitialized) {
                onItemClickListener.onItemClick(item)
            }
        }
    }

    class VideoDiffCallback : DiffUtil.ItemCallback<PlayerEntityItem>() {
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

    interface OnItemClickListener {
        fun onItemClick(item: PlayerEntityItem)
    }
}
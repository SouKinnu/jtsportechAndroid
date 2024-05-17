package com.jtsportech.visport.android.playerdetail.playerevents


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.cloudhearing.android.lib_base.base.BaseListAdapter
import com.cloudhearing.android.lib_base.utils.toDp
import com.cloudhearing.android.lib_common.image.loadRoundCornerImage
import com.cloudhearing.android.lib_common.network.dataSource.player.playerevents.PlayerEventsEntityItem
import com.cloudhearing.android.lib_common.utils.date.DateTimeUtil
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.databinding.ItemVideoList2Binding


class PlayerEventsAdapter :
    BaseListAdapter<PlayerEventsEntityItem, ItemVideoList2Binding>(VideoDiffCallback()) {
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

    override fun bind(binding: ItemVideoList2Binding, item: PlayerEventsEntityItem, position: Int) {
        binding.name.text = item.eventName + " - " + item.eventNum.toString()
        val time =
            DateTimeUtil.get().convertMillis(item.eventFrom) + "~" + DateTimeUtil.get()
                .convertMillis(item.eventDuration + item.eventFrom)
        binding.time.text = time
        binding.image.loadRoundCornerImage(
            url = item.thumbUrl,
            placeHolder = R.drawable.img_video_def,
            radius = 6.toDp.toInt()
        )
        binding.root.setOnClickListener {
            if (::onItemClickListener.isInitialized) {
                onItemClickListener.onItemClick(item)
            }
        }
    }

    class VideoDiffCallback : DiffUtil.ItemCallback<PlayerEventsEntityItem>() {
        override fun areItemsTheSame(
            oldItem: PlayerEventsEntityItem,
            newItem: PlayerEventsEntityItem
        ): Boolean {
            return false
        }

        override fun areContentsTheSame(
            oldItem: PlayerEventsEntityItem,
            newItem: PlayerEventsEntityItem
        ): Boolean {
            return false
        }
    }

    interface OnItemClickListener {
        fun onItemClick(item: PlayerEventsEntityItem)
    }
}
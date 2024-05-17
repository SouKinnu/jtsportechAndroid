package com.jtsportech.visport.android.videoplay.videolist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.cloudhearing.android.lib_base.base.BaseListAdapter
import com.cloudhearing.android.lib_base.utils.toDp
import com.cloudhearing.android.lib_common.image.loadRoundCornerImage
import com.cloudhearing.android.lib_common.network.dataSource.racedetail.Event
import com.jtsportech.visport.android.databinding.ItemVideoListBinding

class VideoListAdapter(private var onCallBack: OnCallBack) :
    BaseListAdapter<Event, ItemVideoListBinding>(VideoDiffCallback()) {
    interface OnCallBack {
        fun onClickListener(eventName: String,id:String)
    }

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemVideoListBinding {
        return ItemVideoListBinding.inflate(inflater, parent, false)
    }

    override fun bind(binding: ItemVideoListBinding, item: Event, position: Int) {

        binding.name.text = item.eventPlayerList[0].playerFrontUserName
        binding.image.loadRoundCornerImage(
            url = item.thumbUrl,
            radius = 6.toDp.toInt()
        )
        binding.LinearLayout.setOnClickListener {
            onCallBack.onClickListener(item.eventName,item.id)
        }
    }

    class VideoDiffCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return false
        }

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
            return false
        }
    }
}
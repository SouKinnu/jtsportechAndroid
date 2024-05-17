package com.jtsportech.visport.android.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.cloudhearing.android.lib_base.base.BaseListAdapter
import com.cloudhearing.android.lib_base.utils.toDp
import com.cloudhearing.android.lib_common.image.loadRoundCornerImage
import com.cloudhearing.android.lib_common.network.dataSource.homebanner.EventsItem
import com.jtsportech.visport.android.databinding.ItemImageBannerBinding
import com.jtsportech.visport.android.utils.img

class BannerAdapter : BaseListAdapter<EventsItem, ItemImageBannerBinding>(ImageDiffCallback()) {
    private var mOnClickListener: ((EventsItem) -> Unit)? = null
    fun setOnClickListener(listener: (EventsItem) -> Unit) {
        mOnClickListener = listener
    }

    class ImageDiffCallback : DiffUtil.ItemCallback<EventsItem>() {
        override fun areItemsTheSame(oldItem: EventsItem, newItem: EventsItem): Boolean {
            return false
        }

        override fun areContentsTheSame(oldItem: EventsItem, newItem: EventsItem): Boolean {
            return false
        }
    }

    override fun createBinding(
        inflater: LayoutInflater, parent: ViewGroup
    ): ItemImageBannerBinding {
        return ItemImageBannerBinding.inflate(inflater, parent, false)
    }

    override fun bind(binding: ItemImageBannerBinding, item: EventsItem, position: Int) {
        binding.image.loadRoundCornerImage(
            url = item.imagePath?.img(), radius = 6.toDp.toInt()
        )
        binding.image.setOnClickListener {
            mOnClickListener?.invoke(item)
        }
    }
}
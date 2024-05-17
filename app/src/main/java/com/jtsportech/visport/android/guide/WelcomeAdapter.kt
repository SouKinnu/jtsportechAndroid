package com.jtsportech.visport.android.guide

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.cloudhearing.android.lib_base.base.BaseListAdapter
import com.cloudhearing.android.lib_common.image.loadRoundCornerImage
import com.cloudhearing.android.lib_common.network.dataSource.welcome.WelcomeItem
import com.jtsportech.visport.android.databinding.ItemImageWelcomeBinding
import com.jtsportech.visport.android.utils.img

class WelcomeAdapter :
    BaseListAdapter<WelcomeItem, ItemImageWelcomeBinding>(ImageDiffCallback()) {
    class ImageDiffCallback : DiffUtil.ItemCallback<WelcomeItem>() {
        override fun areItemsTheSame(oldItem: WelcomeItem, newItem: WelcomeItem): Boolean {
            return false
        }

        override fun areContentsTheSame(oldItem: WelcomeItem, newItem: WelcomeItem): Boolean {
            return false
        }
    }

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemImageWelcomeBinding {
        return ItemImageWelcomeBinding.inflate(inflater, parent, false)
    }

    override fun bind(binding: ItemImageWelcomeBinding, item: WelcomeItem, position: Int) {
        binding.image.loadRoundCornerImage(
            url = item.imgPath.img()
        )
    }
}
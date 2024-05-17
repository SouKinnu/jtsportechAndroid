package com.jtsportech.visport.android.dashboard.message

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import com.cloudhearing.android.lib_base.base.BaseListAdapter
import com.jtsportech.visport.android.dataSource.message.MessageEntity
import com.jtsportech.visport.android.databinding.ItemMessageTypeBinding

/**
 * Author: BenChen
 * Date: 2024/01/03 17:53
 * Email:chenxiaobin@cloudhearing.cn
 */
class MessageTypeAdapter :
    BaseListAdapter<MessageEntity, ItemMessageTypeBinding>(MessageTypDiffCallback()) {

    private var mOnClickListener: ((Int) -> Unit)? = null

    fun setOnClickListener(listener: (Int) -> Unit) {
        mOnClickListener = listener
    }

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemMessageTypeBinding {
        return ItemMessageTypeBinding.inflate(inflater, parent, false)
    }

    override fun bind(binding: ItemMessageTypeBinding, item: MessageEntity, position: Int) {
        val context = binding.root.context

        binding.ivMessageLogo.setImageDrawable(ContextCompat.getDrawable(context, item.iconRes))
        binding.tvTitle.text = item.title
        binding.tvSubtitle.text = item.subtitle
        binding.tvTimer.text = item.time
        binding.ivNewMessage.isVisible = item.hasNewMessage

        binding.root.setOnClickListener {
            mOnClickListener?.invoke(item.type)
        }
    }

    class MessageTypDiffCallback : DiffUtil.ItemCallback<MessageEntity>() {
        override fun areItemsTheSame(oldItem: MessageEntity, newItem: MessageEntity): Boolean {
            return oldItem.type == newItem.type
        }

        override fun areContentsTheSame(oldItem: MessageEntity, newItem: MessageEntity): Boolean {
            return oldItem.subtitle == newItem.subtitle && oldItem.hasNewMessage == newItem.hasNewMessage
        }

    }
}
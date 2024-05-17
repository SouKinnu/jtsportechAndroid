package com.jtsportech.visport.android.message.interactive

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import com.cloudhearing.android.lib_base.base.BaseListAdapter
import com.cloudhearing.android.lib_base.utils.hide
import com.cloudhearing.android.lib_base.utils.show
import com.cloudhearing.android.lib_common.image.loadCircleImage
import com.cloudhearing.android.lib_common.network.dataSource.messages.MessageNotice
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.databinding.ItemInteractiveMessageBinding
import com.jtsportech.visport.android.utils.img

/**
 * Author: BenChen
 * Date: 2024/03/29 19:28
 * Email:chenxiaobin@cloudhearing.cn
 */
class InteractiveMessageAdapter :
    BaseListAdapter<MessageNotice, ItemInteractiveMessageBinding>(InteractiveMessageDiffCallback()) {

    companion object {
        const val CONTENT_TEXT = "TEXT"
        const val CONTENT_AUDIO = "AUDIO"
    }


    private var mOnClickListener: ((MessageNotice) -> Unit)? = null
    private var mOnPlayerClickListener: ((MessageNotice) -> Unit)? = null

    fun setOnClickListener(listener: (MessageNotice) -> Unit) {
        mOnClickListener = listener
    }

    fun setOnPlayerClickListener(listener: (MessageNotice) -> Unit) {
        mOnPlayerClickListener = listener
    }


    class InteractiveMessageDiffCallback : DiffUtil.ItemCallback<MessageNotice>() {
        override fun areItemsTheSame(oldItem: MessageNotice, newItem: MessageNotice): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MessageNotice, newItem: MessageNotice): Boolean {
            return oldItem.msgTargetInfo?.isPlaying == newItem.msgTargetInfo?.isPlaying
        }

    }

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemInteractiveMessageBinding {
        return ItemInteractiveMessageBinding.inflate(inflater, parent, false)
    }

    override fun bind(binding: ItemInteractiveMessageBinding, item: MessageNotice, position: Int) {
        val context = binding.root.context

        val msgTargetInfo = item.msgTargetInfo

        binding.apply {
            ivAvatar.loadCircleImage(url = msgTargetInfo?.frontUserAvatarPath?.img())
            tvName.text = msgTargetInfo?.frontUserName
            tvMessage.text = msgTargetInfo?.contentText
            tvTime.text = msgTargetInfo?.createTime

            if (msgTargetInfo?.contentType == CONTENT_AUDIO) {
                llVoice.show()
                tvMessage.hide()
            } else {
                llVoice.hide()
                tvMessage.show()
            }

            tvDuration.text = "${msgTargetInfo?.audioDuration}″"

            if (msgTargetInfo?.isPlaying == true) {
                ivPlayStatus.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.icon_voice_pause
                    )
                )
            } else {
                ivPlayStatus.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.icon_voice_play
                    )
                )
            }

            llVoice.setOnClickListener {
                mOnPlayerClickListener?.invoke(item)
            }

            root.setOnClickListener {
                mOnClickListener?.invoke(item)
            }
        }
    }
}
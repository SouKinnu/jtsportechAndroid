package com.jtsportech.visport.android.racedetail.messages

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding
import com.cloudhearing.android.lib_base.base.BaseListViewTypeAdapter
import com.cloudhearing.android.lib_base.utils.toDp
import com.cloudhearing.android.lib_common.image.loadRoundCornerImage
import com.cloudhearing.android.lib_common.network.dataSource.messages.Reply
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.databinding.ItemMessagesDirectAudioBinding
import com.jtsportech.visport.android.databinding.ItemMessagesDirectTextBinding
import com.jtsportech.visport.android.databinding.ItemMessagesReplyAudioBinding
import com.jtsportech.visport.android.databinding.ItemMessagesReplyTextBinding
import com.jtsportech.visport.android.utils.img

class MessagesAdapter :
    BaseListViewTypeAdapter<Reply, ViewBinding>(MessagesDiffCallback()) {
    private var mOnMoreClickListener: ((Int, List<Reply>) -> Unit)? = null
    private var mOnReplyClickListener: ((Reply, Int) -> Unit)? = null
    private var mOnPlayClickListener: ((Reply) -> Unit)? = null

    companion object {
        const val CONTENT_AUDIO = "AUDIO"
    }

    fun setOnPlayClickListener(listener: (Reply) -> Unit) {
        mOnPlayClickListener = listener
    }

    fun setOnMoreClickListener(listener: (Int, List<Reply>) -> Unit) {
        mOnMoreClickListener = listener
    }

    fun setOnReplyClickListener(listener: (Reply, Int) -> Unit) {
        mOnReplyClickListener = listener
    }

    override fun getItemViewType(position: Int, item: Reply): Int {
        if (item.criticizeType == "DIRECT") {
            if (item.contentType == "TEXT") return 0
            else if (item.contentType == "AUDIO") return 1
        } else if (item.criticizeType == "REPLY") {
            if (item.contentType == "TEXT") return 2
            else if (item.contentType == "AUDIO") return 3
        }
        return 0
    }

    override fun onCreateBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewBinding {
        return when (viewType) {
            0 -> ItemMessagesDirectTextBinding.inflate(inflater, parent, false)
            1 -> ItemMessagesDirectAudioBinding.inflate(inflater, parent, false)
            2 -> ItemMessagesReplyTextBinding.inflate(inflater, parent, false)
            3 -> ItemMessagesReplyAudioBinding.inflate(inflater, parent, false)
            else -> ItemMessagesDirectTextBinding.inflate(inflater, parent, false)
        }
    }

    override fun onBind(binding: ViewBinding, item: Reply, position: Int) {
        val context = binding.root.context
        val spreadText = ContextCompat.getString(context, R.string.spread)
        val replyText = ContextCompat.getString(context, R.string.reply)
        when (binding) {
            is ItemMessagesDirectTextBinding -> {
                binding.apply {
                    pic.loadRoundCornerImage(
                        url = item.frontUserAvatarPath?.img(),
                        placeHolder = R.drawable.ic_avatar_mine_def,
                        radius = 6.toDp.toInt()
                    )
                    name.text = item.frontUserName
                    time.text = item.createTime
                    content.text = item.contentText
                    if (item.replyList?.isEmpty() == true) spread.visibility = View.GONE
                    else reply.apply {
                        text = "$spreadText ${item.replyList?.size} $replyText"
                        setOnClickListener {
                            item.isReply = true
                            item.replyList?.let { it1 ->
                                mOnMoreClickListener?.invoke(
                                    position,
                                    it1
                                )
                            }
                        }
                        if (item.isReply) {
                            spread.visibility = View.GONE
                        } else
                            spread.visibility = View.VISIBLE
                    }

                    LinearLayout.setOnClickListener {
                        mOnReplyClickListener?.invoke(item, position)
                    }
                }
            }

            is ItemMessagesDirectAudioBinding -> {
                binding.apply {
                    pic.loadRoundCornerImage(
                        url = item.frontUserAvatarPath?.img(),
                        placeHolder = R.drawable.ic_avatar_mine_def,
                        radius = 6.toDp.toInt()
                    )
                    name.text = item.frontUserName
                    time.text = item.createTime
                    second.text = item.audioDuration.toString()
                    if (item.replyList?.isEmpty() == true) spread.visibility = View.GONE
                    else reply.apply {
                        text = "${spreadText}${item.replyList?.size}${replyText}"
                        setOnClickListener {
                            item.isReply = true
                            item.replyList?.let { it1 ->
                                mOnMoreClickListener?.invoke(
                                    position,
                                    it1
                                )
                            }
                        }
                        if (item.isReply) {
                            spread.visibility = View.GONE
                        } else
                            spread.visibility = View.VISIBLE
                    }
                    if (item.isPlaying) isPlay.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.icon_voice_pause
                        )
                    )
                    else isPlay.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.icon_voice_play
                        )
                    )
                    LinearLayout.setOnClickListener {
                        mOnReplyClickListener?.invoke(item, position)
                    }
                    playing.setOnClickListener {
                        mOnPlayClickListener?.invoke(item)
                    }
                }
            }

            is ItemMessagesReplyTextBinding -> {
                binding.apply {
                    pic.loadRoundCornerImage(
                        url = item.frontUserAvatarPath?.img(),
                        placeHolder = R.drawable.ic_avatar_mine_def,
                        radius = 6.toDp.toInt()
                    )
                    name.text = item.frontUserName + " ▸ " + item.toUserName
                    time.text = item.createTime
                    content.text = item.contentText
                    LinearLayout.setOnClickListener {
                        mOnReplyClickListener?.invoke(item, position)
                    }
                }
            }

            is ItemMessagesReplyAudioBinding ->
                binding.apply {
                    pic.loadRoundCornerImage(
                        url = item.frontUserAvatarPath?.img(),
                        placeHolder = R.drawable.ic_avatar_mine_def,
                        radius = 6.toDp.toInt()
                    )
                    name.text = item.frontUserName + " ▸ " + item.toUserName
                    time.text = item.createTime
                    second.text = item.audioDuration.toString()
                    LinearLayout.setOnClickListener {
                        mOnReplyClickListener?.invoke(item, position)
                    }
                    playing.setOnClickListener {
                        mOnPlayClickListener?.invoke(item)
                    }
                    if (item.isPlaying) {
                        isPlay.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.icon_voice_pause
                            )
                        )
                    } else {
                        isPlay.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.icon_voice_play
                            )
                        )
                    }
                }
        }
    }
}

class MessagesDiffCallback : DiffUtil.ItemCallback<Reply>() {
    override fun areItemsTheSame(oldItem: Reply, newItem: Reply): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Reply, newItem: Reply): Boolean {
        return oldItem.isPlaying == newItem.isPlaying
    }
}
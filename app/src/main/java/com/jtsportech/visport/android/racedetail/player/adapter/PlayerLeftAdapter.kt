package com.jtsportech.visport.android.racedetail.player.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.cloudhearing.android.lib_base.base.BaseListAdapter
import com.cloudhearing.android.lib_base.utils.toDp
import com.cloudhearing.android.lib_common.image.loadRoundCornerImage
import com.cloudhearing.android.lib_common.network.dataSource.racedetail.Team1Player
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.databinding.ItemPlayerListBinding
import com.jtsportech.visport.android.utils.img

class PlayerLeftAdapter :
    BaseListAdapter<Team1Player, ItemPlayerListBinding>(EventDataDiffCallback()) {
    private var mOnClickListener: ((Team1Player) -> Unit)? = null
    fun setOnClickListener(listener: (Team1Player) -> Unit) {
        mOnClickListener = listener
    }

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup): ItemPlayerListBinding {
        return ItemPlayerListBinding.inflate(inflater, parent, false)
    }

    override fun bind(binding: ItemPlayerListBinding, item: Team1Player, position: Int) {
        binding.name.text = item.playerFrontUserName
        binding.num.text = item.uniformNo.toString()
        binding.location.text = item.pitchPosition
        binding.image.loadRoundCornerImage(
            url = item.playerUserAvatarPath?.img(),
            radius = 6.toDp.toInt(),
            placeHolder = R.drawable.ic_avatar_mine_def
        )
        binding.LinearLayout.setOnClickListener {
            mOnClickListener?.invoke(item)
        }
    }

    class EventDataDiffCallback : DiffUtil.ItemCallback<Team1Player>() {
        override fun areItemsTheSame(oldItem: Team1Player, newItem: Team1Player): Boolean {
            return false
        }

        override fun areContentsTheSame(oldItem: Team1Player, newItem: Team1Player): Boolean {
            return false
        }
    }
}
package com.jtsportech.visport.android.racedetail.player.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.cloudhearing.android.lib_base.base.BaseListAdapter
import com.cloudhearing.android.lib_common.network.dataSource.racedetail.Team1Player
import com.jtsportech.visport.android.databinding.ItemLineUpBinding

class LineUpAdapter(var drawable: Drawable) :
    BaseListAdapter<Team1Player, ItemLineUpBinding>(EventDataDiffCallback()) {
    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup): ItemLineUpBinding {
        return ItemLineUpBinding.inflate(inflater, parent, false)
    }

    override fun bind(binding: ItemLineUpBinding, item: Team1Player, position: Int) {
        binding.num.text = item.uniformNo.toString()
        binding.num.background = drawable
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
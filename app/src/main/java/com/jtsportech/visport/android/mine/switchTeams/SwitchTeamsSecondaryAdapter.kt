package com.jtsportech.visport.android.mine.switchTeams

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.cloudhearing.android.lib_base.base.IDelegateAdapter
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.components.CtaImageButton
import com.jtsportech.visport.android.dataSource.mine.switchTeams.SwitchTeamEntity
import com.jtsportech.visport.android.dataSource.mine.switchTeams.SwitchTeamLayoutType

/**
 * Author: BenChen
 * Date: 2024/04/02 17:00
 * Email:chenxiaobin@cloudhearing.cn
 */
class SwitchTeamsSecondaryAdapter : IDelegateAdapter<SwitchTeamEntity> {

    private var mOnClickListener: ((SwitchTeamEntity) -> Unit)? = null

    fun setOnClickListener(listener: (SwitchTeamEntity) -> Unit) {
        mOnClickListener = listener
    }

    override fun isForViewType(t: SwitchTeamEntity): Boolean {
        return t.layoutType == SwitchTeamLayoutType.GROUP_LAYOUT
    }

    override fun hasViewType(): Int {
        return SwitchTeamLayoutType.GROUP_LAYOUT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_group, parent, false)
        return SwitchTeamsSecondaryViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        t: SwitchTeamEntity
    ) {
        val viewHolder = holder as SwitchTeamsSecondaryViewHolder
        val context = viewHolder.itemView.context

        if (t.isTeamSelected && t.isGroupSelected == true) {
            holder.mCtaGroup.enable()
        } else {
            holder.mCtaGroup.disable()
        }

        holder.mTvGroupName.text = t.groupName

        holder.itemView.setOnClickListener {
            mOnClickListener?.invoke(t)
        }
    }

    class SwitchTeamsSecondaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mTvGroupName: AppCompatTextView = itemView.findViewById(R.id.tv_group_name)
        val mCtaGroup: CtaImageButton = itemView.findViewById(R.id.cta_group)
    }
}
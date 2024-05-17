package com.jtsportech.visport.android.mine.switchTeams

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.cloudhearing.android.lib_base.base.IDelegateAdapter
import com.cloudhearing.android.lib_base.utils.toDp
import com.cloudhearing.android.lib_common.image.loadRoundCornerImage
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.dataSource.mine.switchTeams.SwitchTeamEntity
import com.jtsportech.visport.android.dataSource.mine.switchTeams.SwitchTeamLayoutType
import com.jtsportech.visport.android.utils.img

/**
 * Author: BenChen
 * Date: 2024/04/02 16:38
 * Email:chenxiaobin@cloudhearing.cn
 */
class SwitchTeamsPrimaryAdapter : IDelegateAdapter<SwitchTeamEntity> {

    private var mOnClickListener: ((SwitchTeamEntity) -> Unit)? = null

    fun setOnClickListener(listener: (SwitchTeamEntity) -> Unit) {
        mOnClickListener = listener
    }

    override fun isForViewType(t: SwitchTeamEntity): Boolean {
        return t.layoutType == SwitchTeamLayoutType.TEAM_LAYOUT
    }

    override fun hasViewType(): Int {
        return SwitchTeamLayoutType.TEAM_LAYOUT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_team, parent, false)
        return SwitchTeamsPrimaryViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        t: SwitchTeamEntity
    ) {
        val viewHolder = holder as SwitchTeamsPrimaryViewHolder
        val context = viewHolder.itemView.context

        holder.mIvChecked.isVisible = t.isTeamSelected
        holder.mIvTeam.loadRoundCornerImage(
            url = t.teamLogoImageFilePath?.img(),
            radius = 6.toDp.toInt(),
            placeHolder = R.drawable.img_team_def
        )
        holder.mTvTeamName.text = t.teamName

        holder.itemView.setOnClickListener {
            mOnClickListener?.invoke(t)
        }
    }

    class SwitchTeamsPrimaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mIvChecked: AppCompatImageView = itemView.findViewById(R.id.iv_checked)
        val mIvTeam: AppCompatImageView = itemView.findViewById(R.id.iv_team)
        val mTvTeamName: AppCompatTextView = itemView.findViewById(R.id.tv_team_name)
    }
}
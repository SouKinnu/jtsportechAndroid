package com.jtsportech.visport.android.message.match

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.cloudhearing.android.lib_base.base.BaseListAdapter
import com.cloudhearing.android.lib_base.utils.toDp
import com.cloudhearing.android.lib_common.image.loadRoundCornerImage
import com.cloudhearing.android.lib_common.network.dataSource.messages.MessageNotice
import com.jtsportech.visport.android.databinding.ItemMatchMessageBinding
import com.jtsportech.visport.android.utils.date.DateUtils
import com.jtsportech.visport.android.utils.img

/**
 * Author: BenChen
 * Date: 2024/03/29 18:17
 * Email:chenxiaobin@cloudhearing.cn
 */
class MatchMessageAdapter :
    BaseListAdapter<MessageNotice, ItemMatchMessageBinding>(MatchMessageDiffCallback()) {

    private var mOnClickListener: ((MessageNotice) -> Unit)? = null

    fun setOnClickListener(listener: (MessageNotice) -> Unit) {
        mOnClickListener = listener
    }

    class MatchMessageDiffCallback : DiffUtil.ItemCallback<MessageNotice>() {
        override fun areItemsTheSame(oldItem: MessageNotice, newItem: MessageNotice): Boolean {
            return false
        }

        override fun areContentsTheSame(oldItem: MessageNotice, newItem: MessageNotice): Boolean {
            return false
        }

    }

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemMatchMessageBinding {
        return ItemMatchMessageBinding.inflate(inflater, parent, false)
    }

    override fun bind(binding: ItemMatchMessageBinding, item: MessageNotice, position: Int) {
        val context = binding.root.context

        val msgTargetInfo = item.msgTargetInfo

        binding.apply {
            tvMessage.text = item.msgContent

            tvStadium.text = msgTargetInfo?.playingField
            tvTeamScore1.text = "${msgTargetInfo?.team1Score}"
            tvTeamScore2.text = "${msgTargetInfo?.team2Score}"
            tvCompetitionInfo.text =
                "${DateUtils.convertToFormat2(msgTargetInfo?.matchTime.orEmpty())}/${msgTargetInfo?.name}"
            tvTeamName1.text = msgTargetInfo?.team1OrganizationName
            tvTeamName2.text = msgTargetInfo?.team2OrganizationName
            ivTeamLogo1.loadRoundCornerImage(
                url = msgTargetInfo?.team1OrgLogoImagePath?.img(),
                radius = 6.toDp.toInt()
            )
            ivTeamLogo2.loadRoundCornerImage(
                url = msgTargetInfo?.team2OrgLogoImagePath?.img(),
                radius = 6.toDp.toInt()
            )
        }
    }
}
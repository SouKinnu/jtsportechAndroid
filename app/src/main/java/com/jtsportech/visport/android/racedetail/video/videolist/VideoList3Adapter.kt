package com.jtsportech.visport.android.racedetail.video.videolist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.cloudhearing.android.lib_base.base.BaseListAdapter
import com.cloudhearing.android.lib_base.utils.toDp
import com.cloudhearing.android.lib_common.image.loadRoundCornerImage
import com.cloudhearing.android.lib_common.network.dataSource.racedetail.Video
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.databinding.ItemVideoList2Binding
import com.cloudhearing.android.lib_common.utils.date.DateTimeUtil

class VideoList3Adapter(private val isTab: Boolean) : BaseListAdapter<Video, ItemVideoList2Binding>(VideoDiffCallback()) {

    private var mOnClickListener: ((String) -> Unit)? = null
    private var selectIndex = -1
    fun setOnClickListener(listener: (String) -> Unit) {
        mOnClickListener = listener
    }

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemVideoList2Binding {
        return ItemVideoList2Binding.inflate(inflater, parent, false)
    }

    override fun bind(binding: ItemVideoList2Binding, item: Video, position: Int) {
        binding.name.apply {
            text = item.perspective
            setTextColor(
                if (isTab)
                    context.getColor(R.color.mine_shaft)
                else
                    context.getColor(R.color.white)
            )

        }
        binding.time.text = DateTimeUtil.get().convertMillis(item.duration)
        binding.image.loadRoundCornerImage(
            url = item.thumbUrl,
            placeHolder = R.drawable.img_video_def,
            radius = 6.toDp.toInt()
        )
        binding.image1.setBackgroundResource(if (selectIndex == position) R.drawable.shape_video_radius_bg else R.color.transparent)
        binding.root.setOnClickListener {
            mOnClickListener?.invoke(item.id)
        }
    }

    fun setSelectIndex(index: Int) {
        selectIndex = index
        notifyDataSetChanged()
    }

    fun setUnSelectIndex(index: Int) {
        selectIndex = -1
        notifyItemChanged(index)
    }

    class VideoDiffCallback : DiffUtil.ItemCallback<Video>() {
        override fun areItemsTheSame(oldItem: Video, newItem: Video): Boolean {
            return false
        }

        override fun areContentsTheSame(oldItem: Video, newItem: Video): Boolean {
            return false
        }
    }
}
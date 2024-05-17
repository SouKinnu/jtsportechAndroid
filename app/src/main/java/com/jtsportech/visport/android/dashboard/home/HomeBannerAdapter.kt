package com.jtsportech.visport.android.dashboard.home

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.cloudhearing.android.lib_base.utils.toDp
import com.cloudhearing.android.lib_common.image.loadRoundCornerImage
import com.cloudhearing.android.lib_common.network.dataSource.homebanner.EventsItem
import com.jtsportech.visport.android.utils.img
import com.youth.banner.adapter.BannerAdapter

/**
 * Author: BenChen
 * Date: 2024/03/05 14:09
 * Email:chenxiaobin@cloudhearing.cn
 */
class HomeBannerAdapter(datas: MutableList<EventsItem>) :
    BannerAdapter<EventsItem, HomeBannerAdapter.BannerViewHolder>(
        datas
    ) {


    //更新数据
    fun updateData(data: List<EventsItem?>) {
        //这里的代码自己发挥，比如如下的写法等等
        mDatas.clear()
        mDatas.addAll(data)
        notifyDataSetChanged()
    }

    //创建ViewHolder，可以用viewType这个字段来区分不同的ViewHolder
    override fun onCreateHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val imageView = AppCompatImageView(parent.context)
        //注意，必须设置为match_parent，这个是viewpager2强制要求的
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        imageView.layoutParams = params
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP

        return BannerViewHolder(imageView)
    }

    override fun onBindView(
        holder: BannerViewHolder,
        data: EventsItem,
        position: Int,
        size: Int
    ) {
        (holder.itemView as AppCompatImageView).loadRoundCornerImage(
            url = data.imagePath?.img(),
            radius = 6.toDp.toInt()
        )
    }

    class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var mImageView: AppCompatImageView? = null

        init {
            mImageView = itemView as AppCompatImageView
        }
    }
}
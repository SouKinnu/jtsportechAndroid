package com.jtsportech.visport.android.dataSource.home

import android.animation.AnimatorInflater
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Rect
import android.graphics.Typeface
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import com.cloudhearing.android.lib_base.base.BaseListAdapter
import com.cloudhearing.android.lib_base.utils.hide
import com.cloudhearing.android.lib_base.utils.show
import com.cloudhearing.android.lib_base.utils.toDp
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.databinding.ItemHomeTabBinding

/**
 * Author: BenChen
 * Date: 2024/01/08 16:51
 * Email:chenxiaobin@cloudhearing.cn
 */
class HomeTabAdapter : BaseListAdapter<HomeTabEntity, ItemHomeTabBinding>(HomeTabDiffCallback()) {


    class HomeTabDiffCallback : DiffUtil.ItemCallback<HomeTabEntity>() {
        override fun areItemsTheSame(oldItem: HomeTabEntity, newItem: HomeTabEntity): Boolean {
            return oldItem.tabText == newItem.tabText
        }

        override fun areContentsTheSame(oldItem: HomeTabEntity, newItem: HomeTabEntity): Boolean {
            return oldItem.selected == newItem.selected && oldItem.isMore == newItem.isMore
                    && oldItem.isOpenMorePop == newItem.isOpenMorePop
        }

    }

    private var mOnItemClickListener: ((Int, AppCompatTextView) -> Unit)? = null

    fun setOnItemClickListener(listener: (Int, AppCompatTextView) -> Unit) {
        mOnItemClickListener = listener
    }

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup): ItemHomeTabBinding {
        return ItemHomeTabBinding.inflate(inflater, parent, false)
    }

    override fun bind(binding: ItemHomeTabBinding, item: HomeTabEntity, position: Int) {
        val context = binding.root.context

        if (item.selected) {
            binding.tvTab.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            binding.tvTab.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            binding.vTabIndicator.show()
        } else {
            binding.tvTab.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            binding.tvTab.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
            binding.vTabIndicator.hide()
        }

        if (item.isMore) {
//            val drawable = ContextCompat.getDrawable(context, R.drawable.icon_arrow_down)
//            drawable!!.bounds = Rect(0, 0, drawable.minimumWidth, drawable.minimumHeight)
//            binding.tvTab.setCompoundDrawables(
//                null,
//                null,
//                drawable,
//                null
//            )
//            binding.tvTab.compoundDrawablePadding = 2.toDp.toInt()

            binding.ivDown.show()

            binding.ivDown.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_arrow_down))

            if (item.isOpenMorePop) {
                setupAnimator(context,binding.ivDown)
            }

        } else {
//            binding.tvTab.setCompoundDrawables(
//                null,
//                null,
//                null,
//                null
//            )
//            binding.tvTab.compoundDrawablePadding = 0

            binding.ivDown.hide()
        }

        binding.tvTab.text = item.tabText


        binding.root.setOnClickListener {
            mOnItemClickListener?.invoke(position, binding.tvTab)
        }
    }

    private fun setupAnimator(context: Context, imageview: AppCompatImageView) {
        val rotationAnimator = AnimatorInflater.loadAnimator(context, R.animator.rotate_animator)
        rotationAnimator.setTarget(imageview)
        rotationAnimator.start()
    }
}
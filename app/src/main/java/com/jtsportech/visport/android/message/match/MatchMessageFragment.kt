package com.jtsportech.visport.android.message.match

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cloudhearing.android.lib_base.base.BaseBindingVmFragment
import com.cloudhearing.android.lib_base.utils.observeState
import com.cloudhearing.android.lib_base.utils.toDp
import com.cloudhearing.android.lib_common.utils.SpacesPlusItemDecoration
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.databinding.FragmentMatchMessageBinding
import com.jtsportech.visport.android.racedetail.RaceDetailActivity

class MatchMessageFragment :
    BaseBindingVmFragment<FragmentMatchMessageBinding, MatchMessageViewModel>(
        FragmentMatchMessageBinding::inflate
    ) {

    private val mMatchMessageAdapter: MatchMessageAdapter by lazy {
        MatchMessageAdapter().apply {
            setOnClickListener {
                RaceDetailActivity.jump(requireActivity(), it.msgTargetInfo?.matchInfoId.orEmpty())
            }
        }
    }

    override fun initView() {
        binding.apply {
            apMatchMessage.setOnClickLeftIconListener {
                goBack()
            }

            rvMessage.apply {
                adapter = mMatchMessageAdapter
                addItemDecoration(SpacesPlusItemDecoration(bottomSpace = 10.toDp.toInt()))
                itemAnimator?.changeDuration = 0L
            }
        }
    }

    override fun initData() {

    }

    override fun initEvent() {
        viewModel.run {
            matchMessageListStateFlow.observeState(this@MatchMessageFragment) {
                mMatchMessageAdapter.submitList(it)
                if (it.isEmpty()) {
                    binding.tvNoNews.visibility = View.VISIBLE
                } else {
                    binding.tvNoNews.visibility = View.GONE
                }
            }
        }
    }

    private fun goBack() {
        requireActivity().onBackPressed()
    }
}
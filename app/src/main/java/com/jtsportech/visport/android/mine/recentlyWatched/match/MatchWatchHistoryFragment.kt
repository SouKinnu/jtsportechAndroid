package com.jtsportech.visport.android.mine.recentlyWatched.match

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cloudhearing.android.lib_base.base.BaseBindingVmFragment
import com.cloudhearing.android.lib_base.utils.observeEvent
import com.cloudhearing.android.lib_base.utils.observeState
import com.cloudhearing.android.lib_base.utils.systremToast
import com.cloudhearing.android.lib_base.utils.toDp
import com.cloudhearing.android.lib_common.utils.SpacesItemDecoration
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.databinding.FragmentMatchWatchHistoryBinding
import com.jtsportech.visport.android.racedetail.RaceDetailActivity

class MatchWatchHistoryFragment :
    BaseBindingVmFragment<FragmentMatchWatchHistoryBinding, MatchWatchHistoryViewModel>(
        FragmentMatchWatchHistoryBinding::inflate
    ) {

    private val mMatchWatchHistoryAdapter: MatchWatchHistoryAdapter by lazy {
        MatchWatchHistoryAdapter().apply {
            setOnClickListener {
                it.id?.let { id -> RaceDetailActivity.jump(requireActivity(), id) }
            }
        }
    }

    override fun initView() {
        binding.apply {
            rvMatch.apply {
                adapter = mMatchWatchHistoryAdapter
                addItemDecoration(SpacesItemDecoration(8.toDp.toInt(), false))
                itemAnimator?.changeDuration = 0
            }
        }
    }

    override fun initData() {

    }

    override fun initEvent() {
        viewModel.run {
            matchWatchHistoryListStateFlow.observeState(this@MatchWatchHistoryFragment) {
                mMatchWatchHistoryAdapter.submitList(it)
            }

            toastFlowEvents.observeEvent(this@MatchWatchHistoryFragment) {
                systremToast(it)
            }
        }
    }


}
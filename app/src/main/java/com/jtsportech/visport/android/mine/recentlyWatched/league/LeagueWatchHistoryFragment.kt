package com.jtsportech.visport.android.mine.recentlyWatched.league

import com.cloudhearing.android.lib_base.base.BaseBindingVmFragment
import com.cloudhearing.android.lib_base.utils.observeEvent
import com.cloudhearing.android.lib_base.utils.observeState
import com.cloudhearing.android.lib_base.utils.systremToast
import com.cloudhearing.android.lib_base.utils.toDp
import com.cloudhearing.android.lib_common.utils.SpacesItemDecoration
import com.jtsportech.visport.android.databinding.FragmentLeagueWatchHistoryBinding
import com.jtsportech.visport.android.racedetail.RaceDetailActivity

class LeagueWatchHistoryFragment :
    BaseBindingVmFragment<FragmentLeagueWatchHistoryBinding, LeagueWatchHistoryViewModel>(
        FragmentLeagueWatchHistoryBinding::inflate
    ) {

    private val mLeagueWatchHistoryAdapter: LeagueWatchHistoryAdapter by lazy {
        LeagueWatchHistoryAdapter().apply {
            setOnClickListener {
                it.id?.let { id -> RaceDetailActivity.jump(requireActivity(), id) }
            }
        }
    }

    override fun initView() {
        binding.apply {
            rvLeague.apply {
                adapter = mLeagueWatchHistoryAdapter
                addItemDecoration(SpacesItemDecoration(12.toDp.toInt(), false))
                itemAnimator?.changeDuration = 0
            }
        }
    }

    override fun initData() {

    }

    override fun initEvent() {
        viewModel.run {
            leagueWatchHistoryListStateFlow.observeState(this@LeagueWatchHistoryFragment) {
                mLeagueWatchHistoryAdapter.submitList(it)
            }

            toastFlowEvents.observeEvent(this@LeagueWatchHistoryFragment) {
                systremToast(it)
            }
        }
    }

}
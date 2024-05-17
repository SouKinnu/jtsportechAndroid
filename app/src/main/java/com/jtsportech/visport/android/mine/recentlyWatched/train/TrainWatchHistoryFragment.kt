package com.jtsportech.visport.android.mine.recentlyWatched.train

import com.cloudhearing.android.lib_base.base.BaseBindingVmFragment
import com.cloudhearing.android.lib_base.utils.observeEvent
import com.cloudhearing.android.lib_base.utils.observeState
import com.cloudhearing.android.lib_base.utils.systremToast
import com.cloudhearing.android.lib_base.utils.toDp
import com.cloudhearing.android.lib_common.utils.SpacesItemDecoration
import com.jtsportech.visport.android.databinding.FragmentTrainWatchHistoryBinding
import com.jtsportech.visport.android.racedetail.RaceDetailActivity

class TrainWatchHistoryFragment :
    BaseBindingVmFragment<FragmentTrainWatchHistoryBinding, TrainWatchHistoryViewModel>(
        FragmentTrainWatchHistoryBinding::inflate
    ) {

    private val mTrainWatchHistoryAdapter: TrainWatchHistoryAdapter by lazy {
        TrainWatchHistoryAdapter().apply {
            setOnClickListener {
                it.id?.let { id -> RaceDetailActivity.jump(requireActivity(), id) }
            }
        }
    }

    override fun initView() {
        binding.apply {
            rvTrain.apply {
                adapter = mTrainWatchHistoryAdapter
                addItemDecoration(SpacesItemDecoration(20.toDp.toInt(), false))
                itemAnimator?.changeDuration = 0
            }
        }
    }

    override fun initData() {

    }

    override fun initEvent() {
        viewModel.run {
            trainWatchHistoryListStateFlow.observeState(this@TrainWatchHistoryFragment) {
                mTrainWatchHistoryAdapter.submitList(it)
            }

            toastFlowEvents.observeEvent(this@TrainWatchHistoryFragment) {
                systremToast(it)
            }
        }
    }


}
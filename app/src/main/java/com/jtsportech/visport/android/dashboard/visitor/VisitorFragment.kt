package com.jtsportech.visport.android.dashboard.visitor

import android.widget.FrameLayout
import com.cloudhearing.android.lib_base.base.BaseBindingVmFragment
import com.cloudhearing.android.lib_base.utils.doOnApplyWindowInsets
import com.cloudhearing.android.lib_base.utils.observeState
import com.cloudhearing.android.lib_base.utils.toDp
import com.cloudhearing.android.lib_common.network.dataSource.homebanner.EventsItem
import com.cloudhearing.android.lib_common.utils.ExternalAppUtils
import com.cloudhearing.android.lib_common.utils.SpacesItemDecoration
import com.jtsportech.visport.android.dashboard.DashboardActivity
import com.jtsportech.visport.android.dashboard.home.HomeBannerAdapter
import com.jtsportech.visport.android.dashboard.home.training.TrainingAdapter
import com.jtsportech.visport.android.databinding.FragmentVisitorBinding
import com.jtsportech.visport.android.racedetail.RaceDetailActivity
import com.jtsportech.visport.android.utils.LINK
import com.jtsportech.visport.android.utils.MATCH
import com.youth.banner.indicator.CircleIndicator
import timber.log.Timber

class VisitorFragment :
    BaseBindingVmFragment<FragmentVisitorBinding, VisitorViewModel>(FragmentVisitorBinding::inflate) {

    private val mTrainingAdapter: TrainingAdapter by lazy {
        TrainingAdapter().apply {
            setOnClickListener {
                it.id?.let { id -> RaceDetailActivity.jump(requireActivity(), id) }
            }
        }
    }

    private val mHomeBannerAdapter: HomeBannerAdapter by lazy {
        HomeBannerAdapter(mutableListOf())
    }

    override fun initView() {
        binding.apply {
            banner.apply {
                setAdapter(mHomeBannerAdapter)
                addBannerLifecycleObserver(this@VisitorFragment)
                setIndicator(CircleIndicator(requireContext()))
                setOnBannerListener { data, position ->
                    handleBannerJumpLogic(data as EventsItem)
                }

                rvTraining.apply {
                    adapter = mTrainingAdapter
                    addItemDecoration(SpacesItemDecoration(20.toDp.toInt(), false))
                    itemAnimator?.changeDuration = 0
                }
            }
        }
    }

    override fun initData() {

    }

    override fun initEvent() {
        viewModel.run {
            trainListStateFlow.observeState(this@VisitorFragment) {
                mTrainingAdapter.submitList(it)
            }

            bannerListStateFlow.observeState(this@VisitorFragment) {
                mHomeBannerAdapter.updateData(it)
            }
        }
    }

    override fun onShow(isFirstLoad: Boolean) {
        super.onShow(isFirstLoad)
        setupStatusBar()
    }

    private fun setupStatusBar() {
        val dashboardActivity = requireActivity() as DashboardActivity
        val rootView = requireActivity().findViewById<FrameLayout>(android.R.id.content)
        rootView.doOnApplyWindowInsets { _, _, padding, _ ->
            Timber.d("padding.top ${padding.top} padding.bottom ${padding.bottom}")
            dashboardActivity.registerPaddingSystemWindowInsets(
                dashboardActivity,
                padding.top == 0,
                padding.bottom == 0
            )
            dashboardActivity.registerTransparentStatusBar(
                dashboardActivity,
                false
            )
        }
    }

    private fun handleBannerJumpLogic(event: EventsItem) {
        when (event.jumpType) {
            LINK -> {
                event.jumpLink?.let {
                    ExternalAppUtils.openUrlInBrowser(requireActivity(), it)
                }
            }

            MATCH -> {
                event.matchInfoId?.let {
                    RaceDetailActivity.jump(requireActivity(), it)
                }
            }
        }
    }

}
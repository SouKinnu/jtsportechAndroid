package com.jtsportech.visport.android.dashboard.home.competition

import com.cloudhearing.android.lib_base.base.BaseBindingVmFragment
import com.cloudhearing.android.lib_base.utils.observeState
import com.cloudhearing.android.lib_base.utils.toDp
import com.cloudhearing.android.lib_common.network.dataSource.homebanner.EventsItem
import com.cloudhearing.android.lib_common.utils.ExternalAppUtils
import com.cloudhearing.android.lib_common.utils.SpacesItemDecoration
import com.cloudhearing.android.lib_common.utils.mmkv.PreferencesWrapper
import com.jtsportech.visport.android.dashboard.home.HomeBannerAdapter
import com.jtsportech.visport.android.databinding.FragmentCompetitionBinding
import com.jtsportech.visport.android.databinding.IncludeBannerBinding
import com.jtsportech.visport.android.dialog.HomeBannerDialog
import com.jtsportech.visport.android.racedetail.RaceDetailActivity
import com.jtsportech.visport.android.utils.LINK
import com.jtsportech.visport.android.utils.MATCH
import com.youth.banner.indicator.CircleIndicator

class CompetitionFragment : BaseBindingVmFragment<FragmentCompetitionBinding, CompetitionViewModel>(
    FragmentCompetitionBinding::inflate
) {
//    private val homeBannerDialog: HomeBannerDialog by lazy {
//        HomeBannerDialog(requireContext()).apply {
//            setOnClickListener {
//                handleBannerJumpLogic(it)
//            }
//        }
//    }
    private val mCompetitionAdapter: CompetitionAdapter by lazy {
        CompetitionAdapter().apply {
            setOnClickListener {
                it.id?.let { id -> RaceDetailActivity.jump(requireActivity(), id) }
            }
        }
    }

    private val mHomeBannerAdapter: HomeBannerAdapter by lazy {
        HomeBannerAdapter(mutableListOf())
    }

    private lateinit var mIncludeBannerBinding: IncludeBannerBinding

    override fun initView() {
//        if (!homeBannerDialog.isToday(PreferencesWrapper.get().getBannerMatchDialog())) {
//            homeBannerDialog.show()
//            PreferencesWrapper.get().setBannerMatchDialog(System.currentTimeMillis())
//        }
        mIncludeBannerBinding = IncludeBannerBinding.bind(binding.root)

        binding.apply {

            mIncludeBannerBinding.banner.apply {
                setAdapter(mHomeBannerAdapter)
                addBannerLifecycleObserver(this@CompetitionFragment)
                indicator = CircleIndicator(requireContext())
                setOnBannerListener { data, position ->
                    handleBannerJumpLogic(data as EventsItem)
                }
            }

            rvCompetition.apply {
                adapter = mCompetitionAdapter
                addItemDecoration(SpacesItemDecoration(7.toDp.toInt()))
                itemAnimator?.changeDuration = 0
            }
        }
    }

    override fun initData() {

    }

    override fun initEvent() {
        viewModel.run {
            competitionListStateFlow.observeState(this@CompetitionFragment) {
                mCompetitionAdapter.submitList(it)
            }

            bannerListStateFlow.observeState(this@CompetitionFragment) {
                mHomeBannerAdapter.updateData(it)
//                if (it.isNotEmpty()) homeBannerDialog.loadData(it)
            }
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
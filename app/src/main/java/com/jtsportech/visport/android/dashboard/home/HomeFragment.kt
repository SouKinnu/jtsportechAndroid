package com.jtsportech.visport.android.dashboard.home

import android.view.Gravity
import android.widget.FrameLayout
import androidx.core.view.get
import androidx.recyclerview.widget.GridLayoutManager
import com.cloudhearing.android.lib_base.base.BaseBindingVmFragment
import com.cloudhearing.android.lib_base.utils.ANTI_SHAKE_THRESHOLD
import com.cloudhearing.android.lib_base.utils.clickFlow
import com.cloudhearing.android.lib_base.utils.doOnApplyWindowInsets
import com.cloudhearing.android.lib_base.utils.hide
import com.cloudhearing.android.lib_base.utils.hideCurrentFragment
import com.cloudhearing.android.lib_base.utils.observeEvent
import com.cloudhearing.android.lib_base.utils.observeState
import com.cloudhearing.android.lib_base.utils.show
import com.cloudhearing.android.lib_base.utils.showFragment
import com.cloudhearing.android.lib_base.utils.throttleFirst
import com.cloudhearing.android.lib_common.network.dataSource.home.competition.Competition
import com.cloudhearing.android.lib_common.network.dataSource.homebanner.EventsItem
import com.cloudhearing.android.lib_common.utils.ExternalAppUtils
import com.cloudhearing.android.lib_common.utils.mmkv.PreferencesWrapper
import com.jtsportech.visport.android.components.popwindow.EventListPopWindow
import com.jtsportech.visport.android.dashboard.DashboardActivity
import com.jtsportech.visport.android.dashboard.home.competition.CompetitionFragment
import com.jtsportech.visport.android.dashboard.home.event.EventFragment
import com.jtsportech.visport.android.dashboard.home.training.TrainingFragment
import com.jtsportech.visport.android.dataSource.UserRole
import com.jtsportech.visport.android.dataSource.home.HomeTabAdapter
import com.jtsportech.visport.android.databinding.FragmentHomeBinding
import com.jtsportech.visport.android.dialog.HomeBannerDialog
import com.jtsportech.visport.android.home.HomeActivity
import com.jtsportech.visport.android.racedetail.RaceDetailActivity
import com.jtsportech.visport.android.utils.LINK
import com.jtsportech.visport.android.utils.MATCH
import com.jtsportech.visport.android.utils.getHomeTabData
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class HomeFragment :
    BaseBindingVmFragment<FragmentHomeBinding, HomeViewModel>(FragmentHomeBinding::inflate) {
    private val events = ArrayList<EventsItem>()
    private val bannerDialog: HomeBannerDialog by lazy {
        HomeBannerDialog(requireContext()).apply {
            setOnClickListener {
                handleBannerJumpLogic(it)
            }
            setOnDismissListener {
                if (events.size > 0) {
                    bannerDialog.loadData(listOf(events.removeFirst()))
                    bannerDialog.show()
                }
            }
        }
    }

    private val mTrainingFragment: TrainingFragment by lazy {
        TrainingFragment()
    }

    private val mCompetitionFragment: CompetitionFragment by lazy {
        CompetitionFragment()
    }

    private val mEventFragment: EventFragment by lazy {
        EventFragment()
    }

    private val mHomeTabAdapter by lazy {
        HomeTabAdapter()
    }

    private val mEventListPopWindow by lazy {
        EventListPopWindow(requireContext()).apply {
            setOnItemClickListener { competition, pos ->
                if (pos == 0) {
                    // 全部比赛不需要传参数信息
                    mEventFragment.updateEventHomeList("", "")
                } else {
                    mEventFragment.updateEventHomeList(
                        competition.name!!,
                        competition.id!!
                    )
                }

                mHomeTabAdapter.submitList(
                    getHomeTabData(
                        requireContext(),
                        mSelectedPosition,
                        mHasMoreEvent,
                        false
                    )
                )
            }

            setOnDismissListener {
                mHomeTabAdapter.submitList(
                    getHomeTabData(
                        requireContext(),
                        mSelectedPosition,
                        mHasMoreEvent,
                        false
                    )
                )
            }
        }
    }

    private var mSelectedPosition = 1
    private var mHasMoreEvent = false

    override fun initView() {
        setupContainer()
        binding.apply {
            ibHomeSide.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    handleSidebar()
                }
                .launchIn(mainScope)

            ibHomeSearch.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    onHome(HomeActivity.SEARCH_MATCHE_PAGE)
                }
                .launchIn(mainScope)
        }
    }

    private fun setupContainer() = binding.apply {
        mainScope.launch {
            mSelectedPosition = withContext(dispatchers.io) {
                val index = getHomeTabData(
                    requireContext(),
                    mSelectedPosition,
                    mHasMoreEvent
                ).indexOfFirst {
                    it.selected
                }
                if (index == -1) 1 else index
            }

            when (mSelectedPosition) {
                0 -> showFragment(mTrainingFragment, flMain.id)
                1 -> showFragment(mCompetitionFragment, flMain.id)
                2 -> showFragment(mEventFragment, flMain.id)
            }

            mHomeTabAdapter.setOnItemClickListener { position, view ->
                var isEventMorePop = false

                hideCurrentFragment()
                when (position) {
                    0 -> showFragment(mTrainingFragment, flMain.id)
                    1 -> showFragment(mCompetitionFragment, flMain.id)
                    2 -> {
                        if (mSelectedPosition == position && mHasMoreEvent) {
                            // 需要展开 popwindow
                            isEventMorePop = true
                            Timber.d("有走 mEventListPopWindow")
                            mEventListPopWindow.showAsDropDown(
                                binding.rvTab[2],
                                binding.rvTab[2].width / 2,
                                0,
                                Gravity.BOTTOM
                            )
//                            mEventListPopWindow.show(binding.rvTab[2])
                        }
                        showFragment(mEventFragment, flMain.id)
                    }
                }

                mSelectedPosition = position

                mHomeTabAdapter.submitList(
                    getHomeTabData(
                        requireContext(),
                        position,
                        mHasMoreEvent,
                        isEventMorePop
                    )
                )
            }
        }
    }

    private fun handleSidebar() {
        val dashboardActivity = requireActivity() as DashboardActivity

        if (dashboardActivity.isDrawerOpen()) {
            dashboardActivity.closeSidebar()
        } else {
            dashboardActivity.openSidebar()
        }
    }

    private fun onHome(@HomeActivity.HOMEPAGE page: String) {
        HomeActivity.jump(requireActivity(), page)
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

    override fun initData() {
    }

    override fun initEvent() {
        viewModel.run {
            homeDialogStateFlow.observeEvent(this@HomeFragment) {
                if (it.eventsItemList.isNotEmpty()) {
                    events.addAll(it.eventsItemList)

                    if (!bannerDialog.isToday(PreferencesWrapper.get().getBannerMatchDialog())) {
                        bannerDialog.loadData(listOf(events.removeFirst()))
                        bannerDialog.show()
                        PreferencesWrapper.get().setBannerMatchDialog(System.currentTimeMillis())
                    }
                } else {
                    if (bannerDialog.isShowing) {
                        bannerDialog.dismiss()
                    }
                }
            }

            userRoleStateFlow.observeState(this@HomeFragment) {
                Timber.d("role $it")
                handlePageDisplayLogic(it)
            }

            eventListDataFlow.observeState(this@HomeFragment) {
                Timber.d("eventListData ${it.size}")
                handleEventList(it)
            }
        }
    }

    private fun handleEventList(list: List<Competition>) {
        mHasMoreEvent = list.isNotEmpty()

        if (list.isEmpty()) {
            return
        }


        mEventListPopWindow.setEventPopList(list)
        mHomeTabAdapter.submitList(
            getHomeTabData(
                requireContext(),
                mSelectedPosition,
                mHasMoreEvent
            )
        )
    }

    private fun handlePageDisplayLogic(role: String) {
        when {
            role == UserRole.HEAD_COACH || role == UserRole.COACH || role == UserRole.MEMBER -> {
                binding.apply {
                    rvTab.show()
                    if (rvTab.adapter == null) {
                        rvTab.adapter = mHomeTabAdapter
                        rvTab.layoutManager = GridLayoutManager(requireContext(), 3)
                        mHomeTabAdapter.submitList(getHomeTabData(requireContext()))
                    }
                }
            }

            role == UserRole.VISITOR || role == UserRole.GUARDER -> {
                binding.apply {
                    rvTab.hide()
                }
            }

            else -> {
                binding.apply {
                    rvTab.hide()
                }
            }
        }
    }

    private fun handleBannerJumpLogic(event: EventsItem) {
        when (event.jumpType) {
            LINK -> {
                event.jumpLink.let {
                    ExternalAppUtils.openUrlInBrowser(requireActivity(), it)
                }
            }

            MATCH -> {
                event.matchInfoId.let {
                    RaceDetailActivity.jump(requireActivity(), it)
                }
            }
        }
    }

}
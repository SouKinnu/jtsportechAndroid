package com.jtsportech.visport.android.dashboard.home.event

import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.cloudhearing.android.lib_base.base.BaseBindingVmFragment
import com.cloudhearing.android.lib_base.utils.ANTI_SHAKE_THRESHOLD
import com.cloudhearing.android.lib_base.utils.clickFlow
import com.cloudhearing.android.lib_base.utils.observeEvent
import com.cloudhearing.android.lib_base.utils.observeState
import com.cloudhearing.android.lib_base.utils.onScrollHorizontallyState
import com.cloudhearing.android.lib_base.utils.systremToast
import com.cloudhearing.android.lib_base.utils.throttleFirst
import com.cloudhearing.android.lib_base.utils.toDp
import com.cloudhearing.android.lib_common.network.dataSource.homebanner.EventsItem
import com.cloudhearing.android.lib_common.utils.ExternalAppUtils
import com.cloudhearing.android.lib_common.utils.SpacesItemDecoration
import com.cloudhearing.android.lib_common.utils.mmkv.PreferencesWrapper
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.dashboard.home.HomeBannerAdapter
import com.jtsportech.visport.android.dashboard.home.training.CalendarPanelAdapter
import com.jtsportech.visport.android.databinding.FragmentEventBinding
import com.jtsportech.visport.android.databinding.IncludeBannerBinding
import com.jtsportech.visport.android.databinding.IncludeEmptyEventsBinding
import com.jtsportech.visport.android.dialog.HomeBannerDialog
import com.jtsportech.visport.android.home.HomeActivity
import com.jtsportech.visport.android.racedetail.RaceDetailActivity
import com.jtsportech.visport.android.utils.LINK
import com.jtsportech.visport.android.utils.MATCH
import com.youth.banner.indicator.CircleIndicator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

class EventFragment :
    BaseBindingVmFragment<FragmentEventBinding, EventViewModel>(FragmentEventBinding::inflate) {
//    private val homeBannerDialog: HomeBannerDialog by lazy {
//        HomeBannerDialog(requireContext()).apply {
//            setOnClickListener {
//                handleBannerJumpLogic(it)
//            }
//        }
//    }
    private val mCalendarPanelAdapter: CalendarPanelAdapter by lazy {
        CalendarPanelAdapter().apply {
            setOnClickListener { rootPosition, childPosition ->
                viewModel.updateCalendarDataSelected(rootPosition, childPosition)
            }
        }
    }

    private val mEventAdapter: EventAdapter by lazy {
        EventAdapter().apply {
            setOpenEventNotifyOnClickListener {
                viewModel.sendMatchBooking(it.id!!)
            }

            setCheckOnClickListener {
                it.id?.let { id -> RaceDetailActivity.jump(requireActivity(), id) }
            }

            setAppointmentOnClickListener {

            }
        }
    }

    private val mHomeBannerAdapter: HomeBannerAdapter by lazy {
        HomeBannerAdapter(mutableListOf())
    }

    private lateinit var mIncludeBannerBinding: IncludeBannerBinding
    private lateinit var mIncludeEmptyEventsBinding: IncludeEmptyEventsBinding

    private var mEventName: String = ""
    private var mLeagueId: String = ""

    override fun initView() {
//        if (!homeBannerDialog.isToday(PreferencesWrapper.get().getBannerLeagueDialog())) {
//            homeBannerDialog.show()
//            PreferencesWrapper.get().setBannerLeagueDialog(System.currentTimeMillis())
//        }
        mIncludeBannerBinding = IncludeBannerBinding.bind(binding.root)
        mIncludeEmptyEventsBinding = IncludeEmptyEventsBinding.bind(binding.root)

        binding.apply {

            mIncludeBannerBinding.banner.apply {
                setAdapter(mHomeBannerAdapter)
                addBannerLifecycleObserver(this@EventFragment)
                setIndicator(CircleIndicator(requireContext()))
                setOnBannerListener { data, position ->
                    handleBannerJumpLogic(data as EventsItem)
                }
            }

            mIncludeEmptyEventsBinding.apply {
                mbNextEvent.clickFlow()
                    .onEach {
                        viewModel.selectedNextDay()
                    }
                    .launchIn(mainScope)
            }

            rvCalendar.apply {
                adapter = mCalendarPanelAdapter
                itemAnimator?.changeDuration = 0

                val snapHelper = LinearSnapHelper()
                snapHelper.attachToRecyclerView(this)


                onScrollHorizontallyState({
                    Timber.d("lastWeek position 走了")
                    lastWeek()
                }, {
                    Timber.d("nextWeek position 走了")
                    nextWeek()
                }, {
                    updateCurrentDate()
                })
            }

            tvToday.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    viewModel.targetedToday()
                }
                .launchIn(mainScope)

            ibLastWeek.clickFlow()
                .onEach {
                    lastWeek(true)
                }
                .launchIn(mainScope)

            ibNextWeek.clickFlow()
                .onEach {
                    nextWeek(true)
                }
                .launchIn(mainScope)

            tvEventType.text = getString(R.string.event_all_events)

            tvAllEvent.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    onHome(HomeActivity.ALL_COMPETITIONS_PAGE)
                }
                .launchIn(mainScope)

            rvEvent.apply {
                adapter = mEventAdapter
                addItemDecoration(SpacesItemDecoration(20.toDp.toInt(), false))
                itemAnimator?.changeDuration = 0
            }
        }
    }

    override fun initData() {

    }

    override fun initEvent() {
        viewModel.run {
            calendarDateTimeStateFlow.observeState(this@EventFragment) {
                binding.tvDate.text = it
            }

            calendarPanelListStateFlow.observeState(this@EventFragment) {
                Timber.d("有数据 $it")
                mCalendarPanelAdapter.submitList(it)
            }

            calendarPanelPositionStateFlow.observeState(this@EventFragment) {
                Timber.d("选中的日期 $it")
                handleShowCurrentPosition(it)
            }

            eventListStateFlow.observeState(this@EventFragment) {
                Timber.d("有走 ${it.size}")
                handleEmptyEvents(it.isEmpty())
                mEventAdapter.submitList(it)
            }

            bannerListStateFlow.observeState(this@EventFragment) {
                mHomeBannerAdapter.updateData(it)
//                if (it.isNotEmpty()) homeBannerDialog.loadData(it)
            }

            calendarTodayFlow.observeState(this@EventFragment) {
                binding.tvToday.isVisible = !it
            }

            targetedListFlowEvents.observeEvent(this@EventFragment) {
                binding.rvCalendar.scrollToPosition(it)
            }

            toastFlowEvents.observeEvent(this@EventFragment) {
                systremToast(it)
            }
        }
    }

    private fun handleShowCurrentPosition(position: Int) {
        val size = mCalendarPanelAdapter.currentList.size

        if (size - 1 >= position) {
            binding.rvCalendar.scrollToPosition(position)
        }
    }

    private fun lastWeek(isNeedScrollToPosition: Boolean = false) = mainScope.launch {
        var position = getCalendarPosition()

        viewModel.lastWeek()
        delay(50L)

        Timber.d("lastWeek position $position")

        if (isNeedScrollToPosition) {
            binding.rvCalendar.scrollToPosition(position)
        } else {
            position += 1
            binding.rvCalendar.scrollToPosition(position)
        }

        viewModel.calculateCurrentDate(mCalendarPanelAdapter.currentList, position)
    }

    private fun nextWeek(isNeedScrollToPosition: Boolean = false) = mainScope.launch {
        var position = getCalendarPosition() + 1
        viewModel.nextWeek()
        delay(50L)

        if (isNeedScrollToPosition) {
            binding.rvCalendar.scrollToPosition(position)
        } else {
            // 下一周，viewModel.nextWeek 方法会在末尾增加一个数据，这时 子 item 的数据展示是最后一个，所以这里就会有问题。
            // 需要额外定位到移动到的位置
            position -= 1
            binding.rvCalendar.scrollToPosition(position)
        }

        viewModel.calculateCurrentDate(mCalendarPanelAdapter.currentList, position)
    }

    private fun updateCurrentDate() {
        val postion = getCalendarPosition()
        viewModel.calculateCurrentDate(mCalendarPanelAdapter.currentList, postion)
    }

    private fun getCalendarPosition(): Int {
        return (binding.rvCalendar.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
//        return (binding.rvCalendar.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
    }

    private fun handleEmptyEvents(isShow: Boolean) {
        if (!::mIncludeEmptyEventsBinding.isInitialized) return

        mIncludeEmptyEventsBinding.groupEmptyEvents.isVisible = isShow

    }


    fun updateEventHomeList(name: String, leagueId: String) {
        if (name.isEmpty()) {
            binding.tvEventType.text = getString(R.string.event_all_events)
        } else {
            binding.tvEventType.text = name
        }

        mEventName = name
        mLeagueId = leagueId

        viewModel.getEventHomeList(name = name, leagueId = leagueId)
    }

    private fun onHome(@HomeActivity.HOMEPAGE page: String) {
        HomeActivity.jump(requireActivity(), page, mEventName, mLeagueId)
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
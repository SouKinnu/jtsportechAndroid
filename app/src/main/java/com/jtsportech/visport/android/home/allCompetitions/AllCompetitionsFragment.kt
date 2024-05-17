package com.jtsportech.visport.android.home.allCompetitions

import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
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
import com.cloudhearing.android.lib_common.utils.SpacesItemDecoration
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.dashboard.home.event.EventAdapter
import com.jtsportech.visport.android.dashboard.home.training.CalendarPanelAdapter
import com.jtsportech.visport.android.databinding.FragmentAllCompetitionsBinding
import com.jtsportech.visport.android.databinding.IncludeEmptyEventsBinding
import com.jtsportech.visport.android.racedetail.RaceDetailActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

class AllCompetitionsFragment :
    BaseBindingVmFragment<FragmentAllCompetitionsBinding, AllCompetitionsViewModel>(
        FragmentAllCompetitionsBinding::inflate
    ) {

    private val mArgs by navArgs<AllCompetitionsFragmentArgs>()

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

    private lateinit var mIncludeEmptyEventsBinding: IncludeEmptyEventsBinding
    override fun initView() {
        mIncludeEmptyEventsBinding = IncludeEmptyEventsBinding.bind(binding.root)

        binding.apply {
            apMoreEvent.setOnClickLeftIconListener {
                goBack()
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


            rvEvent.apply {
                adapter = mEventAdapter
                addItemDecoration(SpacesItemDecoration(20.toDp.toInt(), false))
                itemAnimator?.changeDuration = 0
            }
        }
    }

    override fun initData() {
        updateEventHomeList(mArgs.eventName,mArgs.leagueId)
    }

    override fun initEvent() {
        viewModel.run {
            calendarDateTimeStateFlow.observeState(this@AllCompetitionsFragment) {
                binding.tvDate.text = it
            }

            calendarPanelListStateFlow.observeState(this@AllCompetitionsFragment) {
                Timber.d("有数据 $it")
                mCalendarPanelAdapter.submitList(it)
            }

            calendarPanelPositionStateFlow.observeState(this@AllCompetitionsFragment) {
                Timber.d("选中的日期 $it")
                handleShowCurrentPosition(it)
            }

            eventListStateFlow.observeState(this@AllCompetitionsFragment) {
                Timber.d("有走 ${it.size}")
                handleEmptyEvents(it.isEmpty())
                mEventAdapter.submitList(it)
            }


            calendarTodayFlow.observeState(this@AllCompetitionsFragment) {
                binding.tvToday.isVisible = !it
            }

            targetedListFlowEvents.observeEvent(this@AllCompetitionsFragment){
                binding.rvCalendar.scrollToPosition(it)
            }

            toastFlowEvents.observeEvent(this@AllCompetitionsFragment) {
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

        viewModel.getEventHomeList(name = name, leagueId = leagueId)
    }


    private fun goBack() {
        requireActivity().onBackPressed()
    }

}
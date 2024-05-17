package com.jtsportech.visport.android.home.allCompetitions

import com.cloudhearing.android.lib_base.base.BaseViewModel
import com.cloudhearing.android.lib_base.utils.SharedFlowEvents
import com.cloudhearing.android.lib_base.utils.setEvent
import com.cloudhearing.android.lib_common.network.dataSource.home.competition.Competition
import com.cloudhearing.android.lib_common.network.repository.CompetitionRepository
import com.cloudhearing.android.lib_common.utils.launchRequest
import com.jtsportech.visport.android.dataSource.home.event.CalendarPanelEntity
import com.jtsportech.visport.android.utils.date.DateUtils
import com.jtsportech.visport.android.utils.findSelectedPositions
import com.jtsportech.visport.android.utils.generateCalendarPanelDataList
import com.jtsportech.visport.android.utils.increaseWeekBeforeCalendarPanelDataList
import com.jtsportech.visport.android.utils.increaseWeekNextCalendarPanelDataList
import com.jtsportech.visport.android.utils.judgetToday
import com.jtsportech.visport.android.utils.updateCalendarDataListSelected
import com.jtsportech.visport.android.utils.updateTargetedTodayList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.Calendar
import java.util.Date

class AllCompetitionsViewModel : BaseViewModel() {


    private val mCompetitionRepository: CompetitionRepository by lazy {
        CompetitionRepository()
    }

    private val todayCalendar: Calendar by lazy {
        Calendar.getInstance()
    }


    private val _calendarPanelListStateFlow =
        MutableStateFlow(emptyList<List<CalendarPanelEntity>>())

    val calendarPanelListStateFlow = _calendarPanelListStateFlow.asStateFlow()

    private val _calendarPanelPositionStateFlow =
        MutableStateFlow(0)

    val calendarPanelPositionStateFlow = _calendarPanelPositionStateFlow.asStateFlow()

    private val _calendarDateTimeStateFlow =
        MutableStateFlow("")

    val calendarDateTimeStateFlow = _calendarDateTimeStateFlow.asStateFlow()

    private val _calendarTodayFlow =
        MutableStateFlow(true)

    val calendarTodayFlow = _calendarTodayFlow.asStateFlow()

    private val _eventListStateFlow =
        MutableStateFlow(emptyList<Competition>())

    val eventListStateFlow = _eventListStateFlow.asStateFlow()

    private val _nextWeekFlowEvents = SharedFlowEvents<Unit>()

    val nextWeekFlowEvents = _nextWeekFlowEvents.asSharedFlow()

    private val _targetedListFlowEvents = SharedFlowEvents<Int>()

    val targetedListFlowEvents = _targetedListFlowEvents.asSharedFlow()

    private val _toastFlowEvents = SharedFlowEvents<String>()

    val toastFlowEvents = _toastFlowEvents.asSharedFlow()

    private var mSelectedDate: Date = todayCalendar.time

    private var mName: String = ""
    private var mLeagueId: String = ""

    override fun onStart() {
        super.onStart()
        getCalendarData()
        getEventHomeList()
    }

    fun targetedToday() = launchUi {
        val list = _calendarPanelListStateFlow.value

        withContext(coroutineDispatchers.io) {
            updateTargetedTodayList(list)
        }.let {
            _targetedListFlowEvents.setEvent(it.first)
            _calendarPanelListStateFlow.value = it.second

            calculateCurrentDate(_calendarPanelListStateFlow.value, it.first)
        }
        mSelectedDate = todayCalendar.time
        getEventHomeList()
    }

    fun updateCalendarDataSelected(currentListPosition: Int, selectPosition: Int) = launchUi {
        val list = _calendarPanelListStateFlow.value

        Timber.d("currentListPosition $currentListPosition selectPosition $selectPosition")

        withContext(coroutineDispatchers.io) {
            updateCalendarDataListSelected(list, currentListPosition, selectPosition)
        }.let {
            _calendarPanelListStateFlow.value = it
        }

        selectedDataJudgeToday()

        getEventHomeList()
    }

    fun selectedDataJudgeToday() = launchUi {
        val list = _calendarPanelListStateFlow.value

        withContext(coroutineDispatchers.io) {
            judgetToday(list)
        }?.let {
            _calendarTodayFlow.value = it
        }
    }

    fun calculateCurrentDate(calendarPanelList: List<List<CalendarPanelEntity>>, position: Int) {
        var newPosition = position

        // 需要做兜底操作，向左划可能会数组越界
        if (position < 0) {
            newPosition = 0
        }

        // 需要做兜底操作，向右划可能会数组越界
//        if (position == calendarPanelList.size) {
//            newPosition = calendarPanelList.size - 1
//        }

        Timber.d("calendarPanelList size ${calendarPanelList.size} position $newPosition")

        val calendarPanelEntities = calendarPanelList[newPosition]

        val entity = calendarPanelEntities.first()

        Timber.d("topTime ${entity.topTime} bottomTime ${entity.bottomTime}")

        val dateDescription = DateUtils.convertToFormat4(entity.date)

        mSelectedDate = entity.date

        _calendarDateTimeStateFlow.value = dateDescription
    }

    fun lastWeek() = launchUi {
        val calendarPanelList = withContext(coroutineDispatchers.io) {
            increaseWeekBeforeCalendarPanelDataList(_calendarPanelListStateFlow.value)
        }

        Timber.d("calendarPanelList size: ${calendarPanelList.size}")

        calendarPanelList.let {
            _calendarPanelListStateFlow.value = it
        }
    }

    fun nextWeek() = launchUi {
        val calendarPanelList = withContext(coroutineDispatchers.io) {
            increaseWeekNextCalendarPanelDataList(_calendarPanelListStateFlow.value)
        }

        Timber.d("calendarPanelList size: ${calendarPanelList.size}")

        calendarPanelList?.let {
            _calendarPanelListStateFlow.value = it
        }
    }

    private fun getCalendarData() = launchUi {
        val calendarPanelList = withContext(coroutineDispatchers.io) {
            generateCalendarPanelDataList()
        }

        Timber.d("calendarPanelList size: ${calendarPanelList.size}")

        _calendarPanelListStateFlow.value = calendarPanelList

        findSelectedPosition()
    }

    private fun findSelectedPosition() = launchUi {
        val list = _calendarPanelListStateFlow.value

        val selectedPosition = withContext(coroutineDispatchers.io) {
            list.indexOfFirst {
                val indexOfFirst = it.indexOfFirst {
                    it.selected
                }
                indexOfFirst != -1
            }
        }

        _calendarPanelPositionStateFlow.value = selectedPosition

        calculateCurrentDate(list, selectedPosition)
    }

    private suspend fun findSelectedDate() {
        val list = _calendarPanelListStateFlow.value

        withContext(coroutineDispatchers.io) {
            list.flatten().find { it.selected }
        }?.let {
            mSelectedDate = it.date
        }
    }

    fun getEventHomeList(
        name: String = mName,
        leagueId: String = mLeagueId,
        pageNum: Int = 1,
        pageSize: Int = 20
    ) = launchUi {
        mName = name
        mLeagueId = leagueId

        findSelectedDate()

        launchRequest(isLoading = false, {
            mCompetitionRepository.getLeagueHomeList(
                DateUtils.convertToFormat2(mSelectedDate),
                "LEAGUE",
                pageNum,
                pageSize,
                name = name,
                leagueId = leagueId
            )
        }, {
            if (it != null) {
                Timber.d("数据 ${it.list}")
                _eventListStateFlow.value = it.list
            }
        }, { errorCode, errorMsg ->
            _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
        }, {
            _toastFlowEvents.setEvent("${it.message}")
        })
    }

    fun selectedNextDay() = launchUi {
        val list = _calendarPanelListStateFlow.value

        withContext(coroutineDispatchers.io) {
            findSelectedPositions(list)
        }?.let {
            var currentListPosition = it.first
            var selectPosition = it.second

            // 到了周六，就要从下一周的周一开始
            if (selectPosition > 5) {
                if (currentListPosition != list.size - 1) {
                    currentListPosition++
                    selectPosition = 0
                }
                // 协程切换上下文，所以不是顺序执行的，需要加延迟
                nextWeek()
                delay(50L)
                calculateCurrentDate(_calendarPanelListStateFlow.value, currentListPosition)
            } else {
                selectPosition++
            }

            delay(50L)
            _targetedListFlowEvents.setEvent(currentListPosition)

            updateCalendarDataSelected(currentListPosition, selectPosition)
        }
    }

    fun sendMatchBooking(matchInfoId: String) {
        launchRequest(isLoading = true, {
            mCompetitionRepository.sendMatchBooking(matchInfoId)
        }, {
            getEventHomeList()
        }, { errorCode, errorMsg ->
            _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
        }, {
            _toastFlowEvents.setEvent("${it.message}")
        })
    }
}
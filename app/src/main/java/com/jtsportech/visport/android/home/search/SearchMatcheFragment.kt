package com.jtsportech.visport.android.home.search

import android.view.Gravity
import androidx.annotation.IntDef
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.cloudhearing.android.lib_base.base.BaseBindingVmFragment
import com.cloudhearing.android.lib_base.dataSource.LoadState
import com.cloudhearing.android.lib_base.utils.ANTI_SHAKE_THRESHOLD
import com.cloudhearing.android.lib_base.utils.clickFlow
import com.cloudhearing.android.lib_base.utils.hide
import com.cloudhearing.android.lib_base.utils.observeEvent
import com.cloudhearing.android.lib_base.utils.observeState
import com.cloudhearing.android.lib_base.utils.show
import com.cloudhearing.android.lib_base.utils.systremToast
import com.cloudhearing.android.lib_base.utils.throttleFirst
import com.cloudhearing.android.lib_base.utils.toDp
import com.cloudhearing.android.lib_common.network.dataSource.home.competition.Competition
import com.cloudhearing.android.lib_common.utils.GridSpaceItemDecoration
import com.cloudhearing.android.lib_common.utils.SpacesItemDecoration
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.components.popwindow.EventListPopWindow
import com.jtsportech.visport.android.dataSource.home.search.SearchFilterLayoutType
import com.jtsportech.visport.android.databinding.FragmentSearchMatcheBinding
import com.jtsportech.visport.android.home.search.adapter.SearchFilterAdapter
import com.jtsportech.visport.android.home.search.adapter.SearchFilterContentAdapter
import com.jtsportech.visport.android.home.search.adapter.SearchFilterTitleAdapter
import com.jtsportech.visport.android.home.search.adapter.SearchMatcheAdapter
import com.jtsportech.visport.android.home.search.adapter.SearchMatcheHistoryAdapter
import com.jtsportech.visport.android.racedetail.RaceDetailActivity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class SearchMatcheFragment :
    BaseBindingVmFragment<FragmentSearchMatcheBinding, SearchMatcheViewModel>(
        FragmentSearchMatcheBinding::inflate
    ) {


    @IntDef(
        ALL,
        COMPETITION,
        TRAIN,
        EVENT,
        FILTER
    )
    @Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.PROPERTY)
    @Retention(AnnotationRetention.SOURCE)
    private annotation class TAB_CONTENT_TYPE

    @IntDef(
        TO_BE_SEARCHED,
        SEARCHED
    )
    @Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.PROPERTY)
    @Retention(AnnotationRetention.SOURCE)
    private annotation class SEARCH_STATUS

    companion object {
        /**
         * 全部
         */
        private const val ALL = 0

        /**
         * 比赛
         */
        private const val COMPETITION = 1

        /**
         * 训练
         */
        private const val TRAIN = 2

        /**
         * 赛事
         */
        private const val EVENT = 3

        /**
         * 筛选
         */
        private const val FILTER = 4

        /**
         * 待搜索
         */
        private const val TO_BE_SEARCHED = 0

        /**
         * 已搜索
         */
        private const val SEARCHED = 1

        private const val TRAIN_VALUE = "TRAIN"
        private const val MATCH_VALUE = "MATCH"
        private const val LEAGUE_VALUE = "LEAGUE"

    }

    @TAB_CONTENT_TYPE
    private var mCurrentTabContent: Int = ALL

    @SEARCH_STATUS
    private var mCurrentSearchStatus: Int = TO_BE_SEARCHED

    private val mSearchMatcheHistoryAdapter: SearchMatcheHistoryAdapter by lazy {
        SearchMatcheHistoryAdapter().apply {
            setOnSearchClickListener {
                viewModel.search(it)

                toggleTabContent(ALL)
                toggleSearchStatus(SEARCHED)
            }

            setOnDeleteClickListener {
                viewModel.deleteCompetitionSearchRecord(it)
            }
        }
    }

    private val mSearchFilterAdapter: SearchFilterAdapter by lazy {
        SearchFilterAdapter()
    }

    private val mSearchMatcheAdapter: SearchMatcheAdapter by lazy {
        SearchMatcheAdapter().apply {
            setOnCompetitionClickListener {
                it.id?.let { id -> RaceDetailActivity.jump(requireActivity(), id) }
            }

            setOnTrainingClickListener {
                it.id?.let { id -> RaceDetailActivity.jump(requireActivity(), id) }
            }

            setOnEventClickListener {
                it.id?.let { id -> RaceDetailActivity.jump(requireActivity(), id) }
            }
        }
    }

    private val mSearchFilterAdapterList by lazy {
        arrayListOf(
            SearchFilterTitleAdapter(),
            SearchFilterContentAdapter(),
        )
    }

    private val mEventListPopWindow by lazy {
        EventListPopWindow(requireContext()).apply {
            setOnItemClickListener { competition, pos ->
                if (pos == 0) {
                    // 全部比赛不需要传参数信息
                    viewModel.updateSearchFilterEvent("", competition.name!!)
                } else {
                    viewModel.updateSearchFilterEvent(competition.id!!, competition.name!!)
                }
            }

            setOnDismissListener {

            }
        }
    }

    private var mHasMoreEvent = false

    private val searchValue: String
        get() = binding.etSearch.text.toString().trim()

    override fun initView() {
        binding.apply {
            ibBack.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    goBack()
                }.launchIn(mainScope)

            etSearch.onActionDone {
                handleSearchLogic()
            }

            tvSearch.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    handleSearchLogic()
                }.launchIn(mainScope)

            tvSearchTips.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    viewModel.changeCompetitionSearchRecord()
                }.launchIn(mainScope)

            rvSearchHistory.apply {
                adapter = mSearchMatcheHistoryAdapter
                addItemDecoration(SpacesItemDecoration(16.toDp.toInt()))
                itemAnimator?.changeDuration = 0
            }

            tvAll.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    toggleTabContent(ALL)
                    viewModel.search(searchValue)
                }
                .launchIn(mainScope)

            tvTrain.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    toggleTabContent(TRAIN)
                    viewModel.search(content = searchValue, matchType = TRAIN_VALUE)
                }
                .launchIn(mainScope)

            tvCompetition.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    toggleTabContent(COMPETITION)
                    viewModel.search(content = searchValue, matchType = MATCH_VALUE)
                }
                .launchIn(mainScope)

            tvEvent.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    toggleTabContent(EVENT)
                    viewModel.search(content = searchValue, matchType = LEAGUE_VALUE)
                }
                .launchIn(mainScope)

            tvFilter.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    toggleTabContent(FILTER)
//                    viewModel.getSearchFilterOptionList(requireContext())
                }
                .launchIn(mainScope)

            vMask.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    toggleFilterExpandState()
                }
                .launchIn(mainScope)

            tvCancel.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    toggleFilterExpandState()
                }
                .launchIn(mainScope)

            tvSure.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    toggleFilterExpandState()
                    viewModel.filterSearch(searchValue)
                }
                .launchIn(mainScope)

            rvFilter.apply {
                adapter = mSearchFilterAdapter
                itemAnimator?.changeDuration = 0

                addItemDecoration(GridSpaceItemDecoration(4, 10.toDp.toInt(), 0))
                (layoutManager as GridLayoutManager).spanSizeLookup =
                    object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return when (getHasViewType(position)) {
                                SearchFilterLayoutType.TITLE -> 4
                                else -> 1
                            }
                        }
                    }
            }


            rvMatch.apply {
                adapter = mSearchMatcheAdapter
                addItemDecoration(SpacesItemDecoration(20.toDp.toInt(), false))
                itemAnimator?.changeDuration = 0
            }
        }

        assembleSearchFilterAdapter()
    }

    override fun initData() {

    }

    override fun initEvent() {
        viewModel.run {
            competitionSearchListStateFlow.observeState(this@SearchMatcheFragment) {
                handleSearchTipsLogic()
                Timber.d("view 列表数量 ${it.size}")
                mSearchMatcheHistoryAdapter.submitList(it)
            }

            competitionListStateFlow.observeState(this@SearchMatcheFragment) {
                mSearchMatcheAdapter.submitList(it)
            }

            searchFilterOptionListStateFlow.observeState(this@SearchMatcheFragment) {
                mSearchFilterAdapter.submitList(it.toMutableList())
            }

            eventListDataFlow.observeState(this@SearchMatcheFragment) {
                handleEventList(it)
            }

            toastFlowEvents.observeEvent(this@SearchMatcheFragment) {
                systremToast(it)
            }

            loadState.observeEvent(this@SearchMatcheFragment) {
                handleLoadState(it)
            }
        }
    }

    private fun assembleSearchFilterAdapter() {
        mSearchFilterAdapterList.forEach {
            mSearchFilterAdapter.addDelegate(it)
        }

        (mSearchFilterAdapterList[0] as SearchFilterTitleAdapter)
        (mSearchFilterAdapterList[1] as SearchFilterContentAdapter).apply {
            setOnClickListener {
                viewModel.updateSearchFilterOptionList(it)
            }

            setOnMoreClickListener { entity, v ->
                mEventListPopWindow.showAsDropDown(v, 0, 0, Gravity.BOTTOM)
//                mEventListPopWindow.show(v)
            }
        }
    }

    private fun handleLoadState(state: LoadState) {
        when (state) {
            is LoadState.Start -> {
                showLoadingScreen(state.tip)
            }

            is LoadState.Error -> {
                hideLoadingScreen()
                systremToast("${state.code}:${state.msg}")
            }

            is LoadState.Finish -> {
                hideLoadingScreen()
            }

            else -> {}
        }
    }

    private fun handleSearchLogic() {
        if (searchValue.isEmpty()) {
            return
        }

        viewModel.addCompetitionSearchRecord(searchValue)

        toggleTabContent(ALL)
        toggleSearchStatus(SEARCHED)
    }


    private fun handleSearchTipsLogic() {
        val size = viewModel.mSearchListSize
        val isUnfold = viewModel.misUnfold


        binding.tvSearchTips.text = when {
            size > 4 -> {
                if (isUnfold) {
                    getString(R.string.search_clear_all_searches)
                } else {
                    getString(R.string.search_all_historical_search_history)
                }
            }

            size > 0 -> ""
            else -> getString(R.string.my_team_not)
        }
    }

    private fun toggleTabContent(@TAB_CONTENT_TYPE type: Int) =
        binding.apply {
            when (type) {
                ALL -> {
                    tvAll.isSelected = true
                    tvCompetition.isSelected = false
                    tvTrain.isSelected = false
                    tvEvent.isSelected = false
                    tvFilter.isSelected = false

                    clFilter.hide()
                    vMask.isVisible = false
                }

                COMPETITION -> {
                    tvAll.isSelected = false
                    tvCompetition.isSelected = true
                    tvTrain.isSelected = false
                    tvEvent.isSelected = false
                    tvFilter.isSelected = false

                    clFilter.hide()
                    vMask.isVisible = false
                }

                TRAIN -> {
                    tvAll.isSelected = false
                    tvCompetition.isSelected = false
                    tvTrain.isSelected = true
                    tvEvent.isSelected = false
                    tvFilter.isSelected = false

                    clFilter.hide()
                    vMask.isVisible = false
                }

                EVENT -> {
                    tvAll.isSelected = false
                    tvCompetition.isSelected = false
                    tvTrain.isSelected = false
                    tvEvent.isSelected = true
                    tvFilter.isSelected = false

                    clFilter.hide()
                    vMask.isVisible = false
                }

                FILTER -> {
                    tvAll.isSelected = false
                    tvCompetition.isSelected = false
                    tvTrain.isSelected = false
                    tvEvent.isSelected = false
                    tvFilter.isSelected = true

                    clFilter.show()
                    vMask.isVisible = true
                }
            }

            mCurrentTabContent = type
        }

    private fun toggleFilterExpandState() {
        binding.clFilter.isVisible = !binding.clFilter.isVisible
        binding.vMask.isVisible = !binding.vMask.isVisible
    }

    private fun toggleSearchStatus(@SEARCH_STATUS status: Int) = binding.apply {
        when (status) {
            TO_BE_SEARCHED -> {
                groupSearchHistory.show()
                llSiftTool.hide()
                rvFilter.hide()
            }

            SEARCHED -> {
                groupSearchHistory.hide()
                llSiftTool.show()
                rvMatch.show()
            }
        }

        mCurrentSearchStatus = status
    }

    private fun getHasViewType(postion: Int): Int {
        val viewType = mSearchFilterAdapter.getItemViewType(postion)
        return mSearchFilterAdapter.getDelegate()[viewType].hasViewType()
    }

    private fun handleEventList(list: List<Competition>) {
        mHasMoreEvent = list.isNotEmpty()

        if (list.isEmpty()) {
            return
        }


        mEventListPopWindow.setEventPopList(list)
    }

    private fun goBack() {
        requireActivity().onBackPressed()
    }

}
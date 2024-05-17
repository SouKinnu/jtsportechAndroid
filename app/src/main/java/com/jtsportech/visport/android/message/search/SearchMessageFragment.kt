package com.jtsportech.visport.android.message.search

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntDef
import androidx.core.view.isVisible
import com.cloudhearing.android.lib_base.base.BaseBindingVmFragment
import com.cloudhearing.android.lib_base.utils.ANTI_SHAKE_THRESHOLD
import com.cloudhearing.android.lib_base.utils.clickFlow
import com.cloudhearing.android.lib_base.utils.hide
import com.cloudhearing.android.lib_base.utils.observeState
import com.cloudhearing.android.lib_base.utils.show
import com.cloudhearing.android.lib_base.utils.throttleFirst
import com.cloudhearing.android.lib_base.utils.toDp
import com.cloudhearing.android.lib_common.utils.SpacesPlusItemDecoration
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.databinding.FragmentSearchMessageBinding
import com.jtsportech.visport.android.home.search.SearchMatcheFragment
import com.jtsportech.visport.android.message.interactive.InteractiveMessageAdapter
import com.jtsportech.visport.android.message.match.MatchMessageAdapter
import com.jtsportech.visport.android.racedetail.RaceDetailActivity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class SearchMessageFragment :
    BaseBindingVmFragment<FragmentSearchMessageBinding, SearchMessageViewModel>(
        FragmentSearchMessageBinding::inflate
    ) {

    @IntDef(
        COMPETITION,
        INTERACTION,
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
         * 比赛
         */
        private const val COMPETITION = 0

        /**
         * 互动
         */
        private const val INTERACTION = 1

        /**
         * 待搜索
         */
        private const val TO_BE_SEARCHED = 0

        /**
         * 已搜索
         */
        private const val SEARCHED = 1
    }

    @TAB_CONTENT_TYPE
    private var mCurrentTabContent: Int = COMPETITION

    @SEARCH_STATUS
    private var mCurrentSearchStatus: Int = TO_BE_SEARCHED

    private val mMatchMessageAdapter: MatchMessageAdapter by lazy {
        MatchMessageAdapter().apply {
            setOnClickListener {
                RaceDetailActivity.jump(requireActivity(), it.msgTargetInfo?.matchInfoId.orEmpty())
            }
        }
    }

    private val mInteractiveMessageAdapter: InteractiveMessageAdapter by lazy {
        InteractiveMessageAdapter().apply {
            setOnClickListener {
                RaceDetailActivity.jump(requireActivity(), it.msgTargetInfo?.matchInfoId.orEmpty())
            }

            setOnPlayerClickListener {
                viewModel.togglePlayingStatus(it)
            }
        }
    }

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
                }
                .launchIn(mainScope)

            tvCompetition.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    toggleTabContent(COMPETITION)
                }
                .launchIn(mainScope)

            tvInteraction.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    toggleTabContent(INTERACTION)
                }
                .launchIn(mainScope)

            rvCompetition.apply {
                adapter = mMatchMessageAdapter
                addItemDecoration(SpacesPlusItemDecoration(bottomSpace = 10.toDp.toInt()))
                itemAnimator?.changeDuration = 0L
            }

            rvInteractive.apply {
                adapter = mInteractiveMessageAdapter
                addItemDecoration(SpacesPlusItemDecoration(bottomSpace = 10.toDp.toInt()))
                itemAnimator?.changeDuration = 0L
            }
        }
    }

    override fun initData() {

    }

    override fun initEvent() {
        viewModel.run {
            competitionMessageListFlow.observeState(this@SearchMessageFragment) {
                Timber.d("competitionMessageList 数量 ${it.size}")
                mMatchMessageAdapter.submitList(it)
            }

            interactionMessageListFlow.observeState(this@SearchMessageFragment) {
                Timber.d("interactionMessageList 数量 ${it.size}")
                mInteractiveMessageAdapter.submitList(it)
            }
        }
    }

    private fun goBack() {
        requireActivity().onBackPressed()
    }

    private fun handleSearchLogic() {
        if (searchValue.isEmpty()) {
            return
        }

        viewModel.search(searchValue)

        toggleTabContent(COMPETITION)
        toggleSearchStatus(SEARCHED)
    }

    private fun toggleTabContent(@TAB_CONTENT_TYPE type: Int) =
        binding.apply {
            when (type) {
                COMPETITION -> {
                    tvCompetition.isSelected = true
                    tvInteraction.isSelected = false
                    rvCompetition.show()
                    rvInteractive.hide()
                }

                INTERACTION -> {
                    tvCompetition.isSelected = false
                    tvInteraction.isSelected = true
                    rvCompetition.hide()
                    rvInteractive.show()
                }

                else -> {

                }
            }

            mCurrentTabContent = type
        }

    private fun toggleSearchStatus(@SEARCH_STATUS status: Int) = binding.apply {
        when (status) {
            TO_BE_SEARCHED -> {
                llSiftTool.hide()
                rvCompetition.hide()
                rvInteractive.hide()
            }

            SEARCHED -> {
                llSiftTool.show()
                rvCompetition.show()
                rvInteractive.hide()
            }
        }

        mCurrentSearchStatus = status
    }
}
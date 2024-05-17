package com.jtsportech.visport.android.mine.recentlyWatched

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.cloudhearing.android.lib_base.base.BaseBindingVmFragment
import com.cloudhearing.android.lib_base.utils.hideCurrentFragment
import com.cloudhearing.android.lib_base.utils.showFragment
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.dataSource.home.HomeTabAdapter
import com.jtsportech.visport.android.databinding.FragmentRecentlyWatchedBinding
import com.jtsportech.visport.android.mine.recentlyWatched.league.LeagueWatchHistoryFragment
import com.jtsportech.visport.android.mine.recentlyWatched.match.MatchWatchHistoryFragment
import com.jtsportech.visport.android.mine.recentlyWatched.train.TrainWatchHistoryFragment
import com.jtsportech.visport.android.utils.getHomeTabData
import com.jtsportech.visport.android.utils.getRecentlyWatchedTabData
import com.jtsportech.visport.android.webview.WebActivityArgs
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecentlyWatchedFragment :
    BaseBindingVmFragment<FragmentRecentlyWatchedBinding, RecentlyWatchedViewModel>(
        FragmentRecentlyWatchedBinding::inflate
    ) {

    private val mArgs by navArgs<RecentlyWatchedFragmentArgs>()

    private val mTrainWatchHistoryFragment: TrainWatchHistoryFragment by lazy {
        TrainWatchHistoryFragment()
    }

    private val mMatchWatchHistoryFragment: MatchWatchHistoryFragment by lazy {
        MatchWatchHistoryFragment()
    }

    private val mLeagueWatchHistoryFragment: LeagueWatchHistoryFragment by lazy {
        LeagueWatchHistoryFragment()
    }

    private val mHomeTabAdapter by lazy {
        HomeTabAdapter()
    }

    private var mSelectedPosition: Int = 1

    override fun initView() {
        mSelectedPosition = mArgs.index

        setupContainer()
        binding.apply {
            ibBack.setOnClickListener {
                goBack()
            }

            rvTab.apply {
                adapter = mHomeTabAdapter
                mHomeTabAdapter.submitList(
                    getRecentlyWatchedTabData(
                        requireContext(),
                        mSelectedPosition
                    )
                )
                rvTab.layoutManager = GridLayoutManager(requireContext(), 3)
                itemAnimator?.changeDuration = 0
            }
        }
    }

    override fun initData() {

    }

    override fun initEvent() {

    }

    private fun setupContainer() = binding.apply {
        mainScope.launch {
//            val selectedPosition = withContext(dispatchers.io) {
//                val index = getRecentlyWatchedTabData(requireContext()).indexOfFirst {
//                    it.selected
//                }
//                if (index == -1) 1 else index
//            }

            when (mSelectedPosition) {
                0 -> showFragment(mTrainWatchHistoryFragment, flMain.id)
                1 -> showFragment(mMatchWatchHistoryFragment, flMain.id)
                2 -> showFragment(mLeagueWatchHistoryFragment, flMain.id)
            }

            mHomeTabAdapter.setOnItemClickListener { position,_ ->
                if (mSelectedPosition == position) {
                    return@setOnItemClickListener
                }

                mSelectedPosition = position

                mHomeTabAdapter.submitList(
                    getRecentlyWatchedTabData(
                        requireContext(),
                        mSelectedPosition
                    )
                )
                hideCurrentFragment()
                when (position) {
                    0 -> showFragment(mTrainWatchHistoryFragment, flMain.id)
                    1 -> showFragment(mMatchWatchHistoryFragment, flMain.id)
                    2 -> showFragment(mLeagueWatchHistoryFragment, flMain.id)
                }
            }
        }
    }

    private fun goBack() {
        requireActivity().onBackPressed()
    }


}
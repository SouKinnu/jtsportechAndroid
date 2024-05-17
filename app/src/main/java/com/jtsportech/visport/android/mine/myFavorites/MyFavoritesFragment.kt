package com.jtsportech.visport.android.mine.myFavorites

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.cloudhearing.android.lib_base.base.BaseBindingVmFragment
import com.cloudhearing.android.lib_base.utils.ANTI_SHAKE_THRESHOLD
import com.cloudhearing.android.lib_base.utils.clickFlow
import com.cloudhearing.android.lib_base.utils.handleOnBackPressedDispatcher
import com.cloudhearing.android.lib_base.utils.hide
import com.cloudhearing.android.lib_base.utils.hideCurrentFragment
import com.cloudhearing.android.lib_base.utils.observeEvent
import com.cloudhearing.android.lib_base.utils.show
import com.cloudhearing.android.lib_base.utils.showFragment
import com.cloudhearing.android.lib_base.utils.throttleFirst
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.dataSource.home.HomeTabAdapter
import com.jtsportech.visport.android.databinding.FragmentMyFavoritesBinding
import com.jtsportech.visport.android.mine.myFavorites.league.LeagueFavoritesFragment
import com.jtsportech.visport.android.mine.myFavorites.match.MatchFavoritesFragment
import com.jtsportech.visport.android.mine.myFavorites.train.TrainFavoritesFragment
import com.jtsportech.visport.android.utils.getRecentlyWatchedTabData
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

class MyFavoritesFragment : BaseBindingVmFragment<FragmentMyFavoritesBinding, MyFavoritesViewModel>(
    FragmentMyFavoritesBinding::inflate
) {

    //    private val mSharedViewModel: MyFavoritesSharedViewModel by viewModels()
    private lateinit var mSharedViewModel: MyFavoritesSharedViewModel

    private val mTrainFavoritesFragment: TrainFavoritesFragment by lazy {
        TrainFavoritesFragment()
    }

    private val mMatchFavoritesFragment: MatchFavoritesFragment by lazy {
        MatchFavoritesFragment()
    }

    private val mLeagueFavoritesFragment: LeagueFavoritesFragment by lazy {
        LeagueFavoritesFragment()
    }

    private val mHomeTabAdapter by lazy {
        HomeTabAdapter()
    }

    private var mSelectedPosition: Int = 1
    private var mIsEditMode: Boolean = false
    private var mIsCheckAll: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSharedViewModel =
            ViewModelProvider(requireActivity())[MyFavoritesSharedViewModel::class.java]
    }

    override fun initView() {
        setupContainer()
        binding.apply {
            ibBack.setOnClickListener {
                goBack()
            }

            tvEditMode.clickFlow()
                .onEach {
                    toggleEditMode()
                }
                .launchIn(mainScope)

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

            ivRadio.clickFlow()
                .onEach {
                    toggleCheckAll()
                }
                .launchIn(mainScope)

            tvCheckAll.clickFlow()
                .onEach {
                    toggleCheckAll()
                }
                .launchIn(mainScope)

            tvUnfavorite.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    unfavorite()
                }
                .launchIn(mainScope)
        }
    }

    override fun initData() {

    }

    override fun initEvent() {
        viewModel.run {
            updateFavoriteListFlowEvents.observeEvent(this@MyFavoritesFragment) {
                toggleEditMode()
                updateFavoriteList()
            }
        }

        mSharedViewModel.run {
            checkAllRadioFlowEvents.observeEvent(this@MyFavoritesFragment) {
                Timber.d("共享走了 MyFavoritesFragment $it")
                mIsCheckAll = it
                binding.ivRadio.isSelected = mIsCheckAll
            }

            selectedListFlowEvents.observeEvent(this@MyFavoritesFragment) {
                Timber.d("选中数量 ${it.size}")
                handleSelectedList(it)
            }
        }
    }

    private fun toggleEditMode() {
        mIsEditMode = !mIsEditMode

        if (mIsEditMode) {
            binding.tvEditMode.text = getString(R.string.my_favorites_finish)
            binding.clEditBar.show()
        } else {
            binding.tvEditMode.text = getString(R.string.my_favorites_edit)
            binding.clEditBar.hide()
        }

        notifyFragmentEditMode(mIsEditMode)
    }

    private fun toggleCheckAll() {
        mIsCheckAll = !mIsCheckAll

        binding.ivRadio.isSelected = mIsCheckAll

        notifyFragmentCheckAll(mIsCheckAll)
    }

    private fun updateFavoriteList() {
        when (mSelectedPosition) {
            0 -> mTrainFavoritesFragment.updateFavorites()
            1 -> mMatchFavoritesFragment.updateFavorites()
            2 -> mLeagueFavoritesFragment.updateFavorites()
        }
    }

    private fun unfavorite() {
        when (mSelectedPosition) {
            0 -> mTrainFavoritesFragment.unFavorite()
            1 -> mMatchFavoritesFragment.unFavorite()
            2 -> mLeagueFavoritesFragment.unFavorite()
        }
    }

    private fun exitEditMode() = mainScope.launch {
        mIsEditMode = false
        mIsCheckAll = false

        binding.tvEditMode.text = getString(R.string.my_favorites_edit)
        binding.clEditBar.hide()

        notifyFragmentEditMode(mIsEditMode)
    }

    private fun notifyFragmentEditMode(isEditMode: Boolean) {
        when (mSelectedPosition) {
            0 -> mTrainFavoritesFragment.setEditMode(isEditMode)
            1 -> mMatchFavoritesFragment.setEditMode(isEditMode)
            2 -> mLeagueFavoritesFragment.setEditMode(isEditMode)
        }

    }

    private fun notifyFragmentCheckAll(isCheckAll: Boolean) {
        when (mSelectedPosition) {
            0 -> mTrainFavoritesFragment.setCheckAll(isCheckAll)
            1 -> mMatchFavoritesFragment.setCheckAll(isCheckAll)
            2 -> mLeagueFavoritesFragment.setCheckAll(isCheckAll)
        }
    }

    private fun handleSelectedList(ids: List<String>) {
        if (ids.isEmpty()) {
            return
        }

        viewModel.unfavorite(ids)
    }

    private fun setupContainer() = binding.apply {
        mainScope.launch {
//            val selectedPosition = withContext(dispatchers.io) {
//                val index = getRecentlyWatchedTabData(requireContext(),mSelectedPosition).indexOfFirst {
//                    it.selected
//                }
//                if (index == -1) 1 else index
//            }

            when (mSelectedPosition) {
                0 -> showFragment(mTrainFavoritesFragment, flMain.id)
                1 -> showFragment(mMatchFavoritesFragment, flMain.id)
                2 -> showFragment(mLeagueFavoritesFragment, flMain.id)
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
                    0 -> {
                        showFragment(mTrainFavoritesFragment, flMain.id)
                    }

                    1 -> showFragment(mMatchFavoritesFragment, flMain.id)
                    2 -> showFragment(mLeagueFavoritesFragment, flMain.id)
                }

                exitEditMode()
            }
        }
    }

    private fun goBack() {
        requireActivity().onBackPressed()
    }


}
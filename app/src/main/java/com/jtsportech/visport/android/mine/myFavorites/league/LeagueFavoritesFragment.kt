package com.jtsportech.visport.android.mine.myFavorites.league

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.cloudhearing.android.lib_base.base.BaseBindingVmFragment
import com.cloudhearing.android.lib_base.utils.observeEvent
import com.cloudhearing.android.lib_base.utils.observeState
import com.cloudhearing.android.lib_base.utils.systremToast
import com.cloudhearing.android.lib_base.utils.toDp
import com.cloudhearing.android.lib_common.utils.SpacesItemDecoration
import com.jtsportech.visport.android.databinding.FragmentLeagueFavoritesBinding
import com.jtsportech.visport.android.mine.myFavorites.IEditModeView
import com.jtsportech.visport.android.mine.myFavorites.MyFavoritesSharedViewModel
import com.jtsportech.visport.android.racedetail.RaceDetailActivity
import timber.log.Timber

class LeagueFavoritesFragment :
    BaseBindingVmFragment<FragmentLeagueFavoritesBinding, LeagueFavoritesViewModel>(
        FragmentLeagueFavoritesBinding::inflate
    ), IEditModeView {

    private lateinit var mSharedViewModel: MyFavoritesSharedViewModel

    private val mLeagueFavoritesAdapter: LeagueFavoritesAdapter by lazy {
        LeagueFavoritesAdapter().apply {
            setOnClickListener {
                if (it.isEditMode) {
                    viewModel.updateSelectedState(it)
                } else {
                    RaceDetailActivity.jump(
                        requireActivity(),
                        it.id.orEmpty(),
                        it.favoriteType.orEmpty(),
                        it.eventName.orEmpty(),
                        it.favoriteId.orEmpty()
                    )
                }
            }
        }
    }

    override fun initView() {
        binding.apply {
            rvLeague.apply {
                adapter = mLeagueFavoritesAdapter
                addItemDecoration(SpacesItemDecoration(12.toDp.toInt(), false))
                itemAnimator?.changeDuration = 0
            }
        }
    }

    override fun initData() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSharedViewModel =
            ViewModelProvider(requireActivity())[MyFavoritesSharedViewModel::class.java]
    }

    override fun initEvent() {
        viewModel.run {
            leagueFavoritesListStateFlow.observeState(this@LeagueFavoritesFragment) {
                mSharedViewModel.examineCheckAllRadioState(it)
                mLeagueFavoritesAdapter.submitList(it)
            }

            toastFlowEvents.observeEvent(this@LeagueFavoritesFragment) {
                systremToast(it)
            }
        }
    }

    override fun setEditMode(isEditMode: Boolean) {
        Timber.d("setEditMode $isEditMode")
        viewModel.switchEditMode(isEditMode)
    }

    override fun setCheckAll(isCheckAll: Boolean) {
        viewModel.checkAll(isCheckAll)
    }

    override fun unFavorite() {
        mSharedViewModel.obtainUnFavorites(mLeagueFavoritesAdapter.currentList)
    }

    override fun updateFavorites() {
        viewModel.getFavorites()
    }


}
package com.jtsportech.visport.android.mine.myFavorites.train

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.cloudhearing.android.lib_base.base.BaseBindingVmFragment
import com.cloudhearing.android.lib_base.utils.observeEvent
import com.cloudhearing.android.lib_base.utils.observeState
import com.cloudhearing.android.lib_base.utils.systremToast
import com.cloudhearing.android.lib_base.utils.toDp
import com.cloudhearing.android.lib_common.utils.SpacesItemDecoration
import com.jtsportech.visport.android.databinding.FragmentTrainFavoritesBinding
import com.jtsportech.visport.android.mine.myFavorites.IEditModeView
import com.jtsportech.visport.android.mine.myFavorites.MyFavoritesSharedViewModel
import com.jtsportech.visport.android.racedetail.RaceDetailActivity
import timber.log.Timber


class TrainFavoritesFragment :
    BaseBindingVmFragment<FragmentTrainFavoritesBinding, TrainFavoritesViewModel>(
        FragmentTrainFavoritesBinding::inflate
    ), IEditModeView {

    //    private val mSharedViewModel: MyFavoritesSharedViewModel by viewModels()
    private lateinit var mSharedViewModel: MyFavoritesSharedViewModel

    private val mTrainFavoritesAdapter: TrainFavoritesAdapter by lazy {
        TrainFavoritesAdapter().apply {
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
            rvTrain.apply {
                adapter = mTrainFavoritesAdapter
                addItemDecoration(SpacesItemDecoration(20.toDp.toInt(), false))
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
            trainFavoritesListStateFlow.observeState(this@TrainFavoritesFragment) {
                Timber.d("走了1 $it")
                mSharedViewModel.examineCheckAllRadioState(it)
                mTrainFavoritesAdapter.submitList(it)
            }

            toastFlowEvents.observeEvent(this@TrainFavoritesFragment) {
                systremToast(it)
            }
        }

        mSharedViewModel.run {
            checkAllRadioFlowEvents.observeEvent(this@TrainFavoritesFragment) {
                Timber.d("共享走了 TrainFavoritesFragment")
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
        mSharedViewModel.obtainUnFavorites(mTrainFavoritesAdapter.currentList)
    }

    override fun updateFavorites() {
        viewModel.getFavorites()
    }


}
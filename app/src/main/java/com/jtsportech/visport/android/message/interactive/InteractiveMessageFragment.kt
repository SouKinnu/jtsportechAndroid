package com.jtsportech.visport.android.message.interactive

import com.cloudhearing.android.lib_base.base.BaseBindingVmFragment
import com.cloudhearing.android.lib_base.utils.observeState
import com.cloudhearing.android.lib_base.utils.toDp
import com.cloudhearing.android.lib_common.utils.SpacesPlusItemDecoration
import com.jtsportech.visport.android.databinding.FragmentInteractiveMessageBinding
import com.jtsportech.visport.android.racedetail.RaceDetailActivity

class InteractiveMessageFragment :
    BaseBindingVmFragment<FragmentInteractiveMessageBinding, InteractiveMessageViewModel>(
        FragmentInteractiveMessageBinding::inflate
    ) {

    private val mInteractiveMessageAdapter: InteractiveMessageAdapter by lazy {
        InteractiveMessageAdapter().apply {
            setOnClickListener {
                RaceDetailActivity.jump(requireActivity(),it.msgTargetInfo?.matchInfoId.orEmpty())
            }

            setOnPlayerClickListener {
                viewModel.togglePlayingStatus(it)
            }
        }
    }

    override fun initView() {
        binding.apply {
            apInteractiveMessage.setOnClickLeftIconListener {
                goBack()
            }

            rvMessage.apply {
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
            messageNoticeListStateFlow.observeState(this@InteractiveMessageFragment) {
                mInteractiveMessageAdapter.submitList(it)
            }
        }
    }

    private fun goBack() {
        requireActivity().onBackPressed()
    }

}
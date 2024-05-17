package com.jtsportech.visport.android.mine.setup

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.cloudhearing.android.lib_base.base.BaseBindingVmFragment
import com.cloudhearing.android.lib_base.utils.ANTI_SHAKE_THRESHOLD
import com.cloudhearing.android.lib_base.utils.clickFlow
import com.cloudhearing.android.lib_base.utils.observeState
import com.cloudhearing.android.lib_base.utils.throttleFirst
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.databinding.FragmentSetupBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class SetupFragment :
    BaseBindingVmFragment<FragmentSetupBinding, SetupViewModel>(FragmentSetupBinding::inflate) {
    override fun initView() {
        binding.apply {
            apSetup.setOnClickLeftIconListener {
                goBack()
            }

            sbClearCache.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    viewModel.clearCacheSize()
                }
                .launchIn(mainScope)

            sbCheckForUpdates.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {

                }
                .launchIn(mainScope)

            sbLanguageSwitch.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    onLanguage()
                }
                .launchIn(mainScope)

            sbEncourage.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {

                }
                .launchIn(mainScope)
        }
    }

    override fun initData() {

    }

    override fun initEvent() {
        viewModel.run {
            cacheSizeFlow.observeState(this@SetupFragment) {
                binding.sbClearCache.setStateInfo(it)
            }
        }
    }

    private fun goBack() {
        requireActivity().onBackPressed()
    }

    private fun onLanguage() {
        findNavController().navigate(SetupFragmentDirections.actionSetupFragmentToLanguageFragment())
    }


}
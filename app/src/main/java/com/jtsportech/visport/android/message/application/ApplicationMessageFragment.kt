package com.jtsportech.visport.android.message.application

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cloudhearing.android.lib_base.base.BaseBindingVmFragment
import com.cloudhearing.android.lib_base.utils.ANTI_SHAKE_THRESHOLD
import com.cloudhearing.android.lib_base.utils.clickFlow
import com.cloudhearing.android.lib_base.utils.observeState
import com.cloudhearing.android.lib_base.utils.throttleFirst
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.databinding.FragmentApplicationMessageBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ApplicationMessageFragment :
    BaseBindingVmFragment<FragmentApplicationMessageBinding, ApplicationMessageViewModel>(
        FragmentApplicationMessageBinding::inflate
    ) {
    override fun initView() {
        binding.apply {
            apApplicationMessage.setOnClickLeftIconListener {
                goBack()
            }

            mbCheckForUpdates.clickFlow()
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
            appVersionStateFlow.observeState(this@ApplicationMessageFragment) {
                binding.tvVersion.text =
                    getString(R.string.application_the_version_has_been_updated, it.version)
                binding.tvVersionInfo.text =
                    getString(R.string.application_what_is_new_in_this_update, it.desc)
            }
        }
    }

    private fun goBack() {
        requireActivity().onBackPressed()
    }

}
package com.jtsportech.visport.android.mine.language

import androidx.navigation.fragment.findNavController
import com.cloudhearing.android.lib_base.base.BaseBindingVmFragment
import com.cloudhearing.android.lib_base.utils.ANTI_SHAKE_THRESHOLD
import com.cloudhearing.android.lib_base.utils.clickFlow
import com.cloudhearing.android.lib_base.utils.language.XLanguageUtils
import com.cloudhearing.android.lib_base.utils.throttleFirst
import com.cloudhearing.android.lib_common.utils.mmkv.PreferencesWrapper
import com.jtsportech.visport.android.databinding.FragmentLanguageBinding
import com.jtsportech.visport.android.mine.MineActivity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import java.util.Locale

class LanguageFragment :
    BaseBindingVmFragment<FragmentLanguageBinding, LanguageViewModel>(FragmentLanguageBinding::inflate) {
    override fun initView() {
        setupConfigLanguage()

        binding.apply {
            apLanguage.setOnClickLeftIconListener {
                goBack()
            }

            sbChineseSimplified.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    XLanguageUtils.applyLanguage(Locale.SIMPLIFIED_CHINESE, false)
                }
                .launchIn(mainScope)

            sbEnglish.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    XLanguageUtils.applyLanguage(Locale.ENGLISH, false)
                }
                .launchIn(mainScope)
        }

        Timber.d("getUseLocal: ${PreferencesWrapper.get().getUseLocal()}")
    }

    override fun initData() {

    }

    override fun initEvent() {

    }

    private fun goBack() {
        findNavController().popBackStack()
    }


    private fun setupConfigLanguage() {
        val local = PreferencesWrapper.get().getUseLocal()

        if (local.isEmpty() || local.contains("zh")) {
            binding.sbChineseSimplified.setShowMoreIcon(true)
            binding.sbEnglish.setShowMoreIcon(false)
        } else {
            binding.sbChineseSimplified.setShowMoreIcon(false)
            binding.sbEnglish.setShowMoreIcon(true)
        }
    }

}
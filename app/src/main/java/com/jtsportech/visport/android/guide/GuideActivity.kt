package com.jtsportech.visport.android.guide

import android.content.Intent
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.cloudhearing.android.lib_base.base.BaseBindingVmActivity
import com.cloudhearing.android.lib_base.utils.ANTI_SHAKE_THRESHOLD
import com.cloudhearing.android.lib_base.utils.clickFlow
import com.cloudhearing.android.lib_base.utils.observeState
import com.cloudhearing.android.lib_base.utils.throttleFirst
import com.cloudhearing.android.lib_common.network.dataSource.welcome.WelcomeItem
import com.dylanc.activityresult.launcher.context
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.databinding.ActivityGuideBinding
import com.jtsportech.visport.android.landing.LandingActivity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class GuideActivity :
    BaseBindingVmActivity<ActivityGuideBinding, GuideViewModel>(ActivityGuideBinding::inflate) {
    private val welcomeAdapter: WelcomeAdapter by lazy {
        WelcomeAdapter()
    }
    private val radioGroupLayoutParams: RadioGroup.LayoutParams by lazy {
        RadioGroup.LayoutParams(
            RadioGroup.LayoutParams.WRAP_CONTENT,
            RadioGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(8, 8, 8, 8)
        }
    }
    private val radioButtonList: ArrayList<RadioButton> by lazy {
        ArrayList()
    }

    override fun initView() {
        binding.tvSkip.clickFlow()
            .throttleFirst(ANTI_SHAKE_THRESHOLD)
            .onEach {
                onLogin()
            }
            .launchIn(mainScope)
    }

    override fun initData() {
        viewModel.getWelcomeImg()
        viewModel.leagueFavoritesListStateFlow.observeState(this@GuideActivity) {
            if (it.isNotEmpty()) {
                welcomeAdapter.submitList(it)
                for (e in it) {
                    val radioButton = radioButton()
                    binding.RadioGroup.addView(radioButton, radioGroupLayoutParams)
                    radioButtonList.add(radioButton)
                }
                if (it.size > 1) radioButtonList[0].isChecked = true
                else binding.RadioGroup.visibility = View.GONE
                binding.ViewPager.adapter = welcomeAdapter
                binding.defaultLayout.visibility = View.GONE
            }
            binding.ViewPager.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    for (i in 0 until radioButtonList.size) {
                        when (position) {
                            i -> radioButtonList[i].isChecked = true
                        }
                    }
                }
            })
        }


    }

    override fun initEvent() {

    }

    private fun onLogin() {
        startActivity(Intent(this, LandingActivity::class.java))
        finish()
    }

    private fun radioButton(): RadioButton {
        return RadioButton(context).apply {
            setButtonDrawable(0)
            isFocusable = false
            background =
                ContextCompat.getDrawable(context, R.drawable.selector_dialog_banner)
            View.generateViewId()
        }
    }
}
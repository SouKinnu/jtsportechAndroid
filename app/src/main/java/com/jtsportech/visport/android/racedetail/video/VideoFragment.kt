package com.jtsportech.visport.android.racedetail.video

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import com.cloudhearing.android.lib_base.base.BaseBindingVmFragment
import com.cloudhearing.android.lib_base.utils.CUT_VIDEO_ID
import com.cloudhearing.android.lib_base.utils.CUT_VIDEO_TYPE
import com.cloudhearing.android.lib_base.utils.TAB_INTO_TAB
import com.cloudhearing.android.lib_base.utils.observeEvent
import com.cloudhearing.android.lib_common.network.dataSource.racedetail.RaceDetailEntity
import com.cloudhearing.android.lib_common.utils.DensityUtils
import com.cloudhearing.android.lib_common.utils.mmkv.PreferencesWrapper
import com.jeremyliao.liveeventbus.LiveEventBus
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.databinding.FragmentVideoBinding


class VideoFragment :
    BaseBindingVmFragment<FragmentVideoBinding, VideoViewModel>(FragmentVideoBinding::inflate) {
    private lateinit var matchInfoId: String
    private lateinit var radioButtonList: ArrayList<RadioButton>
    private lateinit var videoListTabAdapter: VideoListTabAdapter
    private lateinit var radioGroupLayoutParams: RadioGroup.LayoutParams
    private var isTab: Boolean = false
    private lateinit var race: RaceDetailEntity

    private fun isMatchInfoIdInitialized() = ::matchInfoId.isInitialized

    companion object {
        fun getInstance(matchInfoId: String): VideoFragment {
            val mInstance = VideoFragment()
            val bundle = Bundle()
            bundle.putString("matchInfoId", matchInfoId)
            mInstance.arguments = bundle
            return mInstance
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            matchInfoId = requireArguments().getString("matchInfoId").toString()
        }
    }

    override fun initView() {
        radioButtonList = ArrayList()
        radioButtonList = arrayListOf()
        if (isMatchInfoIdInitialized()) {
            viewModel.getFavorite(matchInfoId)
            isTab = true
        } else {
            isTab = false
            matchInfoId = PreferencesWrapper.get().getCurrentWatchVideoId()
            viewModel.getFavorite(matchInfoId)
        }
        radioGroupLayoutParams = RadioGroup.LayoutParams(
            RadioGroup.LayoutParams.WRAP_CONTENT,
            RadioGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, 0, 15, 0)
        }

    }

    override fun initData() {
        val radioButton = radioButton("比赛全视频")
        binding.RadioGroup.addView(radioButton, radioGroupLayoutParams)
        radioButtonList.add(radioButton)
        viewModel.leagueFavoritesListStateFlow.observeEvent(this) {
            race = it
            for (element in it.eventNameList) {
                val radioButtons = radioButton(element.name)
                binding.RadioGroup.addView(radioButtons, radioGroupLayoutParams)
                radioButtonList.add(radioButtons)
            }
            binding.RadioGroup.setPadding(10, 0, 10, 0)
            videoListTabAdapter = VideoListTabAdapter(this, it.eventNameList, matchInfoId, isTab)
            binding.ViewPager2.adapter = videoListTabAdapter
            binding.ViewPager2.isUserInputEnabled = false
            binding.ViewPager2.offscreenPageLimit = it.eventNameList.size
            for (i in 0..<radioButtonList.size) {
                radioButtonList[i].isChecked = false
            }
            radioButtonList[0].isChecked = true
        }
    }

    override fun initEvent() {
        binding.RadioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (isTab) {
                for (i in 0..<radioButtonList.size) {
                    if (radioButtonList[i].id == checkedId) {
                        binding.ViewPager2.currentItem = i
                        radioButtonList[i].apply {
                            background = ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.bg_radio_color
                            )
                            setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.ecstasy
                                )
                            )
                        }
                    } else radioButtonList[i].apply {
                        background =
                            ContextCompat.getDrawable(requireContext(), R.drawable.bg_radio_color2)
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.dove_gray))
                    }
                }
            } else {
                for (i in 0..<radioButtonList.size) {
                    if (radioButtonList[i].id == checkedId) {
                        binding.ViewPager2.currentItem = i
                        radioButtonList[i].apply {
                            background =
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.shape_video_tab_bg
                                )
                            setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.yellow_text
                                )
                            )
                        }
                    } else radioButtonList[i].apply {
                        background = null
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
                }
            }
        }

        LiveEventBus.get<String>(CUT_VIDEO_ID).observe(this) {
            for (i in 0 until radioButtonList.size) {
                race.eventList.forEach { eventIt ->
                    if (it == eventIt.id && radioButtonList[i].text == eventIt.eventName) {
                        binding.RadioGroup.check(radioButtonList[i].id)
                        binding.scrollView.scrollTo(200 * i, 0)
                        return@observe
                    }
                }
            }
        }
        LiveEventBus.get<String>(CUT_VIDEO_TYPE).observe(this) {
            binding.RadioGroup.check(radioButtonList[0].id)
            binding.scrollView.scrollTo(0, 0)
        }
        LiveEventBus.get<String>(TAB_INTO_TAB).observe(this) {
            if (isTab) {
                for (i in 0..radioButtonList.size) {
                    if (it == radioButtonList[i].text) {
                        radioButtonList[i].isChecked = true
                        binding.scrollView.scrollTo(200 * i, 0)
                        return@observe
                    }
                }
            }
        }
    }

    private fun radioButton(name: String): RadioButton {
        return RadioButton(context).apply {
            setButtonDrawable(0)
//            width = 190
            gravity = Gravity.CENTER
            textSize = DensityUtils.px2sp(resources.getDimension(R.dimen.sp_12))
            text = name
            if (isTab)
                setPadding(resources.getDimension(R.dimen.dp_10).toInt(), resources.getDimension(R.dimen.dp_5).toInt(), resources.getDimension(R.dimen.dp_10).toInt(), resources.getDimension(R.dimen.dp_5).toInt())
            else
                setPadding(resources.getDimension(R.dimen.dp_5).toInt(), resources.getDimension(R.dimen.dp_10).toInt(), resources.getDimension(R.dimen.dp_5).toInt(), resources.getDimension(R.dimen.dp_10).toInt())
            View.generateViewId()
        }
    }
}
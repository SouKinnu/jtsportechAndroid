package com.jtsportech.visport.android.videoplay

import android.util.DisplayMetrics
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnKeyListener
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.cloudhearing.android.lib_base.base.BaseBindingVmActivity
import com.cloudhearing.android.lib_base.utils.observeEvent
import com.cloudhearing.android.lib_base.utils.observeState
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.databinding.ActivityVideoPlayBinding
import com.jtsportech.visport.android.videoplay.utils.VideoPlayingManger
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.utils.OrientationUtils


class VideoPlayActivity :
    BaseBindingVmActivity<ActivityVideoPlayBinding, VideoPlayViewModel>(ActivityVideoPlayBinding::inflate),
    View.OnClickListener {
    private val videoPlayingManger = VideoPlayingManger(GSYVideoOptionBuilder())
    private lateinit var videoUrl: String
    private lateinit var matchType: String
    private var hasFavorite: Boolean = false
    private lateinit var favoriteId: String
    private lateinit var eventName: String
    private lateinit var matchInfoId: String
    private lateinit var orientationUtils: OrientationUtils
    private lateinit var popMoreChoices: View
    private lateinit var popMoreChoicesWindow: PopupWindow
    private lateinit var popSelect: View
    private lateinit var popSelectWindow: PopupWindow
    private lateinit var radioButtonList: ArrayList<RadioButton>
    private lateinit var radioButtonText: ArrayList<String>
    private lateinit var radioGroup: RadioGroup
    private lateinit var viewPager: ViewPager2
    private lateinit var videoTabAdapter: VideoTabAdapter
    private lateinit var displayMetrics: DisplayMetrics
    private lateinit var download: TextView
    private lateinit var collect: CheckBox
    private lateinit var loop: RadioButton
    private lateinit var downloads: TextView
    private lateinit var collects: RadioButton
    private lateinit var screen: TextView
    private lateinit var wuShi: TextView
    private lateinit var qiShiWu: TextView
    private lateinit var yiBai: TextView
    private lateinit var yiBaiWu: TextView
    private lateinit var erBai: TextView
    private lateinit var sanBai: TextView
    private lateinit var backShi: LinearLayout
    private lateinit var backSanShi: LinearLayout
    private lateinit var backYiFen: LinearLayout
    private lateinit var backSanFen: LinearLayout
    private lateinit var frontShi: LinearLayout
    private lateinit var frontSanShi: LinearLayout
    private lateinit var frontYiFen: LinearLayout
    private lateinit var frontSanFen: LinearLayout
    private lateinit var frontWuFen: LinearLayout
    private lateinit var backWuFen: LinearLayout

    override fun initView() {
        videoUrl = intent.extras?.getString("videoUrl").toString()
        matchType = intent.extras?.getString("matchType").toString()
        hasFavorite = intent.extras?.getBoolean("hasFavorite") == true
        favoriteId = intent.extras?.getString("favoriteId").toString()
        matchInfoId = intent.extras?.getString("matchInfoId").toString()
        eventName = intent.extras?.getString("eventName").toString()
        displayMetrics = resources.displayMetrics
        radioButtonList = arrayListOf()
        radioButtonText = arrayListOf()
        val radioGroupLayoutParams = RadioGroup.LayoutParams(
            RadioGroup.LayoutParams.WRAP_CONTENT,
            RadioGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, 0, 15, 0)
        }


        initPopSelectView()
        initPopMoreChoicesView()
        viewModel.getRaceDetail(matchInfoId)
        val radioButton = radioButton("比赛全部视频")
        radioGroup.addView(radioButton, radioGroupLayoutParams)
        radioButtonList.add(radioButton)
        viewModel.leagueFavoritesListStateFlowEventName.observeState(this) {
            for (element in it) {
                val radioButtons = radioButton(element.name)
                radioGroup.addView(radioButtons, radioGroupLayoutParams)
                radioButtonList.add(radioButtons)
            }
            videoTabAdapter = VideoTabAdapter(it, this)
            viewPager.adapter = videoTabAdapter
            for (i in 0..<radioButtonList.size) {
                radioButtonList[i].isChecked = false
            }
            radioButtonList[0].isChecked = true
        }

        orientationUtils = OrientationUtils(this@VideoPlayActivity, binding.VideoPlayer)
    }

    override fun initData() {
        collect.isChecked = hasFavorite
        videoPlayingManger.playVideo(
            binding.VideoPlayer,
            videoUrl,
            "file:///C:/Users/22826/Desktop/download.jpg",
            "测试横屏显示",
            0,
            "0",
            object : VideoPlayingManger.VideoPlayingMangerlistener {
                override fun onClickStartIcon() {
                    //点击封面的播放按钮

                }

                override fun onAutoComplete() {
                    //播放完
                }
            }
        )
        binding.VideoPlayer.startPlayLogic()
    }

    override fun initEvent() {
        binding.VideoPlayer.compilation.setOnClickListener(this@VideoPlayActivity)
        binding.VideoPlayer.more.setOnClickListener(this@VideoPlayActivity)
        wuShi.setOnClickListener(this@VideoPlayActivity)
        qiShiWu.setOnClickListener(this@VideoPlayActivity)
        yiBai.setOnClickListener(this@VideoPlayActivity)
        yiBaiWu.setOnClickListener(this@VideoPlayActivity)
        erBai.setOnClickListener(this@VideoPlayActivity)
        sanBai.setOnClickListener(this@VideoPlayActivity)

        backShi.setOnClickListener(this@VideoPlayActivity)
        backSanShi.setOnClickListener(this@VideoPlayActivity)
        backYiFen.setOnClickListener(this@VideoPlayActivity)
        backSanFen.setOnClickListener(this@VideoPlayActivity)
        backWuFen.setOnClickListener(this@VideoPlayActivity)

        frontShi.setOnClickListener(this@VideoPlayActivity)
        frontSanShi.setOnClickListener(this@VideoPlayActivity)
        frontYiFen.setOnClickListener(this@VideoPlayActivity)
        frontSanFen.setOnClickListener(this@VideoPlayActivity)
        frontWuFen.setOnClickListener(this@VideoPlayActivity)

        collect.setOnClickListener(this@VideoPlayActivity)
        collects.setOnClickListener(this@VideoPlayActivity)
        collect.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) viewModel.apply {
                getAddFavorite(eventName, matchType, matchInfoId)
                leagueFavoritesListStateFlowAddFavorite.observeEvent(this@VideoPlayActivity) {}
            }
            viewModel.apply {
                getCancelFavorite(favoriteId)
                leagueFavoritesListStateFlowCancelFavorite.observeEvent(this@VideoPlayActivity) {}
            }
            popMoreChoices.setOnKeyListener(object : OnKeyListener {
                override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        popMoreChoicesWindow.dismiss()
                        return true
                    }
                    return false
                }
            })
            popSelect.setOnKeyListener(object : OnKeyListener {
                override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        popSelectWindow.dismiss()
                        return true
                    }
                    return false
                }
            })
            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                for (i in 0..<radioButtonList.size) {
                    if (radioButtonList[i].id == checkedId) {
                        viewPager.currentItem = i
                        radioButtonList[i].apply {
                            background = ContextCompat.getDrawable(
                                this@VideoPlayActivity,
                                R.drawable.bg_radio_color
                            )
                            setTextColor(
                                ContextCompat.getColor(
                                    this@VideoPlayActivity,
                                    R.color.ecstasy
                                )
                            )
                        }
                    } else {
                        radioButtonList[i].apply {
                            background =
                                ContextCompat.getDrawable(
                                    this@VideoPlayActivity,
                                    R.drawable.bg_radio_color2
                                )
                            setTextColor(
                                ContextCompat.getColor(
                                    this@VideoPlayActivity,
                                    R.color.dove_gray
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun radioButton(name: String): RadioButton {
        return RadioButton(this@VideoPlayActivity).apply {
            setButtonDrawable(0)
            textSize = 18f
            text = name
            setPadding(12, 12, 12, 12)
            View.generateViewId()
        }
    }

    private fun createPopWindow(view: View, popWidth: Int, popWeight: Int): PopupWindow {
        return PopupWindow(view).apply {
            setBackgroundDrawable(
                ContextCompat.getDrawable(
                    this@VideoPlayActivity,
                    R.drawable.pop_more_choices
                )
            )
            width = popWidth
            height = popWeight
            isFocusable = true
            isTouchable = true
        }
    }

    private fun popWindowView(getLayout: Int): View {
        return LayoutInflater.from(this@VideoPlayActivity)
            .inflate(getLayout, null).apply {
                isFocusable = true
            }
    }

    private fun initPopSelectView() {
        popSelect = popWindowView(R.layout.select_collection_video)
        popSelectWindow =
            createPopWindow(popSelect, displayMetrics.heightPixels, displayMetrics.widthPixels)
        radioGroup = popSelect.findViewById(R.id.RadioGroup)
        viewPager = popSelect.findViewById(R.id.ViewPager2)
    }

    private fun initPopMoreChoicesView() {
        popMoreChoices = popWindowView(R.layout.more_choices_view)
        popMoreChoicesWindow =
            createPopWindow(
                popMoreChoices,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        download = popMoreChoices.findViewById(R.id.download)
        collect = popMoreChoices.findViewById(R.id.collect)
        loop = popMoreChoices.findViewById(R.id.loop)
        downloads = popMoreChoices.findViewById(R.id.downloads)
        collects = popMoreChoices.findViewById(R.id.collects)
        screen = popMoreChoices.findViewById(R.id.screen)
        wuShi = popMoreChoices.findViewById(R.id.wuShi)
        qiShiWu = popMoreChoices.findViewById(R.id.qiShiWu)
        yiBai = popMoreChoices.findViewById(R.id.yiBai)
        yiBaiWu = popMoreChoices.findViewById(R.id.yiBaiWu)
        erBai = popMoreChoices.findViewById(R.id.erBai)
        sanBai = popMoreChoices.findViewById(R.id.SanBai)
        backShi = popMoreChoices.findViewById(R.id.backShi)
        backSanShi = popMoreChoices.findViewById(R.id.backSanShi)
        backYiFen = popMoreChoices.findViewById(R.id.backYiFen)
        backSanFen = popMoreChoices.findViewById(R.id.backSanFen)
        frontShi = popMoreChoices.findViewById(R.id.frontShi)
        frontSanShi = popMoreChoices.findViewById(R.id.frontSanShi)
        frontYiFen = popMoreChoices.findViewById(R.id.frontYiFen)
        frontSanFen = popMoreChoices.findViewById(R.id.frontSanFen)
        frontWuFen = popMoreChoices.findViewById(R.id.frontWuFen)
        backWuFen = popMoreChoices.findViewById(R.id.backWuFen)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                binding.VideoPlayer.compilation.id -> popSelectWindow.showAsDropDown(
                    binding.rightView,
                    0,
                    0,
                    Gravity.START
                )

                binding.VideoPlayer.more.id -> popMoreChoicesWindow.showAsDropDown(
                    binding.topView,
                    0,
                    0,
                    Gravity.BOTTOM
                )

                qiShiWu.id -> binding.VideoPlayer.speed = 0.75F
                wuShi.id -> binding.VideoPlayer.speed = 0.5F
                yiBai.id -> binding.VideoPlayer.speed = 1.0F
                yiBaiWu.id -> binding.VideoPlayer.speed = 1.5F
                erBai.id -> binding.VideoPlayer.speed = 2.0F
                sanBai.id -> binding.VideoPlayer.speed = 3.0F

                backShi.id -> binding.VideoPlayer.seekTo(binding.VideoPlayer.currentPositionWhenPlaying - 10000)
                backSanShi.id -> binding.VideoPlayer.seekTo(binding.VideoPlayer.currentPositionWhenPlaying - 30000)
                backYiFen.id -> binding.VideoPlayer.seekTo(binding.VideoPlayer.currentPositionWhenPlaying - 60000)
                backSanFen.id -> binding.VideoPlayer.seekTo(binding.VideoPlayer.currentPositionWhenPlaying - 180000)
                backWuFen.id -> binding.VideoPlayer.seekTo(binding.VideoPlayer.currentPositionWhenPlaying - 300000)

                frontShi.id -> binding.VideoPlayer.seekTo(binding.VideoPlayer.currentPositionWhenPlaying + 10000)
                frontSanShi.id -> binding.VideoPlayer.seekTo(binding.VideoPlayer.currentPositionWhenPlaying + 30000)
                frontYiFen.id -> binding.VideoPlayer.seekTo(binding.VideoPlayer.currentPositionWhenPlaying + 60000)
                frontSanFen.id -> binding.VideoPlayer.seekTo(binding.VideoPlayer.currentPositionWhenPlaying + 180000)
                frontWuFen.id -> binding.VideoPlayer.seekTo(binding.VideoPlayer.currentPositionWhenPlaying + 300000)

            }
        }
    }

}
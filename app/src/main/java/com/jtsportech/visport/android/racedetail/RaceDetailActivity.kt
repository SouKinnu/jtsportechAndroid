package com.jtsportech.visport.android.racedetail

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.ToastUtils
import com.cloudhearing.android.lib_base.utils.CURRENT_PLAY_INFO
import com.cloudhearing.android.lib_base.utils.CUT_VIDEO_ID
import com.cloudhearing.android.lib_base.utils.CUT_VIDEO_TYPE
import com.cloudhearing.android.lib_base.utils.TAB_INTO_TAB
import com.cloudhearing.android.lib_base.utils.observeEvent
import com.cloudhearing.android.lib_common.camera.scan.util.PermissionUtils
import com.cloudhearing.android.lib_common.image.loadRoundCornerImage
import com.cloudhearing.android.lib_common.network.dataSource.racedetail.Event
import com.cloudhearing.android.lib_common.network.dataSource.racedetail.RaceDetailEntity
import com.cloudhearing.android.lib_common.network.download.DownloadUtil.cancelDownLoadFile
import com.cloudhearing.android.lib_common.network.download.DownloadUtil.downLoadFile
import com.cloudhearing.android.lib_common.utils.DensityUtils
import com.cloudhearing.android.lib_common.utils.FileUtils
import com.cloudhearing.android.lib_common.utils.ShareUtils
import com.cloudhearing.android.lib_common.utils.mmkv.PreferencesWrapper
import com.jeremyliao.liveeventbus.LiveEventBus
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.components.dialog.CustomDialog
import com.jtsportech.visport.android.components.dialog.DownloadDialog
import com.jtsportech.visport.android.databinding.ActivityRacedetailBinding
import com.jtsportech.visport.android.racedetail.customview.InputDialogForFeedBack
import com.jtsportech.visport.android.racedetail.customview.MyGSYVideoPlayer
import com.jtsportech.visport.android.racedetail.customview.PopupBallPerformance
import com.jtsportech.visport.android.racedetail.customview.PopupMessages
import com.jtsportech.visport.android.racedetail.customview.PopupMore
import com.jtsportech.visport.android.racedetail.customview.PopupSpeed
import com.jtsportech.visport.android.racedetail.customview.PopupVisualAngle
import com.jtsportech.visport.android.racedetail.customview.PopupVoice
import com.jtsportech.visport.android.racedetail.editor.ImageEditorActivity
import com.jtsportech.visport.android.utils.BitmapUtils
import com.jtsportech.visport.android.utils.img
import com.jtsportech.visport.android.utils.record.FileUtil
import com.jtsportech.visport.android.utils.record.SaveUtils
import com.jtsportech.visport.android.utils.video
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.cache.CacheFactory
import com.shuyu.gsyvideoplayer.model.VideoOptionModel
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager
import tv.danmaku.ijk.media.exo2.ExoPlayerCacheManager
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import java.io.File


class RaceDetailActivity :
    BaseVideoActivity<ActivityRacedetailBinding, RaceDetailViewModel>(ActivityRacedetailBinding::inflate),
    View.OnClickListener {
    private lateinit var matchInfoId: String
    private lateinit var favoriteType: String
    private lateinit var eventName: String
    private lateinit var favoriteId: String
    private var firstLoad = true
    private lateinit var speeds: Array<String>

    //MATCH_VIDEO 比赛视频；MATCH_EVENT_VIDEO 比赛视频事件；MATCH_PLAYER_VIDEO 个人集锦
    private var matchType: String = "MATCH_VIDEO"
    private var videoUrl: String = ""
    private var isShare: Boolean = false
    private lateinit var eventUrls: StringBuilder
    private lateinit var matchTitle: String
    private lateinit var popupWindowMessages: PopupWindow
    private lateinit var popupWindowMore: PopupMore

    private lateinit var raceDetailEntity: RaceDetailEntity
    private lateinit var currentEvent: Event

    private lateinit var popVisualAngle: PopupVisualAngle
    private lateinit var popSpeed: PopupSpeed
    private lateinit var popSelect: View
    private lateinit var popSelectWindow: PopupWindow
    private lateinit var displayMetrics: DisplayMetrics
    private lateinit var inputDialogForFeedBack: InputDialogForFeedBack
    private lateinit var popupWindowVoice: PopupWindow
    private lateinit var ballPerformance: PopupBallPerformance
    private lateinit var customDialog: CustomDialog

    private val events = ArrayList<Event>()
    private var count = 0
    private lateinit var downloadDialog: DownloadDialog
    private lateinit var downLoadJob: Job

    private lateinit var removeEvent: Event

    @Target(AnnotationTarget.VALUE_PARAMETER)
    @Retention(AnnotationRetention.SOURCE)
    annotation class RACEDETAIL
    companion object {
        fun jump(
            activity: FragmentActivity,
            @RACEDETAIL id: String,
            favoriteType: String = "MATCH",
            eventName: String = "",
            favoriteId: String = "",
        ) {
            activity.startActivity(Intent(activity, RaceDetailActivity::class.java).apply {
                putExtras(Bundle().apply {
                    putSerializable("id", id)
                    putString("favoriteType", favoriteType)
                    putString("eventName", eventName)
                    putString("favoriteId", favoriteId)
                })
            })
        }
    }

    override fun initView() {
        //EXOPlayer内核，支持格式更多
        PlayerFactory.setPlayManager(Exo2PlayerManager::class.java)
        //exo缓存模式，支持m3u8，只支持exo
        CacheFactory.setCacheManager(ExoPlayerCacheManager::class.java)

        setupBundle()
        displayMetrics = resources.displayMetrics
        downloadDialog = DownloadDialog(this)
        popupWindowMore = PopupMore(this).apply {
            setOnCollectionVideoListener {
                if (!binding.videoPlayer.mainIsCollection) {
                    addCollection("MATCH_VIDEO", "MATCH")
                } else {
                    deleteCollection("MATCH_VIDEO")
                }
            }
            setOnCollectionEventsListener {
                if ("MATCH_EVENT_VIDEO" == matchType) {
                    if (!binding.videoPlayer.currentIsCollection) {
                        addCollection("MATCH_EVENT_VIDEO", "EVENTS")
                    } else {
                        deleteCollection("MATCH_EVENT_VIDEO")
                    }
                } else {
                    ToastUtils.showShort(getString(R.string.race_detail_choose_collection_events))
                }
            }
            setOnShareVideoListener {
                ShareUtils.shareMsg(this@RaceDetailActivity, "", "", videoUrl, null)
            }
            setOnShareEventsListener {
                if ("MATCH_VIDEO" == matchType) {
                    ToastUtils.showShort(getString(R.string.race_detail_share_play_list))
                    return@setOnShareEventsListener
                }
                isShare = true
                eventUrls = StringBuilder()
                if (events.size > 0) {
                    events.clear()
                }
                raceDetailEntity.eventList.forEach {
                    if (currentEvent.eventName == it.eventName) {
                        events.add(it)
                    }
                }
                viewModel.getVideoPlayUrl(events.removeFirst().id, matchType)
            }
        }
        initVideoBuilderMode()
        //允许window 的内容可以上移到刘海屏状态栏
        if (window != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val lp = window.attributes
            lp.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.setAttributes(lp)
        }
        initPopSelectView()
        popSelect.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    popSelectWindow.dismiss()
                    return true
                }
                return false
            }
        })
        ballPerformance = PopupBallPerformance(this).apply {
            setOnPerformanceClickListener(object :
                PopupBallPerformance.OnPerformanceClickListener {
                override fun good() {
                    viewModel.getEvaluate(currentEvent.id, "GOOD")
                }

                override fun normal() {
                    viewModel.getEvaluate(currentEvent.id, "NORMAL")
                }

                override fun poor() {
                    viewModel.getEvaluate(currentEvent.id, "BAD")
                }

            })
        }
        speeds = resources.getStringArray(R.array.speeds)
        popSpeed = PopupSpeed(this).apply {
            setOnSpeedClickListener(object : PopupSpeed.OnSpeedClickListener {
                override fun onSelectSpeed(speedStr: String) {
                    speeds.forEachIndexed { index, s ->
                        if (s == speedStr) {
                            getGSYVideoPlayer().setSpeedPlaying(
                                resources.getStringArray(R.array.speeds)[index].toFloat(),
                                true
                            )
                            getGSYVideoPlayer().setSpeedText()
                            return@forEachIndexed
                        }
                    }
                }

            })
        }
        LiveEventBus.get<String>(CUT_VIDEO_ID).observe(this) {
            hideBallPop()
            raceDetailEntity.eventList.forEach { it1 ->
                if (it == it1.id) {
                    currentEvent = it1
                    return@forEach
                }
            }
            matchType = "MATCH_EVENT_VIDEO"
            viewModel.getVideoPlayUrl(currentEvent.id, matchType)
        }
        LiveEventBus.get<String>(CUT_VIDEO_TYPE).observe(this) {
            hideBallPop()
            matchType = "MATCH_VIDEO"
            viewModel.getVideoPlayUrl(raceDetailEntity.videoList[visualAngle].id, matchType)
        }
        LiveEventBus.get<String>(TAB_INTO_TAB).observe(this) {
            binding.RadioGroup.check(binding.RadioGroup.getChildAt(1).id)
        }
    }

    private fun initPopVisualAngleView() {
        popVisualAngle = PopupVisualAngle(this, raceDetailEntity.videoList).apply {
            setOnVisualAngleClickListener(object : PopupVisualAngle.OnVisualAngleClickListener {
                override fun onVisualAngleCut(position: Int) {
                    visualAngle = position
                    matchType = "MATCH_VIDEO"
                    viewModel.getVideoPlayUrl(raceDetailEntity.videoList[visualAngle].id, matchType)
                }

            })
        }
    }

    private fun initPopSelectView() {
        popSelect =
            LayoutInflater.from(this).inflate(R.layout.select_collection_video, null).apply {
                isFocusable = true
            }
        popSelectWindow = PopupWindow(popSelect).apply {
            setBackgroundDrawable(
                ContextCompat.getDrawable(
                    this@RaceDetailActivity, R.drawable.pop_more_choices
                )
            )
            width = DensityUtils.dp2px(360f)
//            width = resources.getDimension(R.dimen.dp_360).toInt()
//            width = displayMetrics.widthPixels
            height = displayMetrics.widthPixels
            isFocusable = true
            isTouchable = true
            isClippingEnabled = false
        }
    }

    override fun initData() {
        binding.say.isFocusable = false
        viewModel.apply {
            showLoadingScreen("")
            getRaceDetail(matchInfoId)
            leagueFavoritesListLiveDataRaceDetail.observe(this@RaceDetailActivity) {
                hideLoadingScreen()
                raceDetailEntity = it
                if (firstLoad) {
                    firstLoad = false

                    binding.team1Image.loadRoundCornerImage(
                        url = it.matchInfo.team1OrgLogoImagePath.img(),
                    )
                    binding.team2Image.loadRoundCornerImage(
                        url = it.matchInfo.team2OrgLogoImagePath.img(),
                    )
                    binding.teamScore.text =
                        it.matchInfo.team1Score.toString() + ":" + it.matchInfo.team2Score.toString()
                    matchTitle = it.matchInfo.team1GroupName + " VS " + it.matchInfo.team2GroupName
                    initPopVisualAngleView()
                    if ("MATCH" == favoriteType) {
                        getVideoPlayUrl(raceDetailEntity.videoList[visualAngle].id, matchType)
                    } else {
                        raceDetailEntity.eventList.forEach { eventIt ->
                            if (eventName == eventIt.eventName && eventIt.eventNum == 1) {
                                currentEvent = eventIt
                                getVideoPlayUrl(currentEvent.id, matchType)
                                return@forEach
                            }
                        }
                        CoroutineScope(Dispatchers.IO).launch {
                            delay(1000)
                            LiveEventBus.get<String>(TAB_INTO_TAB).post(eventName)
                        }
                    }
                }
                getGSYVideoPlayer().mainIsCollection = raceDetailEntity.matchInfo.hasFavorite
                getGSYVideoPlayer().currentIsCollection = false
                if ("MATCH_EVENT_VIDEO" == matchType) {
                    raceDetailEntity.eventList.forEach { eventIt ->
                        if (currentEvent.id == eventIt.id) {
                            currentEvent = eventIt
                            return@forEach
                        }
                    }
                    raceDetailEntity.eventNameList.forEach { eventNameIt ->
                        if (currentEvent.eventName == eventNameIt.name) {
                            getGSYVideoPlayer().currentIsCollection = eventNameIt.hasFavorite
                            return@forEach
                        }
                    }
                }
                getGSYVideoPlayer().updateUI()
                setMoreSelectedInfo()
            }
            leagueFavoritesListStateFlowVideoPlayUrl.observeEvent(this@RaceDetailActivity) {
                if (isShare) {
                    eventUrls.append(it.data + "\n")
                    if (events.size > 0) {
                        viewModel.getVideoPlayUrl(events.removeFirst().id, matchType)
                    } else {
                        isShare = false
                        ShareUtils.shareMsg(
                            this@RaceDetailActivity,
                            "",
                            "",
                            eventUrls.toString(),
                            null
                        )
                    }
                } else {
                    isRecord = false
                    isNeedShow = "MAtCH" != matchType
                    videoUrl = it.data
                    LiveEventBus.get<String>(CURRENT_PLAY_INFO).post(
                        if ("MATCH_VIDEO" == matchType)
                            "null+$visualAngle"
                        else {
                            currentEvent.eventName + "+" + currentEvent.id
                        }
                    )
                    getGSYVideoPlayer().setUp(videoUrl, false, matchTitle)
                    getGSYVideoPlayer().startPlayLogic()
                    setVideoPlayerCollection()
                    setMoreSelectedInfo()
                }
            }
            leagueFavoritesListLiveDataAddFavorite.observe(this@RaceDetailActivity) {
                if (it.success) {
                    //收藏成功
                    getRaceDetail(matchInfoId)
                } else {
                    //收藏失败
                    hideLoadingScreen()
                }
            }
            leagueFavoritesListLiveDataCancelFavorite.observe(
                this@RaceDetailActivity
            ) {
                if ("成功" == it.msg) {
                    //取消成功
                    getRaceDetail(matchInfoId)
                } else {
                    //取消失败
                    hideLoadingScreen()
                }
            }
            leagueFavoritesLiveDataVideoDownLoadUrl.observe(this@RaceDetailActivity) {
                if (it.success) {
                    //获取下载地址成功
                    downLoad(it.data)
                }
            }
            leagueFavoritesListLiveDataCriticizeVoice.observe(this@RaceDetailActivity) {
                if (it.isNotEmpty()) {
                    ToastUtils.showShort(getString(R.string.race_detail_comments_success))
                }
            }
            leagueFavoritesListLiveDataEvaluate.observe(this@RaceDetailActivity) {
                ToastUtils.showShort(getString(R.string.race_detail_comments_success))
                ballPerformance.dismiss()
            }
            leagueFavoritesListLiveDataWatchRecord.observe(this@RaceDetailActivity) {
            }
            leagueFavoritesLiveDataAudioIsPlay.observe(this@RaceDetailActivity) {
                if (it == 1) {
                    getGSYVideoPlayer().onVideoPause()
                } else if (it == 0) {
                    getGSYVideoPlayer().onVideoResume(false)
                }
            }
        }

        binding.viewPager2.adapter =
            RaceDetailAdapter(this@RaceDetailActivity, matchInfoId)
        binding.viewPager2.currentItem = 0
        binding.viewPager2.offscreenPageLimit = 4
        binding.viewPager2.isUserInputEnabled = false
        binding.videoPlayer.backButton.visibility = View.GONE
        viewModel.getMessagesNum(matchInfoId, 0)
        viewModel.leagueFavoritesListStateFlowMessages.observeEvent(this@RaceDetailActivity) {
            binding.messageSize.text = it.toString()
        }
    }

    override fun videoResume(pauseTime: Long) {
        Timber.d("pauseTime.toInt : ${pauseTime.toInt()}")
        if ("" != videoUrl) {
            val videoOptionModel = VideoOptionModel(
                IjkMediaPlayer.OPT_CATEGORY_PLAYER,
                "seek-at-start",
                pauseTime.toInt()
            )
            val list = ArrayList<VideoOptionModel>()
            list.add(videoOptionModel)
            GSYVideoManager.instance().optionModelList = list

            getGSYVideoPlayer().setUp(videoUrl, false, matchTitle)
            getGSYVideoPlayer().startPlayLogic()
            setVideoPlayerCollection()
            setMoreSelectedInfo()
        }
    }

    private fun downLoad(url: String) {
        val fileName = if ("MATCH_VIDEO" == matchType)
            raceDetailEntity.matchInfo.name + "-" + raceDetailEntity.matchInfo.subType + "-" + raceDetailEntity.matchInfo.id
        else if (count != 0)
            removeEvent.eventName + "-" + removeEvent.eventPlayerList[0].playerFrontUserName + "-" + removeEvent.id
        else
            currentEvent.eventName + "-" + currentEvent.eventPlayerList[0].playerFrontUserName + "-" + currentEvent.id

        val dir = FileUtils.getFileDir(this, "video")
        downLoadJob = viewModel.downLoadFile(
            url.video(), dir.absolutePath, "$fileName.mp4",
            {
                runOnUiThread {
                    try {
                        if ("MATCH_VIDEO" == matchType) {
                            downloadDialog.setContent("${getString(R.string.race_detail_all_games_downloading)}:${it}%")
                        } else {
                            if (count != 0) {
                                downloadDialog.setContent(
                                    "${
                                        getString(R.string.race_detail_event) + currentEvent.eventName + getString(
                                            R.string.race_detail_downloading
                                        )
                                    }（${count - events.size}/${count}）:${it}%"
                                )
                            } else {
                                downloadDialog.setContent(
                                    "${
                                        getString(R.string.race_detail_event) + currentEvent.eventName + getString(
                                            R.string.race_detail_downloading
                                        )
                                    }:${it}%"
                                )

                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        count = 0
                        if (events.size > 0) {
                            events.clear()
                        }
                        downloadDialog.setContent("")
                        downloadDialog.dismiss()
                        getGSYVideoPlayer().onVideoResume(false)
                    }
                }
            }, {
                runOnUiThread {
                    try {
//                        FileUtil.videoSaveToNotifyGalleryToRefreshWhenVersionGreaterQ(
//                            this@RaceDetailActivity,
//                            it
//                        )
                        SaveUtils.saveVideoToAlbum(this@RaceDetailActivity, it.absolutePath)
                        if ("MATCH_VIDEO" == matchType) {
                            downloadDialog.setContent("")
                            downloadDialog.dismiss()
                            getGSYVideoPlayer().onVideoResume(false)
                        } else {
                            if (events.isNotEmpty() && events.size != 0) {
                                removeEvent = events.removeFirst()
                                viewModel.getVideoDownloadUrl(removeEvent.id, matchType)
                            } else {
                                count = 0
                                downloadDialog.setContent("")
                                downloadDialog.dismiss()
                                getGSYVideoPlayer().onVideoResume(false)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        count = 0
                        if (events.size > 0) {
                            events.clear()
                        }
                        downloadDialog.setContent("")
                        downloadDialog.dismiss()
                        getGSYVideoPlayer().onVideoResume(false)
                    }
                }
            }, {
                runOnUiThread {
//                    downloadDialog.setContent("下载失败:${it}")
                    count = 0
                    if (events.size > 0) {
                        events.clear()
                    }
                    downloadDialog.setContent("")
                    downloadDialog.dismiss()
                    getGSYVideoPlayer().onVideoResume(false)
                }
            })
    }

    private fun setupBundle() {
        val bundle = intent.extras
        matchInfoId = bundle?.getString("id") ?: ""
        favoriteType = bundle?.getString("favoriteType") ?: "MATCH"
        eventName = bundle?.getString("eventName") ?: ""
        favoriteId = bundle?.getString("favoriteId") ?: ""

        if ("MATCH" == favoriteType) {
            matchType = "MATCH_VIDEO"
        } else {
            matchType = "MATCH_EVENT_VIDEO"
        }
        PreferencesWrapper.get().setCurrentWatchVideoId(matchInfoId)
    }

    override fun initEvent() {
        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> {
                        binding.comprehensivedataStrip.visibility = View.VISIBLE
                        binding.videoStrip.visibility = View.INVISIBLE
                        binding.graphanalysisStrip.visibility = View.INVISIBLE
                        binding.playerStrip.visibility = View.INVISIBLE
                        binding.comprehensivedata.isChecked = true
                    }

                    1 -> {
                        binding.comprehensivedataStrip.visibility = View.INVISIBLE
                        binding.videoStrip.visibility = View.VISIBLE
                        binding.graphanalysisStrip.visibility = View.INVISIBLE
                        binding.playerStrip.visibility = View.INVISIBLE
                        binding.video.isChecked = true
                    }

                    2 -> {
                        binding.comprehensivedataStrip.visibility = View.INVISIBLE
                        binding.videoStrip.visibility = View.INVISIBLE
                        binding.graphanalysisStrip.visibility = View.VISIBLE
                        binding.playerStrip.visibility = View.INVISIBLE
                        binding.graphanalysis.isChecked = true
                    }

                    3 -> {
                        binding.comprehensivedataStrip.visibility = View.INVISIBLE
                        binding.videoStrip.visibility = View.INVISIBLE
                        binding.graphanalysisStrip.visibility = View.INVISIBLE
                        binding.playerStrip.visibility = View.VISIBLE
                        binding.player.isChecked = true
                    }
                }
            }
        })
        binding.RadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.comprehensivedata.id -> binding.viewPager2.currentItem = 0
                binding.video.id -> binding.viewPager2.currentItem = 1
                binding.graphanalysis.id -> binding.viewPager2.currentItem = 2
                binding.player.id -> binding.viewPager2.currentItem = 3
            }
        }
        binding.message.setOnClickListener(this@RaceDetailActivity)
        binding.say.setOnClickListener(this@RaceDetailActivity)
        binding.back.setOnClickListener(this@RaceDetailActivity)
        binding.more.setOnClickListener(this@RaceDetailActivity)
    }

    override fun onClick(v: View?) {
        if (v != null) when (v.id) {
            binding.back.id -> finish()

            binding.say.id -> {
                inputDialogForFeedBack = InputDialogForFeedBack(
                    this,
                    R.style.Dialog_NoTitle,
                    object : InputDialogForFeedBack.InputDialogListener {
                        override fun getInputTxt(editText: EditText) {
                            inputDialogForFeedBack.dismiss()
                        }

                        override fun getCriticizeText(contentText: String) {
                            viewModel.getCriticize(
                                0, "", contentText, "TEXT",
                                "DIRECT", matchInfoId, ""
                            )
                        }

                        override fun getCriticizeVideo() {
                            if (!com.blankj.utilcode.util.PermissionUtils.isGranted(Manifest.permission.RECORD_AUDIO)) {
                                com.blankj.utilcode.util.PermissionUtils.permission(Manifest.permission.RECORD_AUDIO)
                                    .callback { isAllGranted, _, _, _ ->
//                                        CoroutineScope(Dispatchers.Default).launch {
//                                            delay(500)
//                                            viewModel.leagueFavoritesLiveDataAudioIsPlay.postValue(1)
//                                        }
                                    }.request()
                                return
                            }
                            popupWindowVoice =
                                PopupVoice(
                                    this@RaceDetailActivity, window,
                                    object : PopupVoice.Callback {
                                        override fun getCriticizeVoice(duration: Int, file: File) {
                                            viewModel.getAudio(duration, file)
                                            viewModel.leagueFavoritesListStateFlowAudio.observeEvent(
                                                this@RaceDetailActivity
                                            ) {
                                                viewModel.getCriticize(
                                                    duration, it, "", "AUDIO",
                                                    "DIRECT", matchInfoId, ""
                                                )
                                            }
                                        }
                                    }
                                ).apply {
                                    showAsDropDown(binding.bottomLine, 0, 0, Gravity.TOP)
                                    viewModel.leagueFavoritesLiveDataAudioIsPlay.postValue(1)
                                    setOnDismissListener {
                                        viewModel.leagueFavoritesLiveDataAudioIsPlay.postValue(0)
                                    }
                                }
                        }
                    })
                inputDialogForFeedBack.show()
            }

            binding.message.id -> popupWindowMessages = PopupMessages(
                this@RaceDetailActivity,
                this@RaceDetailActivity,
                matchInfoId,
                window,
                viewModel
            ).apply { showAsDropDown(binding.bottomLine, 0, 0, Gravity.TOP) }

            binding.more.id -> popupWindowMore.apply {
                showAsDropDown(
                    binding.more,
                    -(resources.displayMetrics.widthPixels / 3 - binding.more.width),
                    0,
                    Gravity.BOTTOM
                )
            }
        }
    }

    override fun onPrepared(url: String?, vararg objects: Any?) {
        super.onPrepared(url, *objects)
        if (GSYVideoManager.instance().optionModelList != null) {
            GSYVideoManager.instance().optionModelList = null
        }
    }

    override fun sequentialPlayMode() {
        if ("MATCH_EVENT_VIDEO" == matchType) {
            events.clear()
            raceDetailEntity.eventList.forEach {
                if (currentEvent.eventName == it.eventName) {
                    events.add(it)
                }
            }
            if (currentEvent.eventNum == events.size) {
                matchType = "MATCH_VIDEO"
                val startTime = currentEvent.eventFrom + currentEvent.eventDuration

                val videoOptionModel = VideoOptionModel(
                    IjkMediaPlayer.OPT_CATEGORY_PLAYER,
                    "seek-at-start",
                    startTime
                )
                val list = ArrayList<VideoOptionModel>()
                list.add(videoOptionModel)
                GSYVideoManager.instance().optionModelList = list
                viewModel.getVideoPlayUrl(raceDetailEntity.videoList[visualAngle].id, matchType)
            } else {
                currentEvent = events[currentEvent.eventNum]
                viewModel.getVideoPlayUrl(currentEvent.id, matchType)
            }
        }
    }

    override fun getGSYVideoPlayer(): MyGSYVideoPlayer = binding.videoPlayer.currentPlayer

    override fun getGSYVideoOptionBuilder(): GSYVideoOptionBuilder {
        //内置封面可参考SampleCoverVideo
        val imageView = ImageView(this)
        //loadCover(imageView, url);
        return GSYVideoOptionBuilder()
            .setThumbImageView(imageView)
            .setUrl(videoUrl)
            .setCacheWithPlay(true)
            .setVideoTitle("")
            .setIsTouchWiget(true) //.setAutoFullWithSize(true)
            .setRotateViewAuto(false)
            .setLockLand(false)
            .setShowFullAnimation(false) //打开动画
            .setNeedLockFull(true)
            .setSeekRatio(1f)
    }

    override fun clickForFullScreen() {
    }

    override fun getDetailOrientationRotateAuto(): Boolean = true

    private fun setVideoPlayerCollection() {
        getGSYVideoPlayer().matchType = matchType
        getGSYVideoPlayer().mainIsCollection = raceDetailEntity.matchInfo.hasFavorite
        getGSYVideoPlayer().currentIsCollection = false
        if ("MATCH_EVENT_VIDEO" == matchType) {
            raceDetailEntity.eventNameList.forEach {
                if (currentEvent.eventName == it.name) {
                    getGSYVideoPlayer().currentIsCollection = it.hasFavorite
                    return@forEach
                }
            }
        }
        getGSYVideoPlayer().updateUI()
    }

    private fun setfocus(view: View) {
        view.isFocusable = true
        view.isFocusableInTouchMode = true
        view.requestFocus()
    }

    private fun setMoreSelectedInfo() {
        popupWindowMore.setCollectSelected(raceDetailEntity.matchInfo.hasFavorite)
        popupWindowMore.setCollectsSelected(false)
        if ("MATCH_EVENT_VIDEO" == matchType) {
            raceDetailEntity.eventNameList.forEach {
                if (currentEvent.eventName == it.name) {
                    popupWindowMore.setCollectsSelected(it.hasFavorite)
                    return@forEach
                }
            }
        }
    }

    override fun eventClick() {
        //弹出集锦弹窗
        popSelectWindow.showAsDropDown(getGSYVideoPlayer(), 0, 0, Gravity.END)
    }

    override fun downloadVideo(isEvents: Boolean) {
        //获取视频下载地址
        when (matchType) {
            "MATCH_VIDEO" -> {
                downloadDialog.setDownloadTitle(resources.getString(R.string.download_video))
                viewModel.getVideoDownloadUrl(
                    raceDetailEntity.videoList[visualAngle].id,
                    matchType
                )
            }

            "MATCH_EVENT_VIDEO" -> {
                if (isEvents) {
                    downloadDialog.setDownloadTitle(resources.getString(R.string.download_compilation))
                    if (events.size > 0) {
                        events.clear()
                    }
                    raceDetailEntity.eventList.forEach {
                        if (currentEvent.eventName == it.eventName) {
                            events.add(it)
                        }
                    }
                    count = events.size
                    removeEvent = events.removeFirst()
                    viewModel.getVideoDownloadUrl(removeEvent.id, matchType)
                } else {
                    if (events.size > 0) {
                        events.clear()
                    }
                    count = 0
                    downloadDialog.setDownloadTitle(resources.getString(R.string.download_video))
                    viewModel.getVideoDownloadUrl(currentEvent.id, matchType)
                }
            }
        }
        downloadDialog.setOnCancelClickListener {
            customDialog = CustomDialog(this@RaceDetailActivity).apply {
                setTitle(getString(R.string.race_detail_cancel_download))
                setContent(getString(R.string.race_detail_ready_cancel_download))
                setOnCancelClickListener {
                    dismiss()
                }
                setOnSureClickListener {
                    if (events.size > 0) {
                        events.clear()
                    }
                    count = 0
                    viewModel.cancelDownLoadFile()
                    viewModel.cancel()
                    downloadDialog.setContent("")
                    downloadDialog.dismiss()
                    dismiss()
                }
                show()
            }
        }.show()
        getGSYVideoPlayer().onVideoPause()
    }

    override fun addCollection(matchTypeValue: String, favoriteType: String) {
        //添加收藏
        showLoadingScreen(getString(R.string.race_detail_save_collection_loading))
        if (matchTypeValue == "MATCH_VIDEO") {
            viewModel.getAddFavorite("", favoriteType, raceDetailEntity.matchInfo.id)
        } else {
            viewModel.getAddFavorite(
                currentEvent.eventName,
                favoriteType,
                raceDetailEntity.matchInfo.id
            )
        }
    }

    override fun deleteCollection(matchTypeValue: String) {
        //取消收藏
        showLoadingScreen(getString(R.string.race_detail_cancel_collection_loading))
        if (matchTypeValue == "MATCH_VIDEO") {
            viewModel.getCancelFavorite(raceDetailEntity.matchInfo.favoriteId)
        } else {
            raceDetailEntity.eventNameList.forEach {
                if (currentEvent.eventName == it.name) {
                    viewModel.getCancelFavorite(it.favoriteId)
                    return@forEach
                }
            }
        }
    }

    override fun commentVideo(msg: String) {
        viewModel.getCriticize(
            0,
            "",
            msg,
            "TEXT",
            "DIRECT",
            raceDetailEntity.matchInfo.id,
            ""
        )
    }

    override fun showBallPerformanceDialog() {
        if ("MATCH_EVENT_VIDEO" == matchType) {
            ballPerformance.showAtLocation(
                getGSYVideoPlayer(),
                Gravity.END or Gravity.BOTTOM,
                250,
                250
            )
        }
    }

    override fun hideBallPop() {
        if (::ballPerformance.isInitialized && ballPerformance.isShowing) {
            ballPerformance.dismiss()
        }
    }

    override fun recordPlay() {
        viewModel.addWatchRecord(raceDetailEntity.matchInfo.id)
    }

    /**
     * 视角切换弹窗
     */
    override fun showVisualAngleCut() {
        if (!::popVisualAngle.isInitialized) {
            initPopVisualAngleView()
        }
        popVisualAngle.setCurrentVisualAnglePosition(visualAngle)
            .showAtLocation(getGSYVideoPlayer(), Gravity.END, 0, 0)

    }

    override fun selectSpeed() {
        popSpeed.setCurrentSpeed(getGSYVideoPlayer().speed.toString())
            .showAtLocation(getGSYVideoPlayer(), Gravity.END, 0, 0)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_BACK && getGSYVideoPlayer().getMIfCurrentIsFullscreen()) {
            if (orientationUtils.isEnable) {
                getGSYVideoPlayer().backButton.performClick()
                backToNormal()
            }
            return true
        }
        super.dispatchKeyEvent(event)
        if (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_DEL) {//监听到删除按钮被按下
            val text = getGSYVideoPlayer().write.text
            if (text.isNotEmpty()) {//判断文本框是否有文字，如果有就去掉最后一位
                val newText = text?.substring(0, text.length - 1)
                getGSYVideoPlayer().write.setText(newText)
                if (newText != null) {
                    //设置焦点在最后
                    getGSYVideoPlayer().write.setSelection(newText.length)
                }
            }
        }
        return super.dispatchKeyEvent(event)
    }

    var checkPermissionCode = 100
    override fun screenshot() {
        getGSYVideoPlayer().onVideoPause()
        getGSYVideoPlayer().taskShotPic {
            try {
                BitmapUtils.saveBitmap("screenshot.png", it, this@RaceDetailActivity)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (!Environment.isExternalStorageManager()) {
                        val intent = Intent(
                            Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                        )
                        intent.setData(Uri.parse("package:$packageName"))
                        startActivity(intent)
                    } else {
                        ImageEditorActivity.jump(
                            this@RaceDetailActivity,
                            filesDir.toString() + "/images/" + "screenshot.png",
                            1000
                        )
                    }
                } else {
                    if (!PermissionUtils.checkPermission(
                            this@RaceDetailActivity,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    ) {
                        //申请权限
                        PermissionUtils.requestPermissions(
                            this@RaceDetailActivity, arrayOf(
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            ), checkPermissionCode
                        )
                    } else {
                        ImageEditorActivity.jump(
                            this@RaceDetailActivity,
                            filesDir.toString() + "/images/" + "screenshot.png",
                            1000
                        )
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 1000 && resultCode == Activity.RESULT_OK) {
            val filePath = data?.getStringExtra("data")  //返回结果
        }
    }

    override fun enterFull() {
        if (::popupWindowMessages.isInitialized && popupWindowMessages.isShowing) {
            popupWindowMessages.dismiss()
        }
        if (::popupWindowMore.isInitialized && popupWindowMore.isShowing) {
            popupWindowMore.dismiss()
        }
        if (::popupWindowVoice.isInitialized && popupWindowVoice.isShowing) {
            popupWindowVoice.dismiss()
        }
        getGSYVideoPlayer().setSpeedText()
    }

    override fun backToNormal() {
        if (::popSelectWindow.isInitialized && popSelectWindow.isShowing) {
            popSelectWindow.dismiss()
        }
        if (::ballPerformance.isInitialized && ballPerformance.isShowing) {
            ballPerformance.dismiss()
        }
        if (::popVisualAngle.isInitialized && popVisualAngle.isShowing) {
            popVisualAngle.dismiss()
        }
        if (::popSpeed.isInitialized && popSpeed.isShowing) {
            popSpeed.dismiss()
        }
        if (::customDialog.isInitialized && customDialog.isShowing) {
            customDialog.dismiss()
        }
        if (::downloadDialog.isInitialized && downloadDialog.isShowing) {
            downloadDialog.dismiss()
            viewModel.cancelDownLoadFile()
            viewModel.cancel()
        }
    }
}
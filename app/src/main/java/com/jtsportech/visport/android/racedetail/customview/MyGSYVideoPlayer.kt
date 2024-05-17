package com.jtsportech.visport.android.racedetail.customview

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.RadioButton
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.ToastUtils
import com.jtsportech.visport.android.R
import com.shuyu.gsyvideoplayer.utils.CommonUtil
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Suppress("UNUSED_EXPRESSION")
open class MyGSYVideoPlayer : StandardGSYVideoPlayer {
    constructor(context: Context?, fullFlag: Boolean?) : super(context, fullFlag)
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    public var playAuto: Boolean = false
    public var mainIsCollection: Boolean = false
    public var currentIsCollection: Boolean = false
    public var matchType: String = "MATCH_VIDEO"
    private lateinit var speeds: Array<String>
    private lateinit var textViews: ArrayList<TextView>

    public lateinit var play: ImageView
    private lateinit var more: ImageView
    private lateinit var mBottomFullContainer: RelativeLayout
    public lateinit var compilation: ImageView
    private lateinit var fastforward: ImageView
    private lateinit var loopControl: ImageView
    private lateinit var beisu: TextView
    private lateinit var mode: ImageView
    private lateinit var fastback: ImageView

    private lateinit var popMoreChoices: View
    private lateinit var popMoreChoicesWindow: PopupWindow

    public lateinit var write: EditText
    private lateinit var download: TextView
    private lateinit var collect: TextView
    private lateinit var loop: RadioButton
    private lateinit var downloads: TextView
    private lateinit var collects: TextView
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
    private lateinit var screenShot: ImageView

    fun getMIfCurrentIsFullscreen(): Boolean = mIfCurrentIsFullscreen

    interface OnFuncClickListener {
        fun onEventClick()
        fun onVisualAngleCutClick()

        fun onDownloadVideo()
        fun onDownloadEvents()

        fun onAddCollection(matchType: String, favoriteType: String)

        fun onDeleteCollection(matchType: String)

        fun onCommentVideo(msg: String)
        fun onScreenShot()

        fun onSelectSpeed()
    }

    lateinit var onFuncClickListener: OnFuncClickListener
    private fun isOnFuncClickListenerInitialized() = ::onFuncClickListener.isInitialized

    fun updateUI() {
        collect.isSelected = mainIsCollection
        collects.isSelected = currentIsCollection
    }

    override fun init(context: Context?) {
        super.init(context)
        speeds = context?.resources?.getStringArray(R.array.speeds)!!
        write = findViewById(R.id.write)
        play = findViewById(R.id.play)
        more = findViewById(R.id.more)
        mBottomFullContainer = findViewById(R.id.layout_bottom_full)
        compilation = findViewById(R.id.compilation)
        fastforward = findViewById(R.id.fastforward)
        loopControl = findViewById(R.id.loop)
        beisu = findViewById(R.id.beisu)
        mode = findViewById(R.id.mode)
        fastback = findViewById(R.id.fastback)
        screenShot = findViewById(R.id.screen_shot)


        initPopMoreChoicesView()
        setSpeedText()

        write.setOnEditorActionListener(object : OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                    onFuncClickListener.onCommentVideo(write.text.toString())
                    write.setText("")
                    return false
                }
                return false
            }
        })
        play.setOnClickListener {
            mStartButton.performClick()
        }
        more.setOnClickListener {
            updateUI()
            popMoreChoicesWindow.showAsDropDown(this)
        }
        fastforward.setOnClickListener {
            updateUI()
            popMoreChoicesWindow.showAsDropDown(this)
        }
        loopControl.setOnClickListener {
            playAuto = !playAuto
            setLoopIcon()
            ToastUtils.showShort(
                if (playAuto)
                    context?.getString(R.string.race_detail_loop_play_mode)
                else
                    context?.getString(R.string.race_detail_sequential_play_mode)
            )
        }
        beisu.setOnClickListener {
            updateUI()
//            popMoreChoicesWindow.showAsDropDown(this)
            if (isOnFuncClickListenerInitialized()) {
                onFuncClickListener.onSelectSpeed()
            }
        }
        fastback.setOnClickListener {
            updateUI()
            popMoreChoicesWindow.showAsDropDown(this)
        }
        compilation.setOnClickListener {
            if (isOnFuncClickListenerInitialized()) {
                onFuncClickListener.onEventClick()
            }
        }
        mode.setOnClickListener {
            if (isOnFuncClickListenerInitialized()) {
                onFuncClickListener.onVisualAngleCutClick()
            }
        }
        screenShot.setOnClickListener {
            if (isOnFuncClickListenerInitialized()) {
                onFuncClickListener.onScreenShot()
            }
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
        popMoreChoices.setOnClickListener {
            popMoreChoicesWindow.dismiss()
        }

    }

    fun setSpeedText() {
        beisu.text = "${speed}X"

        speeds.forEachIndexed { index, s ->
            if (s == speed.toString()) {
                textViews[index].setTextColor(mContext.getColor(R.color.ecstasy))
            } else {
                textViews[index].setTextColor(mContext.getColor(R.color.white))
            }
        }
    }

    fun setLoopIcon() {
        if (playAuto) {
            loopControl.setImageResource(R.drawable.icon_loop_n)
        } else {
            loopControl.setImageResource(R.drawable.icon_loop_s)
        }
        loop.isChecked = playAuto
    }


    private fun initPopMoreChoicesView() {
        popMoreChoices = popWindowView(R.layout.more_choices_view)
        popMoreChoicesWindow = createPopWindow(
            popMoreChoices, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
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

        textViews = ArrayList()
        textViews.add(wuShi)
        textViews.add(qiShiWu)
        textViews.add(yiBai)
        textViews.add(yiBaiWu)
        textViews.add(erBai)
        textViews.add(sanBai)

        download.setOnClickListener {
            if (isOnFuncClickListenerInitialized()) {
                onFuncClickListener.onDownloadVideo()
            }
        }
        collect.setOnClickListener {
            if (isOnFuncClickListenerInitialized()) {
                if (mainIsCollection) {
                    onFuncClickListener.onDeleteCollection("MATCH_VIDEO")
                } else {
                    onFuncClickListener.onAddCollection("MATCH_VIDEO", "MATCH")
                }
            }
        }
        downloads.setOnClickListener {
            if (isOnFuncClickListenerInitialized()) {
                if ("MATCH_EVENT_VIDEO" != matchType) {
                    ToastUtils.showShort(context.getString(R.string.race_detail_choose_download_events))
                    return@setOnClickListener
                }
                onFuncClickListener.onDownloadEvents()
            }
        }
        collects.setOnClickListener {
            if (isOnFuncClickListenerInitialized()) {
                if ("MATCH_EVENT_VIDEO" != matchType) {
                    ToastUtils.showShort(context.getString(R.string.race_detail_choose_collection_events))
                    return@setOnClickListener
                }
                if (currentIsCollection) {
                    onFuncClickListener.onDeleteCollection("MATCH_EVENT_VIDEO")
                } else {
                    onFuncClickListener.onAddCollection("MATCH_EVENT_VIDEO", "EVENTS")
                }
            }
        }

        wuShi.setOnClickListener {
            setSpeedPlaying(0.5f, true)
            setSpeedText()
        }
        qiShiWu.setOnClickListener {
            setSpeedPlaying(0.75f, true)
            setSpeedText()
        }
        loop.setOnClickListener {
            playAuto = !playAuto
            setLoopIcon()
            ToastUtils.showShort(
                if (playAuto)
                    context?.getString(R.string.race_detail_loop_play_mode)
                else
                    context?.getString(R.string.race_detail_sequential_play_mode)
            )
        }
        yiBai.setOnClickListener {
            setSpeedPlaying(1.0f, true)
            setSpeedText()
        }
        yiBaiWu.setOnClickListener {
            setSpeedPlaying(1.5f, true)
            setSpeedText()
        }
        erBai.setOnClickListener {
            setSpeedPlaying(2.0f, true)
            setSpeedText()
        }
        sanBai.setOnClickListener {
            setSpeedPlaying(3.0f, true)
            setSpeedText()
        }
        backShi.setOnClickListener {
            gsyVideoManager.seekTo(
                if (currentPositionWhenPlaying - 10 * 1000 > 0) currentPositionWhenPlaying - 10 * 1000
                else 0
            )
        }
        backSanShi.setOnClickListener {
            gsyVideoManager.seekTo(
                if (currentPositionWhenPlaying - 30 * 1000 > 0) currentPositionWhenPlaying - 30 * 1000
                else 0
            )
        }
        backYiFen.setOnClickListener {
            gsyVideoManager.seekTo(
                if (currentPositionWhenPlaying - 60 * 1000 > 0) currentPositionWhenPlaying - 60 * 1000
                else 0
            )
        }
        backSanFen.setOnClickListener {
            gsyVideoManager.seekTo(
                if (currentPositionWhenPlaying - 3 * 60 * 1000 > 0) currentPositionWhenPlaying - 3 * 60 * 1000
                else 0
            )
        }
        backWuFen.setOnClickListener {
            gsyVideoManager.seekTo(
                if (currentPositionWhenPlaying - 5 * 60 * 1000 > 0) currentPositionWhenPlaying - 5 * 60 * 1000
                else 0
            )
        }
        frontShi.setOnClickListener {
            gsyVideoManager.seekTo(currentPositionWhenPlaying + 10 * 1000)
        }
        frontSanShi.setOnClickListener {
            gsyVideoManager.seekTo(currentPositionWhenPlaying + 30 * 1000)
        }
        frontYiFen.setOnClickListener {
            gsyVideoManager.seekTo(currentPositionWhenPlaying + 60 * 1000)
        }
        frontSanFen.setOnClickListener {
            gsyVideoManager.seekTo(currentPositionWhenPlaying + 3 * 60 * 1000)
        }
        frontWuFen.setOnClickListener {
            gsyVideoManager.seekTo(currentPositionWhenPlaying + 5 * 60 * 1000)
        }

    }

    private fun popWindowView(getLayout: Int): View {
        return LayoutInflater.from(context).inflate(getLayout, null).apply {
            isFocusable = true
        }
    }

    private fun createPopWindow(view: View, popWidth: Int, popWeight: Int): PopupWindow {
        return PopupWindow(view).apply {
            setBackgroundDrawable(
                ContextCompat.getDrawable(
                    context, R.drawable.pop_more_choices
                )
            )
            width = popWidth
            height = popWeight
            isFocusable = true
            isTouchable = true
            isClippingEnabled = false
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.video_layout_my
    }

    override fun updateStartImage() {
        super.updateStartImage()
        mStartButton.visibility = View.GONE
        if (mCurrentState == 2) {
            play.setImageResource(R.drawable.icon_video_pause)
        } else {
            play.setImageResource(R.drawable.icon_video_play)
        }
    }

    override fun getCurrentPlayer(): MyGSYVideoPlayer {
        return super.getCurrentPlayer() as MyGSYVideoPlayer
    }

    /**
     * 全屏时将对应处理参数逻辑赋给全屏播放器
     *
     * @param context
     * @param actionBar
     * @param statusBar
     * @return
     */
    override fun startWindowFullscreen(
        context: Context?, actionBar: Boolean, statusBar: Boolean
    ): GSYBaseVideoPlayer? {
        val gsyVideoPlayer: MyGSYVideoPlayer =
            super.startWindowFullscreen(context, actionBar, statusBar) as MyGSYVideoPlayer
        if (gsyVideoPlayer != null) {
            gsyVideoPlayer.setLockClickListener(mLockClickListener)
            gsyVideoPlayer.isNeedLockFull = isNeedLockFull
            initFullUI(gsyVideoPlayer)
            //比如你自定义了返回案件，但是因为返回按键底层已经设置了返回事件，所以你需要在这里重新增加的逻辑
            gsyVideoPlayer.fullscreenButton.visibility = View.GONE
            gsyVideoPlayer.currentIsCollection = currentIsCollection
            gsyVideoPlayer.mainIsCollection = mainIsCollection
            gsyVideoPlayer.matchType = matchType
            gsyVideoPlayer.playAuto = playAuto
            gsyVideoPlayer.onFuncClickListener = onFuncClickListener
            gsyVideoPlayer.isLockLand = false

        }
        return gsyVideoPlayer
    }

    /**
     * 推出全屏时将对应处理参数逻辑返回给非播放器
     *
     * @param oldF
     * @param vp
     * @param gsyVideoPlayer
     */
    override fun resolveNormalVideoShow(
        oldF: View?,
        vp: ViewGroup?,
        gsyVideoPlayer: GSYVideoPlayer?
    ) {
        super.resolveNormalVideoShow(oldF, vp, gsyVideoPlayer)
        if (gsyVideoPlayer != null) {
            val myGSYVideoPlayer: MyGSYVideoPlayer = gsyVideoPlayer as MyGSYVideoPlayer
            fullscreenButton.visibility = View.VISIBLE
            currentIsCollection = myGSYVideoPlayer.currentIsCollection
            mainIsCollection = myGSYVideoPlayer.mainIsCollection
            matchType = myGSYVideoPlayer.matchType
            playAuto = myGSYVideoPlayer.playAuto
            onFuncClickListener = myGSYVideoPlayer.onFuncClickListener

            if (myGSYVideoPlayer.mProgressDialog != null && myGSYVideoPlayer.mProgressDialog.isShowing) {
                myGSYVideoPlayer.mProgressDialog.dismiss()
            }
            if (myGSYVideoPlayer.mBrightnessDialog != null && myGSYVideoPlayer.mBrightnessDialog.isShowing) {
                myGSYVideoPlayer.mBrightnessDialog.dismiss()
            }
            if (myGSYVideoPlayer.mVolumeDialog != null && myGSYVideoPlayer.mVolumeDialog.isShowing) {
                myGSYVideoPlayer.mVolumeDialog.dismiss()
            }
            if (myGSYVideoPlayer.popMoreChoicesWindow.isShowing) {
                myGSYVideoPlayer.popMoreChoicesWindow.dismiss()
            }
        }
    }

    private fun initFullUI(myGSYVideoPlayer: MyGSYVideoPlayer) {
        if (mBottomProgressDrawable != null) {
            myGSYVideoPlayer.setBottomProgressBarDrawable(mBottomProgressDrawable)
        }
        if (mBottomShowProgressDrawable != null && mBottomShowProgressThumbDrawable != null) {
            myGSYVideoPlayer.setBottomShowProgressBarDrawable(
                mBottomShowProgressDrawable, mBottomShowProgressThumbDrawable
            )
        }
        if (mVolumeProgressDrawable != null) {
            myGSYVideoPlayer.setDialogVolumeProgressBar(mVolumeProgressDrawable)
        }
        if (mDialogProgressBarDrawable != null) {
            myGSYVideoPlayer.setDialogProgressBar(mDialogProgressBarDrawable)
        }
        if (mDialogProgressHighLightColor != -11 && mDialogProgressNormalColor != -11) {
            myGSYVideoPlayer.setDialogProgressColor(
                mDialogProgressHighLightColor, mDialogProgressNormalColor
            )
        }

        if (playAuto) {
            myGSYVideoPlayer.loopControl.setImageResource(R.drawable.icon_loop_n)
        } else {
            myGSYVideoPlayer.loopControl.setImageResource(R.drawable.icon_loop_s)
        }
        myGSYVideoPlayer.loop.isChecked = playAuto

        myGSYVideoPlayer.collect.isSelected = mainIsCollection
        myGSYVideoPlayer.collects.isSelected = currentIsCollection
    }

    var pressTime: Long = 0
    override fun touchLongPress(e: MotionEvent?) {
        super.touchLongPress(e)
        when (e?.action) {
            MotionEvent.ACTION_DOWN -> {
                setIsTouchWiget(false)
                setIsTouchWigetFull(false)
                if (e.x < width / 2) {
                    pressTime = currentPositionWhenPlaying
                    CoroutineScope(Dispatchers.Default).launch {
                        back()
                    }
                } else {
                    setSpeedPlaying(2f, true)
                    CoroutineScope(Dispatchers.Default).launch {
                        while (!mIsTouchWiget && !mIsTouchWigetFull) {
                            withContext(Dispatchers.Main) {
                                showProgressDialog(
                                    1f,
                                    CommonUtil.stringForTime(currentPositionWhenPlaying),
                                    currentPositionWhenPlaying,
                                    CommonUtil.stringForTime(duration),
                                    duration
                                )
                            }
                            delay(500)
                        }
                    }
                }
            }
        }
    }

    private suspend fun back() {
        while (!mIsTouchWiget && !mIsTouchWigetFull) {
            withContext(Dispatchers.Main) {
                pressTime -= 1000
                gsyVideoManager.seekTo(pressTime)
                showProgressDialog(
                    0f,
                    CommonUtil.stringForTime(pressTime),
                    pressTime,
                    CommonUtil.stringForTime(duration),
                    duration
                )
            }
            delay(500)
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event!!.action == MotionEvent.ACTION_UP && !mIsTouchWiget && !mIsTouchWigetFull) {
            speed = 1f
            setSpeedText()
            setSpeedPlaying(1f, true)
            setIsTouchWiget(true)
            setIsTouchWigetFull(true)
        }
        return super.onTouch(v, event)
    }

    override fun onClickUiToggle(e: MotionEvent?) {
        super.onClickUiToggle(e)
    }

    override fun hideAllWidget() {
        super.hideAllWidget()
        setViewShowState(mBottomFullContainer, INVISIBLE)
        setViewShowState(more, INVISIBLE)
        setViewShowState(screenShot, INVISIBLE)
        if (!mIfCurrentIsFullscreen) {
            setViewShowState(mBottomFullContainer, GONE)
            setViewShowState(more, GONE)
            setViewShowState(screenShot, GONE)
        }
    }

    override fun changeUiToNormal() {
        super.changeUiToNormal()
        setViewShowState(mBottomFullContainer, INVISIBLE)
        setViewShowState(more, INVISIBLE)
        setViewShowState(screenShot, INVISIBLE)
        if (!mIfCurrentIsFullscreen) {
            setViewShowState(mBottomFullContainer, GONE)
            setViewShowState(more, GONE)
            setViewShowState(screenShot, GONE)
        } else {
            mBottomContainer.setPadding(50, 0, 50, 0)
        }
    }


    override fun changeUiToPreparingShow() {
        super.changeUiToPreparingShow()
        setViewShowState(mBottomFullContainer, VISIBLE)
        setViewShowState(more, VISIBLE)
        setViewShowState(screenShot, VISIBLE)
        if (!mIfCurrentIsFullscreen) {
            setViewShowState(mBottomFullContainer, GONE)
            setViewShowState(more, GONE)
            setViewShowState(screenShot, GONE)
        } else {
            mBottomContainer.setPadding(50, 0, 50, 0)
        }
    }

    override fun changeUiToPlayingShow() {
        super.changeUiToPlayingShow()
        setViewShowState(mBottomFullContainer, VISIBLE)
        setViewShowState(more, VISIBLE)
        setViewShowState(screenShot, VISIBLE)
        if (!mIfCurrentIsFullscreen) {
            setViewShowState(mBottomFullContainer, GONE)
            setViewShowState(more, GONE)
            setViewShowState(screenShot, GONE)
        } else {
            mBottomContainer.setPadding(50, 0, 50, 0)
        }
    }

    override fun changeUiToPauseShow() {
        super.changeUiToPauseShow()
        setViewShowState(mBottomFullContainer, VISIBLE)
        setViewShowState(more, VISIBLE)
        setViewShowState(screenShot, VISIBLE)
        if (!mIfCurrentIsFullscreen) {
            setViewShowState(mBottomFullContainer, GONE)
            setViewShowState(more, GONE)
            setViewShowState(screenShot, GONE)
        } else {
            mBottomContainer.setPadding(50, 0, 50, 0)
        }
    }

    override fun changeUiToPlayingBufferingShow() {
        super.changeUiToPlayingBufferingShow()
        setViewShowState(mBottomFullContainer, VISIBLE)
        setViewShowState(more, VISIBLE)
        setViewShowState(screenShot, VISIBLE)
        if (!mIfCurrentIsFullscreen) {
            setViewShowState(mBottomFullContainer, GONE)
            setViewShowState(more, GONE)
            setViewShowState(screenShot, GONE)
        } else {
            mBottomContainer.setPadding(50, 0, 50, 0)
        }
    }

    override fun changeUiToCompleteShow() {
        super.changeUiToCompleteShow()
        setViewShowState(mBottomFullContainer, VISIBLE)
        setViewShowState(more, VISIBLE)
        setViewShowState(screenShot, VISIBLE)
        if (!mIfCurrentIsFullscreen) {
            setViewShowState(mBottomFullContainer, GONE)
            setViewShowState(more, GONE)
            setViewShowState(screenShot, GONE)
        } else {
            mBottomContainer.setPadding(50, 0, 50, 0)
        }
    }


    override fun changeUiToError() {
        super.changeUiToError()
        setViewShowState(mBottomFullContainer, INVISIBLE)
        setViewShowState(more, INVISIBLE)
        setViewShowState(screenShot, INVISIBLE)
        if (!mIfCurrentIsFullscreen) {
            setViewShowState(mBottomFullContainer, GONE)
            setViewShowState(more, GONE)
            setViewShowState(screenShot, GONE)
        } else {
            mBottomContainer.setPadding(50, 0, 50, 0)
        }
    }

    override fun changeUiToPrepareingClear() {
        super.changeUiToPrepareingClear()
        setViewShowState(mBottomFullContainer, INVISIBLE)
        setViewShowState(more, INVISIBLE)
        setViewShowState(screenShot, INVISIBLE)
        if (!mIfCurrentIsFullscreen) {
            setViewShowState(mBottomFullContainer, GONE)
            setViewShowState(more, GONE)
            setViewShowState(screenShot, GONE)
        } else {
            mBottomContainer.setPadding(50, 0, 50, 0)
        }
    }

    override fun changeUiToPlayingBufferingClear() {
        super.changeUiToPlayingBufferingClear()
        setViewShowState(mBottomFullContainer, INVISIBLE)
        setViewShowState(more, INVISIBLE)
        setViewShowState(screenShot, INVISIBLE)
        if (!mIfCurrentIsFullscreen) {
            setViewShowState(mBottomFullContainer, GONE)
            setViewShowState(more, GONE)
            setViewShowState(screenShot, GONE)
        } else {
            mBottomContainer.setPadding(50, 0, 50, 0)
        }
    }

    override fun changeUiToClear() {
        super.changeUiToClear()
        setViewShowState(mBottomFullContainer, INVISIBLE)
        setViewShowState(more, INVISIBLE)
        setViewShowState(screenShot, INVISIBLE)
        if (!mIfCurrentIsFullscreen) {
            setViewShowState(mBottomFullContainer, GONE)
            setViewShowState(more, GONE)
            setViewShowState(screenShot, GONE)
        } else {
            mBottomContainer.setPadding(50, 0, 50, 0)
        }
    }

    override fun changeUiToCompleteClear() {
        super.changeUiToCompleteClear()
        setViewShowState(mBottomFullContainer, INVISIBLE)
        setViewShowState(more, INVISIBLE)
        setViewShowState(screenShot, INVISIBLE)
        if (!mIfCurrentIsFullscreen) {
            setViewShowState(mBottomFullContainer, GONE)
            setViewShowState(more, GONE)
            setViewShowState(screenShot, GONE)
        } else {
            mBottomContainer.setPadding(50, 0, 50, 0)
        }
    }

    override fun dismissProgressDialog() {
        try {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss()
                mProgressDialog = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 处理锁屏屏幕触摸逻辑
     */
    override fun lockTouchLogic() {
        if (mLockCurScreen) {
            mLockScreen.setImageResource(R.drawable.ic_video_unlock)
            mLockCurScreen = false
        } else {
            mLockScreen.setImageResource(R.drawable.ic_video_lock)
            mLockCurScreen = true
            hideAllWidget()
        }
    }
}
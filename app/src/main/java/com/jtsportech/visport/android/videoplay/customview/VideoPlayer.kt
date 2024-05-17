package com.jtsportech.visport.android.videoplay.customview

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import android.util.AttributeSet
import android.view.View.OnClickListener
import android.widget.EditText
import android.widget.ImageView
import com.jtsportech.visport.android.R
import com.shuyu.gsyvideoplayer.utils.CommonUtil
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer


class VideoPlayer(context: Context?, attrs: AttributeSet?) :
    StandardGSYVideoPlayer(context, attrs) {
    private lateinit var mCoverImage: ImageView
    lateinit var compilation: ImageView
    private lateinit var fastForward: ImageView
    private lateinit var sequence: ImageView
    private lateinit var fastback: ImageView
    private lateinit var write: EditText
    lateinit var more: ImageView
    private lateinit var stop: ImageView
    private lateinit var mode: ImageView

    override fun init(context: Context?) {
        if (activityContext != null) this.mContext = activityContext else this.mContext = context
        initInflate(mContext)
        initView()
        initJudging()
        initEvent()
        mCoverImage = ImageView(context)
        mCoverImage.scaleType = ImageView.ScaleType.CENTER_CROP
        thumbImageView = mCoverImage
    }

    fun initEvent() {
        more.setOnClickListener {
        }
        stop.setOnClickListener {
            clickStartIcon()
        }
    }

    /* fun getCoverView(): ImageView {
         return mCoverImage
     }*/
    override fun startPrepare() {
        super.startPrepare()
        if (gsyVideoManager.listener() != null) {
            gsyVideoManager.listener().onCompletion()
        }
        gsyVideoManager.setListener(this)
        gsyVideoManager.playTag = mPlayTag
        gsyVideoManager.playPosition = mPlayPosition
        mAudioManager.requestAudioFocus(
            onAudioFocusChangeListener,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
        )
        mProgressBar
        mBackUpPlayingBufferState = -1
        gsyVideoManager.prepare(mUrl, mMapHeadData, mLooping, mSpeed, mCache, mCachePath, null)
        setStateAndUi(CURRENT_STATE_PREPAREING)
    }

    override fun getLayoutId(): Int {
        return R.layout.video_player
    }

    override fun updateStartImage() {
        if (mStartButton is ImageView) {
            val imageView = mStartButton as ImageView
            when (mCurrentState) {
                CURRENT_STATE_PLAYING -> {
                    //暂停
                    imageView.setImageResource(R.drawable.icon_video_pause)
                    stop.setImageResource(R.drawable.icon_video_pause)
                }

                CURRENT_STATE_ERROR -> {
                    //错误
                    imageView.setImageResource(R.drawable.icon_video_play)
                    stop.setImageResource(R.drawable.icon_video_play)
                }

                else -> {
                    //播放
                    imageView.setImageResource(R.drawable.icon_video_play)
                    stop.setImageResource(R.drawable.icon_video_play)
                }
            }
        }
    }

    fun initView() {
        mAudioManager =
            mContext.applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        mScreenWidth = mContext.resources.displayMetrics.widthPixels
        mScreenHeight = mContext.resources.displayMetrics.heightPixels
        mTextureViewContainer = findViewById(R.id.surface_container)
        mBottomProgressBar = findViewById(R.id.bottom_progressbar)
        mBottomContainer = findViewById(R.id.layout_bottom)
        mStartButton = findViewById<ImageView>(R.id.start)
        mCurrentTimeTextView = findViewById(R.id.current)
        mThumbImageViewLayout = findViewById(R.id.thumb)
        mTotalTimeTextView = findViewById(R.id.total)
        mTopContainer = findViewById(R.id.layout_top)
        compilation = findViewById(R.id.compilation)
        fastForward = findViewById(R.id.fastforward)
        mProgressBar = findViewById(R.id.progress)
        mTitleTextView = findViewById(R.id.title)
        sequence = findViewById(R.id.sequence)
        fastback = findViewById(R.id.fastback)
        mBackButton = findViewById(R.id.back)
        write = findViewById(R.id.write)
        stop = findViewById(R.id.stop)
        mode = findViewById(R.id.mode)
        more = findViewById(R.id.more)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initJudging() {
        if (isInEditMode) return
        if (mStartButton != null) mStartButton.setOnClickListener(this)
        if (mFullscreenButton != null) {
            mFullscreenButton.setOnClickListener(this)
            mFullscreenButton.setOnTouchListener(this)
        }
        if (mProgressBar != null) mProgressBar.setOnSeekBarChangeListener(this)
        if (mBottomContainer != null) mBottomContainer.setOnClickListener(this)
        if (mTextureViewContainer != null) {
            mTextureViewContainer.setOnClickListener(this)
            mTextureViewContainer.setOnTouchListener(this)
        }
        if (mProgressBar != null) mProgressBar.setOnTouchListener(this)
        if (mThumbImageViewLayout != null) {
            mThumbImageViewLayout.visibility = GONE
            mThumbImageViewLayout.setOnClickListener(this)
        }
        if (mThumbImageView != null && !mIfCurrentIsFullscreen && mThumbImageViewLayout != null) {
            mThumbImageViewLayout.removeAllViews()
            resolveThumbImage(mThumbImageView)
        }
        if (mBackButton != null) mBackButton.setOnClickListener(this)
        if (mLockScreen != null) {
            mLockScreen.visibility = GONE
            mLockScreen.setOnClickListener(OnClickListener { v ->
                if (mCurrentState == CURRENT_STATE_AUTO_COMPLETE ||
                    mCurrentState == CURRENT_STATE_ERROR
                ) return@OnClickListener
                lockTouchLogic()
                if (mLockClickListener != null) mLockClickListener.onClick(v, mLockCurScreen)
            })
        }
        if (activityContext != null) mSeekEndOffset = CommonUtil.dip2px(activityContext, 50f)
    }
}
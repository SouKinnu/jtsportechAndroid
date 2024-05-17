package com.jtsportech.visport.android.playerdetail.simpleplay

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import com.cloudhearing.android.lib_base.base.BaseBindingVmActivity
import com.cloudhearing.android.lib_common.utils.mmkv.PreferencesWrapper
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.databinding.ActivitySimplePlayBinding
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.VideoAllCallBack
import com.shuyu.gsyvideoplayer.utils.OrientationOption
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.zero_code.libEdImage.util.StatusBarUtil

class SimplePlayActivity : BaseBindingVmActivity<ActivitySimplePlayBinding, SimplePlayViewModel>(
    ActivitySimplePlayBinding::inflate
), VideoAllCallBack {
    private lateinit var videoSourceId: String
    private lateinit var videoSourceType: String
    private lateinit var videoTitle: String
    private var videoUrl: String = ""
    private lateinit var orientationUtils: OrientationUtils
    private var isPlay = false
    private var isPause = false

    companion object {
        fun jump(
            activity: FragmentActivity,
            videoSourceId: String,
            videoSourceType: String,
            videoTitle: String
        ) {
            activity.startActivity(Intent(activity, SimplePlayActivity::class.java).apply {
                putExtras(Bundle().apply {
                    putString("videoSourceId", videoSourceId)
                    putString("videoSourceType", videoSourceType)
                    putString("videoTitle", videoTitle)
                })
            })
        }
    }

    override fun initView() {
        StatusBarUtil.setColor(this, resources.getColor(R.color.black), false)
        PreferencesWrapper.get().setVideoPlayerIsLive(true)
        val bundle = intent.extras
        videoSourceId = bundle?.getString("videoSourceId") ?: ""
        videoSourceType = bundle?.getString("videoSourceType") ?: ""
        videoTitle = bundle?.getString("videoTitle") ?: ""

//        binding.videoPlayer.backButton.setVisibility(View.GONE)

        initVideoBuilderMode()

        //允许window 的内容可以上移到刘海屏状态栏
        if (window != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val lp = window.attributes
            lp.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.setAttributes(lp)
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        binding.root.fitsSystemWindows = true

        binding.videoPlayer.backButton.setOnClickListener {
            if (binding.videoPlayer.isIfCurrentIsFullscreen) {
                onBackPressed()
            } else {
                finish()
            }
        }
    }

    override fun initData() {
        viewModel.getVideoPlayUrl(videoSourceId, videoSourceType)
        viewModel.apply {
            leagueFavoritesListLiveDataVideoPlayUrl.observe(this@SimplePlayActivity) {
                videoUrl = it.data
                binding.videoPlayer.enlargeImageRes = R.drawable.ic_play_full_screen
                binding.videoPlayer.startButton
                binding.videoPlayer.setUp(videoUrl, false, videoTitle)
                binding.videoPlayer.startPlayLogic()
            }
        }
    }

    override fun initEvent() {
    }

    private fun getGSYVideoPlayer(): StandardGSYVideoPlayer {
        return binding.videoPlayer
    }

    private fun getGSYVideoOptionBuilder(): GSYVideoOptionBuilder {
        //内置封面可参考SampleCoverVideo
        val imageView = ImageView(this)
        //loadCover(imageView, url);
        return GSYVideoOptionBuilder()
            .setThumbImageView(imageView)
            .setUrl(videoUrl)
            .setCacheWithPlay(true)
            .setIsTouchWiget(true) //.setAutoFullWithSize(true)
            .setRotateViewAuto(false)
            .setLockLand(false)
            .setShowFullAnimation(false) //打开动画
            .setNeedLockFull(true)
            .setSeekRatio(1f)
    }

    private fun clickForFullScreen() {}


    /**
     * 是否启动旋转横屏，true表示启动
     */
    private fun getDetailOrientationRotateAuto(): Boolean {
        return true
    }

    /**
     * 选择builder模式
     */
    private fun initVideoBuilderMode() {
        initVideo()
        getGSYVideoOptionBuilder().setVideoAllCallBack(this)
            .build(getGSYVideoPlayer())
    }

    /**
     * 选择普通模式
     */
    private fun initVideo() {
        //外部辅助的旋转，帮助全屏
        orientationUtils = OrientationUtils(this, getGSYVideoPlayer(), getOrientationOption())
        //初始化不打开外部的旋转
        orientationUtils.isEnable = false
        if (getGSYVideoPlayer().fullscreenButton != null) {
            getGSYVideoPlayer().fullscreenButton.setOnClickListener {
                showFull()
                clickForFullScreen()
            }
        }
    }

    private fun showFull() {
        if (orientationUtils.isLand != 1) {
            //直接横屏
            // ------- ！！！如果不需要旋转屏幕，可以不调用！！！-------
            // 不需要屏幕旋转，还需要设置 setNeedOrientationUtils(false)
            orientationUtils.resolveByClick()
        }
        //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
        getGSYVideoPlayer().startWindowFullscreen(
            this@SimplePlayActivity,
            hideActionBarWhenFull(),
            hideStatusBarWhenFull()
        )
    }

    override fun onBackPressed() {
        // ------- ！！！如果不需要旋转屏幕，可以不调用！！！-------
        // 不需要屏幕旋转，还需要设置 setNeedOrientationUtils(false)
        if (orientationUtils != null) {
            orientationUtils.backToProtVideo()
        }
        if (GSYVideoManager.backFromWindowFull(this)) {
            return
        }
        super.onBackPressed()
    }


    override fun onPause() {
        super.onPause()
        getGSYVideoPlayer().currentPlayer.onVideoPause()
        if (orientationUtils != null) {
            orientationUtils.setIsPause(true)
        }
        isPause = true
    }

    override fun onResume() {
        super.onResume()
        getGSYVideoPlayer().currentPlayer.onVideoResume()
        if (orientationUtils != null) {
            orientationUtils.setIsPause(false)
        }
        isPause = false
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isPlay) {
            getGSYVideoPlayer().currentPlayer.release()
        }
        if (orientationUtils != null) orientationUtils.releaseListener()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        //如果旋转了就全屏
        //如果旋转了就全屏
        if (isPlay && !isPause) {
            getGSYVideoPlayer().onConfigurationChanged(
                this,
                newConfig,
                orientationUtils,
                hideActionBarWhenFull(),
                hideStatusBarWhenFull()
            )
        }
    }

    /**
     * 可配置旋转 OrientationUtils
     */
    private fun getOrientationOption(): OrientationOption? {
        return null
    }

    override fun onStartPrepared(p0: String?, vararg p1: Any?) {
    }

    override fun onPrepared(p0: String?, vararg p1: Any?) {
        if (orientationUtils == null) {
            throw NullPointerException("initVideo() or initVideoBuilderMode() first")
        }
        //开始播放了才能旋转和全屏
        //开始播放了才能旋转和全屏
        orientationUtils.isEnable = getDetailOrientationRotateAuto() && !isAutoFullWithSize()
        isPlay = true
    }

    override fun onClickStartIcon(p0: String?, vararg p1: Any?) {
    }

    override fun onClickStartError(p0: String?, vararg p1: Any?) {
    }

    override fun onClickStop(p0: String?, vararg p1: Any?) {
    }

    override fun onClickStopFullscreen(p0: String?, vararg p1: Any?) {
    }

    override fun onClickResume(p0: String?, vararg p1: Any?) {
    }

    override fun onClickResumeFullscreen(p0: String?, vararg p1: Any?) {
    }

    override fun onClickSeekbar(p0: String?, vararg p1: Any?) {
    }

    override fun onClickSeekbarFullscreen(p0: String?, vararg p1: Any?) {
    }

    override fun onAutoComplete(p0: String?, vararg p1: Any?) {
    }

    override fun onComplete(p0: String?, vararg p1: Any?) {
    }

    override fun onEnterFullscreen(p0: String?, vararg p1: Any?) {
    }

    override fun onQuitFullscreen(p0: String?, vararg p1: Any?) {
        // ------- ！！！如果不需要旋转屏幕，可以不调用！！！-------
        // 不需要屏幕旋转，还需要设置 setNeedOrientationUtils(false)


        // ------- ！！！如果不需要旋转屏幕，可以不调用！！！-------
        // 不需要屏幕旋转，还需要设置 setNeedOrientationUtils(false)
        if (orientationUtils != null) {
            orientationUtils.backToProtVideo()
        }
    }

    override fun onQuitSmallWidget(p0: String?, vararg p1: Any?) {
    }

    override fun onEnterSmallWidget(p0: String?, vararg p1: Any?) {
    }

    override fun onTouchScreenSeekVolume(p0: String?, vararg p1: Any?) {
    }

    override fun onTouchScreenSeekPosition(p0: String?, vararg p1: Any?) {
    }

    override fun onTouchScreenSeekLight(p0: String?, vararg p1: Any?) {
    }

    override fun onPlayError(p0: String?, vararg p1: Any?) {
    }

    override fun onClickStartThumb(p0: String?, vararg p1: Any?) {
    }

    override fun onClickBlank(p0: String?, vararg p1: Any?) {
    }

    override fun onClickBlankFullscreen(p0: String?, vararg p1: Any?) {
    }

    private fun hideActionBarWhenFull(): Boolean {
        return true
    }

    private fun hideStatusBarWhenFull(): Boolean {
        return true
    }

    /**
     * 是否根据视频尺寸，自动选择竖屏全屏或者横屏全屏，注意，这时候默认旋转无效
     */
    private fun isAutoFullWithSize(): Boolean {
        return false
    }
}
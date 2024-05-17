package com.jtsportech.visport.android.racedetail

import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.Keep
import androidx.viewbinding.ViewBinding
import com.cloudhearing.android.lib_base.base.BaseBindingVmActivity
import com.cloudhearing.android.lib_base.base.BaseViewModel
import com.cloudhearing.android.lib_base.base.initListener
import com.cloudhearing.android.lib_common.utils.mmkv.PreferencesWrapper
import com.jtsportech.visport.android.racedetail.customview.MyGSYVideoPlayer
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.VideoAllCallBack
import com.shuyu.gsyvideoplayer.utils.OrientationOption
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import timber.log.Timber
import java.lang.reflect.ParameterizedType


abstract class BaseVideoActivity<VB : ViewBinding, VM : BaseViewModel>(
    private val inflate: (LayoutInflater) -> VB
) : BaseBindingVmActivity<VB, VM>(inflate), initListener, VideoAllCallBack {
    protected var isPlay: Boolean = false
    protected var isPause: Boolean = false
    protected lateinit var orientationUtils: OrientationUtils
    protected var isNeedShow = true
    protected var isRecord = false
    protected var visualAngle = 0
    private var pauseTime: Long = 0

    @Keep
    override fun getViewModelClass(): Class<VM> {
        Timber.d("getViewModelClass ${(javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[1]}")
        return ((javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[1]) as Class<VM>
    }

    /**
     * 选择builder模式
     */
    open fun initVideoBuilderMode() {
        initVideo()
        getGSYVideoOptionBuilder().setVideoAllCallBack(this)
            .build(getGSYVideoPlayer())
    }

    /**
     * 选择普通模式
     */
    open fun initVideo() {
        //外部辅助的旋转，帮助全屏
        orientationUtils = OrientationUtils(this, getGSYVideoPlayer(), getOrientationOption())
        //初始化不打开外部的旋转
        orientationUtils.isEnable = false
        if (getGSYVideoPlayer().fullscreenButton != null) {
            getGSYVideoPlayer().fullscreenButton.setOnClickListener(View.OnClickListener {
                showFull()
                clickForFullScreen()
            })
        }
        getGSYVideoPlayer().setLockClickListener { p0, p1 ->
            orientationUtils.isEnable = !p1
        }
        getGSYVideoPlayer().onFuncClickListener = object : MyGSYVideoPlayer.OnFuncClickListener {
            override fun onEventClick() {
                eventClick()
            }

            override fun onVisualAngleCutClick() {
                showVisualAngleCut()
            }

            override fun onDownloadVideo() {
                downloadVideo(false)
            }

            override fun onDownloadEvents() {
                downloadVideo(true)
            }

            override fun onAddCollection(matchType: String, favoriteType: String) {
                addCollection(matchType, favoriteType)
            }

            override fun onDeleteCollection(matchType: String) {
                deleteCollection(matchType)
            }

            override fun onCommentVideo(msg: String) {
                commentVideo(msg)
            }

            override fun onScreenShot() {
                screenshot()
            }

            override fun onSelectSpeed() {
                selectSpeed()
            }
        }
        getGSYVideoPlayer().setGSYVideoProgressListener { p0, p1, p2, p3 ->
            if (p2 + 10 * 1000 >= p3 && getGSYVideoPlayer().getMIfCurrentIsFullscreen() && isNeedShow) {
                isNeedShow = false
                showBallPerformanceDialog()
            }
            if (p2 > 5 * 1000 && !isRecord) {
                isRecord = true
                recordPlay()
            }
        }
    }

    abstract fun selectSpeed()

    abstract fun showVisualAngleCut()

    abstract fun recordPlay()

    abstract fun showBallPerformanceDialog()

    protected abstract fun eventClick()
    protected abstract fun downloadVideo(isEvents: Boolean)
    protected abstract fun addCollection(matchType: String, favoriteType: String)
    protected abstract fun deleteCollection(matchType: String)
    protected abstract fun commentVideo(msg: String)
    protected abstract fun screenshot()
    open fun showFull() {
        if (orientationUtils.isLand != 1) {
            //直接横屏
            // ------- ！！！如果不需要旋转屏幕，可以不调用！！！-------
            // 不需要屏幕旋转，还需要设置 setNeedOrientationUtils(false)
            orientationUtils.resolveByClick()
        }
        //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
        getGSYVideoPlayer().startWindowFullscreen(
            this,
            hideActionBarWhenFull(),
            hideStatusBarWhenFull()
        )
    }


    override fun onBackPressed() {
        if (!orientationUtils.isEnable) {
            return
        }
        backToNormal()
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
//        getGSYVideoPlayer().onVideoPause()
        if (getGSYVideoPlayer().gsyVideoManager.listener() != null) {
            pauseTime = getGSYVideoPlayer().gsyVideoManager.currentPosition
            Timber.d("pauseTime : $pauseTime")
//            getGSYVideoPlayer().gsyVideoManager.listener().onVideoPause()
            getGSYVideoPlayer().onVideoPause()
        }
        if (orientationUtils != null) {
            orientationUtils.setIsPause(true)
        }
        isPause = true
    }

    override fun onResume() {
        super.onResume()
//        getGSYVideoPlayer().onVideoResume()
        if (PreferencesWrapper.get().getVideoPlayerIsLive()) {
            PreferencesWrapper.get().setVideoPlayerIsLive(false)
            Timber.d("pauseTime : $pauseTime")
            videoResume(pauseTime)
        } else if (getGSYVideoPlayer().gsyVideoManager.listener() != null) {
//            getGSYVideoPlayer().gsyVideoManager.listener().onVideoResume()
            getGSYVideoPlayer().onVideoResume(false)
        }
        if (orientationUtils != null) {
            orientationUtils.setIsPause(false)
        }
        isPause = false
    }

    abstract fun videoResume(pauseTime: Long)

    override fun onDestroy() {
        super.onDestroy()
        if (isPlay) {
            getGSYVideoPlayer().release()
        }
        if (orientationUtils != null) orientationUtils.releaseListener()
    }

    /**
     * orientationUtils 和  detailPlayer.onConfigurationChanged 方法是用于触发屏幕旋转的
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
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
        enterFull()
        backToNormal()
    }

    override fun onStartPrepared(url: String?, vararg objects: Any?) {}

    override fun onPrepared(url: String?, vararg objects: Any?) {
        if (orientationUtils == null) {
            throw NullPointerException("initVideo() or initVideoBuilderMode() first")
        }
        //开始播放了才能旋转和全屏
        orientationUtils.isEnable = getDetailOrientationRotateAuto() && !isAutoFullWithSize()
        isPlay = true
    }

    override fun onClickStartIcon(url: String?, vararg objects: Any?) {}

    override fun onClickStartError(url: String?, vararg objects: Any?) {}

    override fun onClickStop(url: String?, vararg objects: Any?) {}

    override fun onClickStopFullscreen(url: String?, vararg objects: Any?) {
    }

    override fun onClickResume(url: String?, vararg objects: Any?) {}

    override fun onClickResumeFullscreen(url: String?, vararg objects: Any?) {}

    override fun onClickSeekbar(url: String?, vararg objects: Any?) {}

    override fun onClickSeekbarFullscreen(url: String?, vararg objects: Any?) {}

    override fun onAutoComplete(url: String?, vararg objects: Any?) {
        hideBallPop()
        if ((objects[1] as MyGSYVideoPlayer).playAuto) {
            (objects[1] as MyGSYVideoPlayer).gsyVideoManager.seekTo(0)
            (objects[1] as MyGSYVideoPlayer).startPlayLogic()
        } else {
            sequentialPlayMode()
        }
    }

    abstract fun hideBallPop()

    abstract fun sequentialPlayMode()

    override fun onEnterFullscreen(url: String?, vararg objects: Any?) {
//        enterFull()
    }

    abstract fun enterFull()

    override fun onQuitFullscreen(url: String?, vararg objects: Any?) {

        // ------- ！！！如果不需要旋转屏幕，可以不调用！！！-------
        // 不需要屏幕旋转，还需要设置 setNeedOrientationUtils(false)
        if (orientationUtils != null) {
            orientationUtils.backToProtVideo()
//            backToNormal()
        }
    }

    abstract fun backToNormal()


    override fun onQuitSmallWidget(url: String?, vararg objects: Any?) {}

    override fun onEnterSmallWidget(url: String?, vararg objects: Any?) {}

    override fun onTouchScreenSeekVolume(url: String?, vararg objects: Any?) {}

    override fun onTouchScreenSeekPosition(url: String?, vararg objects: Any?) {}

    override fun onTouchScreenSeekLight(url: String?, vararg objects: Any?) {}

    override fun onPlayError(url: String?, vararg objects: Any?) {}

    override fun onClickStartThumb(url: String?, vararg objects: Any?) {}

    override fun onClickBlank(url: String?, vararg objects: Any?) {}

    override fun onClickBlankFullscreen(url: String?, vararg objects: Any?) {}

    override fun onComplete(url: String?, vararg objects: Any?) {}

    fun hideActionBarWhenFull(): Boolean {
        return false
    }

    fun hideStatusBarWhenFull(): Boolean {
        return true
    }

    /**
     * 可配置旋转 OrientationUtils
     */
    open fun getOrientationOption(): OrientationOption? {
        return null
    }

    /**
     * 播放控件
     */
    abstract fun getGSYVideoPlayer(): MyGSYVideoPlayer

    /**
     * 配置播放器
     */
    abstract fun getGSYVideoOptionBuilder(): GSYVideoOptionBuilder

    /**
     * 点击了全屏
     */
    abstract fun clickForFullScreen()

    /**
     * 是否启动旋转横屏，true表示启动
     */
    abstract fun getDetailOrientationRotateAuto(): Boolean

    /**
     * 是否根据视频尺寸，自动选择竖屏全屏或者横屏全屏，注意，这时候默认旋转无效
     */
    fun isAutoFullWithSize(): Boolean {
        return false
    }

}
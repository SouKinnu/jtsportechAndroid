package com.jtsportech.visport.android.videoplay.utils

import com.jtsportech.visport.android.videoplay.customview.VideoPlayer
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack

class VideoPlayingManger(private val gsyVideoOptionBuilder: GSYVideoOptionBuilder) {

    fun playVideo(
        videoView: VideoPlayer,
        videoUrl: String,
        imageUrl: String,
        title: String,
        adapterPosition: Int,
        mPlayTag: String,
        onVideoPlayingMangerlistener: VideoPlayingMangerlistener
    ) {
        gsyVideoOptionBuilder
            .setIsTouchWiget(true) //是否可以滑动界面改变进度，声音等 默认true
            .setUrl(videoUrl) //播放url
            .setCacheWithPlay(true) //是否边缓存，m3u8等无效
            .setRotateViewAuto(true) //是否开启自动旋转
            .setLockLand(true) //一全屏就锁屏横屏，默认false竖屏，可配合setRotateViewAuto使用
            .setPlayTag(mPlayTag) //播放tag防止错误，因为普通的url也可能重复 playTag - 保证不重复就好
            .setShowFullAnimation(true) //是否使用全屏动画效果
            .setNeedLockFull(false) //是否需要全屏锁定屏幕功能 如果单独使用请设置setIfCurrentIsFullscreen为true
            .setNeedShowWifiTip(true) //是否需要显示流量提示,默认true
            .setPlayPosition(adapterPosition) //设置播放位置防止错位 （获取绝对适配器位置）
            .setVideoAllCallBack(object : GSYSampleCallBack() {
                /**
                 * 在进入全屏模式
                 * @param url
                 * @param objects
                 */
                override fun onEnterFullscreen(url: String, vararg objects: Any) {
                    super.onEnterFullscreen(url, *objects)
                    //获取当前正在播放的播放控件，将title隐藏

                }

                override fun onClickStartIcon(url: String?, vararg objects: Any?) {
                    super.onClickStartIcon(url, *objects)
                    onVideoPlayingMangerlistener.onClickStartIcon()
                }

                override fun onAutoComplete(url: String?, vararg objects: Any?) {
                    super.onAutoComplete(url, *objects)
                    onVideoPlayingMangerlistener.onAutoComplete()
                    //播放完之后释放资源
                    GSYVideoManager.releaseAllVideos()
                }
            }).build(videoView)

        /*//设置返回键
        videoView.backButton.visibility = View.GONE*/
        /*videoView.getCoverView().loadRoundCornerImage(
            url = imageUrl.img()
        )*/
    }

    interface VideoPlayingMangerlistener {
        /**
         * 点击了播放按钮
         */
        fun onClickStartIcon()

        /**
         * 播放完了
         */
        fun onAutoComplete()
    }
}
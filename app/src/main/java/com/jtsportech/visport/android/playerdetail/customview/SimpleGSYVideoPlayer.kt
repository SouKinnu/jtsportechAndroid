package com.jtsportech.visport.android.playerdetail.customview

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import com.jtsportech.visport.android.R
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer

class SimpleGSYVideoPlayer : StandardGSYVideoPlayer {
    constructor(context: Context?, fullFlag: Boolean?) : super(context, fullFlag)
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    public lateinit var play: ImageView

    override fun getLayoutId(): Int {
        return R.layout.video_layout_simple
    }

    override fun init(context: Context?) {
        super.init(context)
        play = findViewById(R.id.play)
        play.setOnClickListener {
            mStartButton.performClick()
        }
    }

    override fun updateStartImage() {
        super.updateStartImage()
        if (mCurrentState == 2) {
            play.setImageResource(R.drawable.icon_video_pause)
            if (mStartButton is ImageView) {
                val imageView = mStartButton as ImageView
                imageView.setImageResource(R.drawable.ic_video_pause)
            }
        } else {
            play.setImageResource(R.drawable.icon_video_play)
            if (mStartButton is ImageView) {
                val imageView = mStartButton as ImageView
                imageView.setImageResource(R.drawable.ic_video_play)
            }
        }
    }
}
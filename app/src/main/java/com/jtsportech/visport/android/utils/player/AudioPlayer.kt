package com.jtsportech.visport.android.utils.player

import android.content.Context
import android.media.MediaPlayer
import timber.log.Timber
import java.io.IOException

/**
 * Author: BenChen
 * Date: 2024/03/29 19:42
 * Email:chenxiaobin@cloudhearing.cn
 */
class AudioPlayer private constructor(context: Context) : MediaPlayer.OnPreparedListener,
    MediaPlayer.OnCompletionListener {

    private val listeners = mutableListOf<AudioPlayerListener>()
    private val mediaPlayer: MediaPlayer = MediaPlayer()
    private var mUrl: String = ""


    init {
        mediaPlayer.setOnPreparedListener(this)
        mediaPlayer.setOnCompletionListener(this)
    }

    companion object {
        private var instance: AudioPlayer? = null

        @Synchronized
        fun getInstance(context: Context): AudioPlayer {
            if (instance == null) {
                instance = AudioPlayer(context)
            }
            return instance as AudioPlayer
        }
    }

    fun addListener(listener: AudioPlayerListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: AudioPlayerListener) {
        listeners.remove(listener)
    }

    fun play(url: String) {
        try {
            mUrl = url
            mediaPlayer.reset()
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun stop() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }

        mUrl = ""
    }

    fun release() {
        mediaPlayer.release()
    }

    override fun onPrepared(mp: MediaPlayer) {
        Timber.d("onPrepared")
        mp.start()
        for (listener in listeners) {
            listener.onAudioPlayerStart()
        }
    }

    override fun onCompletion(mp: MediaPlayer) {
        Timber.d("onCompletion")
        for (listener in listeners) {
            listener.onAudioPlayerStop(mUrl)
        }

        mUrl = ""
    }

    interface AudioPlayerListener {
        fun onAudioPlayerStart()
        fun onAudioPlayerStop(url:String)
    }
}
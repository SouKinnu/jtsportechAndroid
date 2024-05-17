package com.jtsportech.visport.android.racedetail.customview

import android.Manifest
import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Message
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.PermissionUtils
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.racedetail.RaceDetailViewModel
import com.jtsportech.visport.android.utils.record.AudioRecorder
import com.jtsportech.visport.android.utils.record.RecordStreamListener
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.abs
import kotlin.math.log10

class PopupVoice(
    private var context: Context?,
    private var window: Window,
    private var callback: Callback,
) :
    PopupWindow(context), RecordStreamListener,
    View.OnClickListener {
    private lateinit var mAudioRecorder: AudioRecorder
    private var viewVoice: View
    private lateinit var seconds: TextView
    private lateinit var delete: ImageView
    private lateinit var vavPath: String
    private lateinit var isPlay: ImageView
    private lateinit var second: TextView
    private lateinit var send: ImageView
    private lateinit var remark: CheckBox
    private lateinit var status: TextView
    private lateinit var playing: RelativeLayout
    private lateinit var ds: RelativeLayout
    private lateinit var mediaPlayer: MediaPlayer
    private var secondsNumber: Int = 0
    private var voiceDuration: Int = 0
    private var vm: RaceDetailViewModel
    private var handler: Handler

    interface Callback {
        fun getCriticizeVoice(duration: Int, file: File)
    }

    init {
        handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                val obj = msg.obj
                seconds.text = "${obj}\""
                if (secondsNumber == 30) handleStopButtonLogic()
            }
        }
        val thread = Thread {
            run {
                while (secondsNumber < 30 && remark.isChecked) {
                    secondsNumber++
                    val message: Message = handler.obtainMessage()
                    message.obj = secondsNumber
                    handler.sendMessage(message)
                    try {
                        Thread.sleep(1000)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        vm = RaceDetailViewModel()
        val mInflater =
            context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        viewVoice = mInflater.inflate(R.layout.pop_voice, null)
        initView()
        contentView = viewVoice
        verifyPermissions()
        val displayMetrics = context!!.resources.displayMetrics
        width = displayMetrics.widthPixels
        height = displayMetrics.heightPixels * 1 / 2
        setBackgroundDrawable(ContextCompat.getDrawable(context!!, R.drawable.bg_white))
        animationStyle = R.style.popwindow_anim
        remark.isChecked = false
        isPlay.isSelected = false
        remark.setOnCheckedChangeListener { _, isChecked ->
            if (!PermissionUtils.isGranted(Manifest.permission.RECORD_AUDIO)) return@setOnCheckedChangeListener
            if (isChecked) {
                handleStartButtonLogic()
                thread.start()
                val play = ContextCompat.getString(context!!, R.string.voice_status_play)
                status.text = play
            } else handleStopButtonLogic()
        }
        isPlay.setOnClickListener {
            isPlay.isSelected = !isPlay.isSelected
            if (isPlay.isSelected) {
                mediaPlayer.start()
            } else {
                mediaPlayer.pause()
            }
        }
        setTouchInterceptor(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event != null) return event.action == MotionEvent.ACTION_OUTSIDE
                return false
            }
        })
        viewVoice.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dismiss()
                    return true
                }
                return false
            }
        })
        delete.setOnClickListener(this@PopupVoice)
        send.setOnClickListener(this@PopupVoice)
        playing.setOnClickListener(this@PopupVoice)
    }

    private fun initView() {
        isFocusable = true
        isTouchable = true
        seconds = viewVoice.findViewById(R.id.seconds)
        delete = viewVoice.findViewById(R.id.delete)
        isPlay = viewVoice.findViewById(R.id.is_play)
        second = viewVoice.findViewById(R.id.second)
        send = viewVoice.findViewById(R.id.send)
        remark = viewVoice.findViewById(R.id.remark)
        status = viewVoice.findViewById(R.id.status)
        ds = viewVoice.findViewById(R.id.ds)
        playing = viewVoice.findViewById(R.id.playing)
    }

    private fun verifyPermissions() {
        PermissionUtils.permission(Manifest.permission.RECORD_AUDIO)
            .callback { isAllGranted, _, _, _ ->
                if (isAllGranted) {
                    mAudioRecorder = AudioRecorder.getInstance()
                }
            }.request()
    }

    private fun handleStartButtonLogic() {
        if (mAudioRecorder.status == AudioRecorder.Status.STATUS_NO_READY) {
            // 初始化录音
            mAudioRecorder.createDefaultAudio(SimpleDateFormat("yyyyMMddhhmmss").format(Date()))
            mAudioRecorder.startRecord(this)
        }
    }

    override fun recordOfByte(data: ByteArray, begin: Int, end: Int) {
        Timber.tag("fenbei").d("当前分贝 %s", calculateVolume(data))
    }


    private fun calculateVolume(buffer: ByteArray): Double {
        var sumVolume = 0.0
        var avgVolume = 0.0
        var volume = 0.0
        for (i in buffer.indices step 2) {
            val v1 = buffer[i].toInt() and 0xFF
            val v2 = buffer[i + 1].toInt() and 0xFF
            var temp = v1 + (v2 shl 8) // 小端
            if (temp >= 0x8000) {
                temp = 0xFFFF - temp
            }
            sumVolume += abs(temp)
        }
        avgVolume = sumVolume / buffer.size / 2
        volume = log10(1 + avgVolume) * 10
        return volume
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            send.id -> {
                callback.getCriticizeVoice(voiceDuration, File(vavPath))
                dismiss()
            }

            delete.id -> dismiss()

        }
    }

    fun handleStopButtonLogic() {
        val finish = ContextCompat.getString(context!!, R.string.voice_status_finish)
        status.text = finish
        mAudioRecorder.stopRecord()
        handler.removeCallbacksAndMessages(null)
        second.text = "${secondsNumber}\""
        voiceDuration = secondsNumber
        ds.visibility = View.VISIBLE
        remark.visibility = View.INVISIBLE
        vavPath =
            "data/data/com.jtsportech.visport.android/files/pauseRecordDemo/wav/" + mAudioRecorder.fileName + ".wav"
        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(vavPath)
                prepare()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
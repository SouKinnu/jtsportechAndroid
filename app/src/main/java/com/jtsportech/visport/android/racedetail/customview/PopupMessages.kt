package com.jtsportech.visport.android.racedetail.customview

import android.Manifest
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.PermissionUtils
import com.cloudhearing.android.lib_base.utils.observeEvent
import com.cloudhearing.android.lib_base.utils.observeState
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.racedetail.RaceDetailViewModel
import com.jtsportech.visport.android.racedetail.messages.MessagesAdapter
import com.jtsportech.visport.android.utils.record.AudioRecorder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class PopupMessages(
    private var context: Context?,
    private var lifecycleOwner: LifecycleOwner,
    private var matchInfoId: String,
    private var window: Window,
    private var viewModel: RaceDetailViewModel
) : PopupWindow(context), View.OnClickListener {
    private var viewMessages: View
    private lateinit var sortType: RadioGroup
    private lateinit var all: RadioButton
    private lateinit var latest: RadioButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var comment: TextView
    private lateinit var bottom: View
    private lateinit var close: ImageView
    private var comments: String
    private lateinit var say: EditText
    private lateinit var voice: ImageView

    //    lateinit var viewModel: RaceDetailViewModel
    private lateinit var popupWindowVoice: PopupWindow
    private var orange: Int
    private var gray: Int
    private var bgWhite: Drawable
    private val mMessagesAdapter: MessagesAdapter by lazy {
        MessagesAdapter().apply {
            setOnMoreClickListener { i, replies ->
                viewModel.interactionMessageListFlow.value.addAll(i + 1, replies)
                notifyDataSetChanged()
            }
            setOnReplyClickListener { reply, i ->
                reply.id?.let { inputDialog("REPLY", it, i) }
            }
            setOnPlayClickListener {
                viewModel.togglePlayingStatus(it)
            }
        }
    }

    init {
        darkenBackground(0.2F, window)
        val mInflater =
            context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        viewMessages = mInflater.inflate(R.layout.activity_messages, null)
        initView()
        orange = ContextCompat.getColor(context!!, R.color.international_orange)
        gray = ContextCompat.getColor(context!!, R.color.dove_gray)
        comments = ContextCompat.getString(context!!, R.string.comments)
        bgWhite = ContextCompat.getDrawable(context!!, R.drawable.bg_white)!!
        val displayMetrics = context!!.resources.displayMetrics
        contentView = viewMessages
        width = displayMetrics.widthPixels
        height = displayMetrics.heightPixels * 3 / 4
        setBackgroundDrawable(bgWhite)
        animationStyle = R.style.popwindow_anim
        viewModel.getMessagesNum(matchInfoId, 0)
        viewModel.leagueFavoritesListStateFlowMessages.observeEvent(lifecycleOwner) {
            comment.text = it.toString() + comments
        }
        viewModel.interactionMessageListFlow.observeState(lifecycleOwner) {
            if (it.isNotEmpty()) {
                mMessagesAdapter.submitList(it)
                mMessagesAdapter.notifyDataSetChanged()
            }
        }
        setTouchInterceptor(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event != null) {
                    return event.action == MotionEvent.ACTION_OUTSIDE
                }
                return false
            }
        })
        setOnDismissListener {
            darkenBackground(1F, window)
        }
        viewMessages.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dismiss()
                    return true
                }
                return false
            }
        })
        say.isFocusable = false
        say.setOnClickListener(this@PopupMessages)
        close.setOnClickListener(this@PopupMessages)
        voice.setOnClickListener(this@PopupMessages)
        all.setTextColor(orange)
        getMessages(0, matchInfoId)
        sortType.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                all.id -> {
                    all.setTextColor(orange)
                    latest.setTextColor(gray)
                    getMessages(0, matchInfoId)
                }

                latest.id -> {
                    latest.setTextColor(orange)
                    all.setTextColor(gray)
                    getMessages(1, matchInfoId)
                }
            }
        }
        recyclerView.adapter = mMessagesAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun darkenBackground(bgColor: Float, window: Window) {
        val lp = window.attributes
        lp.alpha = bgColor
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window.attributes = lp
    }

    private fun initView() {
        isFocusable = true
        isTouchable = true
//        viewModel = RaceDetailViewModel()
        sortType = viewMessages.findViewById(R.id.sortType)
        all = viewMessages.findViewById(R.id.all)
        all.isChecked = true
        latest = viewMessages.findViewById(R.id.latest)
        recyclerView = viewMessages.findViewById(R.id.RecyclerView)
        comment = viewMessages.findViewById(R.id.comments)
        close = viewMessages.findViewById(R.id.close)
        voice = viewMessages.findViewById(R.id.voice)
        bottom = viewMessages.findViewById(R.id.bottom)
        say = viewMessages.findViewById(R.id.say)
    }

    private fun getMessages(
        sortType: Int, matchInfoId: String
    ) {
        viewModel.getMessages(matchInfoId, sortType)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                close.id -> dismiss()
                say.id -> inputDialog("DIRECT", "", 0)
                voice.id -> showPopupWindowVoice(false, "")
            }
        }
    }

    private fun inputDialog(criticizeType: String, replyCriticizeId: String, i: Int) {
        InputDialogForFeedBack(
            context!!,
            R.style.Dialog_NoTitle,
            object : InputDialogForFeedBack.InputDialogListener {
                override fun getInputTxt(editText: EditText) {
                    dismiss()
                }

                override fun getCriticizeText(contentText: String) {
                    mMessagesAdapter.currentList[i].isReply = false
                    getCriticized(
                        0,
                        "",
                        contentText,
                        "TEXT",
                        criticizeType,
                        matchInfoId,
                        replyCriticizeId
                    )

                }

                override fun getCriticizeVideo() {
                    showPopupWindowVoice(true, replyCriticizeId)
                }
            }).show()

    }

    fun showPopupWindowVoice(isReply: Boolean, replyCriticizeId: String) {
        if (!PermissionUtils.isGranted(Manifest.permission.RECORD_AUDIO)) {
            PermissionUtils.permission(Manifest.permission.RECORD_AUDIO)
                .callback { isAllGranted, _, _, _ ->
                    showPopupWindowVoice(isReply, replyCriticizeId)
                    CoroutineScope(Dispatchers.Default).launch {
                        delay(500)
                        viewModel.leagueFavoritesLiveDataAudioIsPlay.postValue(1)
                    }
                }.request()
            return
        }
        popupWindowVoice =
            PopupVoice(context, window,
                object : PopupVoice.Callback {
                    override fun getCriticizeVoice(duration: Int, file: File) {
                        viewModel.apply {
                            getAudio(duration, file)
                            viewModel.leagueFavoritesListStateFlowAudio.observeEvent(lifecycleOwner) {
                                if (it.isNotEmpty())
                                    if (isReply)
                                        getCriticized(
                                            duration, it, "", "AUDIO",
                                            "REPLY", matchInfoId, replyCriticizeId
                                        )
                                    else
                                        getCriticized(
                                            duration, it, "", "AUDIO",
                                            "DIRECT", matchInfoId, ""
                                        )
                            }
                        }
                    }
                }
            ).apply {
                setOnDismissListener {
                    viewModel.leagueFavoritesLiveDataAudioIsPlay.postValue(0)
                }
                showAsDropDown(bottom, 0, 0, Gravity.TOP)
                viewModel.leagueFavoritesLiveDataAudioIsPlay.postValue(1)
            }
    }

    fun getCriticized(
        audioDuration: Int, audioFileId: String,
        contentText: String, contentType: String,
        criticizeType: String, matchInfoId: String,
        replyCriticizeId: String
    ) {
        viewModel.getCriticize(
            audioDuration, audioFileId, contentText, contentType,
            criticizeType, matchInfoId, replyCriticizeId
        )
        viewModel.leagueFavoritesListLiveDataCriticizeVoice.observe(lifecycleOwner) {
            if (it.isNotEmpty()) {
                viewModel.getMessagesNum(matchInfoId, 0)
                if (latest.isChecked) {
                    getMessages(1, matchInfoId)
                } else if (all.isChecked) {
                    getMessages(0, matchInfoId)
                }
            }
        }

    }
}
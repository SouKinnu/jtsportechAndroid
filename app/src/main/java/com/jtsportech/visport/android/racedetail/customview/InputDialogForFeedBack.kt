package com.jtsportech.visport.android.racedetail.customview

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.ScreenUtils
import com.jtsportech.visport.android.R
import java.util.Timer
import java.util.TimerTask


class InputDialogForFeedBack(
    context: Context,
    themeResId: Int,
    var listener: InputDialogListener
) :
    Dialog(context, themeResId) {
    private lateinit var editText: EditText
    private lateinit var contentView: View
    private lateinit var remarkVoice: ImageView


    interface InputDialogListener {
        fun getInputTxt(editText: EditText)
        fun getCriticizeText(contentText: String)
        fun getCriticizeVideo()
    }

    init {
        initView()
    }

    private fun initView() {
        contentView = LayoutInflater.from(context).inflate(R.layout.dialog_input_for, null)
        editText = contentView.findViewById(R.id.dialog_input_et)
        remarkVoice = contentView.findViewById(R.id.remark_voice)
        setContentView(contentView)
        window?.setGravity(Gravity.BOTTOM)
        window?.setWindowAnimations(R.style.popwindow_anim)
        editText.setOnClickListener {
            listener.getInputTxt(editText)
            editText.setText("")
        }
        remarkVoice.setOnClickListener {
            listener.getCriticizeVideo()
            dismiss()
        }
        setCancelable(true)
        show()
        val lp: WindowManager.LayoutParams? = window?.attributes
        if (lp != null) {
            lp.width = ScreenUtils.getScreenWidth()
        }
        window?.attributes = lp
    }

    override fun dismiss() {
        KeyboardUtils.hideSoftInput(editText)
        editText.isFocusable = false
        editText.showSoftInputOnFocus = false
        editText.isFocusableInTouchMode = false
        editText.requestFocus()
        super.dismiss()
    }

    override fun show() {
        super.show()
        Timer().schedule(object : TimerTask() {
            override fun run() {
                try {
                    val inputManager =
                        editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputManager.showSoftInput(editText, 0)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }, 300)
        //与键盘绑定
        editText.isFocusable = true
        editText.showSoftInputOnFocus = true
        editText.isFocusableInTouchMode = true
        editText.requestFocus()
        editText.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_SEND -> {
                    listener.getCriticizeText(editText.text.toString())
                    editText.setText("")//发送后输入框清空
                    dismiss()
                    true
                }

                else -> false
            }
        }

    }
}
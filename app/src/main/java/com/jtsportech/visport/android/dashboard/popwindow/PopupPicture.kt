package com.jtsportech.visport.android.dashboard.popwindow

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.jtsportech.visport.android.R

class PopupPicture(
    private var context: Context?,
    private var window: Window
) : PopupWindow(context) {
    private var view: View
    private var tvAlbum: TextView
    private var tvCamera: TextView
    private var tvCancel: TextView

    init {
        darkenBackground(0.4F, window)
        val mInflater =
            context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = mInflater.inflate(R.layout.pop_picture, null)
        tvAlbum = view.findViewById(R.id.tv_album)
        tvCamera = view.findViewById(R.id.tv_camera)
        tvCancel = view.findViewById(R.id.tv_cancel)
        tvCancel.setOnClickListener {
            dismiss()
        }
        initView()
        contentView = view
        width = WindowManager.LayoutParams.MATCH_PARENT
        height = WindowManager.LayoutParams.WRAP_CONTENT
        setBackgroundDrawable(context?.resources?.getDrawable(R.color.transparent))
        animationStyle = R.style.popwindow_anim
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
        view.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dismiss()
                    return true
                }
                return false
            }
        })
    }

    fun setOnAlbumListener(l: View.OnClickListener) {
        tvAlbum.setOnClickListener(l)
    }

    fun setOnCameraListener(l: View.OnClickListener) {
        tvCamera.setOnClickListener(l)
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
    }
}
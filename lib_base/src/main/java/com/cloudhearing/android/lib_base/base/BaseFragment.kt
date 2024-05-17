package com.cloudhearing.android.lib_base.base

import android.app.ProgressDialog
import android.graphics.Rect
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.cloudhearing.android.lib_base.base.delegate.impl.CoroutineDelegateImpl
import com.cloudhearing.android.lib_base.base.delegate.listener.CoroutineDelegate
import com.cloudhearing.android.lib_base.component.dialog.LoadingOverlayDialog
import com.cloudhearing.android.lib_base.utils.HasLoadingOverlay
import com.cloudhearing.android.lib_base.utils.hideKeyboard

/**
 * Author: BenChen
 * Date: 2023/12/20 11:00
 * Email:chenxiaobin@cloudhearing.cn
 */
open class BaseFragment : Fragment(), CoroutineDelegate by CoroutineDelegateImpl(),
    HasLoadingOverlay {

    private val mLoadingProgressDialog by lazy {
//        ProgressDialog(requireContext())
        LoadingOverlayDialog(requireContext())
    }

    /**
     * Increase Lifecycle monitoring
     *
     */
    protected fun IBaseViewModel.addObserver() {
        lifecycle.addObserver(this)
    }


    /**
     * Adds tap to dismiss behaviour to [ViewGroup]
     * @receiver ViewGroup
     */
    protected fun ViewGroup.addTapToDismissBehaviour() {
        isFocusable = true
        isFocusableInTouchMode = true
        isClickable = true
        setOnTouchListener { _, event ->
            interceptTouchEvent(event)
        }
    }

    /**
     * Intercepts [MotionEvent] triggered by [ViewGroup.setOnTouchListener]
     * @param event MotionEvent?
     * @return Boolean True if the event has been consumed.
     */
    protected open fun interceptTouchEvent(event: MotionEvent?): Boolean {
        event?.let { ev ->
            if (ev.action == MotionEvent.ACTION_UP) {
                hideKeyboard()
                return true
            }
        }

        return false
    }


    /**
     * Triggers the action while dismissing the keyboard when IME is set to
     * [EditorInfo.IME_ACTION_DONE]
     *
     * @receiver EditText
     * @param block Function0<Unit>
     */
    protected fun EditText.onActionDone(block: () -> Unit) {
        this.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                block()
                true
            } else {
                false
            }
        }
    }

    /**
     * Triggers the action while dismissing the keyboard when IME is set to
     * [EditorInfo.IME_ACTION_SEARCH]
     *
     * @param block
     */
    protected fun EditText.onActionSearch(block: () -> Unit) {
        this.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard()
                block()
                true
            } else {
                false
            }
        }
    }

    protected fun SoftKeyBoardListener(
        keyBoardShow: (height: Int) -> Unit,
        keyBoardHide: (height: Int) -> Unit
    ) {
        var rootViewVisibleHeight: Int = 0
        //Gets the root view of the activity
        val rootView = activity?.window?.decorView
        //Listen for a change in the global layout of the view tree or a change in the visual state of a view in the view tree
        rootView?.viewTreeObserver?.addOnGlobalLayoutListener {
            //Gets the size of the current root view displayed on the screen
            val r = Rect()
            rootView.getWindowVisibleDisplayFrame(r)
            val visibleHeight = r.height()
            if (rootViewVisibleHeight == 0) {
                rootViewVisibleHeight = visibleHeight
                return@addOnGlobalLayoutListener
            }
            //The root view display height has not changed and can be viewed as the soft keyboard display/hide state has not changed
            if (rootViewVisibleHeight == visibleHeight) return@addOnGlobalLayoutListener
            //The root view displays a smaller height of more than 200, which can be viewed as a soft keyboard display
            if (rootViewVisibleHeight - visibleHeight > 200) {
                keyBoardShow(rootViewVisibleHeight)
                rootViewVisibleHeight = visibleHeight
                return@addOnGlobalLayoutListener
            }
            //The root view shows that the height has increased by more than 200, which can be seen as a hidden soft keyboard
            if (visibleHeight - rootViewVisibleHeight > 200) {
                keyBoardHide(visibleHeight - rootViewVisibleHeight)
                rootViewVisibleHeight = visibleHeight
                return@addOnGlobalLayoutListener
            }
        }
    }

    override fun showLoadingScreen(messageId: Int) {
        mLoadingProgressDialog.apply {
            setTitles(messageId)
            if (!isShowing) show()
        }
    }

    override fun showLoadingScreen(message: String) {
        mLoadingProgressDialog.apply {
            setTitles(message)
            if (!isShowing) show()
        }
    }

    override fun showLoadingScreenWithNoMessage() {
        mLoadingProgressDialog.apply {
            setTitle("")
            if (!isShowing) show()
        }
    }

    override fun hideLoadingScreen() {
        mLoadingProgressDialog.dismiss()
    }
}
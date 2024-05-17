package com.cloudhearing.android.lib_base.utils

import android.app.Dialog
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.inputmethodservice.InputMethodService
import android.os.Build
import android.text.*
import android.text.Annotation
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.annotation.ColorRes
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.text.getSpans
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.cloudhearing.android.lib_base.R
import com.cloudhearing.android.lib_base.base.BaseBindingDialog
import com.cloudhearing.android.lib_base.base.IBaseViewModel
import com.permissionx.guolindev.PermissionX
import timber.log.Timber


/**
 * Makes the status bar fully transparent. Works on API 23+ only. For pre-marshmallow versions, the status bar will
 * always be as declared per styles.xml. The root layout should have the `android:fitsSystemWindows` property set
 * to `true` if you want to avoid status bar being drawn over the screen.
 * @receiver Activity
 * @param statusBarTextColor StatusBarTextColor Choose whether the status bar text should be light or dark.
 */
fun AppCompatActivity.enableTransparentStatusBar(statusBarTextColor: StatusBarTextColor) {
    with(window) {
        when (statusBarTextColor) {
            StatusBarTextColor.LIGHT -> {
                decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                statusBarColor = Color.TRANSPARENT
            }

            StatusBarTextColor.DARK -> {
                decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                statusBarColor = Color.TRANSPARENT
            }
        }
    }
}


/**
 * Makes the status bar fully transparent. Works on API 23+ only. For pre-marshmallow versions, the status bar will
 * always be as declared per styles.xml. The root layout should have the `android:fitsSystemWindows` property set
 * to `true` if you want to avoid status bar being drawn over the screen.
 * @receiver Fragment
 * @param statusBarTextColor StatusBarTextColor Choose whether the status bar text should be light or dark.
 */
fun Fragment.enableTransparentStatusBar(statusBarTextColor: StatusBarTextColor) {
    this.activity?.apply {
        with(window) {
            when (statusBarTextColor) {
                StatusBarTextColor.LIGHT -> {
                    decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    statusBarColor = Color.TRANSPARENT
                }

                StatusBarTextColor.DARK -> {
                    decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    statusBarColor = Color.TRANSPARENT
                }
            }
        }
    }
}

/**
 * 处理返回键的事件
 *
 * @param onBackPressed
 */
fun AppCompatActivity.handleOnBackPressedDispatcher(onBackPressed: OnBackPressedCallback.() -> Unit) {
    onBackPressedDispatcher.addCallback {
        onBackPressed.invoke(this)

    }
}

/**
 * 处理返回键的事件
 *
 * @param onBackPressed
 */
fun Fragment.handleOnBackPressedDispatcher(onBackPressed: OnBackPressedCallback.() -> Unit) {
    requireActivity().onBackPressedDispatcher.addCallback {
        onBackPressed.invoke(this)
    }
}

/**
 * 显示键盘
 *
 */
fun AppCompatActivity.showKeyboard() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        InputMethodService().requestShowSelf(InputMethodManager.RESULT_UNCHANGED_SHOWN)
    } else {
        val manager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        manager.showSoftInputFromInputMethod(currentFocus?.rootView?.windowToken, 0)
    }
}

/**
 * 显示键盘
 *
 */
fun Fragment.showKeyboard() {
    requireActivity().apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            InputMethodService().requestShowSelf(InputMethodManager.RESULT_UNCHANGED_SHOWN)
        } else {
            val manager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            manager.showSoftInputFromInputMethod(currentFocus?.rootView?.windowToken, 0)
        }
    }
}

/**
 * 显示键盘
 *
 */
fun Dialog.showKeyboard() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        InputMethodService().requestShowSelf(InputMethodManager.RESULT_UNCHANGED_SHOWN)
    } else {
        val manager = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        manager.showSoftInputFromInputMethod(currentFocus?.rootView?.windowToken, 0)
    }
}

/**
 * 隐藏键盘
 *
 */
fun AppCompatActivity.hideKeyboard() {
    val manager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    manager.hideSoftInputFromWindow(currentFocus?.rootView?.windowToken, 0)
}

/**
 * 隐藏键盘
 *
 */
fun Fragment.hideKeyboard() {
    requireActivity().apply {
        val manager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(currentFocus?.rootView?.windowToken, 0)
    }
}

/**
 * 系统吐司
 *
 * @param message
 */
fun AppCompatActivity.systremToast(message: String?) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

/**
 * 系统吐司
 *
 * @param message
 */
fun Fragment.systremToast(message: String?) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

/**
 * 当前 Fragment 的子 Fragment
 *
 * @param id
 * @return
 */
fun AppCompatActivity.currentChildFragment(@IdRes id: Int): Fragment? = try {
    val mainNavFragment = supportFragmentManager.findFragmentById(id)
    val currentRootFragment =
        mainNavFragment!!.childFragmentManager.primaryNavigationFragment
    Timber.i("currentRootFragment $currentRootFragment")
    currentRootFragment
} catch (e: Exception) {
    e.printStackTrace()
    null
}

/**
 * 当前的 Fragment
 *
 * @return
 */
fun AppCompatActivity.currentFragment(): Fragment? = try {
    val fragments = supportFragmentManager.fragments
    var currentFragment: Fragment? = null
    if (fragments.isNotEmpty()) {
        for (fragment in fragments) {
            if (fragment != null && fragment.isVisible) {
                currentFragment = fragment
                break
            }
        }
    }
    Timber.i("currentRootFragment $currentFragment")
    currentFragment
} catch (e: Exception) {
    e.printStackTrace()
    null
}

/**
 * 当前的 Fragment
 *
 * @return
 */
fun Fragment.currentFragment(): Fragment? = try {
    val fragments = childFragmentManager.fragments
    var currentFragment: Fragment? = null
    if (fragments.isNotEmpty()) {
        for (fragment in fragments) {
            if (fragment != null && fragment.isVisible) {
                currentFragment = fragment
                break
            }
        }
    }
    Timber.i("currentRootFragment $currentFragment")
    currentFragment
} catch (e: Exception) {
    e.printStackTrace()
    null
}

/**
 * 隐藏当前 Fragment
 *
 */
fun AppCompatActivity.hideCurrentFragment() {
    supportFragmentManager.beginTransaction().apply {
        currentFragment()?.let { hide(it) }
        commitAllowingStateLoss()
    }
}

/**
 * 显示当前 Fragment
 *
 * @param fragment
 * @param containerViewId
 */
fun AppCompatActivity.showFragment(fragment: Fragment, @IdRes containerViewId: Int) {
    supportFragmentManager.beginTransaction().apply {
        val tag = fragment.javaClass.name
        if (fragment.isAdded) {
            Timber.d("$tag show")
            show(fragment)
        } else {
            Timber.d("$tag add")
            add(containerViewId, fragment, tag)
        }
        commitAllowingStateLoss()
    }
}

/**
 * fragment里面替换fragment
 *
 * @param contentView
 * @param fragment
 */
fun Fragment.replaceFragment(fragment: Fragment, @IdRes contentView: Int) {
    childFragmentManager.beginTransaction().apply {
        replace(contentView, fragment)
        commitAllowingStateLoss()
    }
}

/**
 * 显示当前的 Fragment
 *
 * @param fragment
 * @param containerViewId
 */
fun Fragment.showFragment(fragment: Fragment, @IdRes containerViewId: Int) {
    childFragmentManager.beginTransaction().apply {
        val tag = fragment.javaClass.name
        if (fragment.isAdded) {
            Timber.d("$tag show")
            show(fragment)
        } else {
            Timber.d("$tag add")
            add(containerViewId, fragment, tag)
        }
        commitAllowingStateLoss()
    }
}

/**
 * 隐藏当前 Fragment
 *
 */
fun Fragment.hideCurrentFragment() {
    childFragmentManager.beginTransaction().apply {
        currentFragment()?.let {
            hide(it)
        }
        commitAllowingStateLoss()
    }
}


/**
 * Checks if the Application with [packageName] exists.
 * @param packageName String
 * @param packageManager PackageManager
 * @return Boolean
 */
fun Context.isPackageInstalled(
    packageName: String,
    packageManager: PackageManager
): Boolean {
    return try {
        packageManager.getPackageInfo(packageName, 0)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}

val Number.toDp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )

val Number.toSp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )

val Number.toPx
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_PX,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )

/**
 * get [android.R.attr.selectableItemBackgroundBorderless] Drawable
 *
 * @param context
 * @return
 */
fun getSelectableItemBackgroundBorderlessDrawable(context: Context): Drawable? {
    val attrs = intArrayOf(android.R.attr.selectableItemBackgroundBorderless)
    val ta = context.obtainStyledAttributes(attrs)
    val drawable = ta.getDrawable(0)
    ta.recycle()
    return drawable
}

inline fun AppCompatSeekBar.onProgressChanged(crossinline event: (progress: Int) -> Unit) {
    setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            Timber.i("fromUser $fromUser progress $progress")
            event(progress)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
            Timber.i("progress $progress")
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            Timber.i("progress $progress")
        }
    })
}

inline fun AppCompatSeekBar.onProgressStopChanged(crossinline event: (progress: Int) -> Unit) {
    setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            Timber.i("fromUser $fromUser progress $progress")
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
            Timber.i("progress $progress")
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            Timber.i("progress $progress")
            event(progress)
        }
    })
}

inline fun AppCompatSeekBar.onProgressEvent(
    crossinline changedEvent: (progress: Int) -> Unit,
    crossinline startEvent: (progress: Int) -> Unit,
    crossinline stopEvent: (progress: Int) -> Unit
) {
    setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            Timber.i("fromUser $fromUser progress $progress")
            changedEvent(progress)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
            Timber.i("progress $progress")
            startEvent(progress)
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            Timber.i("progress $progress")
            stopEvent(progress)
        }
    })
}

/**
 * Scans the [CharSequence] for the [Annotation] with provided [Annotation] key and returns [SpannableString] with
 * its boundaries.
 * @receiver CharSequence
 * @param annotationKey String Key used to find the Annotation
 * @return Triple<SpannableString, Int, Int> -> SpannableString, spanStart, spanEnd
 */
fun CharSequence.spanWithAnnotationKey(
    annotationKey: String
): Triple<SpannableString, Int, Int> {
    SpannableString(this).apply {
        getSpans<android.text.Annotation>(0, length).first { it.key == annotationKey }.let {
            return Triple(this, getSpanStart(it), getSpanEnd(it))
        }
    }
}

fun currentTime(): Long {
    return System.currentTimeMillis()
}

fun View.setClickableAndFocusable(clickable: Boolean) {
    isClickable = clickable
    isFocusable = clickable
}


/**
 * Create a text click event
 * @param callback (String) -> Unit
 * @return Unit
 */
inline fun TextView.toSpannableString(
    @ColorRes colorId: Int = R.color.ecstasy,
    isBold: Boolean = true,
    crossinline callback: (String) -> Unit
): Unit =
    SpannableString(text).run {
        val linkedHashMap = LinkedHashMap<String, Pair<Int, Int>>()
        getSpans<Annotation>(0, length).forEach { an ->
            linkedHashMap[an.key] = Pair(getSpanStart(an), getSpanEnd(an))
        }

        linkedHashMap.entries.forEach { kv ->
            setSpan(SpannableClickHandler(false) {
                callback.invoke(kv.key)
            }, kv.value.first, kv.value.second, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context, colorId)),
                kv.value.first,
                kv.value.second,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            if (isBold) {
                setSpan(
                    StyleSpan(Typeface.BOLD),
                    kv.value.first,
                    kv.value.second,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }

        movementMethod = LinkMovementMethod.getInstance()
        text = this
    }


/**
 * [TextView] 效果文字
 *
 * @param colorId
 * @param isBold
 */
inline fun TextView.toEffectString(
    @ColorRes colorId: Int = androidx.appcompat.R.color.primary_material_light,
    isBold: Boolean = true
): Unit =
    SpannableString(text).run {
        val linkedHashMap = LinkedHashMap<String, Pair<Int, Int>>()
        getSpans<Annotation>(0, length).forEach { an ->
            linkedHashMap[an.key] = Pair(getSpanStart(an), getSpanEnd(an))
        }

        linkedHashMap.entries.forEach { kv ->
            setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context, colorId)),
                kv.value.first,
                kv.value.second,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            setSpan(
                RelativeSizeSpan(3.0f),
                kv.value.first,
                kv.value.second,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE
            )
            if (isBold) {
                setSpan(
                    StyleSpan(Typeface.BOLD),
                    kv.value.first,
                    kv.value.second,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }

        movementMethod = LinkMovementMethod.getInstance()
        text = this
    }


/**
 * Helper function to parse [TextWatcher.onTextChanged] event of [TextWatcher] only.
 * @receiver EditText
 * @param event (input: String) -> Unit
 */
inline fun EditText.onTextChange(crossinline event: (input: String) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            // Left empty on purpose
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Left empty on purpose
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val response = s?.toString() ?: ""
            event(response.trim())
        }
    })
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

/**
 * 水平滑动状态
 *
 * @param reachTopEvent
 * @param reachBottomEvent
 */
fun RecyclerView.onScrollHorizontallyState(
    reachTopEvent: () -> Unit,
    reachBottomEvent: () -> Unit,
    onScrollStateChanged: (() -> Unit)? = null
) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            Timber.d("newState ${newState}")
            Timber.d(
                "到达底部 ${this@onScrollHorizontallyState.canScrollHorizontally(1)}  到达顶部 ${
                    this@onScrollHorizontallyState.canScrollHorizontally(
                        -1
                    )
                }"
            )
            // 当前状态为停止滑动
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                if (!this@onScrollHorizontallyState.canScrollHorizontally(1)) {
                    Timber.d("到达底部")
                    reachBottomEvent.invoke()
                    return
                } else if (!this@onScrollHorizontallyState.canScrollHorizontally(-1)) {
                    Timber.d("到达顶部")
                    reachTopEvent.invoke()
                    return
                }
            }

            onScrollStateChanged?.invoke()
        }
    })
}

/**
 * 垂直滑动状态
 *
 * @param reachTopEvent
 * @param reachBottomEvent
 */
fun RecyclerView.onScrollVerticallyState(
    reachTopEvent: () -> Unit,
    reachBottomEvent: () -> Unit
) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            // 当前状态为停止滑动
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                if (!this@onScrollVerticallyState.canScrollVertically(1)) {
                    Timber.d("到达底部")
                    reachTopEvent.invoke()
                } else if (!this@onScrollVerticallyState.canScrollVertically(-1)) {
                    Timber.d("到达顶部")
                    reachBottomEvent.invoke()
                }
            }
        }
    })
}


/**
 * 权限请求
 *
 * @param fragment
 * @param permissions
 * @param onPermissionsGranted
 */
inline fun onRequestPermissions(
    fragment: Fragment,
    requestReason: String,
    positiveText: String,
    negativeText: String,
    vararg permissions: String,
    crossinline onPermissionsGranted: (() -> Unit)
) {
    PermissionX.init(fragment)
        .permissions(*permissions)
        .setDialogTintColor(Color.parseColor("#1972e8"), Color.parseColor("#8ab6f5"))
        .explainReasonBeforeRequest()
        .onExplainRequestReason { scope, deniedList, _ ->
            scope.showRequestReasonDialog(
                deniedList,
                requestReason,
                positiveText,
                negativeText
            )
        }
        .onForwardToSettings { _, _ ->

        }
        .request { allGranted, _, _ ->
            if (allGranted)
                onPermissionsGranted()
        }
}

/**
 * 经过校验的显示
 *
 * @param callback
 */
fun Dialog.safetyShow(callback: (() -> Unit)? = null) {
    if (isShowing) {
        return
    }

    show()

    callback?.invoke()
}

/**
 * 经过校验的隐藏
 *
 * @param callback
 */
fun Dialog.safetyHide(callback: (() -> Unit)? = null) {
    if (!isShowing) {
        return
    }

    hide()

    callback?.invoke()
}
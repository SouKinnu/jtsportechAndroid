package com.cloudhearing.android.lib_base.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineScope
import timber.log.Timber
import kotlin.properties.Delegates


abstract class BaseBindingFragment<VB : ViewBinding>(
    private val inflate: (LayoutInflater, ViewGroup?, Boolean) -> VB
) : BaseFragment(), CoroutineScope {

    private var _binding: VB? = null
    val binding: VB get() = _binding!!
    override fun onResume() {
        super.onResume()
        Timber.tag("TAG").e("onResume BaseBindingFragment：" + javaClass.name)
        onShow(true)
    }

    override fun onPause() {
        super.onPause()
        Timber.tag("TAG").e("onPause BaseBindingFragment：" + javaClass.name)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        Timber.tag("TAG").e("onHiddenChanged $hidden BaseBindingFragment：" + javaClass.name)

        if (hidden) {
            onHide()
        } else {
            onShow()
        }
    }

    /**
     * 首次加载页面是不会走 onHiddenChanged 的
     * Fragment 的 hide 和 show 时使用的
     *
     */
    open fun onShow(isFirstLoad: Boolean = false) {
        Timber.tag("TAG").e("onShow BaseBindingFragment：" + javaClass.name)
    }

    open fun onHide() {
        Timber.tag("TAG").e("onHide BaseBindingFragment：" + javaClass.name)
    }

    /**
     * 布局存在标志
     */
    var bingActiveFlag by Delegates.notNull<Boolean>()
        private set

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bingActiveFlag = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bingActiveFlag = false
        _binding = null
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        _binding = null
//    }
}
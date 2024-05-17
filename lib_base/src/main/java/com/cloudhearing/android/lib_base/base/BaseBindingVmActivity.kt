package com.cloudhearing.android.lib_base.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.Keep
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import timber.log.Timber
import java.lang.reflect.ParameterizedType

/**
 * Author: BenChen
 * Date: 2023/06/05 16:07
 * Email:chenxiaobin@cloudhearing.cn
 */
abstract class BaseBindingVmActivity<VB : ViewBinding, VM : BaseViewModel>(
    private val inflate: (LayoutInflater) -> VB
) : BaseBindingActivity<VB>(inflate), initListener {

    protected lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[getViewModelClass()]
        viewModel.addObserver()

        initView()
        initData()
        initEvent()
    }

    @Keep
    open fun getViewModelClass(): Class<VM> {
        Timber.d("getViewModelClass ${(javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[1]}")
        return ((javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[1]) as Class<VM>
    }
}
package com.cloudhearing.android.lib_base.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

/**
 * Author: BenChen
 * Date: 2023/05/24 17:40
 * Email:chenxiaobin@cloudhearing.cn
 */
abstract class BaseBindingVmFragment<VB : ViewBinding, VM : BaseViewModel>(
    private val inflate: (LayoutInflater, ViewGroup?, Boolean) -> VB
) :
    BaseBindingFragment<VB>(inflate) {

    protected lateinit var viewModel: VM

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[getViewModelClass()]
        super.onViewCreated(view, savedInstanceState)
        viewModel.addObserver()

        initView()
        initData()
        initEvent()
    }

    @Keep
    private fun getViewModelClass(): Class<VM> {
        return ((javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[1]) as Class<VM>
    }

    abstract fun initView()

    abstract fun initData()

    abstract fun initEvent()
}
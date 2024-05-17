package com.cloudhearing.android.lib_base.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding

/**
 * Author: BenChen
 * Date: 2023/12/19 18:19
 * Email:chenxiaobin@cloudhearing.cn
 */
abstract class BaseCoroutineBindingActivity<VB : ViewBinding>(
    private val inflate: (LayoutInflater) -> VB
) : BaseCoroutineActivity() {

    lateinit var binding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflate(layoutInflater)
        setContentView(binding.root)
    }
}
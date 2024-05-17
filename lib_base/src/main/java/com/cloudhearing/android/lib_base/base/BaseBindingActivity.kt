package com.cloudhearing.android.lib_base.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding


abstract class BaseBindingActivity<VB : ViewBinding>(
    private val inflate: (LayoutInflater) -> VB
) : BaseActivity() {

    lateinit var binding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflate(layoutInflater)
        setContentView(binding.root)
    }
}
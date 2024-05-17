package com.jtsportech.visport.android.mine.about

import androidx.navigation.fragment.findNavController
import com.cloudhearing.android.lib_base.base.BaseBindingVmFragment
import com.cloudhearing.android.lib_base.utils.toDp
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.databinding.FragmentAboutBinding
import com.jtsportech.visport.android.utils.QRCodeGenerator

class AboutFragment :
    BaseBindingVmFragment<FragmentAboutBinding, AboutViewModel>(FragmentAboutBinding::inflate) {
    override fun initView() {
        binding.apply {
            apAbout.setOnClickLeftIconListener {
                requireActivity().onBackPressed()
            }

        }
    }

    override fun initData() {
        val bitmap = QRCodeGenerator.createQRImage(
            "https://test.sztcpay.com:10443/jtsport-admin/#/login",
            72.toDp.toInt(),
            72.toDp.toInt()
        )

        binding.ivQr.setImageBitmap(bitmap)
    }

    override fun initEvent() {

    }


}
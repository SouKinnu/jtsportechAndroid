package com.jtsportech.visport.android.landing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cloudhearing.android.lib_base.base.BaseBindingActivity
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.databinding.ActivityLandingBinding
import com.jtsportech.visport.android.utils.helper.QQHelper
import com.jtsportech.visport.android.utils.helper.UMHelper
import com.tencent.connect.common.Constants
import com.tencent.tauth.Tencent
import timber.log.Timber

class LandingActivity :
    BaseBindingActivity<ActivityLandingBinding>(ActivityLandingBinding::inflate) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        QQHelper.INSTANCE.handleActivityResult(requestCode, resultCode, data)
    }

}
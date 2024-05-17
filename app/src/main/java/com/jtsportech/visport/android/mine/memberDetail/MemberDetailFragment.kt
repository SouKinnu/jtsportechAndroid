package com.jtsportech.visport.android.mine.memberDetail

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navArgs
import com.cloudhearing.android.lib_base.base.BaseBindingVmFragment
import com.cloudhearing.android.lib_base.utils.ANTI_SHAKE_THRESHOLD
import com.cloudhearing.android.lib_base.utils.clickFlow
import com.cloudhearing.android.lib_base.utils.throttleFirst
import com.cloudhearing.android.lib_base.utils.toDp
import com.cloudhearing.android.lib_common.image.loadRoundCornerImage
import com.cloudhearing.android.lib_common.utils.mmkv.PreferencesWrapper
import com.cloudhearing.android.lib_common.utils.startCallPhoneActivity
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.databinding.FragmentMemberDetailBinding
import com.jtsportech.visport.android.utils.img
import com.jtsportech.visport.android.utils.maskPhoneNumber
import com.jtsportech.visport.android.webview.WebActivityArgs
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class MemberDetailFragment :
    BaseBindingVmFragment<FragmentMemberDetailBinding, MemberDetailViewModel>(
        FragmentMemberDetailBinding::inflate
    ) {

    private val mArgs by navArgs<MemberDetailFragmentArgs>()

    override fun initView() {
        val member = mArgs.member

        binding.apply {
            apMemberDetail.setOnClickLeftIconListener {
                goBack()
            }

            ivLogo.loadRoundCornerImage(
                url = member.avatarImageFilePath?.img(),
                radius = 6.toDp.toInt(),
                placeHolder = R.drawable.ic_mine_team_avatar_def
            )

            tvName.text = member.name.orEmpty()
            tvTeamName.text = member.organizationName.orEmpty()
            tvLocationValue.text = member.pitchPosition ?: "--"
            tvJerseyNumberValue.text = "${
                member.uniformNo.takeIf {
                    it != null
                } ?: "--"
            }"

            tvHeightValue.text = "${member.userHeight ?: "0"} CM"

            tvWeightValue.text = "${member.userWeight ?: "0"} KG"

            member.phoneNo?.let {
                tvCallPhone.text = it.maskPhoneNumber()
            }

            tvCallPhone.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    onCallPhone()
                }
                .launchIn(mainScope)
        }
    }

    override fun initData() {

    }

    override fun initEvent() {

    }

    private fun onCallPhone() {
        val phoneNumber = mArgs.member.phoneNo

        if (phoneNumber.isNullOrEmpty()) {
            Timber.e("队员的手机号码是空")
            return
        }

        context?.startCallPhoneActivity(phoneNumber)
    }

    private fun goBack() {
        findNavController().popBackStack()
    }


}
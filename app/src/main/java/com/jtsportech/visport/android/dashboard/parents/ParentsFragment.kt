package com.jtsportech.visport.android.dashboard.parents

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.cloudhearing.android.lib_base.base.BaseBindingVmFragment
import com.cloudhearing.android.lib_base.utils.doOnApplyWindowInsets
import com.cloudhearing.android.lib_base.utils.observeState
import com.cloudhearing.android.lib_base.utils.toDp
import com.cloudhearing.android.lib_common.image.loadCircleImage
import com.cloudhearing.android.lib_common.image.loadRoundCornerImage
import com.cloudhearing.android.lib_common.network.dataSource.mine.User
import com.cloudhearing.android.lib_common.network.dataSource.mine.UserInfo
import com.cloudhearing.android.lib_common.utils.GridSpaceItemDecoration
import com.cloudhearing.android.lib_common.utils.mmkv.PreferencesWrapper
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.dashboard.DashboardActivity
import com.jtsportech.visport.android.databinding.FragmentParentsBinding
import com.jtsportech.visport.android.playerdetail.simpleplay.SimplePlayActivity
import com.jtsportech.visport.android.utils.img
import timber.log.Timber

class ParentsFragment :
    BaseBindingVmFragment<FragmentParentsBinding, ParentsViewModel>(FragmentParentsBinding::inflate) {

    private val mVideoHighlightAdapter: VideoHighlightAdapter by lazy {
        VideoHighlightAdapter().apply {
            setOnClickListener {
                SimplePlayActivity.jump(
                    requireActivity(),
                    it.id,
                    "MATCH_PLAYER_VIDEO",
                    it.matchInfoName + "-" + it.playerFrontUserName
                )
            }
        }
    }

    override fun initView() {
        binding.apply {
            rvVideoHighlight.apply {
                adapter = mVideoHighlightAdapter
                addItemDecoration(GridSpaceItemDecoration(2, 16.toDp.toInt(), 12.toDp.toInt()))
                itemAnimator?.changeDuration = 0
            }
        }
    }

    override fun initData() {

    }

    override fun initEvent() {
        viewModel.run {
            userInfoStateFlow.observeState(this@ParentsFragment) {
                handleUserInfo(it)
            }

            videoHightlightStateFlow.observeState(this@ParentsFragment) {
                mVideoHighlightAdapter.submitList(it)
            }
        }
    }

    override fun onShow(isFirstLoad: Boolean) {
        super.onShow(isFirstLoad)
        setupStatusBar()
    }

    private fun setupStatusBar() {
        val dashboardActivity = requireActivity() as DashboardActivity
        val rootView = requireActivity().findViewById<FrameLayout>(android.R.id.content)
        rootView.doOnApplyWindowInsets { _, _, padding, _ ->
            Timber.d("padding.top ${padding.top} padding.bottom ${padding.bottom}")
            dashboardActivity.registerPaddingSystemWindowInsets(
                dashboardActivity,
                padding.top == 0,
                padding.bottom == 0
            )
            dashboardActivity.registerTransparentStatusBar(
                dashboardActivity,
                false
            )
        }
    }

    private fun handleUserInfo(user: User) = binding.apply {
        binding.ivLogo.loadCircleImage(
            user.avatarImageFilePath?.img(),
            placeHolder = R.drawable.ic_mine_team_avatar_def
        )
        tvName.text = user.name
        tvTeamName.text = PreferencesWrapper.get().getOrganizationName()
//        tvTeamName.text = user.orgRoleList?.get(0)?.organizationName
        tvHeightValue.text = "${user.userHeight?.toInt() ?: 0} CM"
        tvWeightValue.text = "${user.userWeight?.toInt() ?: 0} KG"
        tvLocationValue.text = "——"
        tvJerseyNumberValue.text = "——"
    }

}
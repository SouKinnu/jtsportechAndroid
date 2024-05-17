package com.jtsportech.visport.android.dashboard.mine

import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.ToastUtils
import com.cloudhearing.android.lib_base.base.BaseBindingVmFragment
import com.cloudhearing.android.lib_base.dataSource.LoadState
import com.cloudhearing.android.lib_base.utils.ANTI_SHAKE_THRESHOLD
import com.cloudhearing.android.lib_base.utils.clickFlow
import com.cloudhearing.android.lib_base.utils.doOnApplyWindowInsets
import com.cloudhearing.android.lib_base.utils.hide
import com.cloudhearing.android.lib_base.utils.observeEvent
import com.cloudhearing.android.lib_base.utils.observeState
import com.cloudhearing.android.lib_base.utils.show
import com.cloudhearing.android.lib_base.utils.systremToast
import com.cloudhearing.android.lib_base.utils.throttleFirst
import com.cloudhearing.android.lib_common.image.loadCircleImage
import com.cloudhearing.android.lib_common.utils.ShareUtils
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.components.dialog.InvitecodeDialog
import com.jtsportech.visport.android.dashboard.DashboardActivity
import com.jtsportech.visport.android.dataSource.UserRole
import com.jtsportech.visport.android.databinding.FragmentMineBinding
import com.jtsportech.visport.android.mine.MineActivity
import com.jtsportech.visport.android.utils.img
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class MineFragment :
    BaseBindingVmFragment<FragmentMineBinding, MineViewModel>(FragmentMineBinding::inflate) {

    private val mInvitecodeDialog: InvitecodeDialog by lazy {
        InvitecodeDialog(requireContext()).apply {
            setLeftButtonlickListener {
                ToastUtils.showShort(getString(R.string.profile_save_success))
            }

            setRightButtonlickListener {
                Timber.d("走了回调")
                ShareUtils.shareImage(requireContext(), it, "", "", "")
            }
        }
    }

    override fun initView() {

        binding.apply {
            tvRecentlyWatched.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    onMine(MineActivity.RECENTLY_WATCHED_PAGE)
                }
                .launchIn(mainScope)

            tvMyFavorites.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    onMine(MineActivity.MY_FAVORITES_PAGE)
                }
                .launchIn(mainScope)

            tvMyTeam.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    onMine(MineActivity.MY_TEAM_PAGE)
                }
                .launchIn(mainScope)

            sbPersonalCenter.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    onMine(MineActivity.PERSONAL_CENTER_PAGE)
                }
                .launchIn(mainScope)

            sbPrivacyAndSecurity.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    onMine(MineActivity.PRIVACY_AND_SECURITY_PAGE)
                }
                .launchIn(mainScope)

            sbSetUp.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    onMine(MineActivity.SET_UP_PAGE)
                }
                .launchIn(mainScope)

            sbAboutUs.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    onMine(MineActivity.ABOUT_PAGE)
                }
                .launchIn(mainScope)

            sbInviteFriends.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
//                    onMine(MineActivity.INVITE_FRIENDS_PAGE)
                    viewModel.getInvitecode()
                }
                .launchIn(mainScope)
        }

        handleDisplayBarLogic()
    }

    override fun onShow(isFirstLoad: Boolean) {
        super.onShow(isFirstLoad)
        setupStatusBar()
    }

    private fun setupStatusBar() {
        val dashboardActivity = requireActivity() as DashboardActivity
        val index =
            if (viewModel.role == UserRole.VISITOR || viewModel.role == UserRole.GUARDER) 1 else 2

        if (dashboardActivity.getCurrentTabIndex() != index) {
            return
        }

        val rootView = requireActivity().findViewById<FrameLayout>(android.R.id.content)
        rootView.doOnApplyWindowInsets { _, _, padding, _ ->
            Timber.d("xiaobin padding.top ${padding.top}")
            if (padding.top > 0) {
                dashboardActivity.registerImmersiveStatusBar(
                    dashboardActivity,
                    true
                )
//                dashboardActivity.registerMarginSystemWindowInsets(
//                    dashboardActivity,
//                    false,
//                    false
//                )
            }
            return@doOnApplyWindowInsets
        }
    }

    override fun initData() {

    }

    override fun initEvent() {
        viewModel.run {
            avatarImageFilePathFlow.observeState(this@MineFragment) {
                binding.ivAvatar.loadCircleImage(
                    it.img(),
                    placeHolder = R.drawable.ic_avatar_mine_def
                )
            }

            nicknameFlow.observeState(this@MineFragment) {
                binding.tvNickname.text = it
            }

            logoImageFilePathFlow.observeState(this@MineFragment) {
                if (viewModel.role == UserRole.VISITOR) return@observeState

                binding.ivTeamLogo.loadCircleImage(it.img(), placeHolder = R.drawable.img_team_def)
            }

            organizationNameFlow.observeState(this@MineFragment) {
                if (viewModel.role == UserRole.VISITOR) return@observeState

                binding.tvTeamName.text = it
            }

            headCoachAvatarImageFilePathFlow.observeState(this@MineFragment) {
//                binding.ivInstructorLogo.loadCircleImage(it.img())
            }

            headCoachFrontUserNameFlow.observeState(this@MineFragment) {
//                binding.tvInstructorName.text = it
            }

            toastFlowEvents.observeEvent(this@MineFragment) {
                systremToast(it)
            }

            loadState.observeEvent(this@MineFragment) {
                handleLoadState(it)
            }

            invitecodeFlowEvents.observeEvent(this@MineFragment) {
                showInvitecodeDialog(it)
            }
        }
    }

    private fun showInvitecodeDialog(code: String) = mInvitecodeDialog.apply {
        if (isShowing) {
            return@apply
        }

        setCode(code)

        show()
    }

    private fun hideInvitecodeDialog() = mInvitecodeDialog.apply {
        if (!isShowing) {
            return@apply
        }

        hide()
    }

    private fun handleLoadState(state: LoadState) {
        when (state) {
            is LoadState.Start -> {
                showLoadingScreen(state.tip)
            }

            is LoadState.Error -> {
                hideLoadingScreen()
                systremToast("${state.code}:${state.msg}")
            }

            is LoadState.Finish -> {
                hideLoadingScreen()
            }

            else -> {}
        }
    }

    private fun handleDisplayBarLogic() = binding.apply {
        when (viewModel.role) {
            UserRole.HEAD_COACH, UserRole.COACH -> {
                ivInstructorLogo.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.img_role_coach
                    )
                )
                tvInstructorName.text = getString(R.string.my_team_coach)
            }

            UserRole.MEMBER -> {
                ivInstructorLogo.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.img_role_member
                    )
                )
                tvInstructorName.text = getString(R.string.my_team_member)
            }

            UserRole.VISITOR -> {
                ivInstructorLogo.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.img_role_visitor
                    )
                )


                tvInstructorName.text = getString(R.string.profile_visitor)
                sbInviteFriends.visibility = View.GONE

                tvTeamName.text = "- -"
                ivTeamLogo.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.img_team_def
                    )
                )
            }

            UserRole.GUARDER -> {
                ivInstructorLogo.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.img_role_visitor
                    )
                )

                tvInstructorName.text = getString(R.string.profile_parents)
                sbInviteFriends.visibility = View.GONE
            }
        }
    }

    private fun onMine(@MineActivity.MINEPAGE page: String) {
        MineActivity.jump(requireActivity(), page)
    }

}
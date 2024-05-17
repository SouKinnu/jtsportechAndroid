package com.jtsportech.visport.android.mine.myTeam

import androidx.navigation.fragment.findNavController
import com.cloudhearing.android.lib_base.base.BaseBindingVmFragment
import com.cloudhearing.android.lib_base.dataSource.LoadState
import com.cloudhearing.android.lib_base.utils.ANTI_SHAKE_THRESHOLD
import com.cloudhearing.android.lib_base.utils.clickFlow
import com.cloudhearing.android.lib_base.utils.hide
import com.cloudhearing.android.lib_base.utils.observeEvent
import com.cloudhearing.android.lib_base.utils.observeState
import com.cloudhearing.android.lib_base.utils.show
import com.cloudhearing.android.lib_base.utils.systremToast
import com.cloudhearing.android.lib_base.utils.throttleFirst
import com.cloudhearing.android.lib_base.utils.toDp
import com.cloudhearing.android.lib_common.image.loadRoundCornerImage
import com.cloudhearing.android.lib_common.network.dataSource.mine.TeamMembers
import com.cloudhearing.android.lib_common.network.dataSource.mine.User
import com.cloudhearing.android.lib_common.utils.SpacesItemDecoration
import com.cloudhearing.android.lib_common.utils.mmkv.PreferencesWrapper
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.dataSource.UserRole
import com.jtsportech.visport.android.databinding.FragmentMyTeamBinding
import com.jtsportech.visport.android.utils.img
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MyTeamFragment :
    BaseBindingVmFragment<FragmentMyTeamBinding, MyTeamViewModel>(FragmentMyTeamBinding::inflate) {

    private val mTeamMemberAdapter: TeamMemberAdapter by lazy {
        TeamMemberAdapter().apply {
            setClickListener {
                onMemberDetail(it)
            }
        }
    }

    override fun initView() {
        binding.apply {
            apMyTeam.setOnClickLeftIconListener {
                goBack()
            }

            rvMember.apply {
                adapter = mTeamMemberAdapter
                addItemDecoration(SpacesItemDecoration(10.toDp.toInt(), false))
//                addItemDecoration(
//                    DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL).apply {
//                        setDrawable(
//                            ContextCompat.getDrawable(
//                                requireContext(),
//                                R.drawable.shape_menber_divider
//                            )!!
//                        )
//                    }
//                )
                itemAnimator?.changeDuration = 0
            }

            tvToggleTeam.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    onSwitchTeam()
                }
                .launchIn(mainScope)

            if (viewModel.hasInviterFrontUserId) {

            }
        }
    }

    override fun initData() {

    }

    override fun initEvent() {
        viewModel.run {
            temaMemberListStateFlow.observeState(this@MyTeamFragment) {
                mTeamMemberAdapter.submitList(it)
            }

            organizationNameFlow.observeState(this@MyTeamFragment) {
                binding.tvTeamName.text = it
            }

            logoImageFilePathFlow.observeState(this@MyTeamFragment) {
                binding.ivTeamLogo.loadRoundCornerImage(url = it.img(), radius = 6.toDp.toInt(), placeHolder = R.drawable.img_team_def)
            }

            groupNameFlow.observeState(this@MyTeamFragment) {
                binding.tvTeamUnderlingName.text = "—— ${it}"
            }

            toastFlowEvents.observeEvent(this@MyTeamFragment) {
                systremToast(it)
            }

            loadState.observeEvent(this@MyTeamFragment) {
                handleLoadState(it)
            }

            userStateFlow.observeState(this@MyTeamFragment) {
                handleParentsUi(it)
            }

            hasTeamStateFlow.observeState(this@MyTeamFragment) {
                handleVisitorUi(!it)
            }
        }
    }

    private fun handleParentsUi(user: User) = binding.apply {
        if (!viewModel.hasInviterFrontUserId) {
            return@apply
        }

        tvTeamName.text = viewModel._organizationNameFlow.value
        if (PreferencesWrapper.get().getGroupId().isEmpty()) {
            tvTeamUnderlingName.text = "—— ${getString(R.string.my_team_no_group)}"
        }
        if (viewModel.role == UserRole.GUARDER) {
            tvToggleTeam.alpha = 0f
            tvToggleTeam.isEnabled = false
        }
    }

    private fun handleVisitorUi(isShow: Boolean) = binding.apply {
        if (!isShow) {
            return@apply
        }

        groupTeam.hide()
        tvNoTeam.show()
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

    private fun onSwitchTeam() {
        findNavController().navigate(MyTeamFragmentDirections.actionMyTeamFragmentToSwitchTeamsFragment())
    }

    private fun onMemberDetail(member: TeamMembers) {
        findNavController().navigate(
            MyTeamFragmentDirections.actionMyTeamFragmentToMemberDetailFragment(
                member
            )
        )
    }


    private fun goBack() {
        requireActivity().onBackPressed()
    }
}
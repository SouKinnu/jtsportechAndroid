package com.jtsportech.visport.android.mine.switchTeams

import androidx.navigation.fragment.findNavController
import com.cloudhearing.android.lib_base.base.BaseBindingVmFragment
import com.cloudhearing.android.lib_base.dataSource.LoadState
import com.cloudhearing.android.lib_base.utils.SIGN_OUT
import com.cloudhearing.android.lib_base.utils.observeEvent
import com.cloudhearing.android.lib_base.utils.observeState
import com.cloudhearing.android.lib_base.utils.safetyShow
import com.cloudhearing.android.lib_base.utils.systremToast
import com.cloudhearing.android.lib_base.utils.toDp
import com.cloudhearing.android.lib_common.utils.SpacesItemDecoration
import com.jeremyliao.liveeventbus.LiveEventBus
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.components.dialog.SignOutDialog
import com.jtsportech.visport.android.dataSource.mine.switchTeams.SwitchTeamEntity
import com.jtsportech.visport.android.databinding.FragmentSwitchTeamsBinding

class SwitchTeamsFragment : BaseBindingVmFragment<FragmentSwitchTeamsBinding, SwitchTeamsViewModel>(
    FragmentSwitchTeamsBinding::inflate
) {

    private val mSwitchTeamsDialog: SignOutDialog by lazy {
        SignOutDialog(requireContext()).apply {
            setTitle(R.string.privacy_and_security_prompt)
            setPositiveButtonLable(getString(R.string.alert_cancel))
            setNegativeButtonLable(getString(R.string.alert_sure))
        }
    }

    private val mSwitchTeamsAdapter: SwitchTeamsAdapter by lazy {
        SwitchTeamsAdapter()
    }

    private val mSwitchTeamsAdapterList by lazy {
        arrayListOf(
            SwitchTeamsPrimaryAdapter(),
            SwitchTeamsSecondaryAdapter()
        )
    }

    override fun initView() {
        binding.apply {
            apSwitchTeams.setOnClickLeftIconListener {
                goBack()
            }

            rvTeam.apply {
                adapter = mSwitchTeamsAdapter
                addItemDecoration(SpacesItemDecoration(10.toDp.toInt(), false))
                itemAnimator?.changeDuration = 0
            }
        }

        assembleSwitchTeamsAdapter()
    }

    override fun initData() {

    }

    override fun initEvent() {
        viewModel.run {
            teamListStateFlow.observeState(this@SwitchTeamsFragment) {
                mSwitchTeamsAdapter.submitList(it.toMutableList())
            }

            toastFlowEvents.observeEvent(this@SwitchTeamsFragment) {
                systremToast(it)
            }

            signOutNoticeEvents.observeEvent(this@SwitchTeamsFragment) {
                onSignOut()
            }

            loadState.observeEvent(this@SwitchTeamsFragment) {
                handleLoadState(it)
            }
        }
    }

    private fun assembleSwitchTeamsAdapter() {
        mSwitchTeamsAdapterList.forEach {
            mSwitchTeamsAdapter.addDelegate(it)
        }

        (mSwitchTeamsAdapterList[0] as SwitchTeamsPrimaryAdapter).setOnClickListener {
            handleToggleTeamLogic(it)
        }

        (mSwitchTeamsAdapterList[1] as SwitchTeamsSecondaryAdapter).setOnClickListener {
            handleToggleGroupLogic(it)
        }
    }

    private fun handleToggleTeamLogic(team: SwitchTeamEntity) {
        if (team.teamId == viewModel.currentOrganizationId) {
            systremToast(getString(R.string.switchTeams_already_on_the_current_team))
            return
        }

        showSwitchTeamDialog(team)
    }

    private fun handleToggleGroupLogic(team: SwitchTeamEntity) {
        if (team.teamId == viewModel.currentOrganizationId && team.groupId == viewModel.currentGroupId) {
            systremToast(getString(R.string.switchTeams_already_on_the_current_team))
            return
        }

        showSwitchGroupDialog(team, team.teamId == viewModel.currentOrganizationId)
    }

    private fun showSwitchTeamDialog(team: SwitchTeamEntity) = mSwitchTeamsDialog.apply {
        setSubtitle(
            getString(
                R.string.switchTeams_the_current_team_needs_to_be_switched_to,
                team.teamName
            )
        )

        setNegativeButtonlickListener {
            viewModel.changeOrganization(team.teamId.orEmpty(), "")
        }

        safetyShow()
    }

    private fun showSwitchGroupDialog(team: SwitchTeamEntity, isSameTeam: Boolean) =
        mSwitchTeamsDialog.apply {
            setSubtitle(
                getString(
                    R.string.switchTeams_the_current_team_needs_to_be_switched_to_group,
                    team.teamName,
                    team.groupName
                )
            )

            setNegativeButtonlickListener {
                viewModel.changeOrganization(
                    team.teamId.orEmpty(),
                    team.groupId.orEmpty(),
                    isSameTeam
                )
            }

            safetyShow()
        }

    private fun onSignOut() {
        LiveEventBus.get<String>(SIGN_OUT).post("")
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

    private fun goBack() {
        findNavController().popBackStack()
    }

}
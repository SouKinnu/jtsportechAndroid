package com.jtsportech.visport.android.dashboard.home

import com.cloudhearing.android.lib_base.base.BaseViewModel
import com.cloudhearing.android.lib_common.network.dataSource.home.competition.Competition
import com.cloudhearing.android.lib_common.network.dataSource.homebanner.HomeBanner
import com.cloudhearing.android.lib_common.network.repository.CompetitionRepository
import com.cloudhearing.android.lib_common.utils.launchRequest
import com.cloudhearing.android.lib_common.utils.mmkv.PreferencesWrapper
import com.jtsportech.visport.android.dataSource.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

class HomeViewModel : BaseViewModel() {

    private val mCompetitionRepository: CompetitionRepository by lazy {
        CompetitionRepository()
    }

    private val _userRoleStateFlow = MutableStateFlow(UserRole.UNKOWN)

    val userRoleStateFlow = _userRoleStateFlow.asStateFlow()

    private val _eventListDataFlow = MutableStateFlow(emptyList<Competition>())

    val eventListDataFlow = _eventListDataFlow.asStateFlow()

    private val _homeDialogStateFlow = MutableStateFlow(emptyList<HomeBanner>())

    val homeDialogStateFlow = _homeDialogStateFlow.asStateFlow()


    override fun onStart() {
        super.onStart()
        getHomeDialog()
        getHomeDialogData()
        getPageDisplayLogic()
    }

    fun getHomeDialog() {
        launchRequest(
            isLoading = false, {
                mCompetitionRepository.getHomeDialog()
            }, {
                if (it != null) {
                    _homeDialogStateFlow.value = listOf(it)
                }
            }, { errorCode, errorMsg ->

            }, {

            }
        )

    }

    fun getHomeDialogData() {
        launchRequest(
            isLoading = false, {
                mCompetitionRepository.getLeagueList()
            }, {
                if (it != null) {
                    _eventListDataFlow.value = it
                }
            }, { errorCode, errorMsg ->

            }, {

            }
        )

    }

    private fun getPageDisplayLogic() {
        val role = PreferencesWrapper.get().getRole()

        Timber.d("role $role")

        when {
            role == UserRole.HEAD_COACH -> {
                _userRoleStateFlow.value = UserRole.HEAD_COACH
            }

            role == UserRole.COACH -> {
                _userRoleStateFlow.value = UserRole.COACH
            }

            role == UserRole.MEMBER -> {
                _userRoleStateFlow.value = UserRole.MEMBER
            }

            role == UserRole.VISITOR -> {
                _userRoleStateFlow.value = UserRole.VISITOR
            }

            role == UserRole.GUARDER -> {
                _userRoleStateFlow.value = UserRole.GUARDER
            }

            else -> {
                _userRoleStateFlow.value = UserRole.UNKOWN
            }
        }
    }

}
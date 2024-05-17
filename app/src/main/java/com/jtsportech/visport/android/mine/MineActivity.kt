package com.jtsportech.visport.android.mine

import android.content.Intent
import android.os.Bundle
import androidx.annotation.StringDef
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.cloudhearing.android.lib_base.base.BaseBindingVmActivity
import com.cloudhearing.android.lib_base.utils.SIGN_OUT
import com.cloudhearing.android.lib_base.utils.context.AppProvider
import com.cloudhearing.android.lib_base.utils.handleOnBackPressedDispatcher
import com.jeremyliao.liveeventbus.LiveEventBus
import com.jtsportech.visport.android.NavMineDirections
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.dashboard.mine.MineFragment
import com.jtsportech.visport.android.databinding.ActivityMineBinding
import com.jtsportech.visport.android.landing.LandingActivity
import com.jtsportech.visport.android.mine.changePassword.ChangePasswordFragment
import com.jtsportech.visport.android.utils.PreferencesBusinessHelper
import com.jtsportech.visport.android.utils.helper.QQHelper
import timber.log.Timber

class MineActivity :
    BaseBindingVmActivity<ActivityMineBinding, MineViewModel>(ActivityMineBinding::inflate) {

    override val transparentStatusBar: Boolean
        get() = false

    override val paddingTopSystemWindowInsets: Boolean
        get() = true

    @StringDef(
        RECENTLY_WATCHED_PAGE,
        MY_FAVORITES_PAGE,
        MY_TEAM_PAGE,
        SWITCH_TEAMS_PAGE,
        PERSONAL_CENTER_PAGE,
        PRIVACY_AND_SECURITY_PAGE,
        SET_UP_PAGE,
        ABOUT_PAGE,
        INVITE_FRIENDS_PAGE
    )
    @Target(AnnotationTarget.VALUE_PARAMETER)
    @Retention(AnnotationRetention.SOURCE)
    annotation class MINEPAGE
    companion object {
        /**
         * 最近观看
         */
        const val RECENTLY_WATCHED_PAGE = "recently_watched_page"

        /**
         * 我的收藏
         */
        const val MY_FAVORITES_PAGE = "my_favorites_page"

        /**
         * 我的队伍
         */
        const val MY_TEAM_PAGE = "my_team_page"

        /**
         * 切换队伍
         */
        const val SWITCH_TEAMS_PAGE = "switch_teams_page"

        /**
         * 个人中心
         */
        const val PERSONAL_CENTER_PAGE = "personal_center_page"

        /**
         * 隐私政策
         */
        const val PRIVACY_AND_SECURITY_PAGE = "privacy_and_security_page"

        /**
         * 设置
         */
        const val SET_UP_PAGE = "set_up_page"

        /**
         * 关于
         */
        const val ABOUT_PAGE = "about_page"

        /**
         * 邀请好友
         */
        const val INVITE_FRIENDS_PAGE = "invite_friends_page"

        /**
         * 一级界面
         */
        private val rootDestinationId = mutableSetOf(
            R.id.profileFragment,
            R.id.recentlyWatchedFragment,
            R.id.myFavoritesFragment,
            R.id.myTeamFragment,
            R.id.privacyAndSecurityFragment,
            R.id.setupFragment,
            R.id.aboutFragment,
            R.id.inviteFriendsFragment,
        )

        /**
         * 不需要关闭页面标志位
         */
        private var NO_CLOSE_PAGE_FLAG = true

        /**
         * 跳转页面
         *
         * @param activity
         * @param page
         * @param index
         */
        fun jump(
            activity:
            FragmentActivity, @MINEPAGE page: String,
            index: Int = 1
        ) {
            activity.startActivity(Intent(activity, MineActivity::class.java).apply {
                putExtras(Bundle().apply {
                    putSerializable("page", page)
                    putInt("index", index)
                })
            })
        }
    }

    private var page = PERSONAL_CENTER_PAGE
    private var index = 1

    private lateinit var mNavController: NavController

    override fun initView() {
        mNavController = findNavController(R.id.navgraphMine)

//        handleOnBackPressedDispatcher {
//            Timber.d("走了")
//
//            val currentDestination = mNavController.currentDestination
//
//            if (currentDestination != null) {
//                val currentDestinationId = currentDestination.id
//                Timber.d("currentDestinationId $currentDestinationId")
//
////                if (currentDestinationId == R.id.profileFragment) {
////                    NO_CLOSE_PAGE_FLAG = true
////
////                    this@MineActivity.finish()
////                }
//
//                if (rootDestinationId.contains(currentDestinationId)){
//                    NO_CLOSE_PAGE_FLAG = true
//
//                    this@MineActivity.finish()
//                }
//            }
//        }

//        mNavController.addOnDestinationChangedListener { controller, destination, arguments ->
//
//            Timber.d("走了")
//
//            if (NO_CLOSE_PAGE_FLAG) {
//                NO_CLOSE_PAGE_FLAG = false
//                return@addOnDestinationChangedListener
//            }
//
//            val currentDestinationId = destination.id
//
//
//            if (rootDestinationId.contains(currentDestinationId)) {
//                NO_CLOSE_PAGE_FLAG = true
//
//                this.finish()
//            } else {
//                NO_CLOSE_PAGE_FLAG = true
//            }
//        }
    }

    override fun initData() {
        setupBundle()
        handleJumpPage()
    }

    override fun initEvent() {
        LiveEventBus.get<String>(SIGN_OUT).observe(this) {
            PreferencesBusinessHelper.onSignOut()
            onLanding()
        }
    }

    private fun onLanding() {
        ContextCompat.startActivity(
            this,
            Intent(this, LandingActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            },
            null
        )
    }

    private fun setupBundle() {
        val bundle = intent.extras
        page = bundle?.getString("page") ?: PERSONAL_CENTER_PAGE
        index = bundle?.getInt("index") ?: 1
    }

    private fun handleJumpPage() {
        NO_CLOSE_PAGE_FLAG = true

        when (page) {
            RECENTLY_WATCHED_PAGE -> onRecentlyWatched()
            MY_FAVORITES_PAGE -> onMyFavorites()
            MY_TEAM_PAGE -> onMyTeam()
            SWITCH_TEAMS_PAGE -> onSwitchTeams()
            PRIVACY_AND_SECURITY_PAGE -> onPrivacyAndSecurity()
            SET_UP_PAGE -> onSetup()
            ABOUT_PAGE -> onAbout()
            INVITE_FRIENDS_PAGE -> onInviteFriends()
            else -> {

            }
        }
    }

    private fun onRecentlyWatched() {
        mNavController.navigate(
            NavMineDirections.actionGlobalRecentlyWatchedFragment(index)
        )
    }

    private fun onMyFavorites() {
        mNavController.navigate(
            NavMineDirections.actionGlobalMyFavoritesFragment()
        )
    }

    private fun onMyTeam() {
        mNavController.navigate(
            NavMineDirections.actionGlobalMyTeamFragment()
        )
    }

    private fun onSwitchTeams() {
        mNavController.navigate(
            NavMineDirections.actionGlobalSwitchTeamsFragment()
        )
    }

    private fun onPrivacyAndSecurity() {
        mNavController.navigate(
            NavMineDirections.actionGlobalPrivacyAndSecurityFragment()
        )
    }

    private fun onSetup() {
        mNavController.navigate(
            NavMineDirections.actionGlobalSetupFragment()
        )
    }

    private fun onAbout() {
        mNavController.navigate(
            NavMineDirections.actionGlobalAboutFragment()
        )
    }

    private fun onInviteFriends() {
        mNavController.navigate(
            NavMineDirections.actionGlobalInviteFriendsFragment()
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        QQHelper.INSTANCE.handleActivityResult(requestCode, resultCode, data)
    }

}
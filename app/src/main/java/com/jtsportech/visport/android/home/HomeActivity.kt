package com.jtsportech.visport.android.home

import android.content.Intent
import android.os.Bundle
import androidx.annotation.StringDef
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.cloudhearing.android.lib_base.base.BaseBindingVmActivity
import com.dylanc.activityresult.launcher.context
import com.jtsportech.visport.android.NavHomeDirections
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.databinding.ActivityHomeBinding
import com.jtsportech.visport.android.dialog.MatchNoticeDialog


class HomeActivity :
    BaseBindingVmActivity<ActivityHomeBinding, HomeViewModel>(ActivityHomeBinding::inflate) {
    override val transparentStatusBar: Boolean
        get() = false

    override val paddingTopSystemWindowInsets: Boolean
        get() = true


    @StringDef(
        SEARCH_MATCHE_PAGE,
        ALL_COMPETITIONS_PAGE,
    )
    @Target(AnnotationTarget.VALUE_PARAMETER)
    @Retention(AnnotationRetention.SOURCE)
    annotation class HOMEPAGE

    companion object {
        /**
         * 搜索比赛页面
         */
        const val SEARCH_MATCHE_PAGE = "search_matche_page"

        /**
         * 全部比赛页面
         */
        const val ALL_COMPETITIONS_PAGE = "all_competitions_page"

        fun jump(
            activity: FragmentActivity,
            @HOMEPAGE page: String,
            eventName: String = "",
            leagueId: String = ""
        ) {
            activity.startActivity(Intent(activity, HomeActivity::class.java).apply {
                putExtras(Bundle().apply {
                    putSerializable("page", page)
                    putString("eventName", eventName)
                    putString("leagueId", leagueId)
                })
            })
        }
    }

    private var page = SEARCH_MATCHE_PAGE
    private var eventName: String = ""
    private var leagueId: String = ""

    private lateinit var mNavController: NavController

    override fun initView() {
        mNavController = findNavController(R.id.navgraphHome)
    }

    override fun initData() {
        setupBundle()
        handleJumpPage()
    }

    override fun initEvent() {

    }

    private fun setupBundle() {
        val bundle = intent.extras
        page = bundle?.getString("page") ?: SEARCH_MATCHE_PAGE
        eventName = bundle?.getString("eventName") ?: ""
    }

    private fun handleJumpPage() {
        when (page) {
            ALL_COMPETITIONS_PAGE -> onAllCompetitions()
            else -> {

            }
        }
    }

    private fun onAllCompetitions() {
        mNavController.navigate(
            NavHomeDirections.actionGlobalAllCompetitionsFragment(eventName, leagueId)
        )
    }
}
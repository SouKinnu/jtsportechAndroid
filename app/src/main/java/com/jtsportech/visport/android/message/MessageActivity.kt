package com.jtsportech.visport.android.message

import android.content.Intent
import android.os.Bundle
import androidx.annotation.StringDef
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.cloudhearing.android.lib_base.base.BaseBindingVmActivity
import com.jtsportech.visport.android.NavMessageDirections
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.databinding.ActivityMessageBinding
import com.jtsportech.visport.android.mine.MineActivity

class MessageActivity :
    BaseBindingVmActivity<ActivityMessageBinding, MessageViewModel>(ActivityMessageBinding::inflate) {

    override val transparentStatusBar: Boolean
        get() = false

    override val paddingTopSystemWindowInsets: Boolean
        get() = true


    @StringDef(
        MATCH_MESSAGE_PAGE,
        APPLICATION_MESSAGE_PAGE,
        INTERACTIVE_MESSAGE_PAGE,
        SEARCH_MESSAGE_PAGE,
    )
    @Target(AnnotationTarget.VALUE_PARAMETER)
    @Retention(AnnotationRetention.SOURCE)
    annotation class MESSAGEPAGE

    companion object {
        /**
         * 比赛消息
         */
        const val MATCH_MESSAGE_PAGE = "match_message_page"

        /**
         * 应用消息
         */
        const val APPLICATION_MESSAGE_PAGE = "application_message_page"

        /**
         * 互动消息
         */
        const val INTERACTIVE_MESSAGE_PAGE = "interactive_message_page"

        /**
         * 消息搜索
         */
        const val SEARCH_MESSAGE_PAGE = "search_message_page"

        /**
         * 跳转页面
         *
         * @param activity
         * @param page
         */
        fun jump(activity: FragmentActivity, @MESSAGEPAGE page: String) {
            activity.startActivity(Intent(activity, MessageActivity::class.java).apply {
                putExtras(Bundle().apply {
                    putSerializable("page", page)
                })
            })
        }

    }

    private var page = MATCH_MESSAGE_PAGE

    private lateinit var mNavController: NavController

    override fun initView() {
        mNavController = findNavController(R.id.navgraphMessage)
    }

    override fun initData() {
        setupBundle()
        handleJumpPage()
    }

    override fun initEvent() {

    }

    private fun setupBundle() {
        val bundle = intent.extras
        page = bundle?.getString("page") ?: MineActivity.PERSONAL_CENTER_PAGE
    }

    private fun handleJumpPage() {
        when (page) {
            APPLICATION_MESSAGE_PAGE -> onApplicationMessage()
            INTERACTIVE_MESSAGE_PAGE -> onInteractiveMessage()
            SEARCH_MESSAGE_PAGE -> onSearchMessage()
        }
    }

    private fun onInteractiveMessage() {
        mNavController.navigate(
            NavMessageDirections.actionGlobalInteractiveMessageFragment()
        )
    }

    private fun onApplicationMessage() {
        mNavController.navigate(
            NavMessageDirections.actionGlobalApplicationMessageFragment()
        )
    }

    private fun onSearchMessage() {
        mNavController.navigate(
            NavMessageDirections.actionGlobalSearchMessageFragment()
        )
    }

}
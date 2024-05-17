package com.jtsportech.visport.android.dashboard.message

import android.widget.FrameLayout
import com.cloudhearing.android.lib_base.base.BaseBindingVmFragment
import com.cloudhearing.android.lib_base.utils.ANTI_SHAKE_THRESHOLD
import com.cloudhearing.android.lib_base.utils.clickFlow
import com.cloudhearing.android.lib_base.utils.doOnApplyWindowInsets
import com.cloudhearing.android.lib_base.utils.observeState
import com.cloudhearing.android.lib_base.utils.safetyShow
import com.cloudhearing.android.lib_base.utils.throttleFirst
import com.cloudhearing.android.lib_base.utils.toDp
import com.cloudhearing.android.lib_common.utils.SpacesItemDecoration
import com.cloudhearing.android.lib_common.utils.SpacesPlusItemDecoration
import com.cloudhearing.android.lib_common.utils.flow.ChannelEventProcessor
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.components.dialog.SignOutDialog
import com.jtsportech.visport.android.dashboard.DashboardActivity
import com.jtsportech.visport.android.dataSource.message.MessageType
import com.jtsportech.visport.android.databinding.FragmentMessageBinding
import com.jtsportech.visport.android.message.MessageActivity
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class MessageFragment :
    BaseBindingVmFragment<FragmentMessageBinding, MessageViewModel>(FragmentMessageBinding::inflate) {

    private val mClearUnreadDialog: SignOutDialog by lazy {
        SignOutDialog(requireContext()).apply {
            setTitle(getString(R.string.privacy_and_security_prompt))
            setSubtitle(getString(R.string.message_are_you_sure_to_clear_unread_messages))
            setNegativeButtonLable(getString(R.string.alert_cancel))
            setPositiveButtonLable(getString(R.string.alert_sure))
            setPositiveButtonlickListener {
                viewModel.updateMessageStatus()
            }
        }
    }


    private val mMessageTypeAdapter: MessageTypeAdapter by lazy {
        MessageTypeAdapter().apply {
            setOnClickListener {
                handleMessageJump(it)
            }
        }
    }

    override fun initView() {
        binding.apply {
            tvClearUnread.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    mClearUnreadDialog.safetyShow()
                }
                .launchIn(mainScope)

            tvSearch.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    onSearchMessage()
                }
                .launchIn(mainScope)

            rvMessage.apply {
                adapter = mMessageTypeAdapter
                addItemDecoration(
                    SpacesPlusItemDecoration(
                        topSpace = 0,
                        leftSpace = 0,
                        rightSpace = 0,
                        bottomSpace = 20.toDp.toInt()
                    )
                )
                itemAnimator?.changeDuration = 0L
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
            return@doOnApplyWindowInsets
        }
    }

    override fun initData() {

    }

    override fun initEvent() {
        viewModel.run {
            messageListFlow.observeState(this@MessageFragment) {
                Timber.d("消息数量 ${it.size}")
                mMessageTypeAdapter.submitList(it)
            }

            numberOfMessagesFlow.observeState(this@MessageFragment) {
                binding.tvQuantity.text = getString(R.string.message_message_quantity, it)
            }
        }
    }

    private fun handleMessageJump(type: Int) {
        when (type) {
            MessageType.TOURNAMENT_NOTIFICATIONS -> MessageActivity.jump(
                requireActivity(),
                MessageActivity.MATCH_MESSAGE_PAGE
            )

            MessageType.APP_MESSAGES -> MessageActivity.jump(
                requireActivity(),
                MessageActivity.APPLICATION_MESSAGE_PAGE
            )

            MessageType.INTERACTIVE_MESSAGES -> MessageActivity.jump(
                requireActivity(),
                MessageActivity.INTERACTIVE_MESSAGE_PAGE
            )
        }
    }

    private fun onSearchMessage() {
        MessageActivity.jump(
            requireActivity(),
            MessageActivity.SEARCH_MESSAGE_PAGE
        )
    }

}
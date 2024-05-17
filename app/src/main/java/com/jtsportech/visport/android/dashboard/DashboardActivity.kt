package com.jtsportech.visport.android.dashboard

import android.content.Intent
import android.view.KeyEvent
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.cloudhearing.android.lib_base.base.BaseBindingVmActivity
import com.cloudhearing.android.lib_base.utils.ANTI_SHAKE_THRESHOLD
import com.cloudhearing.android.lib_base.utils.SIGN_OUT
import com.cloudhearing.android.lib_base.utils.clickFlow
import com.cloudhearing.android.lib_base.utils.hideCurrentFragment
import com.cloudhearing.android.lib_base.utils.observeEvent
import com.cloudhearing.android.lib_base.utils.safetyShow
import com.cloudhearing.android.lib_base.utils.showFragment
import com.cloudhearing.android.lib_base.utils.systremToast
import com.cloudhearing.android.lib_base.utils.throttleFirst
import com.cloudhearing.android.lib_common.camera.scan.CameraScan
import com.cloudhearing.android.lib_common.utils.GsonUtil
import com.cloudhearing.android.lib_common.utils.mmkv.PreferencesWrapper
import com.jeremyliao.liveeventbus.LiveEventBus
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.components.NavigationBarView
import com.jtsportech.visport.android.components.dialog.SignOutDialog
import com.jtsportech.visport.android.dashboard.home.HomeFragment
import com.jtsportech.visport.android.dashboard.message.MessageFragment
import com.jtsportech.visport.android.dashboard.mine.MineFragment
import com.jtsportech.visport.android.dashboard.parents.ParentsFragment
import com.jtsportech.visport.android.dashboard.scanning.QRCodeScanningActivity
import com.jtsportech.visport.android.dashboard.visitor.VisitorFragment
import com.jtsportech.visport.android.dataSource.UserRole
import com.jtsportech.visport.android.dataSource.home.scanning.QRCodeEntity
import com.jtsportech.visport.android.databinding.ActivityDashboardBinding
import com.jtsportech.visport.android.landing.LandingActivity
import com.jtsportech.visport.android.mine.MineActivity
import com.jtsportech.visport.android.utils.getGreeting
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class DashboardActivity :
    BaseBindingVmActivity<ActivityDashboardBinding, DashboardViewModel>(ActivityDashboardBinding::inflate) {

    companion object {
        private const val SCAN_REQUEST_CODE = 10000
    }

    override val transparentStatusBar: Boolean
        get() = false

    override val paddingTopSystemWindowInsets: Boolean
        get() = false

    override val paddingBottomSystemWindowInsets: Boolean
        get() = true


    private val mHomeFragment: Fragment by lazy {
        when (viewModel.role) {
            UserRole.HEAD_COACH, UserRole.COACH, UserRole.MEMBER -> HomeFragment()
            UserRole.VISITOR -> VisitorFragment()
            UserRole.GUARDER-> ParentsFragment()

            else -> HomeFragment()
        }
    }

    private val mMessageFragment: MessageFragment by lazy {
        MessageFragment()
    }

    private val mMineFragment: MineFragment by lazy {
        MineFragment()
    }

    private val mSignOutDialog: SignOutDialog by lazy {
        SignOutDialog(this).apply {
            setPositiveButtonlickListener {

            }

            setNegativeButtonlickListener {
                onSignOut()
            }
        }
    }

    override fun initView() {
        setupDashboard()
        setupSidebar()
    }

    override fun initData() {

    }

    override fun initEvent() {
        viewModel.run {
            toastFlowEvents.observeEvent(this@DashboardActivity) {
                systremToast(it)
            }
        }

        LiveEventBus.get<String>(SIGN_OUT).observe(this) {
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

    fun openSidebar() {
        binding.drawerlayout.openDrawer(GravityCompat.START)
    }

    fun closeSidebar() {
        binding.drawerlayout.closeDrawer(GravityCompat.START)
    }

    fun isDrawerOpen(): Boolean {
        return binding.drawerlayout.isDrawerOpen(GravityCompat.START)
    }

    private fun setupDashboard() {
        binding.apply {
            nbDashboard.apply {

                when (viewModel.role) {
                    UserRole.HEAD_COACH, UserRole.COACH, UserRole.MEMBER -> {
                        when (currentIndex) {
                            0 -> showFragment(mHomeFragment, flMain.id)
                            1 -> showFragment(mMessageFragment, flMain.id)
                            2 -> showFragment(mMineFragment, flMain.id)
                        }

                        addNavigations(
                            listOf(
                                NavigationBarView.Navigation(
                                    R.drawable.selector_menu_home,
                                    R.string.dashboard_home,
                                    isTextShow = true
                                ),
                                NavigationBarView.Navigation(
                                    R.drawable.selector_menu_message,
                                    R.string.dashboard_message,
                                    isTextShow = true
                                ),
                                NavigationBarView.Navigation(
                                    R.drawable.selector_menu_mine,
                                    R.string.dashboard_mine,
                                    isTextShow = true
                                )
                            )
                        )

                        setOnItemClickListener { index ->
                            hideCurrentFragment()
                            when (index) {
                                0 -> showFragment(mHomeFragment, flMain.id)
                                1 -> showFragment(mMessageFragment, flMain.id)
                                2 -> showFragment(mMineFragment, flMain.id)
                            }
                        }

                    }

                    else -> {
                        // 游客
                        when (currentIndex) {
                            0 -> showFragment(mHomeFragment, flMain.id)
                            1 -> showFragment(mMineFragment, flMain.id)
                        }

                        addNavigations(
                            listOf(
                                NavigationBarView.Navigation(
                                    R.drawable.selector_menu_home,
                                    R.string.dashboard_home,
                                    isTextShow = true
                                ),
                                NavigationBarView.Navigation(
                                    R.drawable.selector_menu_mine,
                                    R.string.dashboard_mine,
                                    isTextShow = true
                                )
                            )
                        )

                        setOnItemClickListener { index ->
                            hideCurrentFragment()
                            when (index) {
                                0 -> showFragment(mHomeFragment, flMain.id)
                                1 -> showFragment(mMineFragment, flMain.id)
                            }
                        }
                    }
                }
            }
        }
    }

    fun getCurrentTabIndex(): Int {
        return binding.nbDashboard.currentIndex
    }

    private fun setupSidebar() {
        binding.apply {
            tvGreeting.text = getGreeting()

            ivScan.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    onQRCodeScanning()
                }
                .launchIn(mainScope)

            tvRecently.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {

                }
                .launchIn(mainScope)

            tvRecentlyAll.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    MineActivity.jump(this@DashboardActivity, MineActivity.RECENTLY_WATCHED_PAGE)
                }
                .launchIn(mainScope)

            sbCompetition.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    MineActivity.jump(this@DashboardActivity, MineActivity.RECENTLY_WATCHED_PAGE, 1)
                }
                .launchIn(mainScope)

            sbTraining.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    MineActivity.jump(this@DashboardActivity, MineActivity.RECENTLY_WATCHED_PAGE, 0)
                }
                .launchIn(mainScope)

            sbVideo.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    MineActivity.jump(this@DashboardActivity, MineActivity.RECENTLY_WATCHED_PAGE, 2)
                }
                .launchIn(mainScope)

            sbSwitchTeams.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    MineActivity.jump(this@DashboardActivity, MineActivity.SWITCH_TEAMS_PAGE)
                }
                .launchIn(mainScope)

            sbTeamInfo.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    MineActivity.jump(this@DashboardActivity, MineActivity.MY_TEAM_PAGE)
                }
                .launchIn(mainScope)

            mbSignOut.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    mSignOutDialog.safetyShow()
                }
                .launchIn(mainScope)
        }


    }

    private fun onSignOut() {
        LiveEventBus.get<String>(SIGN_OUT).post("")
    }

    private fun onQRCodeScanning() {
        startActivityForResult(Intent(this, QRCodeScanningActivity::class.java), SCAN_REQUEST_CODE)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 解决系统侧滑返回和抽屉侧滑显示冲突
            binding.drawerlayout.openDrawer(GravityCompat.START)
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SCAN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    val receiveData = data?.extras?.getString(CameraScan.SCAN_RESULT)
                    val qrCode = GsonUtil.gsonToBean(receiveData, QRCodeEntity::class.java)
                    viewModel.alterQrcode(qrCode.id.orEmpty())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

}
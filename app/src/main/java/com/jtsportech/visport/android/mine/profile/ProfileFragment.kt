package com.jtsportech.visport.android.mine.profile

import android.Manifest
import android.os.Build
import android.view.Gravity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.blankj.utilcode.util.UriUtils
import com.cloudhearing.android.lib_base.base.BaseBindingVmFragment
import com.cloudhearing.android.lib_base.dataSource.LoadState
import com.cloudhearing.android.lib_base.utils.ANTI_SHAKE_THRESHOLD
import com.cloudhearing.android.lib_base.utils.SIGN_OUT
import com.cloudhearing.android.lib_base.utils.clickFlow
import com.cloudhearing.android.lib_base.utils.observeEvent
import com.cloudhearing.android.lib_base.utils.observeState
import com.cloudhearing.android.lib_base.utils.onRequestPermissions
import com.cloudhearing.android.lib_base.utils.safetyShow
import com.cloudhearing.android.lib_base.utils.systremToast
import com.cloudhearing.android.lib_base.utils.throttleFirst
import com.cloudhearing.android.lib_common.image.loadCircleImage
import com.dylanc.activityresult.launcher.PickContentLauncher
import com.dylanc.activityresult.launcher.TakePictureLauncher
import com.jeremyliao.liveeventbus.LiveEventBus
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.components.dialog.SignOutDialog
import com.jtsportech.visport.android.dashboard.popwindow.PopupPicture
import com.jtsportech.visport.android.databinding.FragmentProfileBinding
import com.jtsportech.visport.android.utils.BitmapUtils
import com.jtsportech.visport.android.utils.getDesByUserRole
import com.jtsportech.visport.android.utils.img
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

class ProfileFragment :
    BaseBindingVmFragment<FragmentProfileBinding, ProfileViewModel>(FragmentProfileBinding::inflate) {

    private val mSignOutDialog: SignOutDialog by lazy {
        SignOutDialog(requireActivity()).apply {
            setPositiveButtonlickListener {

            }

            setNegativeButtonlickListener {
                onSignOut()
            }
        }
    }

    private val pickContentLauncher = PickContentLauncher(this)
    private val takePictureLauncher = TakePictureLauncher(this)
    override fun initView() {
        binding.apply {
            apProfile.setOnClickLeftIconListener {
                goBack()
            }

            ivAvatar.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA)
                    } else {
                        arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA
                        )
                    }


                    onRequestPermissions(
                        fragment = this@ProfileFragment,
                        requestReason = getString(R.string.profile_you_ll_need_to_use_your_storage_permissions_to_access_the_album_content),
                        positiveText = getString(R.string.profile_agree),
                        negativeText = getString(R.string.profile_refuse),
                        permissions = permission

                    ) {
                        showPicWindow()
                    }
                }
                .launchIn(mainScope)

            sbUserName.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {

                }
                .launchIn(mainScope)

            sbUserRole.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {

                }
                .launchIn(mainScope)

            sbPhone.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    onChangePhoneNumber()
                }
                .launchIn(mainScope)

            sbCurrentSubject.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {

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

    private fun showPicWindow() {
        PopupPicture(requireActivity(), requireActivity().window).apply {
            setOnAlbumListener {
                dismiss()
                pickPicture()
            }
            setOnCameraListener {
                dismiss()
                takePicture()
            }
            showAtLocation(binding.root, Gravity.BOTTOM, 0, 0)
        }
    }

    override fun initData() {

    }

    override fun initEvent() {
        viewModel.run {
            avatarImageFilePathFlow.observeState(this@ProfileFragment) {
                binding.ivAvatar.loadCircleImage(
                    it.img(),
                    placeHolder = R.drawable.ic_avatar_mine_def
                )
            }

            nicknameFlow.observeState(this@ProfileFragment) {
                binding.sbUserName.setStateInfo(it)
            }

            organizationNameFlow.observeState(this@ProfileFragment) {
                if (it.isEmpty()) {
                    binding.sbCurrentSubject.setStateInfo(getString(R.string.my_team_not))
                } else {
                    binding.sbCurrentSubject.setStateInfo(it)
                }
            }

            userRoleFlow.observeState(this@ProfileFragment) {
                binding.sbUserRole.setStateInfo(
                    it.getDesByUserRole(
                        requireContext(),
                        viewModel.hasInviterFrontUserId
                    )
                )
            }

            phoneNumberFlow.observeState(this@ProfileFragment) {
                binding.sbPhone.setStateInfo(it)
            }

            toastFlowEvents.observeEvent(this@ProfileFragment) {
                systremToast(it)
            }

            loadState.observeEvent(this@ProfileFragment) {
                handleLoadState(it)
            }
        }
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

    private fun pickPicture() = lifecycleScope.launch {
        val uri = pickContentLauncher.launchForImageResult()
        val path = UriUtils.uri2File(uri).absolutePath
        Timber.i("选择的图片 $uri 路径 $path")
        viewModel.uploadAvatarImage(BitmapUtils.compressImage(path))
    }

    private fun takePicture() = lifecycleScope.launch {
        val uri = takePictureLauncher.launchForResult()
        val path = UriUtils.uri2File(uri).absolutePath
        requireActivity().contentResolver.delete(uri, null, null)
        Timber.i("选择的图片 $uri 路径 $path")
        viewModel.uploadAvatarImage(BitmapUtils.compressImage(path))
    }

    private fun onChangePhoneNumber() {
        findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToChangePhoneNumberFragment())
    }

    private fun goBack() {
        requireActivity().finish()
    }

}
package com.jtsportech.visport.android.racedetail.editor

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.cloudhearing.android.lib_base.base.BaseBindingActivity
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.databinding.ActivityImageEditorBinding
import com.zero_code.libEdImage.adapter.BaseAdapter
import com.zero_code.libEdImage.core.EditImageMode
import com.zero_code.libEdImage.databinding.ItemEditImageToolsLayoutBinding
import com.zero_code.libEdImage.loadBitmap
import com.zero_code.libEdImage.saveBitMap
import com.zero_code.libEdImage.setChangeListener
import com.zero_code.libEdImage.ui.text.EditImageText
import com.zero_code.libEdImage.util.StatusBarUtil
import com.zero_code.libEdImage.view.EditImageToolsBean

class ImageEditorActivity : BaseBindingActivity<ActivityImageEditorBinding>(
    ActivityImageEditorBinding::inflate
) {
    companion object {
        private const val checkPermissionCode: Int = 0xf11

        fun jump(
            activity: Activity,
            src: String,
            requestCode: Int,
            onError: ((Exception) -> Unit)? = null
        ) {
            val intent = Intent(activity, ImageEditorActivity::class.java)
            if (src.isBlank()) {
                onError?.invoke(RuntimeException("资源错误"))
            }
            if (src.startsWith("http") || src.startsWith("https")) {
                onError?.invoke(RuntimeException("资源错误，目前不支持网络图片"))
                return
            }
            intent.putExtra("data", src)
            activity.startActivityForResult(intent, requestCode)
        }
    }

    private var rotate = 0f
    private var isVerticalMirror = false
    private var isHorizontalMirror = false

    /**
     * 资源
     */
    private val src by lazy {
        intent.getStringExtra("data")
    }

    /**
     * 选中的工具下标
     */
    private var selectedToolsIndex = -1
    private var selectedControlIndex = -1


    /**
     * 工具数据
     */
    private val toolsList = arrayListOf(
        EditImageToolsBean(                  // 文字
            intArrayOf(
                R.drawable.icon_text_pre, R.drawable.icon_text_nor
            )
        ), EditImageToolsBean(                     // 画笔
            intArrayOf(
                R.drawable.icon_line_pre, R.drawable.icon_line_nor
            )
        ), EditImageToolsBean(                  // 剪裁
            intArrayOf(
                R.drawable.icon_select_pre, R.drawable.icon_select_nor
            )
        )
    )

    private val controlList = arrayListOf(
        EditImageToolsBean(                  // 回退
            intArrayOf(
                R.drawable.icon_delete_white, R.drawable.icon_delete_white
            )
        ), EditImageToolsBean(                  // 取消
            intArrayOf(
                R.drawable.icon_cancer, R.drawable.icon_cancer
            )
        ), EditImageToolsBean(                  // 完成
            intArrayOf(
                R.drawable.icon_sure, R.drawable.icon_sure
            )
        )
    )


    /**
     * 编辑工具的适配器
     */
    private val toolsAdapter by lazy {
        BaseAdapter<EditImageToolsBean, ItemEditImageToolsLayoutBinding>(
            R.layout.item_edit_image_tools_layout, toolsList
        ).apply {
            onBind { itemBingding, position, data ->

                itemBingding.itemToolsImage.setImageResource(data.getImage())

                itemBingding.root.setOnClickListener {
                    if (binding.includeColors.cgColors.visibility == View.VISIBLE)
                        binding.includeColors.cgColors.visibility = View.GONE
                    binding.edTools.visibility = View.VISIBLE
                    when (position) {
                        0 -> {                           // 文字
                            binding.mEditView.mode = EditImageMode.NONE
                            binding.mEditView.addStickerText(
                                EditImageText(
                                    "文字", Color.RED
                                )
                            )
                        }

                        1 -> {                           // 涂鸦
                            binding.mEditView.mode = EditImageMode.DOODLE
                            binding.includeColors.cgColors.visibility = View.VISIBLE
                            if (binding.includeColors.cgColors.checkedRadioButtonId == null) binding.includeColors.cgColors.checkColor =
                                resources.getColor(com.zero_code.libEdImage.R.color.image_color_red)
                        }

                        2 -> {                           // 剪裁
                            binding.mEditView.mode = EditImageMode.CLIP
                            binding.edTools.visibility = View.GONE
                            binding.clipView.visibility = View.VISIBLE
//                            binding.mEditView?.resetClip()
                        }
                    }
                    if (selectedToolsIndex != -1) toolsList[selectedToolsIndex].isSelected = false
                    data.isSelected = true
                    selectedToolsIndex = position
                    notifyDataSetChanged()
                }
            }
        }
    }

    private val controlAdapter by lazy {
        BaseAdapter<EditImageToolsBean, ItemEditImageToolsLayoutBinding>(
            R.layout.item_edit_image_tools_layout, controlList
        ).apply {
            onBind { itemBingding, position, data ->
                itemBingding.itemToolsImage.setImageResource(data.getImage())
                itemBingding.root.setOnClickListener {
//                    if (binding.includeColors.cgColors.visibility == View.VISIBLE)
//                        binding.includeColors.cgColors.visibility = View.GONE
//                    binding.edTools.visibility = View.VISIBLE
                    when (position) {
                        0 -> {                              //设置返回上一步
                            val mode: EditImageMode? = binding.mEditView.mode
                            if (mode === EditImageMode.DOODLE) {
                                binding.mEditView.undoDoodle()
                            } else if (mode === EditImageMode.MOSAIC) {
                                binding.mEditView.undoMosaic()
                            } else if (mode == EditImageMode.ARROWS) {
                                binding.mEditView.clearLastArrows()
                            }
                        }

                        1 -> {
                            setResult(Activity.RESULT_CANCELED, intent)
                            finish()
                        }

                        2 -> {
                            val saveBitmap = binding.mEditView.saveBitmap()
                            val path = saveBitMap(
                                bitmap = saveBitmap,
                                name = System.currentTimeMillis().toString(),
                                description = "batchat_app_image"
                            )
                            if (path.isNullOrBlank()) {
                                throw java.lang.RuntimeException("保存文件错误！")
                            }
                            saveBitmap.recycle()
                            intent.putExtra("data", path)
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        }
                    }
                    if (selectedControlIndex != -1) controlList[selectedControlIndex].isSelected =
                        false
                    data.isSelected = true
                    selectedControlIndex = position
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        if (src.isNullOrBlank()) throw RuntimeException("请指定资源和返回标识！")
        checkPermission()
        initView()
    }

    /**
     * 检查权限
     */
    private fun checkPermission() {
        //申请权限（Android6.0及以上 需要动态申请危险权限）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                )
                intent.setData(Uri.parse("package:$packageName"))
                startActivity(intent)
            }
        } else if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), checkPermissionCode
            )
        } else {
            initView()
        }
    }

    /**
     * 权限申请回掉
     */
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == checkPermissionCode && Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            if (permissions.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initView()
            } else {
                Toast.makeText(this, "权限已拒绝", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }


    /**
     * 初始化View
     */
    private fun initView() {
        //初始化状态栏
//        changeStatusBarColor(
//            resources.getColor(com.zero_code.libEdImage.R.color.ed_image_title_bar_bg_color),
//            false
//        )
//        StatusBarUtil.setTransparent(this)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LOW_PROFILE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        //初始化工具栏
        binding.editImageToolsList.apply {
            layoutManager =
                LinearLayoutManager(this@ImageEditorActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = toolsAdapter
        }
        binding.editImageControlList.apply {
            layoutManager =
                LinearLayoutManager(this@ImageEditorActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = controlAdapter
        }
        //适配状态兰入侵
//        binding.statusBar.apply {
//            val lp = layoutParams
//            lp.height = StatusBarUtil.getStatusBarHeight(context)
//            layoutParams = lp
//        }
        //初始化编辑View

        val bitmap = loadBitmap(src!!)
        binding.mEditView.setImageBitmap(bitmap)

        //设置涂鸦颜色选择监听
        binding.includeColors.cgColors.setChangeListener {
            binding.mEditView.setPenColor(binding.includeColors.cgColors.checkColor)
        }

        /**
         * 确认
         */
        binding.btnEdOk.setOnClickListener {
            binding.edTools.visibility = View.VISIBLE
            binding.clipView.visibility = View.GONE
            isHorizontalMirror = false
            isVerticalMirror = false
            rotate = 0f
            binding.mEditView.doClip()
        }
        /**
         * 重置
         * TODO 在多次进行旋转 翻转后，重置有一定几率不能恢复至初始状态
         */
        binding.edTvRestore.setOnClickListener {
            if (isHorizontalMirror) binding.mEditView.doHorizontalMirror()
            if (isVerticalMirror) binding.mEditView.doVerticalMirror()
            isHorizontalMirror = false
            isVerticalMirror = false
            binding.mEditView.doRotate(-rotate)
            rotate = 0f
            binding.mEditView.resetClip()
        }
        /**
         * 关闭
         */
        binding.btnEdClose.setOnClickListener {
            binding.edTools.visibility = View.VISIBLE
            binding.clipView.visibility = View.GONE
            binding.mEditView.cancelClip()
        }
        /**
         * left 旋转
         */
        binding.edImageRotateLeft.setOnClickListener {
            rotate += -90
            binding.mEditView.doRotate(-90f)
        }
        /**
         * Right 旋转
         */
        binding.edImageRotateRight.setOnClickListener {
            rotate += 90
            binding.mEditView.doRotate(90f)
        }
        /**
         * 向上镜面反转
         */
        binding.edImageMirrorUp.setOnClickListener {
            binding.mEditView.doVerticalMirror()
            isVerticalMirror = !isVerticalMirror
        }
        /**
         * 向左镜像反转
         */
        binding.edImageMirrorLeft.setOnClickListener {
            binding.mEditView.doHorizontalMirror()
            isHorizontalMirror = !isHorizontalMirror
        }
        //初始化 剪裁相关得View
        initClipView()
    }


    override fun onDestroy() {
        binding.mEditView.getImage()?.recycle()
        System.gc()
        System.gc()
        super.onDestroy()
    }

    /**
     * 初始化 剪裁相关得View
     */
    private fun initClipView() {

    }


    /**
     * 改变状态栏颜色
     * @param color
     * @param isCilp 是否需要padding状态栏高度，如果需要自己实现状态栏逻辑就传入false
     * @param dl 如果要兼容DrawerLayout则传入
     */
    private fun changeStatusBarColor(
        @ColorInt color: Int,
        isCilp: Boolean = true,
        dl: androidx.drawerlayout.widget.DrawerLayout? = null
    ) {
        //如果dl不为空则都使用半透明，因为dl可能拉出白色背景
        if (dl != null) {
            StatusBarUtil.setStatusBarLightMode(this, false)
            StatusBarUtil.setColorTranslucentForDrawerLayout(this, dl, color)
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //如果版本号大于等于M，则必然可以修改状态栏颜色
            StatusBarUtil.setColor(this, color, isCilp)
            StatusBarUtil.setStatusBarLightModeByColor(this, color)
            return
        }
        //这里处理的是版本号低于M的系统
        //判断设置的颜色是深色还是浅色，然后设置statusBar的文字颜色
        val status = StatusBarUtil.setStatusBarLightModeByColor(this, color)
        //fixme 如果手机机型不能改状态栏颜色就不允许开启沉浸式,如果业务需求请修改代码逻辑
        if (!status) {//如果状态栏的文字颜色改变失败了则设置为半透明
            StatusBarUtil.setColorTranslucent(this, color, isCilp)
        } else {//如果状态栏的文字颜色改变成功了则设置为全透明
            StatusBarUtil.setColor(this, color, isCilp)
            //改变了状态栏后需要重新设置一下状态栏文字颜色
            StatusBarUtil.setStatusBarLightModeByColor(this, color)
        }

    }
}
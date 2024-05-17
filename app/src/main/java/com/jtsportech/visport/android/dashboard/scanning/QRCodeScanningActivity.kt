package com.jtsportech.visport.android.dashboard.scanning

import android.content.Intent
import android.graphics.Point
import android.widget.ImageView
import com.cloudhearing.android.lib_common.camera.scan.AnalyzeResult
import com.cloudhearing.android.lib_common.camera.scan.CameraScan
import com.cloudhearing.android.lib_common.camera.scan.util.PointUtils
import com.cloudhearing.android.lib_common.mlkit.vision.barcode.QRCodeCameraScanActivity
import com.google.mlkit.vision.barcode.common.Barcode
import com.jtsportech.visport.android.R
import timber.log.Timber

class QRCodeScanningActivity : QRCodeCameraScanActivity() {

    private lateinit var ivResult: ImageView
    private lateinit var ivBack: ImageView

    override fun initUI() {
        super.initUI()

        ivResult = findViewById(R.id.ivResult)
        ivBack = findViewById(R.id.ivBack)

        ivBack.setOnClickListener {
            if (viewfinderView.isShowPoints) {//如果是结果点显示时，用户点击了返回键，则认为是取消选择当前结果，重新开始扫码
                ivResult.setImageResource(0)
                viewfinderView.showScanner()
                cameraScan.setAnalyzeImage(true)
            } else {
                finish()
            }
        }
    }

    override fun initCameraScan(cameraScan: CameraScan<MutableList<Barcode>>) {
        super.initCameraScan(cameraScan)
        cameraScan.setPlayBeep(true)
            .setVibrate(true)
            .bindFlashlightView(ivFlashlight)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_qrcode_scanning
    }

    override fun onBackPressed() {
        if (viewfinderView.isShowPoints) {//如果是结果点显示时，用户点击了返回键，则认为是取消选择当前结果，重新开始扫码
            ivResult.setImageResource(0)
            viewfinderView.showScanner()
            cameraScan.setAnalyzeImage(true)
            return
        }
        super.onBackPressed()
    }

    override fun onScanResultCallback(result: AnalyzeResult<MutableList<Barcode>>) {

        cameraScan.setAnalyzeImage(false)
        val results = result.result

        //取预览当前帧图片并显示，为结果点提供参照
        ivResult.setImageBitmap(previewView.bitmap)
        val points = ArrayList<Point>()
        var width = result.bitmapWidth
        var height = result.bitmapHeight
        for (barcode in results) {
            barcode.boundingBox?.let { box ->
                //将实际的结果中心点坐标转换成界面预览的坐标
                val point = PointUtils.transform(
                    box.centerX(),
                    box.centerY(),
                    width,
                    height,
                    viewfinderView.width,
                    viewfinderView.height
                )
                points.add(point)
            }
        }
        //设置Item点击监听
        viewfinderView.setOnItemClickListener {
            Timber.d("scan result:${results[it].displayValue}")
            //显示点击Item将所在位置扫码识别的结果返回
            val intent = Intent()
            intent.putExtra(CameraScan.SCAN_RESULT, results[it].displayValue)
            setResult(RESULT_OK, intent)
            finish()

            /*
                显示结果后，如果需要继续扫码，则可以继续分析图像
             */
//            ivResult.setImageResource(0)
//            viewfinderView.showScanner()
//            cameraScan.setAnalyzeImage(true)
        }
        //显示结果点信息
        viewfinderView.showResultPoints(points)


        if (results.size == 1) {//只有一个结果直接返回
            Timber.d("scan result:${results[0].displayValue}")
            val intent = Intent()
            intent.putExtra(CameraScan.SCAN_RESULT, results[0].displayValue)
            setResult(RESULT_OK, intent)
            finish()
        }

    }

}
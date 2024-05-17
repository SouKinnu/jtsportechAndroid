package com.jtsportech.visport.android.components.dialog

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Environment
import androidx.core.content.ContextCompat
import com.cloudhearing.android.lib_base.base.BaseBindingDialog
import com.cloudhearing.android.lib_base.utils.toDp
import com.cloudhearing.android.lib_common.utils.ThreadPoolManager
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.databinding.DialogInvitecodeBinding
import com.jtsportech.visport.android.utils.BitmapUtils
import com.jtsportech.visport.android.utils.QRCodeGenerator
import com.jtsportech.visport.android.utils.saveToAlbum
import timber.log.Timber
import java.util.Date

/**
 * Author: BenChen
 * Date: 2024/02/26 19:28
 * Email:chenxiaobin@cloudhearing.cn
 */
class InvitecodeDialog(context: Context) : BaseBindingDialog<DialogInvitecodeBinding>(
    context,
    inflate = DialogInvitecodeBinding::inflate,
    widthPercentage = 0.85f
) {

    private var mLeftButtonlickListener: (() -> Unit)? = null
    private var mRightButtonlickListener: ((String) -> Unit)? = null

    private var mQrCodeBitmap: Bitmap? = null

    fun setLeftButtonlickListener(listener: () -> Unit) {
        mLeftButtonlickListener = listener
    }

    fun setRightButtonlickListener(listener: (String) -> Unit) {
        mRightButtonlickListener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.apply {
            ivClose.setOnClickListener {
                dismiss()
            }

            tvSaveAlbum.setOnClickListener {
                dismiss()
                generateQrCode(false)
                mLeftButtonlickListener?.invoke()
            }

            tvShareFriend.setOnClickListener {
                dismiss()
                generateQrCode()
            }
        }
    }

    fun setCode(code: String) {
        mQrCodeBitmap = QRCodeGenerator.createQRImage(
            code,
            40.toDp.toInt(),
            40.toDp.toInt()
        )

        binding.tvInviteCode.text = code

        binding.ivQr.setImageBitmap(mQrCodeBitmap)
    }

    private fun generateQrCode(isSharedFriend: Boolean = true) {
        val bgBitmap = BitmapUtils.drawableToBitmap(
            ContextCompat.getDrawable(
                context,
                R.drawable.img_invites
            )!!
        )!!

        val codeBitmap = BitmapUtils.textToBitmap(
            binding.tvInviteCode.text.toString(), 16, Color.WHITE,
            Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        )!!

        val mergeBitmap =
            BitmapUtils.mergeQrCodeBitmap(
                bgBitmap,
                mQrCodeBitmap!!,
                codeBitmap
            )


        ThreadPoolManager.getThreadPool().submit {
            val fileName = "qrcode_${Date().time}.png"
            val path =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + "/" + "$fileName"
            mergeBitmap.saveToAlbum(context, "$fileName", "")
//            BitmapUtils.saveBitmap("qrcode.png", mergeBitmap, context)

            Timber.d("走了")
            if (isSharedFriend) {
//                mRightButtonlickListener?.invoke(context.filesDir.toString() + "/images/"+"qrcode.png")
                mRightButtonlickListener?.invoke(path)
            }
        }
    }
}
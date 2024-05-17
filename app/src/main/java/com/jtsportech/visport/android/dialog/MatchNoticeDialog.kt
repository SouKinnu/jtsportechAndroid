package com.jtsportech.visport.android.dialog

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.cloudhearing.android.lib_base.utils.observeState
import com.jtsportech.visport.android.databinding.DialogMatchNoticeBinding
import com.jtsportech.visport.android.dialog.base.BindingDialog
import com.jtsportech.visport.android.dialog.viewmodel.DialogViewModel


class MatchNoticeDialog(context: Context, lifecycleOwner: LifecycleOwner) :
    BindingDialog<DialogMatchNoticeBinding>(
        context,
        inflate = DialogMatchNoticeBinding::inflate,
        width = 0.85f
    ) {

    init {
        val vm = DialogViewModel()
        vm.getMessageNotice()
        vm.messageNoticeStateFlow.observeState(lifecycleOwner) {
            if (it.isNotEmpty()) {
                binding.content.text = it[0].msgContent
            }

        }

        binding.go.setOnClickListener {
            dismiss()
        }
    }
}
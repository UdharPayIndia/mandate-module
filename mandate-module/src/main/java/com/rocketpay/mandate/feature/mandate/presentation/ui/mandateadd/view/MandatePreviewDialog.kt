package com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.view

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import com.rocketpay.mandate.databinding.DialogMandatePreviewRpBinding
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.viewmodel.MandatePreviewDialogVM

internal class MandatePreviewDialog(
    context: Context,
    mandatePreviewDialogVM: MandatePreviewDialogVM
) : Dialog(context) {

    init {
        val dialogMandatePreviewNewBinding = DialogMandatePreviewRpBinding.inflate(layoutInflater)
        dialogMandatePreviewNewBinding.vm = mandatePreviewDialogVM
        setContentView(dialogMandatePreviewNewBinding.root)

        val layoutParams: WindowManager.LayoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(window?.attributes)
        layoutParams.width = (layoutParams.width * 0.7).toInt()
        window?.attributes = layoutParams
    }
}

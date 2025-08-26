package com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.setWidthPercent
import com.rocketpay.mandate.databinding.ProgressDialogRpBinding

internal class ProgressDialog(context: Context, progressDialogVM: ProgressDialogVM) : Dialog(context) {

    private val progressDialogBinding = ProgressDialogRpBinding.inflate(layoutInflater)

    init {
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        setCancelable(false)
        progressDialogBinding.vm = progressDialogVM
        setContentView(progressDialogBinding.root)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWidthPercent(70)
    }

}

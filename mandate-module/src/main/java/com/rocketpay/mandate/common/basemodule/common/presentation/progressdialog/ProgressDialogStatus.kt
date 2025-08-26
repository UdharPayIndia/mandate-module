package com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog

internal sealed class ProgressDialogStatus(val value: String) {
    object Progress : ProgressDialogStatus("progress")
    object Success : ProgressDialogStatus("success")
    object Error : ProgressDialogStatus("error")
    object Init : ProgressDialogStatus("init")

    companion object {
        val map by lazy {
            mapOf(
                "progress" to Progress,
                "success" to Success,
                "error" to Error,
                "init" to Init
            )
        }
    }
}

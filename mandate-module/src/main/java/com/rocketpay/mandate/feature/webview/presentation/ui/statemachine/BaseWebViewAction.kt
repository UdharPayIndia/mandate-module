package com.rocketpay.mandate.feature.webview.presentation.ui.statemachine

import androidx.annotation.Keep

internal sealed class BaseWebViewAction(val value: String) {
    object CallUs : BaseWebViewAction("call_us")
    object ChatWithUs : BaseWebViewAction("chat_with_us")
    object SendEvent : BaseWebViewAction("send_event")
    object ShowStory : BaseWebViewAction("show_story")
    object ShowVideo : BaseWebViewAction("show_video")
    object Close : BaseWebViewAction("close")
    object GetReportClick: BaseWebViewAction("get_report_click")

    companion object {
        val map by lazy {
            mapOf(
                "call_us" to CallUs,
                "chat_with_us" to ChatWithUs,
                "send_event" to SendEvent,
                "show_story" to ShowStory,
                "show_video" to ShowVideo,
                "close" to Close,
                "get_report_click" to GetReportClick
            )
        }

        fun get(action: String): BaseWebViewAction? {
            return map[action]
        }
    }
}

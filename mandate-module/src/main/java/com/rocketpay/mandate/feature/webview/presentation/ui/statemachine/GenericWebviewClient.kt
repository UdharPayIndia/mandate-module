package com.rocketpay.mandate.feature.webview.presentation.ui.statemachine

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import java.net.URISyntaxException

internal class GenericWebviewClient(val context: Context) : WebViewClient() {

    override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onReceivedError(view: WebView, req: WebResourceRequest, rerr: WebResourceError) {
        onReceivedError(view, rerr.errorCode, rerr.description.toString(), req.url.toString())
    }

    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        val url = url ?: ""
        if (url.startsWith("http") || url.startsWith("https")) {
            return false
        } else if (url.startsWith("intent")) {
            try {
                val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                val fallbackUrl = intent.getStringExtra("browser_fallback_url");
                if (fallbackUrl != null) {
                    view?.loadUrl(fallbackUrl)
                    return true
                }
            } catch (e: URISyntaxException) { //not an intent uri
            }
        }else if (url.startsWith("tel:")) {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse(url))
            context.startActivity(intent)
            return true
        }
        return false
    }
}

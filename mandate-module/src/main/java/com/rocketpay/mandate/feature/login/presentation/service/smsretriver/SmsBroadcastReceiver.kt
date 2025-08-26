package com.rocketpay.mandate.feature.login.presentation.service.smsretriver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import java.util.regex.Pattern

internal class SmsBroadcastReceiver(private val listener: SmsRetrieverListener) : BroadcastReceiver() {

    companion object {
        const val otpExtractionRegex = "^\\d+(?=\\sis)|(?<=is\\s)\\d+?\$"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent?.action) {
            val extras = intent.extras ?: return
            val status = extras.get(SmsRetriever.EXTRA_STATUS) as? Status ?: return
            when (status.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val sms = extras.getString(SmsRetriever.EXTRA_SMS_MESSAGE)
                    if (!sms.isNullOrEmpty()) {
                        listener.onSmsReceived(extractOtp(sms))
                    }
                }
            }
        }
    }

    private fun extractOtp(sms: String): String? {
        val pattern = Pattern.compile(otpExtractionRegex)
        val matcher = pattern.matcher(sms)
        return if (matcher.find()) {
            matcher.group(0)
        } else {
            null
        }
    }
}

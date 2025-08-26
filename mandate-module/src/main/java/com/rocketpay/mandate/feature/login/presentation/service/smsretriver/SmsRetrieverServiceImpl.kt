package com.rocketpay.mandate.feature.login.presentation.service.smsretriver

import android.content.Context
import android.content.IntentFilter
import android.os.Build
import com.google.android.gms.auth.api.phone.SmsRetriever

internal class SmsRetrieverServiceImpl(val context: Context, val listener: SmsRetrieverListener): SmsRetrieverService {

    private var smsBroadcastReceiver = SmsBroadcastReceiver(listener)
    private var isSmsBroadcastReceiverRegistered: Boolean = false

    companion object {
        private const val ACTION_PERMISSION_SMS_RECEIVER = "com.google.android.gms.auth.api.phone.SMS_RETRIEVED"
    }

    override fun startService() {
        val client = SmsRetriever.getClient(context)
        client.startSmsRetriever()
        val filter = IntentFilter(ACTION_PERMISSION_SMS_RECEIVER)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.registerReceiver(smsBroadcastReceiver, filter, Context.RECEIVER_EXPORTED)
        }else{
            context.registerReceiver(smsBroadcastReceiver, filter)
        }
        isSmsBroadcastReceiverRegistered = true
    }

    override fun stopService() {
        if (isSmsBroadcastReceiverRegistered) {
            isSmsBroadcastReceiverRegistered = false
            context.unregisterReceiver(smsBroadcastReceiver)
        }
    }
}

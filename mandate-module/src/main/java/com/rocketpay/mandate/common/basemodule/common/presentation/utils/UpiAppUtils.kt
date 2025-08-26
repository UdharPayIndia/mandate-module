package com.rocketpay.mandate.common.basemodule.common.presentation.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.rocketpay.mandate.R

internal object UpiAppUtils {

    fun openUpiApp(context: Context, upiIntent: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            val upiLink = if(upiIntent.startsWith("intent")){
                upiIntent.replaceFirst("intent", "upi")
            }else{
                upiIntent
            }
            intent.data = Uri.parse(upiLink)
            context.startActivity(intent)
        } catch (e: Exception) {
            try{
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(upiIntent)
                context.startActivity(intent)
            }catch (e: java.lang.Exception) {
                Toast.makeText(
                    context,
                    context.getString(R.string.rp_ivr_invoice_something_went_wrong),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
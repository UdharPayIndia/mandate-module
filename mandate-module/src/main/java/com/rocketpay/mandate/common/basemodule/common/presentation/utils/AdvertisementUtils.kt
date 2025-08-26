package com.rocketpay.mandate.common.basemodule.common.presentation.utils

import android.content.Context
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal object AdvertisementUtils {

    suspend fun getAdvertisementId(context: Context): String? {
        return withContext(Dispatchers.IO) {
            try {
                AdvertisingIdClient.getAdvertisingIdInfo(context).id
            } catch (e: Exception) {
                null
            }
        }
    }
}
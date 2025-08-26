package com.rocketpay.mandate.main.init.network

import com.rocketpay.mandate.R
import com.udharpay.core.networkmanager.data.NetworkRequest
import com.udharpay.core.networkmanager.domain.entities.GenericErrorResponse
import com.udharpay.core.networkmanager.domain.entities.Outcome
import com.udharpay.core.networkmanager.domain.interceptor.ResponseInterceptor
import com.rocketpay.mandate.common.basemodule.common.data.network.NetworkUtils
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal class NetworkResponseInterceptor : ResponseInterceptor {
    override suspend fun <T> intercept(
        outcome: Outcome<T>,
        networkRequest: NetworkRequest
    ): Outcome<T> {
        return if (!NetworkUtils.isNetworkAvailable()) {
            Outcome.Error(GenericErrorResponse("NO_NETWORK",
                ResourceManager.getInstance().getString(R.string.rp_no_internet)))
        } else {
            outcome
        }
    }
}

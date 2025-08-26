package com.rocketpay.mandate.common.basemodule.common.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import com.rocketpay.mandate.main.init.MandateManager

internal object NetworkUtils: LiveData<Boolean>() {

    private val networkRequest = NetworkRequest.Builder()
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .build()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            postValue(true)
        }

        override fun onUnavailable() {
            super.onUnavailable()
            postValue(false)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            postValue(getActiveNetworkAvailable())
        }
    }

    private fun getActiveNetworkAvailable(): Boolean{
        var connectivityManager: ConnectivityManager = MandateManager.getInstance().getContext()
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager?.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    public override fun onActive() {
        var connectivityManager: ConnectivityManager = MandateManager.getInstance().getContext()
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        postValue(isNetworkAvailable())
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
        super.onActive()
    }

    override fun onInactive() {
        var connectivityManager: ConnectivityManager = MandateManager.getInstance().getContext()
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.unregisterNetworkCallback(networkCallback)
        super.onInactive()
    }

    fun isNetworkAvailable(): Boolean {
        var connectivityManager: ConnectivityManager = MandateManager.getInstance().getContext()
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }
}

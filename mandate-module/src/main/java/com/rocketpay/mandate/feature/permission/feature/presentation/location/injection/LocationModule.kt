package com.rocketpay.mandate.feature.permission.feature.presentation.location.injection

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.rocketpay.mandate.feature.permission.feature.presentation.location.LocationExtractor
import com.rocketpay.mandate.feature.permission.feature.presentation.location.LocationRegister
import dagger.Module
import dagger.Provides

@Module
internal open class LocationModule(val context: Context) {

    @Provides
    internal fun provideLocationExtractor(): LocationExtractor {
        return LocationExtractor(context)
    }

    @Provides
    internal fun provideLocationRegister(): LocationRegister {
        return LocationRegister()
    }

    @Provides
    internal fun provideFusedLocationProviderClient(): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }
}

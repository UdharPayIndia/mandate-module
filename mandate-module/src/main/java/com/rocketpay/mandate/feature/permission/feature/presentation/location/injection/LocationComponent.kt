package com.rocketpay.mandate.feature.permission.feature.presentation.location.injection

import android.content.Context
import com.rocketpay.mandate.feature.permission.feature.presentation.location.LocationManager
import dagger.Component

@Component(modules = [LocationModule::class])
internal interface LocationComponent {

    fun inject(locationManager: LocationManager)

    object Initializer {
        fun init(context: Context): LocationComponent {
            val locationModule = LocationModule(context)
            return DaggerLocationComponent.builder()
                .locationModule(locationModule).build()
        }
    }
}

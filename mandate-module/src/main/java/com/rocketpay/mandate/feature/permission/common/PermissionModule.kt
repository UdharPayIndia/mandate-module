package com.rocketpay.mandate.feature.permission.common


import com.rocketpay.mandate.feature.permission.feature.data.PermissionRepositoryImpl
import com.rocketpay.mandate.feature.permission.feature.data.local.datastore.PermissionDataStore
import com.rocketpay.mandate.feature.permission.feature.domain.repositories.PermissionRepository
import com.rocketpay.mandate.feature.permission.feature.domain.usecases.PermissionUseCase
import com.rocketpay.mandate.feature.permission.feature.presentation.location.LocationManager
import com.rocketpay.mandate.common.basemodule.common.data.cache.DataStore
import com.rocketpay.mandate.main.init.MandateManager
import dagger.Module
import dagger.Provides

@Module
internal open class PermissionModule {

    @Provides
    internal fun providePermissionUseCase(permissionRepository: PermissionRepository): PermissionUseCase {
        return PermissionUseCase(permissionRepository)
    }

    @Provides
    internal fun providePermissionRepository(permissionDataStore: PermissionDataStore): PermissionRepository {
        return PermissionRepositoryImpl(permissionDataStore)
    }

    @Provides
    internal fun providePermissionDataStore(): PermissionDataStore {
        return PermissionDataStore(DataStore(MandateManager.getInstance().getContext(), "permission"))
    }

    @Provides
    internal fun provideLocationManager(): LocationManager {
        return LocationManager(MandateManager.getInstance().getContext())
    }
}

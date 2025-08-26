package com.rocketpay.mandate.main.init.sync

import androidx.lifecycle.asFlow
import com.rocketpay.mandate.main.GlobalState
import com.udharpay.core.syncmanager.domain.enities.DefaultSyncConstraint
import com.rocketpay.mandate.common.basemodule.common.data.network.NetworkUtils
import com.udharpay.kernel.kernelcommon.register.Register
import kotlinx.coroutines.flow.Flow

internal class SyncConstraintObserverRegister: Register<String, Flow<Any>>() {

    init {
        register(DefaultSyncConstraint.LOGIN, GlobalState.isLogin)
        register(DefaultSyncConstraint.NETWORK, NetworkUtils.asFlow())
    }
}

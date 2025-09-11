package com.rocketpay.mandate.feature.webview.presentation.injection


import dagger.Module
import dagger.Provides

@Module
internal open class GenericWebviewModule {

    @Provides
    fun provideSimpleStateMachineFactory(): GenericWebviewStateMachineFactory {
        return GenericWebviewStateMachineFactory()
    }
}

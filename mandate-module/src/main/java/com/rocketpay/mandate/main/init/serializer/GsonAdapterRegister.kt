package com.rocketpay.mandate.main.init.serializer

import com.udharpay.kernel.kernelcommon.register.Register
import java.lang.reflect.Type

internal class GsonAdapterRegister: Register<Type, Any>() {
    init {
        register(Animal::class.java, AnimalJsonAdapter())
        //...
    }
}

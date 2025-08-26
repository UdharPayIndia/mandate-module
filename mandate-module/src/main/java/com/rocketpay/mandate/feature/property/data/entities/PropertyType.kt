package com.rocketpay.mandate.feature.property.data.entities

internal sealed class PropertyType(val value: Int){
    object Merchant: PropertyType(0)
    object User: PropertyType(1)
    object Device: PropertyType(2)
    object Miscellaneous: PropertyType(3)
}
package com.rocketpay.mandate.common.mvistatemachine.liveevent

import androidx.annotation.MainThread
import androidx.collection.ArraySet
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer

internal class LiveEvent<T> : MediatorLiveData<T>() {

    private val values = ArraySet<T?>()

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        val wrapper = ObserverWrapper(observer, values)
        super.observe(owner, wrapper)
    }

    @MainThread
    override fun setValue(t: T?) {
        values.add(t)
        super.setValue(t)
    }

    override fun postValue(t: T?) {
        values.add(t)
        super.postValue(t)
    }

    private class ObserverWrapper<T>(val observer: Observer<in T>, val values: ArraySet<T?>) : Observer<T> {
        override fun onChanged(t: T) {
            val tempValues = ArraySet(values)
            values.clear()
            tempValues.forEach {
                it?.let { event ->
                    observer.onChanged(event)
                }
            }
        }
    }
}
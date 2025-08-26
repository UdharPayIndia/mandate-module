package com.rocketpay.mandate.common.basemodule.common.eventbus.activityresultcallback

object FragmentResultBus {

    private val observers = HashMap<String, (Any?) -> Unit>()

    fun register(event: String, fragmentResultCallBack: (Any?) -> Unit) {
        observers[event] = fragmentResultCallBack
    }

    fun unRegister(event: String) {
        if (observers.containsKey(event)) {
            observers.remove(event)
        }
    }

    fun fire(event: String, any: Any?) {
        observers[event]?.let {
            it(any)
        }
    }
}

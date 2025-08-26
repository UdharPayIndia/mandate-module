package com.udharpay.kernel.kernelcommon.register

open class Register<K, V> {

    private val hashMap = HashMap<K, V>()

    fun register(key: K, value: V) {
        if (isRegistered(key)) return
        hashMap[key] = value
    }

    fun unregister(key: K) {
        if (!isRegistered(key)) return
        hashMap.remove(key)
    }

    fun get(key: K): V {
        if (!isRegistered(key)) throw NoSuchElementException()
        return hashMap[key]!!
    }

    fun getOrNull(key: K): V? {
        return hashMap[key]
    }

    fun getEntries(): Set<Map.Entry<K, V>> {
        return hashMap.entries
    }

    fun getValueList(): List<V> {
        return hashMap.map { it.value }.toList()
    }

    fun getKeyList(): List<K> {
        return hashMap.map { it.key }.toList()
    }

    fun isRegistered(key: K): Boolean {
        return hashMap.containsKey(key)
    }
}

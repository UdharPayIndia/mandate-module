package com.rocketpay.mandate.common.basemodule.common.data.cache

import android.content.Context
import android.content.SharedPreferences

internal class DataStore(context: Context, name: String) {

    private val preference: SharedPreferences = context.getSharedPreferences(name, 0)
    private val editor: SharedPreferences.Editor = preference.edit()

    fun setString(key: String, value: String) {
        editor.putString(key, value).apply()
    }

    fun setBoolean(key: String, value: Boolean) {
        editor.putBoolean(key, value).apply()
    }

    fun setLong(key: String, value: Long) {
        editor.putLong(key, value).apply()
    }

    fun setFloat(key: String, value: Float) {
        editor.putFloat(key, value).apply()
    }

    fun setInt(key: String, value: Int) {
        editor.putInt(key, value).apply()
    }

    fun setStringSet(key: String, value: Set<String>) {
        editor.putStringSet(key, value).apply()
    }

    fun getString(key: String, value: String): String {
        return preference.getString(key, value) ?: value
    }

    fun getBoolean(key: String, value: Boolean): Boolean {
        return preference.getBoolean(key, value)
    }

    fun getLong(key: String, value: Long): Long {
        return preference.getLong(key, value)
    }

    fun getFloat(key: String, value: Float): Float {
        return preference.getFloat(key, value)
    }

    fun getInt(key: String, value: Int): Int {
        return preference.getInt(key, value)
    }

    fun getStringSet(key: String, value: Set<String>): Set<String> {
        return preference.getStringSet(key, value) ?: value
    }
}

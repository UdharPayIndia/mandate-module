package com.rocketpay.mandate.feature.login.data.datasource.local

import com.rocketpay.mandate.common.basemodule.common.data.cache.DataStore

internal class LoginDataStore(private val dataStore: DataStore) {

    companion object {
        const val LOGIN_DATA_STORE = "login"
        private const val TOKEN = "token"
        private const val USER_ID = "userId"
        private const val ACCOUNT_ID = "accountId"
        const val NEW_USER = "newUser"
        const val LOGGED_IN_TIMESTAMP = "loggedInTimeStamp"


        private const val COUNTRY_CODE = "country_code"
        private const val MOBILE_NUMBER = "mobileNumber"
        private const val NAME = "name"

        const val WHATSAPP_CONSENT = "moe_wa_subscription"
    }

    fun getCountryCode(): String {
        return dataStore.getString(COUNTRY_CODE, "+91")
    }

    fun setCountryCode(country: String) {
        dataStore.setString(COUNTRY_CODE, country)
    }

    fun setMobileNumber(phone: String) {
        dataStore.setString(MOBILE_NUMBER, phone)
    }

    fun getMobileNumber(): String {
        return dataStore.getString(MOBILE_NUMBER, "")
    }

    fun getUserId(): String {
        return dataStore.getString(USER_ID, "")
    }

    fun setUserId(userId: String) {
        dataStore.setString(USER_ID, userId)
    }

    fun getAccountId(): String {
        return dataStore.getString(ACCOUNT_ID, "")
    }

    fun setAccountId(userId: String) {
        dataStore.setString(ACCOUNT_ID, userId)
    }

    fun getToken(): String {
        return dataStore.getString(TOKEN, "")
    }

    fun setToken(token: String) {
        dataStore.setString(TOKEN, token)
    }

    fun setNewUser(isNewUser: Boolean) {
        dataStore.setBoolean(NEW_USER, isNewUser)
    }

    fun isNewUser(): Boolean {
        return dataStore.getBoolean(NEW_USER, false)
    }

    fun isLoggedIn(): Boolean {
        return getToken().isNotEmpty()
    }

    fun setName(name: String) {
        dataStore.setString(NAME, name)
    }

    fun getName(): String {
        return dataStore.getString(NAME, "")
    }

    fun isWhatsAppConsentEnabled(): Boolean {
        return dataStore.getBoolean(WHATSAPP_CONSENT, false)
    }

    fun setIsWhatsAppConsentEnabled(flag: Boolean) {
        dataStore.setBoolean(WHATSAPP_CONSENT, flag)
    }

    fun setLoggedInTimeStamp(value: Long) {
        dataStore.setLong(LOGGED_IN_TIMESTAMP, value)
    }

    fun getLoggedInTimeStamp(): Long {
        return dataStore.getLong(LOGGED_IN_TIMESTAMP, 0L)
    }
}

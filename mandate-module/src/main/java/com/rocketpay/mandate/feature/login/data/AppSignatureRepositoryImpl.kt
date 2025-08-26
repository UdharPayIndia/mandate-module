package com.rocketpay.mandate.feature.login.data

import android.content.Context
import android.content.pm.PackageManager
import android.util.Base64
import com.rocketpay.mandate.feature.login.domain.repositories.AppSignatureRepository
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*
import kotlin.collections.ArrayList

/**
 * This is a helper class to generate your message hash to be included in your SMS message.
 *
 * Without the correct hash, your app won't receive the message callback. This only needs to be
 * generated once per app and stored. Then you can remove this helper class from your code.
 */

internal class AppSignatureRepositoryImpl(val context: Context): AppSignatureRepository {

    private val algorithm = "SHA-256"
    private val numberOfHashedBytes = 9
    private val numberOfBase64Char = 11

    override fun getAppSignature(): String {
        val signatures = getAppSignatures()
        return if (signatures.isNotEmpty()) {
            signatures.first()
        } else {
            ""
        }
    }

    /**
     * Get all the app signatures for the current package
     * @return
     */
    private fun getAppSignatures(): ArrayList<String> {
        val appCodes = ArrayList<String>()
        try {
            // Get all package signatures for the current package
            val signatures = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES).signatures.orEmpty()

            // For each signature create a compatible hash
            for (signature in signatures) {
                createHash(context.packageName, signature.toCharsString())?.let {
                    appCodes.add(String.format("%s", it))
                }
            }
        } catch (e: Exception) {
        }
        return appCodes
    }

    private fun createHash(packageName: String, signature: String): String? {
        try {
            val messageDigest = MessageDigest.getInstance(algorithm)
            val appInfo = "$packageName $signature"
            messageDigest.update(appInfo.toByteArray(StandardCharsets.UTF_8))
            var hashSignature = messageDigest.digest()
            messageDigest.reset()

            // truncated into "HashedBytes"
            hashSignature = Arrays.copyOfRange(hashSignature, 0, numberOfHashedBytes)

            // encode into "Base64Char"
            var base64Hash = Base64.encodeToString(hashSignature, Base64.NO_PADDING or Base64.NO_WRAP)
            base64Hash = base64Hash.substring(0, numberOfBase64Char)
            return base64Hash
        } catch (e: Exception) {
            return null
        }
    }
}

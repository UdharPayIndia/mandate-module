package com.rocketpay.mandate.feature.kyc.domain.entities

import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal sealed class KycIdentityDocumentTypeEnum(val value: String, val translation: String) {
    object AADHAAR : KycIdentityDocumentTypeEnum("aadhaar", ResourceManager.getInstance().getString(
        R.string.rp_aadhaar_number))
    object PASSPORT : KycIdentityDocumentTypeEnum("passport",ResourceManager.getInstance().getString(R.string.rp_passport))
    object VOTER : KycIdentityDocumentTypeEnum("voter", ResourceManager.getInstance().getString(R.string.rp_voter_id))
    object DRIVING_LICENSE : KycIdentityDocumentTypeEnum("driving_license", ResourceManager.getInstance().getString(R.string.rp_driving_license))

    companion object {
        val map by lazy {
            mapOf(
                "aadhaar" to AADHAAR,
                "passport" to PASSPORT,
                "voter" to VOTER,
                "driving_license" to DRIVING_LICENSE,
            )
        }

        fun get(value: String?): KycIdentityDocumentTypeEnum? {
            return map[value]
        }
    }
}

package com.rocketpay.mandate.feature.kyc.domain.entities

import com.rocketpay.mandate.R

internal sealed class KycErrorCode(val value: String, val title: Int, val message: Int) {
    object BusinessPanMismatchError : KycErrorCode("PAN_BUSINESS_NAME_MISMATCH", R.string.rp_pan_number_does_not_match, R.string.rp_pan_number_does_not_match_with_please_enter_correct_pan_number)
    object PanIncorrectError : KycErrorCode("INVALID_PAN", R.string.rp_pan_details_are_incorrect, R.string.rp_try_with_correct_pan_details)
    object PanVerificationFailedError : KycErrorCode("PAN_DETAILS_TIMEOUT", R.string.rp_unable_to_verify_pan, R.string.rp_we_could_not_verify_pan_details)
    object BankDetailsIncorrectError : KycErrorCode("INVALID_BANK_ACCOUNT", R.string.rp_bank_details_are_incorrect, R.string.rp_try_with_correct_bank_details)
    object BankPanMismatchError : KycErrorCode("BANK_BUSINESS_NAME_MISMATCH", R.string.rp_bank_and_pan_details_did_not_match, R.string.rp_make_sure_pan_and_bank_account_belong_to_same_person)
    object BankVerificationFailedError : KycErrorCode("BANK_DETAILS_TIMEOUT", R.string.rp_unable_to_verify_bank, R.string.rp_we_could_not_verify_bank_details)
    object OwnerDetailsVerificationError : KycErrorCode("INVALID_OWNER_DETAILS", R.string.rp_owner_details_verification_failed_title, R.string.rp_kyc_sdk_error)
    object AadhaarMismatchError : KycErrorCode("AADHAR_NAME_MISMATCH", R.string.rp_pan_and_adhaar_did_not_match, R.string.rp_make_sure_pan_and_adhaar_belong_to_same_person)
    object KycInitError : KycErrorCode("KYC_INIT_FAILED", R.string.rp_registration_failed_bacause_of_technical_problem, R.string.rp_please_contact_us_if_issue_persists)
    object KycAuthError : KycErrorCode("KYC_AUTH_FAILED", R.string.rp_registration_failed_bacause_of_technical_problem, R.string.rp_please_contact_us_if_issue_persists)
    object GenericError : KycErrorCode("generic_error", R.string.rp_registration_failed_please_try_again, R.string.rp_please_contact_us_if_issue_persists)
    object SdkNameMismatchError : KycErrorCode("SDK_BUSINESS_NAME_MISMATCH", R.string.rp_aadhaar_details_did_not_match, R.string.rp_make_sure_aadhaar_and_business_belong_to_same_person)

    companion object {
        val map by lazy {
            mapOf(
                "KYC_INIT_FAILED" to KycInitError,
                "KYC_AUTH_FAILED" to KycAuthError,
                "INVALID_PAN" to PanIncorrectError,
                "PAN_DETAILS_TIMEOUT" to PanVerificationFailedError,
                "INVALID_BANK_ACCOUNT" to BankDetailsIncorrectError,
                "NAME_MATCH_FAILURE" to BankPanMismatchError,
                "BANK_DETAILS_TIMEOUT" to BankVerificationFailedError,
                "INVALID_OWNER_DETAILS" to OwnerDetailsVerificationError,
                "KYC_AADHAR_NAME_MISMATCH" to AadhaarMismatchError,
                "PAN_BUSINESS_NAME_MISMATCH" to BusinessPanMismatchError,
                "BANK_BUSINESS_NAME_MISMATCH" to BankPanMismatchError,
                "SDK_BUSINESS_NAME_MISMATCH" to SdkNameMismatchError,
                "generic_error" to GenericError,
                )
        }

        fun get(value: String): KycErrorCode {
            return map[value] ?: GenericError
        }
    }
}
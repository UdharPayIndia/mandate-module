package com.rocketpay.mandate.feature.installment.data.mapper

import com.rocketpay.mandate.feature.installment.data.entities.InstallmentPenaltyDto
import com.rocketpay.mandate.feature.installment.domain.entities.InstallmentPenalty
import com.rocketpay.mandate.feature.installment.domain.entities.InstallmentState
import com.udharpay.kernel.kernelcommon.mapper.ListMapper

internal class InstallmentPenaltyDtoToEntMapper : ListMapper<InstallmentPenaltyDto, InstallmentPenalty> {
    override fun map(source: InstallmentPenaltyDto): InstallmentPenalty {
        return InstallmentPenalty(amount = source.amount, captureAt = source.captureAt, status = InstallmentState.get(source.status))
    }
}

package com.rocketpay.mandate.feature.installment.data.mapper

import com.rocketpay.mandate.feature.installment.data.entities.InstallmentJourneyDto
import com.rocketpay.mandate.feature.installment.domain.entities.InstallmentJourney
import com.rocketpay.mandate.feature.installment.domain.entities.InstallmentState
import com.udharpay.kernel.kernelcommon.mapper.ListMapper

internal class InstallmentJourneyDtoToDomMapper : ListMapper<InstallmentJourneyDto, InstallmentJourney> {
    override fun map(source: InstallmentJourneyDto): InstallmentJourney {
        return InstallmentJourney(
            createdAt = source.created_at,
            state = InstallmentState.get(source.state),
            status = InstallmentState.get(source.status),
            statusDescription = source.status_description,
            timeState = TimeState.get(source.time_state)
        )
    }
}


sealed class TimeState(val value: String) {

    object Future : TimeState("future")
    object Past : TimeState("past")
    object Present : TimeState("present")

    companion object {
        val map by lazy {
            mapOf(
                "future" to Future,
                "past" to Past,
                "present" to Present
            )
        }

        fun get(timeState: String?): TimeState {
            return map[timeState] ?: Future
        }
    }
}
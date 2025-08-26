package com.rocketpay.mandate.feature.mandate.presentation.ui.mandatelist.statemachine

internal data class MandateSearchFilterSort(
    val mandateFilter: MandateFilter = MandateFilter.AllMandates,
    val mandateSort: MandateSort = MandateSort.LatestUpdate,
    val queryText: String = ""
)

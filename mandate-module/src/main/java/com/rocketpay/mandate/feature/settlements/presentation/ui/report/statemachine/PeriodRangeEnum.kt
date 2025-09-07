package com.rocketpay.mandate.feature.settlements.presentation.ui.report.statemachine

import android.icu.util.Calendar
import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal sealed class PeriodRangeEnum(val value: String, val translation: String) {
    data object Today: PeriodRangeEnum("Today", ResourceManager.getInstance().getString(R.string.rp_today))
    data object Yesterday: PeriodRangeEnum("Yesterday", ResourceManager.getInstance().getString(R.string.rp_yesterday))
    data object LastSevenDays: PeriodRangeEnum("LastSevenDays", ResourceManager.getInstance().getString(R.string.rp_last_seven_days))
    data object LastMonth: PeriodRangeEnum("LastMonth", ResourceManager.getInstance().getString(R.string.rp_last_month))
    data object Custom: PeriodRangeEnum("Custom", ResourceManager.getInstance().getString(R.string.rp_custom_date_range))

    companion object{
        fun getPeriodRangeList(): List<PeriodRangeEnum> {
            return listOf(Today, Yesterday, LastSevenDays, LastMonth, Custom)
        }


        fun getTimeStamp(periodRangeEnum: PeriodRangeEnum): Pair<Long, Long>{
            return when(periodRangeEnum){
                Today -> {
                    val calendar = Calendar.getInstance()

                    // ----- Start of Today -----
                    calendar.set(Calendar.HOUR_OF_DAY, 0)
                    calendar.set(Calendar.MINUTE, 0)
                    calendar.set(Calendar.SECOND, 0)
                    calendar.set(Calendar.MILLISECOND, 0)
                    val startOfToday = calendar.timeInMillis

                    return startOfToday to System.currentTimeMillis()
                }
                Yesterday -> {
                    val calendar = Calendar.getInstance()

                    // ----- Start of yesterday -----
                    calendar.add(Calendar.DAY_OF_YEAR, -1) // move to yesterday
                    calendar.set(Calendar.HOUR_OF_DAY, 0)
                    calendar.set(Calendar.MINUTE, 0)
                    calendar.set(Calendar.SECOND, 0)
                    calendar.set(Calendar.MILLISECOND, 0)
                    val startOfYesterday = calendar.timeInMillis

                    // ----- End of yesterday -----
                    calendar.set(Calendar.HOUR_OF_DAY, 23)
                    calendar.set(Calendar.MINUTE, 59)
                    calendar.set(Calendar.SECOND, 59)
                    calendar.set(Calendar.MILLISECOND, 999)
                    val endOfYesterday = calendar.timeInMillis

                    return startOfYesterday to endOfYesterday
                }
                LastSevenDays -> {
                    val calendar = Calendar.getInstance()

                    // ----- End time (yesterday at 23:59:59.999) -----
                    calendar.add(Calendar.DAY_OF_YEAR, -1) // move to yesterday
                    calendar.set(Calendar.HOUR_OF_DAY, 23)
                    calendar.set(Calendar.MINUTE, 59)
                    calendar.set(Calendar.SECOND, 59)
                    calendar.set(Calendar.MILLISECOND, 999)
                    val endOfLast7Days = calendar.timeInMillis

                    // ----- Start time (7 days ago at 00:00:00.000) -----
                    calendar.add(Calendar.DAY_OF_YEAR, -6) // move back total 7 days window
                    calendar.set(Calendar.HOUR_OF_DAY, 0)
                    calendar.set(Calendar.MINUTE, 0)
                    calendar.set(Calendar.SECOND, 0)
                    calendar.set(Calendar.MILLISECOND, 0)
                    val startOfLast7Days = calendar.timeInMillis

                    return startOfLast7Days to endOfLast7Days
                }
                LastMonth -> {
                    val calendar = Calendar.getInstance()

                    // Move to last month
                    calendar.add(Calendar.MONTH, -1)

                    // ----- Start of last month -----
                    calendar.set(Calendar.DAY_OF_MONTH, 1)
                    calendar.set(Calendar.HOUR_OF_DAY, 0)
                    calendar.set(Calendar.MINUTE, 0)
                    calendar.set(Calendar.SECOND, 0)
                    calendar.set(Calendar.MILLISECOND, 0)
                    val startOfLastMonth = calendar.timeInMillis

                    // ----- End of last month -----
                    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                    calendar.set(Calendar.HOUR_OF_DAY, 23)
                    calendar.set(Calendar.MINUTE, 59)
                    calendar.set(Calendar.SECOND, 59)
                    calendar.set(Calendar.MILLISECOND, 999)
                    val endOfLastMonth = calendar.timeInMillis

                    return startOfLastMonth to endOfLastMonth

                }
                Custom -> Pair(0L, 0L)
            }
        }
    }
}
package com.rocketpay.mandate.common.basemodule.common.presentation.utils

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import java.util.*

internal object DatePickerUtils {

    fun showDatePicker(context: Context, maxDate: String = "", date: String = "", minDate: String = "", addRemoveButton: Boolean = true, title: String? = null, onCancel: (() -> Unit)? = null, action: (String?, Long?) -> Unit) {
        val calendar = Calendar.getInstance()
        var maxDateInMiles = 0L
        var minDateInMiles = 0L

        if (maxDate.isNotEmpty()) {
            calendar.set(maxDate.subSequence(0, 4).toString().toInt(), maxDate.subSequence(5, 7).toString().toInt() - 1, maxDate.subSequence(8, 10).toString().toInt())
            maxDateInMiles = calendar.timeInMillis
        }

        if (date.isNotEmpty()) {
            calendar.set(date.subSequence(0, 4).toString().toInt(), date.subSequence(5, 7).toString().toInt() - 1, date.subSequence(8, 10).toString().toInt())
        }

        val mDatePicker = DatePickerDialog(
            context, R.style.RpMyCalendar, { _, year, month, dayOfMonth ->
                action(DateUtils.getDate(year, month + 1, dayOfMonth), DateUtils.getMilliSecondsFromYearMonthDate(year, month, dayOfMonth))
            },
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        )
        if (addRemoveButton) {
            mDatePicker.setButton(DialogInterface.BUTTON_NEUTRAL, ResourceManager.getInstance().getString(R.string.rp_remove)) { _, _ -> action(null, null) }
        }

        mDatePicker.setOnCancelListener { onCancel?.invoke() }

        if (!title.isNullOrEmpty()) {
            mDatePicker.setTitle(title)
        }

        if (maxDate.isNotEmpty()) {
            mDatePicker.datePicker.maxDate = maxDateInMiles
        }

        if (minDate.isNotEmpty()) {
            calendar.set(minDate.subSequence(0, 4).toString().toInt(), minDate.subSequence(5, 7).toString().toInt() - 1, minDate.subSequence(8, 10).toString().toInt())
            minDateInMiles = calendar.timeInMillis
        }

        if (minDate.isNotEmpty()) {
            mDatePicker.datePicker.minDate = minDateInMiles
        }

        mDatePicker.show()
    }
}
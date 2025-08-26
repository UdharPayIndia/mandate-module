package com.rocketpay.mandate.feature.business.presentation.ui.viewmodel

import android.graphics.drawable.Drawable
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.rocketpay.mandate.common.basemodule.common.presentation.adapter.RecyclerViewAdapter
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.DataValidator
import com.rocketpay.mandate.feature.business.domain.entities.BusinessField
import com.rocketpay.mandate.feature.business.presentation.ui.statemachine.BusinessProfileEvent

internal class ItemBusinessProfileVM(
    private val businessField: BusinessField,
    private val dispatchEvent: (BusinessProfileEvent) -> Unit,
    private val businessProperties: MutableLiveData<Map<String, String?>>
) : RecyclerViewAdapter.RecyclerViewItemViewModel {

    val title = ObservableField<String>()
    val subtitle = ObservableField<String>()

    val textValue = ObservableField<String>()
    val textInputLayoutVisibility = ObservableInt()
    val textHint = ObservableField<String>()
    val textError = ObservableField<String>()

    val dataValidator = DataValidator()
    private var regex: String = ""

    val selectedDrawable = ObservableField<Drawable>()

    init {
        businessProperties.observeForever {
            val businessFieldType = it[businessField.type] ?: ""
            updateUi(businessFieldType)
        }
    }

    private fun updateUi(businessFieldType: String) {
        if (businessField.type_meta == null) {
            textInputLayoutVisibility.set(View.GONE)
            title.set(businessField.title)
            if (businessFieldType.isEmpty()) {
                subtitle.set(businessField.hint)
            } else {
                val businessFieldValues = businessField.type_values.filter { it.type == businessFieldType }
                val displayValue = if (businessFieldValues.isEmpty()) {
                    businessField.hint
                } else {
                    businessFieldValues.first().title
                }
                subtitle.set(displayValue)
            }
        } else {
            textInputLayoutVisibility.set(View.VISIBLE)
            textHint.set(businessField.type_meta.hint)
            regex = businessField.type_meta.regex
            textValue.set(businessFieldType)
        }
    }

    fun onTextChanged(text: CharSequence) {
        val enteredValue = text.toString()
        if (dataValidator.isValidString(enteredValue, regex)) {
            textError.set(null)
            dispatchEvent(BusinessProfileEvent.BusinessFieldSelected(businessField.type, enteredValue))
        } else {
            textError.set(businessField.type_meta?.error)
            dispatchEvent(BusinessProfileEvent.DisablePrimaryAction)
        }
    }

    fun onItemClick() {
        dispatchEvent(BusinessProfileEvent.BusinessFieldClick(businessField))
    }
}

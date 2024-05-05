package com.ferdidrgn.anlikdepremler.util.dataBindingHelpers

import androidx.core.widget.doAfterTextChanged
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.databinding.InverseBindingMethod
import androidx.databinding.InverseBindingMethods
import com.ferdidrgn.anlikdepremler.util.components.CustomDatePicker
import com.ferdidrgn.anlikdepremler.util.components.CustomEditText
import com.ferdidrgn.anlikdepremler.util.components.CustomNumberEditText
import com.ferdidrgn.anlikdepremler.util.components.CustomToolbar
import com.ferdidrgn.anlikdepremler.util.helpers.show

object CustomDataBindingUtils {

    @InverseBindingMethods(
        InverseBindingMethod(
            type = CustomEditText::class, attribute = "bind:custom_edit_text", event =
            "bind:textAttrChanged", method = "bind:getText"
        ),
        InverseBindingMethod(
            type = CustomNumberEditText::class,
            attribute = "bind:cst_number_changeable_edit_text",
            event =
            "bind:textAttrChanged",
            method = "bind:getText"
        ),
        InverseBindingMethod(
            type =
            CustomToolbar::class, attribute = "bind:custom_toolbar_changeable_text", event =
            "bind:textAttrChanged", method = "bind:getToolBarText"
        ),
        InverseBindingMethod(
            type =
            CustomDatePicker::class, attribute = "bind:cst_picker_changeable_text", event =
            "bind:textAttrChanged", method = "bind:getText"
        ),
    )
    class CustomEditTextBinder {
        companion object {

            @BindingAdapter("app:textAttrChanged")
            @JvmStatic
            fun setListener(editText: CustomEditText, listener: InverseBindingListener?) {
                if (listener != null) {
                    editText.editText.doAfterTextChanged { listener.onChange() }
                }
            }

            @InverseBindingAdapter(attribute = "custom_edit_text", event = "app:textAttrChanged")
            @JvmStatic
            fun getCustomEditText(nMe: CustomEditText): String {
                return nMe.editText.text.toString()
            }

            @BindingAdapter("custom_edit_text")
            @JvmStatic
            fun setCustomEditText(editText: CustomEditText, text: String?) {
                text?.let {
                    if (it != editText.editText.text.toString()) {
                        editText.editText.setText(it)
                    }
                }
            }
        }
    }

    class CustomToolBarTextBinder {
        companion object {
            @BindingAdapter("app:textAttrChanged")
            @JvmStatic
            fun setListener(toolBar: CustomToolbar, listener: InverseBindingListener?) {
                if (listener != null) {
                    toolBar.tvTitle.show()
                    toolBar.tvTitle.doAfterTextChanged {
                        listener.onChange()
                    }
                }
            }

            @InverseBindingAdapter(
                attribute = "custom_toolbar_changeable_text",
                event = "app:textAttrChanged"
            )
            @JvmStatic
            fun getCustomToolBarText(toolBar: CustomToolbar): String {
                toolBar.tvTitle.show()
                return toolBar.tvTitle.text.toString()
            }

            @BindingAdapter("custom_toolbar_changeable_text")
            @JvmStatic
            fun setCustomToolBarText(toolBar: CustomToolbar, text: String?) {
                text?.let { customText ->
                    if (customText != toolBar.tvTitle.text.toString()) {
                        toolBar.tvTitle.show()
                        toolBar.tvTitle.text = customText
                    }
                }
            }
        }
    }

    class CustomNumberEditTextBinder {
        companion object {
            @BindingAdapter("app:textAttrChanged")
            @JvmStatic
            fun setListener(editText: CustomNumberEditText, listener: InverseBindingListener?) {
                if (listener != null) {
                    editText.editTextView.doAfterTextChanged { listener.onChange() }
                }
            }

            @InverseBindingAdapter(
                attribute = "cst_number_changeable_edit_text",
                event = "app:textAttrChanged"
            )
            @JvmStatic
            fun getCustomEditText(nMe: CustomNumberEditText): String {
                return nMe.editTextView.text.toString()
            }

            @BindingAdapter("cst_number_changeable_edit_text")
            @JvmStatic
            fun setCustomEditText(editText: CustomNumberEditText, text: String?) {
                text?.let {
                    if (it != editText.editTextView.text.toString()) {
                        editText.editTextView.setText(it)
                    }
                }
            }
        }
    }

    class CustomDatePickerBinder {
        companion object {

            @BindingAdapter("app:textAttrChanged")
            @JvmStatic
            fun setListener(customDatePicker: CustomDatePicker, listener: InverseBindingListener?) {
                if (listener != null) {
                    customDatePicker.tvSelectedDate.doAfterTextChanged { listener.onChange() }
                }
            }

            @InverseBindingAdapter(
                attribute = "cst_picker_changeable_text",
                event = "app:textAttrChanged"
            )
            @JvmStatic
            fun getCustomEditText(nMe: CustomDatePicker): String {
                return nMe.tvSelectedDate.text.toString()
            }

            @BindingAdapter("cst_picker_changeable_text")
            @JvmStatic
            fun setCustomEditText(customDatePicker: CustomDatePicker, text: String?) {
                text?.let {
                    if (it != customDatePicker.tvSelectedDate.text.toString()) {
                        customDatePicker.tvSelectedDate.text = it
                    }
                }
            }
        }
    }

}
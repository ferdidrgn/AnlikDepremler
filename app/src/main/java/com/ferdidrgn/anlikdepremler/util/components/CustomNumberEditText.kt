package com.ferdidrgn.anlikdepremler.util.components

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.text.method.KeyListener
import android.util.AttributeSet
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import com.ferdidrgn.anlikdepremler.R

class CustomNumberEditText : ConstraintLayout {
    lateinit var editTextView: EditText

    constructor(context: Context) : super(context) {
        initLayout(context, null, null)
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        initLayout(context, attributeSet, null)
    }

    constructor(context: Context, attributeSet: AttributeSet, style: Int) : super(
        context,
        attributeSet,
        style
    ) {
        initLayout(context, attributeSet, style)
    }

    private fun initLayout(context: Context, attributeSet: AttributeSet?, style: Int?) {
        inflate(context, R.layout.custom_number_edit_text, this)
        editTextView = findViewById(R.id.custom_number_editText)
        editTextView.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL

        val layoutAttribute =
            context.obtainStyledAttributes(attributeSet, R.styleable.CustomNumberEditText)

        editTextViewAddPlus(
            layoutAttribute.getBoolean(R.styleable.CustomNumberEditText_cst_number_add_plus, false)
        )

        layoutAttribute.getString(R.styleable.CustomNumberEditText_cst_number_edit_text)
            ?.let { string -> setText(string) }

        hintText(layoutAttribute.getString(R.styleable.CustomNumberEditText_cst_number_hint_text))
    }

    private fun editTextViewAddPlus(isAddPlus: Boolean) {
        if (isAddPlus) {
            editTextView.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if (s.toString().isNotEmpty() && s.toString().first() != '+') {
                        editTextView.setText("+${s.toString()}")
                        editTextView.setSelection(editTextView.text.length)
                    }
                }
            })
        }
    }

    fun setText(text: String) {
        editTextView.setText(text)
    }

    fun hintText(text: String?) {
        editTextView.hint = text
    }

    internal fun emailType() {
        editTextView.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        editTextView.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(100))
    }

    fun countCharacter(count: Int) {
        editTextView.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(count))
    }

    fun inputTypeNumber() {
        val keyListener: KeyListener =
            DigitsKeyListener.getInstance(context.getString(R.string.numbersKey))
        editTextView.keyListener = keyListener
        editTextView.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
    }

    fun formatPrice() {
        //editTextView.addTextChangedListener(NumberTextWatcher(editText))
    }

}
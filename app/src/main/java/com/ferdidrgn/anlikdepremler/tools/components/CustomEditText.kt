package com.ferdidrgn.anlikdepremler.tools.components

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.ferdidrgn.anlikdepremler.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class CustomEditText : ConstraintLayout {
    lateinit var editText: TextInputEditText
    lateinit var tlEditText: TextInputLayout

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
        inflate(context, R.layout.custom_edit_text, this)
        editText = findViewById(R.id.etCustomEditText)
        tlEditText = findViewById(R.id.tlEditText)
        val layoutAttribute =
            context.obtainStyledAttributes(attributeSet, R.styleable.CustomEditText)

        hintText(layoutAttribute.getString(R.styleable.CustomEditText_custom_edit_hint))
    }

    private fun hintText(text: String?) {
        tlEditText.hint = text
    }

}
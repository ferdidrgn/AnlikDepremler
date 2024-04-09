package com.ferdidrgn.anlikdepremler.tools.components

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
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

    private val handler = Handler(Looper.getMainLooper())
    private val delay: Long = 2000 // 2 seconds delay
    private var timerRunnable: Runnable? = null

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

    fun changeableText(status: (Boolean) -> Unit) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                // If there's already a timer running, remove the callbacks
                timerRunnable?.let { handler.removeCallbacks(it) }

                // Create a new timer runnable
                timerRunnable = Runnable {
                    // Call your ViewModel function here
                    // Assuming viewModel is accessible here
                    if (s.toString().isNotEmpty())
                        status(true)
                    else
                        status(false)
                }

                // Post the runnable with a delay
                handler.postDelayed(timerRunnable!!, delay)
            }
        })
    }

}
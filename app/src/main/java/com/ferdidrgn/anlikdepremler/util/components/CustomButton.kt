package com.ferdidrgn.anlikdepremler.util.components

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kotlin.properties.Delegates
import com.ferdidrgn.anlikdepremler.R
import com.ferdidrgn.anlikdepremler.util.helpers.show

class CustomButton : ConstraintLayout {

    private var backgroundColorFromAttr by Delegates.notNull<Int>()
    private lateinit var customButton: ConstraintLayout
    private lateinit var imageView: ImageView

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
        inflate(context, R.layout.custom_button, this)
        customButton = findViewById(R.id.btnCustomButton)
        val layoutAttribute = context.obtainStyledAttributes(attributeSet, R.styleable.CustomButton)
        setTextView(layoutAttribute.getString(R.styleable.CustomButton_cst_btn_name))
        getDisableBackgroundTint(
            layoutAttribute.getColor(
                R.styleable.CustomButton_cst_btn_backgroundColorFromXml,
                ContextCompat.getColor(context, R.color.red_err)
            )
        )
        val setButtonColor = (
                layoutAttribute.getResourceId(
                    R.styleable.CustomButton_cst_btn_color, 0
                ))
        setImage(layoutAttribute.getInt(R.styleable.CustomButton_cst_btn_endIcon, 0))
    }

    private fun getDisableBackgroundTint(color: Int) {
        backgroundColorFromAttr = color
        customButton.backgroundTintList = ColorStateList.valueOf(backgroundColorFromAttr)
    }

    private fun setTextView(text: String?) {
        val textView = findViewById<TextView>(R.id.tvCustomButtonLabel)
        textView.text = text
    }

    fun setButtonActive() {
        customButton.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(customButton.context, R.color.primary2))
        isClickable = true
    }

    fun setButtonDisable() {
        customButton.backgroundTintList = ColorStateList.valueOf(backgroundColorFromAttr)
        isClickable = false
    }

    fun setImage(image: Int?) {
        if (image != null) {
            imageView = findViewById(R.id.imgView)
            imageView.show()
            imageView.setImageResource(image)
        }
    }
}
package com.ferdidrgn.anlikdepremler.tools.components

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import com.ferdidrgn.anlikdepremler.R
import com.ferdidrgn.anlikdepremler.tools.DEFAULT_DATE_FORMAT
import java.text.SimpleDateFormat
import java.util.*

class CustomDatePicker : LinearLayout {

    private lateinit var customDatePicker: LinearLayout
    lateinit var tvSelectedDate: TextView
    val calendar: Calendar = Calendar.getInstance()

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
        inflate(context, R.layout.custom_date_picker, this)
        customDatePicker = findViewById(R.id.llCustomDatePicker)
        tvSelectedDate = findViewById(R.id.tvSelectedDate)
        val layoutAttribute =
            context.obtainStyledAttributes(attributeSet, R.styleable.CustomDatePicker)
        hintText(layoutAttribute.getString(R.styleable.CustomDatePicker_cst_date_picker_hint_text))

    }

    init {
        getDate()
    }

    private fun hintText(text: String?) {
        tvSelectedDate.hint = text
    }

    fun setCustomDataPickerClick() {

        val dateSetListener =
            OnDateSetListener { datePicker, year, month, day ->
                var localMonth = month
                localMonth += 1
                calendar[year, month] = day
                val date: String = makeDateString(day, localMonth, year)
                tvSelectedDate.text = date
            }

        val style: Int = AlertDialog.THEME_HOLO_LIGHT
        val cal: Calendar = Calendar.getInstance()
        val year: Int = cal.get(android.icu.util.Calendar.YEAR)
        val month: Int = cal.get(android.icu.util.Calendar.MONTH)
        val day: Int = cal.get(android.icu.util.Calendar.DAY_OF_MONTH)
        val datePickerDialog =
            DatePickerDialog(context, style, dateSetListener, year, month, day)
        datePickerDialog.show()

    }

    @SuppressLint("SimpleDateFormat")
    fun getDate(): String {
        val date = Date(calendar.timeInMillis)
        return SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale("en")).format(date)
    }

    private fun makeDateString(day: Int, month: Int, year: Int): String {
        return "$day-$month-$year"
    }

    //Hour and Minute
    fun popTimePicker() {
        var hour = 0
        var minute: Int = 0
        val style: Int = AlertDialog.THEME_HOLO_LIGHT
        val onTimeSetListener =
            TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                hour = selectedHour
                minute = selectedMinute
                calendar[Calendar.HOUR_OF_DAY] = selectedHour
                calendar[Calendar.MINUTE] = selectedMinute

                tvSelectedDate.text = java.lang.String.format(
                    Locale.getDefault(),
                    "%02d:%02d",
                    hour,
                    minute
                )
            }

        val timePickerDialog =
            TimePickerDialog(context, style, onTimeSetListener, hour, minute, true)
        timePickerDialog.setTitle("Select Time")
        timePickerDialog.show()
    }
}
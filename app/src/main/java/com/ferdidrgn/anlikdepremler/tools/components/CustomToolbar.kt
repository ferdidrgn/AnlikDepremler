package com.ferdidrgn.anlikdepremler.tools.components

import android.app.Activity
import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.ferdidrgn.anlikdepremler.R
import com.ferdidrgn.anlikdepremler.enums.ToolBarTitles
import com.ferdidrgn.anlikdepremler.tools.ClientPreferences
import com.ferdidrgn.anlikdepremler.tools.hide
import com.ferdidrgn.anlikdepremler.tools.show

class CustomToolbar : LinearLayout {

    lateinit var tvTitle: TextView
    private lateinit var imgLogo: ImageView
    lateinit var guest: TextView
    private var token = ClientPreferences.inst.token
    private lateinit var imgNotification: ImageView
    lateinit var imgFilter: ImageView
    lateinit var closeIcon: ImageView
    lateinit var backIcon: ImageView
    private lateinit var layoutAttribute: TypedArray

    constructor(context: Context) : super(context) {
        initLayout(context, null, null)
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        initLayout(context, attributeSet, null)
    }

    constructor(context: Context, attributeSet: AttributeSet, style: Int) : super(
        context, attributeSet, style
    ) {
        initLayout(context, attributeSet, style)
    }

    private fun initLayout(context: Context, attributeSet: AttributeSet?, style: Int?) {
        inflate(context, R.layout.custom_toolbar, this)
        tvTitle = findViewById(R.id.tvTitle)
        imgLogo = findViewById(R.id.imgLogo)
        guest = findViewById(R.id.tGuest)
        imgNotification = findViewById(R.id.imgNotification)
        imgFilter = findViewById(R.id.imgFilter)
        closeIcon = findViewById(R.id.imgClose)
        backIcon = findViewById(R.id.imgBack)
        layoutAttribute = context.obtainStyledAttributes(attributeSet, R.styleable.CustomToolbar)
        visibilityOfBackIcon(
            layoutAttribute.getBoolean(
                R.styleable.CustomToolbar_cst_toolbar_backIcon_visible, false
            )
        )
        visibilityOfCloseIcon(
            layoutAttribute.getBoolean(
                R.styleable.CustomToolbar_cst_toolbar_closeIcon_visible, false
            )
        )

        val ctTitle = layoutAttribute.getString(R.styleable.CustomToolbar_cst_toolbar_text)
        setUpTitle(ctTitle, false)

        toLoginActivity()

    }

    fun closeIconOnBackPress(activity: Activity) {
        closeIcon.setOnClickListener {
            activity.onBackPressed()
        }
    }

    fun backIconOnBackPress(activity: Activity) {
        backIcon.setOnClickListener {
            activity.onBackPressed()
        }
    }

    private fun visibilityOfCloseIcon(isEnable: Boolean) {
        if (isEnable) {
            closeIcon.show()
        } else {
            closeIcon.hide()
        }
    }

    fun visibilityOfBackIcon(isEnable: Boolean) {
        if (isEnable) {
            backIcon.show()
        } else {
            backIcon.hide()
        }
    }

    private fun setUpTitle(ctTitle: String?, IsIconVisible: Boolean = false) {
        if (ctTitle.isNullOrEmpty() && IsIconVisible.not()) {
            visibilityText(ctTitle)
        } else {
            if (IsIconVisible) {
                imgLogo.show()
            } else {
                imgLogo.hide()
                tvTitle.show()
                tvTitle.text = ctTitle
            }
        }
    }

    private fun visibilityText(text: String?) {
        when (text) {
            ToolBarTitles.Home.Entry(tvTitle.context).toString() -> {
                tvTitle.show()
                tvTitle.text = text
            }

            ToolBarTitles.NearEarthquake.Entry(tvTitle.context).toString() -> {
                tvTitle.show()
                tvTitle.text = text
            }

            ToolBarTitles.NowEarthquake.Entry(tvTitle.context).toString() -> {
                tvTitle.show()
                tvTitle.text = text
            }

            ToolBarTitles.Settings.Entry(tvTitle.context).toString() -> {
                tvTitle.show()
                tvTitle.text = text
            }

            ToolBarTitles.Language.Entry(tvTitle.context).toString() -> {
                tvTitle.show()
                tvTitle.text = text
            }
        }
    }


    private fun toLoginActivity() {
        guest.setOnClickListener {
            //NavHandler.instance.toLoginActivity(context)
        }
    }

    private fun toNotificationsActivity() {
        imgNotification.setOnClickListener {
            //NavHandler.instance.toNotifications(context)
        }
    }


    //User control, if there is a token gone guest else show notification icon
    private fun checkToken(token: String): Boolean {
        if (token == "") {
            guest.show()
            return false
        }
        return true
    }
}
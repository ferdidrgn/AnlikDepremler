package com.ferdidrgn.anlikdepremler.ui.termsAndConditionsandPrivacePolicy

import android.text.Html
import com.ferdidrgn.anlikdepremler.R
import com.ferdidrgn.anlikdepremler.base.BaseViewModel
import com.ferdidrgn.anlikdepremler.tools.enums.Response
import com.ferdidrgn.anlikdepremler.tools.enums.WhichTermsAndPrivacy
import com.ferdidrgn.anlikdepremler.repository.AppToolsFireBaseQueries
import com.ferdidrgn.anlikdepremler.tools.mainScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class TermsAndConditionsandPrivacePolicyViewModel @Inject constructor(private val appToolsFireBaseQueries: AppToolsFireBaseQueries) :
    BaseViewModel() {


    val tvTermsAndCondition = MutableStateFlow("")
    val toolBarText = MutableStateFlow("")


    fun getHtmlFromUrl(whichTermsAndPrivacy: WhichTermsAndPrivacy) {
        mainScope {
            showLoading()

            //NOT: We determined the data we will pull from the db and also determined the toolbar text.
            when (whichTermsAndPrivacy) {
                WhichTermsAndPrivacy.TermsAndCondition -> {
                    toolBarText.value = message(R.string.terms_condition)
                    appToolsFireBaseQueries.getTermsConditionOrPrivacyPolicy("termsAndCondition") { status, html ->
                        hideLoading()
                        loopWhen(status, html)
                    }
                }
                WhichTermsAndPrivacy.PrivacyAndPolicy -> {
                    toolBarText.value = message(R.string.privace_policy)
                    appToolsFireBaseQueries.getTermsConditionOrPrivacyPolicy("privacyPolicy") { status, html ->
                        hideLoading()
                        loopWhen(status, html)
                    }
                }
            }
        }
    }

    private fun loopWhen(status: Response, html: String?) {
        mainScope {
            showLoading()

            var value = ""
            when (status) {
                Response.ThereIs -> {
                    html?.let { data ->
                        value = data
                    }
                }
                Response.Empty -> {
                    value = message(R.string.error_server)
                    errorMessage.postValue(value)
                }
                Response.ServerError -> {
                    value = message(R.string.error_server)
                    errorMessage.postValue(value)
                }
                else -> {
                    value = message(R.string.error_message)
                    errorMessage.postValue(value)
                }
            }
            val formattedHtml = Html.fromHtml(value)
            tvTermsAndCondition.value = formattedHtml.toString()
            hideLoading()
        }
    }
}
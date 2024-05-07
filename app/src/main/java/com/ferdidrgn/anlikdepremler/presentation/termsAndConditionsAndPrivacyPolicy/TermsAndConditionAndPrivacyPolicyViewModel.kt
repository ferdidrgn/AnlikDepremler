package com.ferdidrgn.anlikdepremler.presentation.termsAndConditionsAndPrivacyPolicy

import android.text.Html
import com.ferdidrgn.anlikdepremler.R
import com.ferdidrgn.anlikdepremler.util.base.BaseViewModel
import com.ferdidrgn.anlikdepremler.domain.useCase.termsConditionPrivacyPolicy.GetTermsConditionOrPrivacyPolicyUseCase
import com.ferdidrgn.anlikdepremler.util.helpers.PRIVACY_POLICY
import com.ferdidrgn.anlikdepremler.util.helpers.Response
import com.ferdidrgn.anlikdepremler.util.helpers.TERMS_AND_CONDITION
import com.ferdidrgn.anlikdepremler.util.helpers.WhichTermsAndPrivacy
import com.ferdidrgn.anlikdepremler.util.helpers.mainScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class TermsAndConditionAndPrivacyPolicyViewModel @Inject constructor(
    private val getTermsConditionOrPrivacyPolicyUseCase: GetTermsConditionOrPrivacyPolicyUseCase
) :
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
                    getTermsConditionOrPrivacyPolicyUseCase(TERMS_AND_CONDITION) { status, html ->
                        hideLoading()
                        loopWhen(status, html)
                    }
                }

                WhichTermsAndPrivacy.PrivacyAndPolicy -> {
                    toolBarText.value = message(R.string.privace_policy)
                    getTermsConditionOrPrivacyPolicyUseCase(PRIVACY_POLICY) { status, html ->
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
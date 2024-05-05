package com.ferdidrgn.anlikdepremler.domain.useCase.termsConditionPrivacyPolicy

import com.ferdidrgn.anlikdepremler.tools.enums.Response

interface GetTermsConditionOrPrivacyPolicyUseCase {
    operator fun invoke(whichTermsAndPrivacy: String, status: (Response, String?) -> Unit): Unit
}
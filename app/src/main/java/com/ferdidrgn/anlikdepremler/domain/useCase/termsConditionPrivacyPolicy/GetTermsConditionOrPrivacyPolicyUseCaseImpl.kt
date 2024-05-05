package com.ferdidrgn.anlikdepremler.domain.useCase.termsConditionPrivacyPolicy

import com.ferdidrgn.anlikdepremler.domain.repository.AppToolsFireBaseQueriesRepository
import com.ferdidrgn.anlikdepremler.util.helpers.Response
import javax.inject.Inject

class GetTermsConditionOrPrivacyPolicyUseCaseImpl @Inject constructor(
    private val appToolsFireBaseQueriesRepository: AppToolsFireBaseQueriesRepository,
) : GetTermsConditionOrPrivacyPolicyUseCase {

    override operator fun invoke(
        whichTermsAndPrivacy: String,
        status: (Response, String?) -> Unit): Unit =
        appToolsFireBaseQueriesRepository.getTermsConditionOrPrivacyPolicy(whichTermsAndPrivacy, status)
}
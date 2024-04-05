package com.ferdidrgn.anlikdepremler.domain

import com.ferdidrgn.anlikdepremler.repository.AppToolsFireBaseQueriesRepository
import com.ferdidrgn.anlikdepremler.tools.enums.Response
import javax.inject.Inject

class GetTermsConditionOrPrivacyPolicyUseCaseImpl @Inject constructor(
    private val appToolsFireBaseQueriesRepository: AppToolsFireBaseQueriesRepository,
) : GetTermsConditionOrPrivacyPolicyUseCase {

    override operator fun invoke(
        whichTermsAndPrivacy: String,
        status: (Response, String?) -> Unit
    ): Unit =
        appToolsFireBaseQueriesRepository.getTermsConditionOrPrivacyPolicy(
            whichTermsAndPrivacy,
            status
        )
}
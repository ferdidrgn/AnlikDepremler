package com.ferdidrgn.anlikdepremler.domain.useCase.contactUsEmail

import com.ferdidrgn.anlikdepremler.domain.repository.AppToolsFireBaseQueriesRepository
import com.ferdidrgn.anlikdepremler.tools.enums.Response
import javax.inject.Inject

class GetContactUsEmailUseCaseImpl @Inject constructor(
    private val appToolsFireBaseQueriesRepository: AppToolsFireBaseQueriesRepository,
) : GetContactUsEmailUseCase {
    override fun invoke(status: (Response, String?) -> Unit): Unit =
        appToolsFireBaseQueriesRepository.getContactUsEmail(status)
}
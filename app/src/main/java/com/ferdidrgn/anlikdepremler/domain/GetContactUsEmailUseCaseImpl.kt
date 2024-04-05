package com.ferdidrgn.anlikdepremler.domain

import com.ferdidrgn.anlikdepremler.data.repositroy.AppToolsFireBaseQueriesRepository
import com.ferdidrgn.anlikdepremler.tools.enums.Response
import javax.inject.Inject

class GetContactUsEmailUseCaseImpl @Inject constructor(
    private val appToolsFireBaseQueriesRepository: AppToolsFireBaseQueriesRepository,
) : GetContactUsEmailUseCase {
    override fun invoke(status: (Response, String?) -> Unit) =
        appToolsFireBaseQueriesRepository.getContactUsEmail(status)
}
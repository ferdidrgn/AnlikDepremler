package com.ferdidrgn.anlikdepremler.domain.useCase.contactUsEmail

import com.ferdidrgn.anlikdepremler.util.helpers.Response

interface GetContactUsEmailUseCase {
    operator fun invoke(status: (Response, String?) -> Unit): Unit
}
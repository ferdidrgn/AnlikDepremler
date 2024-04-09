package com.ferdidrgn.anlikdepremler.domain.useCase

import com.ferdidrgn.anlikdepremler.tools.enums.Response

interface GetContactUsEmailUseCase {
    operator fun invoke(status: (Response, String?) -> Unit): Unit
}
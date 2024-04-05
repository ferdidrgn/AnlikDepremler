package com.ferdidrgn.anlikdepremler.domain

import com.ferdidrgn.anlikdepremler.tools.enums.Response

interface GetContactUsEmailUseCase {
    operator fun invoke(status: (Response, String?) -> Unit): Unit
}
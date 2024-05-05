package com.ferdidrgn.anlikdepremler.domain.repository

import com.ferdidrgn.anlikdepremler.util.helpers.Response

interface AppToolsFireBaseQueriesRepository {

    fun getTermsConditionOrPrivacyPolicy(
        whichTermsAndPrivacy: String,
        status: (Response, String?) -> Unit
    ): Unit

    fun getContactUsEmail(status: (Response, String?) -> Unit): Unit
}
package com.ferdidrgn.anlikdepremler.data.repository

import com.ferdidrgn.anlikdepremler.tools.enums.Response

interface AppToolsFireBaseQueriesRepository {

    fun getTermsConditionOrPrivacyPolicy(
        whichTermsAndPrivacy: String,
        status: (Response, String?) -> Unit
    ): Unit

    fun getContactUsEmail(status: (Response, String?) -> Unit): Unit
}
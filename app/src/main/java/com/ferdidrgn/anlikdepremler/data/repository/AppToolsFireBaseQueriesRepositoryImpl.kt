package com.ferdidrgn.anlikdepremler.data.repository

import com.ferdidrgn.anlikdepremler.domain.repository.AppToolsFireBaseQueriesRepository
import com.ferdidrgn.anlikdepremler.tools.enums.Response
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

class AppToolsFireBaseQueriesRepositoryImpl @Inject constructor() :
    AppToolsFireBaseQueriesRepository {

    private val fireStoreAppToolsRef = Firebase.firestore.collection("AppTools")

    override fun getTermsConditionOrPrivacyPolicy(
        whichTermsAndPrivacy: String,
        status: (Response, String?) -> Unit
    ) {
        fireStoreAppToolsRef.orderBy(whichTermsAndPrivacy)
            .addSnapshotListener { value, error ->
                if (error != null) status.invoke(Response.ServerError, null)
                else {
                    if (value != null) {
                        if (!value.isEmpty) {
                            val documents = value.documents
                            for (document in documents) {
                                val html =
                                    if (document.get(whichTermsAndPrivacy) != null) document.get(
                                        whichTermsAndPrivacy
                                    ) as String else ""
                                status.invoke(Response.ThereIs, html)
                            }
                        } else
                            status.invoke(Response.Empty, null)
                    } else
                        status.invoke(Response.Empty, null)
                }
            }
    }

    override fun getContactUsEmail(status: (Response, String?) -> Unit) {
        fireStoreAppToolsRef.orderBy("contactUsEmail")
            .addSnapshotListener { value, error ->
                if (error != null) status.invoke(Response.ServerError, null)
                else {
                    if (value != null) {
                        if (!value.isEmpty) {
                            val documents = value.documents
                            for (document in documents) {
                                val email =
                                    if (document.get("contactUsEmail") != null) document.get("contactUsEmail") as String else ""
                                status.invoke(Response.ThereIs, email)
                            }
                        } else
                            status.invoke(Response.Empty, null)
                    } else
                        status.invoke(Response.Empty, null)
                }
            }
    }
}
package com.ferdidrgn.anlikdepremler.repository

import com.ferdidrgn.anlikdepremler.enums.Response
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AppToolsFireBaseQueries {

    private val fireStoreAppToolsRef = Firebase.firestore.collection("AppTools")

    fun getTermsConditionOrPricavePolicy(
        whichTermsAndPrivace: String,
        status: (Response, String?) -> Unit
    ) {

        fireStoreAppToolsRef.orderBy(whichTermsAndPrivace)
            .addSnapshotListener { value, error ->
                if (error != null) status.invoke(Response.ServerError, null)
                else {
                    if (value != null) {
                        if (!value.isEmpty) {
                            val documents = value.documents
                            for (document in documents) {
                                val html =
                                    if (document.get(whichTermsAndPrivace) != null) document.get(
                                        whichTermsAndPrivace
                                    ) as String else ""
                                status.invoke(Response.ThereIs, html)
                            }
                        } else {
                            status.invoke(Response.Empty, null)
                        }
                    } else {
                        status.invoke(Response.Empty, null)
                    }
                }
            }
    }

    fun getContactUsEmail(status: (Response, String?) -> Unit) {
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
                        } else {
                            status.invoke(Response.Empty, null)
                        }
                    } else {
                        status.invoke(Response.Empty, null)
                    }
                }
            }
    }
}
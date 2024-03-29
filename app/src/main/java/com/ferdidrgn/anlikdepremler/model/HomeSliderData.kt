package com.ferdidrgn.anlikdepremler.model

import com.ferdidrgn.anlikdepremler.base.ListAdapterItem

data class HomeSliderData(
    val image: String? = null,
    val title: String? = null,
    val description: String? = null,
    val link: String? = null,
    override val mId: Long = 1L

) : ListAdapterItem

package com.ferdidrgn.anlikdepremler.domain.model

import com.ferdidrgn.anlikdepremler.util.base.ListAdapterItem

data class HomeSliderData(
    val image: String? = null,
    val title: String? = null,
    val description: String? = null,
    val link: String? = null,
    override val mId: Long = 1L

) : ListAdapterItem

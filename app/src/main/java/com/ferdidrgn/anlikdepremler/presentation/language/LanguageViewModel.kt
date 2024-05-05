package com.ferdidrgn.anlikdepremler.presentation.language

import androidx.lifecycle.MutableLiveData
import com.ferdidrgn.anlikdepremler.util.base.BaseViewModel
import com.ferdidrgn.anlikdepremler.domain.useCase.contactUsEmail.GetContactUsEmailUseCase
import com.ferdidrgn.anlikdepremler.util.helpers.ClientPreferences
import com.ferdidrgn.anlikdepremler.util.helpers.ContextLanguages
import com.ferdidrgn.anlikdepremler.util.helpers.Languages
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val getContactUsEmailUseCase: GetContactUsEmailUseCase
) : BaseViewModel() {

    val whichButtonSelected = MutableLiveData<Boolean>()
    var selected: (() -> Unit)? = null

    fun firstState() {
        whichButtonSelected.postValue(false)
        if (ClientPreferences.inst.language == Languages.TURKISH.language)
            whichButtonSelected.postValue(false)
        else
            whichButtonSelected.postValue(true)
    }

    fun turkishLanguageItemClicked() {
        whichButtonSelected.postValue(false)
        ClientPreferences.inst.language = Languages.TURKISH.language
        ClientPreferences.inst.contextLanguage = ContextLanguages.TURKISH.language
        selected?.invoke()
    }

    fun englishLanguageItemClicked() {
        whichButtonSelected.postValue(true)
        ClientPreferences.inst.language = Languages.English.language
        ClientPreferences.inst.contextLanguage = ContextLanguages.English.language
        selected?.invoke()
    }
}
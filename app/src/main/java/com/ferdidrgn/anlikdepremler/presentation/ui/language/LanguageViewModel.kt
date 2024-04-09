package com.ferdidrgn.anlikdepremler.presentation.ui.language

import androidx.lifecycle.MutableLiveData
import com.ferdidrgn.anlikdepremler.base.BaseViewModel
import com.ferdidrgn.anlikdepremler.tools.enums.ContextLanguages
import com.ferdidrgn.anlikdepremler.tools.enums.Languages
import com.ferdidrgn.anlikdepremler.domain.useCase.GetContactUsEmailUseCase
import com.ferdidrgn.anlikdepremler.tools.ClientPreferences
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
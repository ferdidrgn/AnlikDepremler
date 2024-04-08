package com.ferdidrgn.anlikdepremler.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ferdidrgn.anlikdepremler.tools.getContext
import com.ferdidrgn.anlikdepremler.tools.helpers.LiveEvent
import com.ferdidrgn.anlikdepremler.tools.mainScope

open class BaseViewModel : ViewModel() {
    val error = MutableLiveData<Err?>()
    val eventShowOrHideProgress = MutableLiveData<Boolean>()
    val successMessage = LiveEvent<String?>()
    val errorMessage = LiveEvent<String?>()

    fun showLoading() {
        mainScope {
            eventShowOrHideProgress.value = true
        }
    }

    fun hideLoading() {
        mainScope {
            eventShowOrHideProgress.value = false
        }
    }

    fun serverMessage(err: Err?) {
        error.postValue(
            Err(
                message = err?.message,
                code = err?.code
            )
        )
    }

    fun message(message: Int): String {
        return getContext().resources?.getString(message).toString()
    }
}
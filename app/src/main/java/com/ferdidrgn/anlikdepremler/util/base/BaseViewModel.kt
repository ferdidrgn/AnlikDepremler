package com.ferdidrgn.anlikdepremler.util.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ferdidrgn.anlikdepremler.util.helpers.getContext
import com.ferdidrgn.anlikdepremler.util.helpers.LiveEvent
import com.ferdidrgn.anlikdepremler.util.helpers.mainScope

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
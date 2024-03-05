package com.ferdidrgn.anlikdepremler.base

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ferdidrgn.anlikdepremler.R
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

    fun timeHideLoading() {
        mainScope {
            showLoading()
            Handler(Looper.getMainLooper()).postDelayed({
                hideLoading()
            }, 2000)
        }
    }

    fun serverMessage(err: Err?) {
        error.postValue(
            Err(
                message = message(R.string.error_server),
                code = err?.code
            )
        )
    }
    fun message(message: Int): String {
        return getContext().resources?.getString(message).toString()
    }
}
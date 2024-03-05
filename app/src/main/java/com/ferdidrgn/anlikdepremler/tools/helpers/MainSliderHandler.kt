package com.ferdidrgn.anlikdepremler.tools.helpers

import android.os.Handler

class MainSliderHandler {

    private val handler: Handler = Handler()
    private var onHandlerListener: OnHandlerListener? = null
    private val delayTime = 3000

    fun setHandler(onHandlerListener: OnHandlerListener?) {
        this.onHandlerListener = onHandlerListener
        addChanging()
    }

    fun removeChanging() {
        handler.removeCallbacks(runnable)
    }

    fun addChanging() {
        removeChanging()
        handler.postDelayed(runnable, delayTime.toLong())
    }

    private val runnable: Runnable = object : Runnable {
        override fun run() {
            onHandlerListener?.handlerAction()
            handler.postDelayed(this, delayTime.toLong())
        }
    }
}
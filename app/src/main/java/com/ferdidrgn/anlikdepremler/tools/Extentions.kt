package com.ferdidrgn.anlikdepremler.tools

import android.content.Context
import android.content.res.Configuration
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ferdidrgn.anlikdepremler.tools.helpers.MainSliderHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.hideAndHold() {
    this.visibility = View.INVISIBLE
}

fun ImageView.downloadFromUrl(url: String?) {
    val options = RequestOptions()
    //.error(com.github.dhaval2404.imagepicker.R.drawable.ucrop_ic_cross)
    Glide.with(context)
        .setDefaultRequestOptions(options)
        .load(url)
        .into(this)
}

fun Context.setAppLocale(contextLanguages: String, languages: String): Context {
    val locale = Locale(contextLanguages)
    Locale.setDefault(locale)
    val config = Configuration()
    config.setLocale(locale)
    config.setLayoutDirection(locale)
    ClientPreferences.inst.language = languages
    return createConfigurationContext(config)
}

fun ViewPager2.getPositionAndSendHandler2(
    list: List<Any?>?,
    handler: MainSliderHandler,
    sendPosition: (Int) -> Unit
) {
    var currentPosition = 0

    registerOnPageChangeCallback(object :
        ViewPager2.OnPageChangeCallback() {
        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
            if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                handler.removeChanging()
            } else {
                handler.addChanging()
            }
        }

        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            currentPosition = position
            sendPosition.invoke(position)
        }
    })

    handler.setHandler {
        val listSize = list?.size
        if (listSize != null) {
            if (listSize > 0) {
                if (currentPosition == (listSize - 1)) {
                    currentPosition = 0
                } else {
                    currentPosition++
                }
                setCurrentItem(currentPosition, true)
                sendPosition.invoke(currentPosition)
            }
        }
    }
}

suspend fun CoroutineScope.executeBody(block: suspend CoroutineScope.() -> Unit) {
    try {
        block.invoke(this)
    } catch (e: Exception) {
        e.printStackTrace();
        log(e.message ?: "")
    }
}

fun AppCompatActivity.ioScope(block: suspend CoroutineScope.() -> Unit) =
    lifecycleScope.launch { executeBody(block) }

fun AppCompatActivity.mainScope(block: suspend CoroutineScope.() -> Unit) =
    lifecycleScope.launch { executeBody(block) }

fun ViewModel.ioScope(block: suspend CoroutineScope.() -> Unit) =
    viewModelScope.launch { withContext(Dispatchers.IO) { executeBody(block) } }

fun ViewModel.mainScope(block: suspend CoroutineScope.() -> Unit) =
    viewModelScope.launch { executeBody(block) }

fun Fragment.ioScope(block: suspend CoroutineScope.() -> Unit) =
    lifecycleScope.launch { withContext(Dispatchers.IO) { executeBody(block) } }

fun Fragment.mainScope(block: suspend CoroutineScope.() -> Unit) =
    lifecycleScope.launch { executeBody(block) }
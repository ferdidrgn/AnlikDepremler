package com.ferdidrgn.anlikdepremler.base

import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.ViewDataBinding
import com.ferdidrgn.anlikdepremler.R
import com.ferdidrgn.anlikdepremler.tools.ClientPreferences
import com.ferdidrgn.anlikdepremler.tools.*

abstract class BaseActivity<VM : BaseViewModel, DB : ViewDataBinding> : AppCompatActivity() {

    protected var loadingView: View? = null
    private var viewGroup: ViewGroup? = null
    protected lateinit var viewModel: VM
    protected lateinit var binding: DB
    protected lateinit var networkMonitor: NetworkManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        changeTheme()

        if (ClientPreferences.inst.isDarkMode == true)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) //To make the screen on all the time
        binding = getDataBinding()
        viewModel = getVM().value
        binding.lifecycleOwner = this
        observeData()
        setContentView(binding.root)
        onCreateFinished(savedInstanceState)

        networkMonitor = NetworkManager(this)
        networkMonitor.register()

    }

    protected open fun changeTheme() {}

    protected abstract fun onCreateFinished(savedInstance: Bundle?)

    abstract fun getDataBinding(): DB

    abstract fun getVM(): Lazy<VM>

    fun observeData() {
        viewModel.eventShowOrHideProgress.observe(this) { isLoading ->
            if (isLoading) showProgress() else hideProgress()
        }
    }

    fun hideProgress() {
        loadingView.let {
            viewGroup?.removeView(loadingView)
            loadingView = null
        }
    }

    fun showProgress(viewGroupToAttack: ViewGroup? = null) {
        if (loadingView == null) {
            viewGroup = viewGroupToAttack ?: findViewById(android.R.id.content)
            setupLoadingView(viewGroupToAttack)
            viewGroup?.addView(loadingView)
        }
    }

    private val inflate: LayoutInflater
        get() = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater

    fun setupLoadingView(viewGroupToAttack: ViewGroup?) {
        loadingView = inflate.inflate(R.layout.view_loading, viewGroupToAttack, false)
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(
            ContextWrapper(
                ClientPreferences.inst.contextLanguage?.let { contextLanguage ->
                    newBase?.setAppLocale(contextLanguage, ClientPreferences.inst.language)
                }
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        networkMonitor.unregister()
        binding.unbind()
    }

}
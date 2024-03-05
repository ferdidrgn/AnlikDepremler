package com.ferdidrgn.anlikdepremler.base

import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.ferdidrgn.anlikdepremler.R
import com.ferdidrgn.anlikdepremler.tools.ClientPreferences
import com.ferdidrgn.anlikdepremler.tools.setAppLocale
import com.ferdidrgn.anlikdepremler.tools.showToast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheet<VM : BaseViewModel, DB : ViewDataBinding> :
    BottomSheetDialogFragment() {
    protected lateinit var viewModel: VM
    protected lateinit var binding: DB

    private var isProgressShow = false
    //private var progressDialog: Loading? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.bottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = getDataBinding()
        viewModel = getVM().value
        binding.lifecycleOwner = this
        observeEvents()
        onCreateFinished(savedInstanceState)
        return binding.root
    }

    protected abstract fun onCreateFinished(savedInstance: Bundle?)

    abstract fun getVM(): Lazy<VM>

    abstract fun getDataBinding(): DB

    open fun setStatusBarColorIfPossible(color: Int) {
        dialog?.window?.statusBarColor = color
    }

    private fun showProgress() {
        (requireActivity() as? BaseActivity<*, *>)?.showProgress(dialog?.findViewById(android.R.id.content))
    }

    private fun hideProgress() {
        (requireActivity() as? BaseActivity<*, *>)?.hideProgress()
    }

    private fun observeEvents() {
        viewModel.eventShowOrHideProgress.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading)
                showProgress()
            else
                hideProgress()
        }
        viewModel.error.observe(viewLifecycleOwner) { err ->
            showToast(err?.message.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.unbind()
    }

    override fun onPause() {
        super.onPause()
        hideProgress()
    }

    override fun onAttach(context: Context) {
        super.onAttach(
            ContextWrapper(
                ClientPreferences.inst.contextLanguage?.let { contextLanguage ->
                    ClientPreferences.inst.language?.let { language ->
                        context.setAppLocale(contextLanguage, language)
                    }
                }
            )
        )
    }
}
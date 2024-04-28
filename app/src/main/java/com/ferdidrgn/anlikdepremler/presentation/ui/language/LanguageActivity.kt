package com.ferdidrgn.anlikdepremler.presentation.ui.language

import android.os.Bundle
import androidx.activity.viewModels
import com.ferdidrgn.anlikdepremler.base.BaseActivity
import com.ferdidrgn.anlikdepremler.databinding.ActivityLanguageBinding
import com.ferdidrgn.anlikdepremler.tools.NavHandler
import com.ferdidrgn.anlikdepremler.tools.builderADS
import com.ferdidrgn.anlikdepremler.tools.enums.ToMain
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LanguageActivity : BaseActivity<LanguageViewModel, ActivityLanguageBinding>() {
    override fun getVM(): Lazy<LanguageViewModel> = viewModels()

    override fun getDataBinding(): ActivityLanguageBinding =
        ActivityLanguageBinding.inflate(layoutInflater)

    override fun onCreateFinished(savedInstance: Bundle?) {
        binding.viewModel = viewModel
        binding.customToolbar.backIconOnBackPress(this)
        viewModel.firstState()
        setAds(binding.adView)
        observeEvents()

    }

    private fun observeEvents() {
        viewModel.selected = { NavHandler.instance.toMainActivity(this, ToMain.Home) }
    }

    override fun onResume() {
        super.onResume()

        setAds(binding.adView)
    }
}
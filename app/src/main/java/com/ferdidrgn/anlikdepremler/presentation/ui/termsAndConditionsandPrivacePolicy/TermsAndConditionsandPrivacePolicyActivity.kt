package com.ferdidrgn.anlikdepremler.presentation.ui.termsAndConditionsandPrivacePolicy

import android.os.Bundle
import androidx.activity.viewModels
import com.ferdidrgn.anlikdepremler.base.BaseActivity
import com.ferdidrgn.anlikdepremler.databinding.ActivityTermsAndConditionsandPrivacePolicyBinding
import com.ferdidrgn.anlikdepremler.tools.enums.WhichTermsAndPrivacy
import com.ferdidrgn.anlikdepremler.tools.WHICH_TERMS_PRIVACE
import com.ferdidrgn.anlikdepremler.tools.builderADS
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TermsAndConditionsandPrivacePolicyActivity :
    BaseActivity<TermsAndConditionAndPrivacyPolicyViewModel, ActivityTermsAndConditionsandPrivacePolicyBinding>() {

    override fun getVM(): Lazy<TermsAndConditionAndPrivacyPolicyViewModel> = viewModels()

    override fun getDataBinding(): ActivityTermsAndConditionsandPrivacePolicyBinding =
        ActivityTermsAndConditionsandPrivacePolicyBinding.inflate(layoutInflater)

    override fun onCreateFinished(savedInstance: Bundle?) {
        binding.viewModel = viewModel
        builderADS(this, binding.adView)
        binding.customToolbar.backIconOnBackPress(this)
        observe()
    }


    private fun observe() {
        val type = intent.getSerializableExtra(WHICH_TERMS_PRIVACE)
        viewModel.getHtmlFromUrl(type as WhichTermsAndPrivacy)
    }

}
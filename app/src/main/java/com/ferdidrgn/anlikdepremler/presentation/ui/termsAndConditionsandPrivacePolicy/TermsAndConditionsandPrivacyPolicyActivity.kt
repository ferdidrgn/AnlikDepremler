package com.ferdidrgn.anlikdepremler.presentation.ui.termsAndConditionsandPrivacePolicy

import android.os.Bundle
import androidx.activity.viewModels
import com.ferdidrgn.anlikdepremler.base.BaseActivity
import com.ferdidrgn.anlikdepremler.databinding.ActivityTermsAndConditionsandPrivacePolicyBinding
import com.ferdidrgn.anlikdepremler.tools.enums.WhichTermsAndPrivacy
import com.ferdidrgn.anlikdepremler.tools.WHICH_TERMS_PRIVACY
import com.ferdidrgn.anlikdepremler.tools.builderADS
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TermsAndConditionsandPrivacyPolicyActivity :
    BaseActivity<TermsAndConditionAndPrivacyPolicyViewModel, ActivityTermsAndConditionsandPrivacePolicyBinding>() {

    override fun getVM(): Lazy<TermsAndConditionAndPrivacyPolicyViewModel> = viewModels()

    override fun getDataBinding(): ActivityTermsAndConditionsandPrivacePolicyBinding =
        ActivityTermsAndConditionsandPrivacePolicyBinding.inflate(layoutInflater)

    override fun onCreateFinished(savedInstance: Bundle?) {
        binding.viewModel = viewModel
        setAds(binding.adView)
        binding.customToolbar.backIconOnBackPress(this)
        observe()
    }


    private fun observe() {
        val type = intent.getSerializableExtra(WHICH_TERMS_PRIVACY)
        viewModel.getHtmlFromUrl(type as WhichTermsAndPrivacy)
    }

    override fun onResume() {
        super.onResume()

        setAds(binding.adView)
    }

}
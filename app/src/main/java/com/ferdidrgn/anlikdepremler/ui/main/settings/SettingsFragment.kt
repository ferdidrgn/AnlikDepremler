package com.ferdidrgn.anlikdepremler.ui.main.settings

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.activityViewModels
import com.ferdidrgn.anlikdepremler.R
import com.ferdidrgn.anlikdepremler.base.BaseFragment
import com.ferdidrgn.anlikdepremler.databinding.FragmentSettingsBinding
import com.ferdidrgn.anlikdepremler.enums.WhichTermsAndPrivace
import com.ferdidrgn.anlikdepremler.tools.APP_LINK
import com.ferdidrgn.anlikdepremler.tools.ClientPreferences
import com.ferdidrgn.anlikdepremler.tools.NavHandler
import com.ferdidrgn.anlikdepremler.tools.builderADS
import com.ferdidrgn.anlikdepremler.tools.showToast
import com.ferdidrgn.anlikdepremler.ui.main.MainActivity
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : BaseFragment<SettingsViewModel, FragmentSettingsBinding>() {

    override fun getVM(): Lazy<SettingsViewModel> = activityViewModels()

    override fun getDataBinding(): FragmentSettingsBinding =
        FragmentSettingsBinding.inflate(layoutInflater)

    override fun onCreateFinished(savedInstanceState: Bundle?) {
        binding.viewModel = viewModel

        builderADS(requireContext(), binding.adView)
        clickEvents()
    }

    override fun onResume() {
        super.onResume()

        builderADS(requireContext(), binding.adView)
    }

    private fun clickEvents() {

        viewModel.btnLanguageClicked.observe(viewLifecycleOwner) {
            NavHandler.instance.toLanguageActivity(requireContext())
        }
        viewModel.btnNotificationPermission.observe(viewLifecycleOwner) {

        }
        viewModel.btnOnShareAppClick.observe(viewLifecycleOwner) {
            requireContext().shareLink()
        }
        viewModel.btnRateAppClicked.observe(viewLifecycleOwner) {
            reviewRequest()
        }
        viewModel.btnContactUsClicked.observe(viewLifecycleOwner) {
            contactUs()
        }
        viewModel.btnChangeThemeClicked.observe(viewLifecycleOwner) {
            themeLightOrDark()
        }
        viewModel.btnPrivacePolicyClicked.observe(viewLifecycleOwner) {
            NavHandler.instance.toTermsConditionsAndPrivacePolicyActivity(
                requireContext(),
                WhichTermsAndPrivace.PrivaceAndPolicy
            )
        }
        viewModel.btnTermsAndConditionsClicked.observe(viewLifecycleOwner) {
            NavHandler.instance.toTermsConditionsAndPrivacePolicyActivity(
                requireContext(),
                WhichTermsAndPrivace.TermsAndCondtion
            )
        }
    }

    private fun Context.shareLink() {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, APP_LINK)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun reviewRequest() {
        try {
            val manager = ReviewManagerFactory.create(requireActivity())
            manager.requestReviewFlow()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val reviewInfo: ReviewInfo = task.result
                        manager.launchReviewFlow(requireActivity(), reviewInfo)
                            .addOnFailureListener {
                                viewModel.errorMessage.postValue(it.message)
                            }.addOnCompleteListener {
                                ClientPreferences.inst.reviewStatus = true
                                viewModel.successMessage.postValue(getString(R.string.thank_you_for_review))
                            }
                    }
                }.addOnFailureListener {
                    viewModel.errorMessage.postValue(it.message)
                }
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            viewModel.errorMessage.postValue(e.localizedMessage)
        }
    }

    private fun themeLightOrDark() {

        if (ClientPreferences.inst.isDarkMode == true) {
            ClientPreferences.inst.isDarkMode = false
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            NavHandler.instance.toChangeTheme(requireActivity() as MainActivity)
            (requireActivity() as MainActivity).finish()

        } else {
            ClientPreferences.inst.isDarkMode = true
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            NavHandler.instance.toChangeTheme(requireActivity() as MainActivity)
            (requireActivity() as MainActivity).finish()
        }
    }

    private fun contactUs() {
        val eMail = viewModel.getContactUs()
        val i = Intent(Intent.ACTION_SEND)
        i.type = "message/rfc822"
        i.putExtra(Intent.EXTRA_EMAIL, arrayOf(eMail))
        i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.contact_us_producter))
        i.putExtra(Intent.EXTRA_TEXT, getString(R.string.notification))
        try {
            startActivity(Intent.createChooser(i, getString(R.string.send_mail)))
        } catch (ex: ActivityNotFoundException) {
            showToast(getString(R.string.error_not_mail_installed))
        }
    }
}
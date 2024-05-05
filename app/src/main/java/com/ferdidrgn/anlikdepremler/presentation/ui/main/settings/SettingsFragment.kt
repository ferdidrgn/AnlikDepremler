package com.ferdidrgn.anlikdepremler.presentation.ui.main.settings

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.activityViewModels
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetailsParams
import com.ferdidrgn.anlikdepremler.R
import com.ferdidrgn.anlikdepremler.base.BaseFragment
import com.ferdidrgn.anlikdepremler.databinding.FragmentSettingsBinding
import com.ferdidrgn.anlikdepremler.tools.enums.WhichTermsAndPrivacy
import com.ferdidrgn.anlikdepremler.tools.APP_LINK
import com.ferdidrgn.anlikdepremler.tools.ClientPreferences
import com.ferdidrgn.anlikdepremler.tools.DONATION_SMALL
import com.ferdidrgn.anlikdepremler.tools.NavHandler
import com.ferdidrgn.anlikdepremler.tools.builderADS
import com.ferdidrgn.anlikdepremler.tools.showToast
import com.ferdidrgn.anlikdepremler.presentation.ui.main.MainActivity
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory
import dagger.hilt.android.AndroidEntryPoint
import android.provider.Settings

@AndroidEntryPoint
class SettingsFragment : BaseFragment<SettingsViewModel, FragmentSettingsBinding>(),
    PurchasesUpdatedListener {

    private lateinit var billingClient: BillingClient
    private var isBillingLoadSuccess = false

    override fun getVM(): Lazy<SettingsViewModel> = activityViewModels()

    override fun getDataBinding(): FragmentSettingsBinding =
        FragmentSettingsBinding.inflate(layoutInflater)

    override fun onCreateFinished(savedInstanceState: Bundle?) {
        binding.viewModel = viewModel
        binding.visibility = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

        setAds(binding.adView)
        initBillingClint()
        clickEvents()
    }

    override fun onResume() {
        super.onResume()
        setAds(binding.adView)
    }

    private fun clickEvents() {

        viewModel.apply {
            btnLanguageClicked.observe(viewLifecycleOwner) {
                NavHandler.instance.toLanguageActivity(requireContext())
            }
            btnNotificationPermission.observe(viewLifecycleOwner) {
                openNotificationSettings()
            }
            btnOnShareAppClick.observe(viewLifecycleOwner) {
                requireContext().shareLink()
            }
            btnRateAppClicked.observe(viewLifecycleOwner) {
                reviewRequest()
            }
            btnContactUsClicked.observe(viewLifecycleOwner) {
                contactUs()
            }
            btnChangeThemeClicked.observe(viewLifecycleOwner) {
                themeLightOrDark()
            }
            btnPrivacePolicyClicked.observe(viewLifecycleOwner) {
                goToTermsAndConditionsAction(true)
            }
            btnTermsAndConditionsClicked.observe(viewLifecycleOwner) {
                goToTermsAndConditionsAction(false)
            }

            btnBuyCoffeeUrlClick.observe(viewLifecycleOwner) {
                NavHandler.instance.toBuyCoffeeUrl(requireContext())
            }

            btnBuyCoffeeGooglePlayClick.observe(viewLifecycleOwner) {
                if (isBillingLoadSuccess)
                    loadAllSkus(DONATION_SMALL)
                else
                    showToast(getString(R.string.billing_error))
            }
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

    private fun goToTermsAndConditionsAction(isPrivacyPolicy: Boolean) {
        NavHandler.instance.toTermsConditionsAndPrivacyPolicyActivity(
            requireContext(),
            if (isPrivacyPolicy) WhichTermsAndPrivacy.PrivacyAndPolicy else WhichTermsAndPrivacy.TermsAndCondition
        )
    }

    private fun openNotificationSettings() {
        val intent = Intent()
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, requireActivity().packageName)
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                intent.putExtra("app_package", requireActivity().packageName)
                intent.putExtra("app_uid", requireActivity().applicationInfo.uid)
            }

            else -> {
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                intent.addCategory(Intent.CATEGORY_DEFAULT)
                intent.data = android.net.Uri.parse("package:${requireActivity().packageName}")
            }
        }
        requireContext().startActivity(intent)
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

    private fun initBillingClint() {
        billingClient = BillingClient.newBuilder(requireContext())
            .setListener(this)
            .enablePendingPurchases()
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                // Faturalandırma hizmeti bağlantısı kesildi, gerekirse tekrar bağlanmayı deneyebiliriz.
                showToast(getString(R.string.billing_error))
            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK)
                    isBillingLoadSuccess =
                        true // Faturalandırma bağlantısı başarıyla kuruldu, satın alma işlemlerini gerçekleştirebiliriz.
                else
                    showToast(getString(R.string.billing_error) + " " + billingResult.debugMessage) //"Faturalandırma bağlantısı başarısız: ${billingResult.debugMessage}"
            }
        })
    }

    private fun loadAllSkus(productId: String) {
        val skuDetailsParams = SkuDetailsParams.newBuilder()
            .setSkusList(listOf(productId))
            .setType(BillingClient.SkuType.INAPP)
            .build()

        billingClient.querySkuDetailsAsync(skuDetailsParams) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                skuDetailsList?.let { skuDetails ->
                    if (skuDetails.isNotEmpty()) {
                        val skuDetails = skuDetails[0]
                        val billingFlowParams = BillingFlowParams.newBuilder()
                            .setSkuDetails(skuDetails)
                            .build()

                        billingClient.launchBillingFlow(requireActivity(), billingFlowParams)
                    }
                }
            } else
                showToast(getString(R.string.billing_details_failed) + " " + billingResult.debugMessage)
        }
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null)
            handlePurchases(purchases)
        else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED)
            showToast(getString(R.string.billing_cancel))   //"Kullanıcı satın alma işlemi iptal etti"
        else
            showToast(getString(R.string.billing_failed) + " " + billingResult.debugMessage)
    }

    private fun handlePurchases(purchases: List<Purchase>) {
        for (purchase in purchases) {
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED)
                consumePurchase(purchase) // Satın alınan ürünleri işleme almak için burada gerekli işlemler yapılabilir.
        }
    }

    private fun consumePurchase(purchase: Purchase) {
        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        billingClient.consumeAsync(consumeParams) { billingResult, purchaseToken ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchaseToken != null)
                showToast(getString(R.string.billing_consume_success)) // Satın alınan ürün başarıyla tüketildi, gerekli işlemler yapılabilir.
            else
                showToast(getString(R.string.billing_consume_failed) + " " + billingResult.debugMessage)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        billingClient.endConnection()
    }
}
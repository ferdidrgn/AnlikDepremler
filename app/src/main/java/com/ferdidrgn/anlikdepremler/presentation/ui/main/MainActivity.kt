package com.ferdidrgn.anlikdepremler.presentation.ui.main

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.ferdidrgn.anlikdepremler.R
import com.ferdidrgn.anlikdepremler.base.BaseActivity
import com.ferdidrgn.anlikdepremler.databinding.ActivityMainBinding
import com.ferdidrgn.anlikdepremler.tools.enums.ToMain
import com.ferdidrgn.anlikdepremler.tools.ClientPreferences
import com.ferdidrgn.anlikdepremler.tools.TO_MAIN
import com.ferdidrgn.anlikdepremler.tools.showToast
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {

    private lateinit var navController: NavController
    private val MY_CODE = 123
    private lateinit var appUpdateManager: AppUpdateManager
    private val updateType = AppUpdateType.IMMEDIATE //IMMEDIATE (force) or FLEXIBLE (recommend)

    override fun getVM(): Lazy<MainViewModel> = viewModels()

    override fun getDataBinding(): ActivityMainBinding =
        ActivityMainBinding.inflate(layoutInflater)

    override fun onCreateFinished(savedInstance: Bundle?) {

        getNavHost()
        checkForAppUpdate()
        askNotificationPermission()
        reviewPopUp()
        whereToFromIntentActivity()
    }


    override fun onResume() {
        super.onResume()
        if (updateType == AppUpdateType.IMMEDIATE)
            appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
                if (info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS)
                    appUpdateManager.startUpdateFlowForResult(info, updateType, this, MY_CODE)
            }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (updateType == AppUpdateType.FLEXIBLE)
            appUpdateManager.unregisterListener(installStateUpdatedListener)
        networkMonitor.unregister()
        binding.unbind()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == MY_CODE)
            if (resultCode != RESULT_OK)
                showToast(getString(R.string.error_update_failed))
    }

    private fun whereToFromIntentActivity() {
        val toMain = intent.getSerializableExtra(TO_MAIN) as ToMain?
        whereToGetBottomNavItem(toMain ?: return)
    }

    fun whereToGetBottomNavItem(toMain: ToMain) {
        when (toMain) {
            ToMain.Home -> binding.bottomNav.selectedItemId = R.id.homeFragmentNav

            ToMain.NearEarthquake -> binding.bottomNav.selectedItemId =
                R.id.nearEarthquakeFragmentNav

            ToMain.NowEarthquake -> binding.bottomNav.selectedItemId = R.id.nowEarthquakeFragmentNav

            ToMain.Settings -> binding.bottomNav.selectedItemId = R.id.settingsFragmentNav
        }
    }

    private fun getNavHost() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNav =
            findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNav)
        bottomNav.setupWithNavController(navController)
    }

    private fun checkForAppUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)

        if (updateType == AppUpdateType.FLEXIBLE)
            appUpdateManager.registerListener(installStateUpdatedListener)

        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            val isUpdteAvailable = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            val isUpdateAllowed = when (updateType) {
                AppUpdateType.FLEXIBLE -> info.isFlexibleUpdateAllowed
                AppUpdateType.IMMEDIATE -> info.isImmediateUpdateAllowed
                else -> false
            }

            if (isUpdteAvailable && isUpdateAllowed)
                appUpdateManager.startUpdateFlowForResult(info, updateType, this, MY_CODE)
        }
    }

    private val installStateUpdatedListener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            showToast(getString(R.string.success_update_downloaded))
        }
        lifecycleScope.launch {
            delay(5.seconds)
            appUpdateManager.completeUpdate()
        }
    }


    private fun reviewPopUp() {
        if (ClientPreferences.inst.reviewStatus.not()) {
            if (ClientPreferences.inst.reviewCounter % 3 == 0) {
                try {
                    val manager = ReviewManagerFactory.create(this)
                    manager.requestReviewFlow()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val reviewInfo: ReviewInfo = task.result
                                manager.launchReviewFlow(this, reviewInfo)
                                    .addOnFailureListener {
                                        viewModel.errorMessage.postValue(it.message)
                                    }.addOnCompleteListener {
                                        viewModel.successMessage.postValue(getString(R.string.thank_you_for_review))
                                    }
                            }
                        }.addOnFailureListener {
                            viewModel.errorMessage.postValue(it.message)
                        }
                } catch (e: ActivityNotFoundException) {
                    viewModel.errorMessage.postValue(e.localizedMessage)
                }
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted)
        //showToast(getString(R.string.success_permission_granted_notification))
        else {
            //showToast(getString(R.string.permission_denied_notification))
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            )
            // İzin zaten verilmişse yapılacak işlemler
            else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS))
            // Kullanıcıya izin talebinin nedenini açıklamak için uygun bir durumdaysa yapılacak işlemler
            else
            // İzin talebinde bulun
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        //It works in reverse. I don't know why.
        if (binding.bottomNav.selectedItemId == R.id.homeFragmentNav)
            binding.bottomNav.selectedItemId = R.id.homeFragmentNav
        else
            finish()
    }

    override fun changeTheme() = setTheme(R.style.Theme_AnlikDepremler)
}
package com.ferdidrgn.anlikdepremler.tools

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ferdidrgn.anlikdepremler.R
import com.ferdidrgn.anlikdepremler.base.BasePopUp
import com.ferdidrgn.anlikdepremler.presentation.ui.main.MainActivity

class NetworkManager(activity: AppCompatActivity) {
    val contextBase = activity.applicationContext
    val activityBase = activity

    private val connectivityManager: ConnectivityManager =
        contextBase.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val networkRequest: NetworkRequest =
        NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR).build()


    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        // network is available for use
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            Log.d("NET CONNECTION", "SUCCESS")
        }

        // Network capabilities have changed for the network
        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            val unmetered =
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
        }

        // lost network connection
        override fun onLost(network: Network) {
            super.onLost(network)
            Log.d("NET LOST", "ERROR")
            showToast("İnternet Bağlantısı Yok")
            showNetworkLostDialog()
        }
    }

    fun register() {
        val networkRequest2: NetworkRequest =
            NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR).build()
        val networkRequest = NetworkRequest.Builder().build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
        connectivityManager.registerNetworkCallback(networkRequest2, networkCallback)
    }

    fun unregister() {
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        } catch (e: Exception) {
            // Handle exception if callback is not registered
        }
    }

    private fun showNetworkLostDialog() {
        val popUp = BasePopUp()
        popUp.apply {
            setPositiveText(contextBase.getString(R.string.resend_code))
            setNegativeText(contextBase.getString(R.string.cancel))
            setDesc(contextBase.applicationContext.getString(R.string.resend_code))
            setOnPositiveClick {
                restartApp()
                dismiss()
            }
            setOnNegativeClick {
                closeApp()
                dismiss()
            }
        }
        popUp.show(activityBase.supportFragmentManager, "")
    }

    private fun restartApp() {
        val intent = Intent(activityBase, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        activityBase.startActivity(intent)
        activityBase.finish()
    }

    private fun closeApp() {
        activityBase.finishAffinity()
    }
}

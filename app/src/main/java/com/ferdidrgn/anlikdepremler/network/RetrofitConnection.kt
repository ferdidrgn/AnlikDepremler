package com.ferdidrgn.anlikdepremler.network

import com.ferdidrgn.anlikdepremler.BuildConfig
import com.ferdidrgn.anlikdepremler.enums.Environment
import com.ferdidrgn.anlikdepremler.tools.ClientPreferences
import com.ferdidrgn.anlikdepremler.tools.log
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response
import java.net.URISyntaxException

class HeaderInterceptor : Interceptor {

    @Volatile
    private var host = BuildConfig.API_ADDRESS.toHttpUrlOrNull()

    private fun setHostBaseUrl() {
        when (ClientPreferences.inst.baseUrl) {
            Environment.test.url -> {
                host = Environment.test.url.toHttpUrlOrNull()
            }

            Environment.preprod.url -> {
                host = Environment.preprod.url.toHttpUrlOrNull()
            }

            Environment.prod.url -> {
                host = Environment.prod.url.toHttpUrlOrNull()
            }
        }
    }


    override fun intercept(chain: Interceptor.Chain): Response {
        setHostBaseUrl()
        var token: String = if (ClientPreferences.inst.token.isNullOrEmpty().not()) {
            String.format("Bearer %s", ClientPreferences.inst.token)
        }else{
            String.format("Bearer %s", ClientPreferences.inst.guestToken)
        }
        log(token)

        var request = chain.request()

        if (host != null) {
            var newUrl: HttpUrl? = null
            try {
                newUrl = request.url.newBuilder()
                    .scheme(host!!.scheme)
                    .host(host!!.toUrl().toURI().host)
                    .build()

                request = request.newBuilder()
                    //.addHeader(CLIENT, MOBILE)
                    //.addHeader(AUTHORIZATION, token)
                    //.addHeader(DEVICE_TYPE, PHONE)
                    //.addHeader(DEVICE_MODEL, android.os.Build.MODEL)
                    //.addHeader(APP_VERSION,BuildConfig.VERSION_NAME)
                    //.addHeader(OS, ANDROID)
                    //.addHeader(OS_VERSION, android.os.Build.VERSION.RELEASE)
                    //.addHeader(CARRIER, ClientPreferences.inst.carrier ?: "")
                    //.addHeader(CONNECTION, ClientPreferences.inst.connection ?: "")
                    //.addHeader(ACCEPT_LANGUAGE, ClientPreferences.inst.language ?: "")
                    .url(newUrl)
                    .build()


            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }
        }

        return chain.proceed(request)
    }
}
package ke.co.coopbank.financialliteracy.api

import android.content.SharedPreferences
import ke.co.coopbank.financialliteracy.utilities.ACCESS_TOKEN
import okhttp3.*
import java.io.IOException

class RequestInterceptor(private val mSharedPrefManager: SharedPreferences) : Interceptor,
    Authenticator {

    /**
     * Interceptor class for setting of the headers for every request
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val accessToken = mSharedPrefManager.getString(ACCESS_TOKEN, null)
        request = request.newBuilder()
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer $accessToken")
            .build()
        return chain.proceed(request)
    }

    /**
     * Authenticator for when the authToken need to be refresh and updated
     * everytime we get a 401 error code
     */
    @Throws(IOException::class)
    override fun authenticate(route: Route?, response: Response): Request? {
        var requestAvailable: Request? = null
        val accessToken = mSharedPrefManager.getString(ACCESS_TOKEN, null)
        try {
            requestAvailable = response.request.newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build()
            return requestAvailable
        } catch (ex: Exception) {
        }
        return requestAvailable
    }
}
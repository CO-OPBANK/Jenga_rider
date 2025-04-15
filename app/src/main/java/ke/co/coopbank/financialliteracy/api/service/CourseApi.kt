package ke.co.coopbank.financialliteracy.api.service

import android.os.Looper
import com.apollographql.apollo.ApolloClient
import ke.co.coopbank.financialliteracy.utilities.GRAPHQL_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class CourseApi(
    private val httpLoggingInterceptor: HttpLoggingInterceptor
) {
    fun getApolloClient(): ApolloClient {
        check(Looper.myLooper() == Looper.getMainLooper()) {
            "Only the main thread can get the apolloClient instance"
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()

        return ApolloClient.builder()
            .serverUrl(GRAPHQL_URL)
            .okHttpClient(okHttpClient)
            .build()
    }
}
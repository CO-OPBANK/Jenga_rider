package ke.co.coopbank.financialliteracy.di

import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ke.co.coopbank.financialliteracy.api.service.CourseApi
import ke.co.coopbank.financialliteracy.api.RequestInterceptor
import ke.co.coopbank.financialliteracy.api.service.AuthService
import ke.co.coopbank.financialliteracy.api.service.ReportService
import ke.co.coopbank.financialliteracy.utilities.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        preferences: SharedPreferences
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(RequestInterceptor(preferences))
            .authenticator(RequestInterceptor(preferences))
            .connectTimeout(CONNECT_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun provideAuthService(okHttpClient: OkHttpClient): AuthService {
        return Retrofit.Builder()
            .baseUrl(AUTH_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
            .create(AuthService::class.java)
    }

    @Singleton
    @Provides
    fun provideReportService(okHttpClient: OkHttpClient): ReportService {
        return Retrofit.Builder()
            .baseUrl(REPORT_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
            .create(ReportService::class.java)
    }

    @Singleton
    @Provides
    fun provideCourseService(httpLoggingInterceptor: HttpLoggingInterceptor) =
        CourseApi(httpLoggingInterceptor)
}
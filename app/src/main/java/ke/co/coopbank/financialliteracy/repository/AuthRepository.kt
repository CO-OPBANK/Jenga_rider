package ke.co.coopbank.financialliteracy.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import ke.co.coopbank.financialliteracy.AppExecutors
import ke.co.coopbank.financialliteracy.api.ApiResponse
import ke.co.coopbank.financialliteracy.api.NetworkBoundResource
import ke.co.coopbank.financialliteracy.api.Resource
import ke.co.coopbank.financialliteracy.api.request.OtpRequest
import ke.co.coopbank.financialliteracy.api.request.ProfileRequest
import ke.co.coopbank.financialliteracy.api.request.RegisterRequest
import ke.co.coopbank.financialliteracy.api.request.VerifyOtpRequest
import ke.co.coopbank.financialliteracy.api.response.OtpResponse
import ke.co.coopbank.financialliteracy.api.response.PhotoUploadResponse
import ke.co.coopbank.financialliteracy.api.response.VerifyOtpResponse
import ke.co.coopbank.financialliteracy.api.service.AuthService
import ke.co.coopbank.financialliteracy.db.dao.CountyDao
import ke.co.coopbank.financialliteracy.db.dao.SaccoDao
import ke.co.coopbank.financialliteracy.db.dao.UserDao
import ke.co.coopbank.financialliteracy.model.County
import ke.co.coopbank.financialliteracy.model.Sacco
import ke.co.coopbank.financialliteracy.model.Token
import ke.co.coopbank.financialliteracy.model.User
import ke.co.coopbank.financialliteracy.utilities.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val authService: AuthService,
    private val appExecutors: AppExecutors,
    private val userDao: UserDao,
    private val saccoDao: SaccoDao,
    private val countyDao: CountyDao,
) {
    private val rateLimit = RateLimiter<String>(10, TimeUnit.MINUTES)

    fun getSaccos(): LiveData<Resource<List<Sacco>>> {
        val key = "saccos"
        return object : NetworkBoundResource<List<Sacco>, List<Sacco>>(appExecutors) {
            override fun saveCallResult(item: List<Sacco>) {
                saccoDao.insert(item)
            }

            override fun shouldFetch(data: List<Sacco>?): Boolean {
                return data == null || data.isEmpty() || rateLimit.shouldFetch(key)
            }

            override fun loadFromDb() = saccoDao.all()

            override fun createCall() = authService.getSaccos()

            override fun onFetchFailed() {
                rateLimit.reset(key)
            }
        }.asLiveData()
    }

    fun getCounties(): LiveData<Resource<List<County>>> {
        val key = "counties"
        return object : NetworkBoundResource<List<County>, List<County>>(appExecutors) {
            override fun saveCallResult(item: List<County>) {
                countyDao.insert(item)
            }

            override fun shouldFetch(data: List<County>?): Boolean {
                return data == null || data.isEmpty() || rateLimit.shouldFetch(key)
            }

            override fun loadFromDb() = countyDao.getCounties()

            override fun createCall() = authService.getCounties()

            override fun onFetchFailed() {
                rateLimit.reset(key)
            }
        }.asLiveData()
    }

    fun sendOtp(phoneNumber: String): LiveData<Resource<OtpResponse>> {
        return object :
            NetworkBoundResource<OtpResponse, OtpResponse>(appExecutors) {

            private var resultsDb: OtpResponse? = null

            override fun saveCallResult(item: OtpResponse) {
                resultsDb = item
            }

            override fun shouldFetch(data: OtpResponse?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<OtpResponse> {
                return if (resultsDb == null) {
                    AbsentLiveData.create()
                } else {
                    object : LiveData<OtpResponse>() {
                        override fun onActive() {
                            super.onActive()
                            value = resultsDb
                        }
                    }
                }
            }

            override fun createCall(): LiveData<ApiResponse<OtpResponse>> {
                return authService.resendOtp(OtpRequest(phoneNumber))
            }

        }.asLiveData()
    }

    fun verifyOtp(phoneNumber: String, otp: String): LiveData<Resource<VerifyOtpResponse>> {
        return object :
            NetworkBoundResource<VerifyOtpResponse, VerifyOtpResponse>(appExecutors) {

            private var resultsDb: VerifyOtpResponse? = null

            override fun saveCallResult(item: VerifyOtpResponse) {
                resultsDb = item
                item.user?.let { userDao.insert(it) }
                sharedPreferences.edit {
                    putString(ACCESS_TOKEN, item.accessToken)
                    putString(USER_ID, item.user?.id.toString())
                    putString(CURRENT_USER_PHONE, item.user?.phoneNumber)
                    apply()
                }
            }

            override fun shouldFetch(data: VerifyOtpResponse?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<VerifyOtpResponse> {
                return if (resultsDb == null) {
                    AbsentLiveData.create()
                } else {
                    object : LiveData<VerifyOtpResponse>() {
                        override fun onActive() {
                            super.onActive()
                            value = resultsDb
                        }
                    }
                }
            }

            override fun createCall(): LiveData<ApiResponse<VerifyOtpResponse>> {
                return authService.verifyOtp(VerifyOtpRequest(phoneNumber, otp))
            }

        }.asLiveData()
    }

    fun register(registerRequest: RegisterRequest): LiveData<Resource<Token>> {
        return object :
            NetworkBoundResource<Token, Token>(appExecutors) {

            private var resultsDb: Token? = null

            override fun saveCallResult(item: Token) {
                resultsDb = item
                sharedPreferences.edit {
                    putString(ACCESS_TOKEN, item.accessToken)
                    apply()
                }
            }

            override fun shouldFetch(data: Token?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<Token> {
                return if (resultsDb == null) {
                    AbsentLiveData.create()
                } else {
                    object : LiveData<Token>() {
                        override fun onActive() {
                            super.onActive()
                            value = resultsDb
                        }
                    }
                }
            }

            override fun createCall(): LiveData<ApiResponse<Token>> {
                return authService.register(registerRequest)
            }

        }.asLiveData()
    }

    fun getUser(phoneNumber: String): LiveData<Resource<User>> {
        return object : NetworkBoundResource<User, User>(appExecutors) {
            override fun saveCallResult(item: User) {
                userDao.insert(item)
                sharedPreferences.edit {
                    putString(CURRENT_USER_PHONE, item.phoneNumber)
                    putString(USER_ID, item.id.toString())
                    apply()
                }
            }

            override fun shouldFetch(data: User?): Boolean {
                return true
            }

            override fun loadFromDb() = userDao.findByPhone(phoneNumber)

            override fun createCall() = authService.getUser()

        }.asLiveData()
    }

    fun getLoggedInUser(): LiveData<User>? {
        val phoneNumber = sharedPreferences.getString(CURRENT_USER_PHONE, null)
        return phoneNumber?.let { userDao.findByPhone(it) }
    }

    fun updateProfile(profileRequest: ProfileRequest): LiveData<Resource<Token>> {
        return object :
            NetworkBoundResource<Token, Token>(appExecutors) {

            private var resultsDb: Token? = null

            override fun saveCallResult(item: Token) {
                resultsDb = item
                sharedPreferences.edit {
                    putString(ACCESS_TOKEN, item.accessToken)
                    apply()
                }
            }

            override fun shouldFetch(data: Token?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<Token> {
                return if (resultsDb == null) {
                    AbsentLiveData.create()
                } else {
                    object : LiveData<Token>() {
                        override fun onActive() {
                            super.onActive()
                            value = resultsDb
                        }
                    }
                }
            }

            override fun createCall(): LiveData<ApiResponse<Token>> {
                return authService.updateProfile(profileRequest)
            }

        }.asLiveData()
    }

    fun uploadPhoto(file: File): LiveData<Resource<PhotoUploadResponse>> {
        val phoneNumber = sharedPreferences.getString(CURRENT_USER_PHONE, null)
        val requestFile: RequestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("profilePicture", file.name, requestFile)

        return object :
            NetworkBoundResource<PhotoUploadResponse, PhotoUploadResponse>(appExecutors) {

            private var resultsDb: PhotoUploadResponse? = null

            override fun saveCallResult(item: PhotoUploadResponse) {
                resultsDb = item
                phoneNumber?.let { userDao.updatePhotoUrl(it, item.profilePicture) }
            }

            override fun shouldFetch(data: PhotoUploadResponse?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<PhotoUploadResponse> {
                return if (resultsDb == null) {
                    AbsentLiveData.create()
                } else {
                    object : LiveData<PhotoUploadResponse>() {
                        override fun onActive() {
                            super.onActive()
                            value = resultsDb
                        }
                    }
                }
            }

            override fun createCall(): LiveData<ApiResponse<PhotoUploadResponse>> {
                return authService.uploadPhoto(body)
            }

        }.asLiveData()
    }

    suspend fun setBotAccount(item: VerifyOtpResponse) {
        item.user?.let { userDao.save(it) }
        sharedPreferences.edit {
            putString(ACCESS_TOKEN, item.accessToken)
            putString(USER_ID, item.user?.id.toString())
            putString(CURRENT_USER_PHONE, item.user?.phoneNumber)
            apply()
        }
    }
}
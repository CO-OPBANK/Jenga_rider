package ke.co.coopbank.financialliteracy.api.service

import androidx.lifecycle.LiveData
import ke.co.coopbank.financialliteracy.api.ApiResponse
import ke.co.coopbank.financialliteracy.api.request.OtpRequest
import ke.co.coopbank.financialliteracy.api.request.ProfileRequest
import ke.co.coopbank.financialliteracy.api.request.RegisterRequest
import ke.co.coopbank.financialliteracy.api.request.VerifyOtpRequest
import ke.co.coopbank.financialliteracy.api.response.OtpResponse
import ke.co.coopbank.financialliteracy.api.response.PhotoUploadResponse
import ke.co.coopbank.financialliteracy.api.response.VerifyOtpResponse
import ke.co.coopbank.financialliteracy.model.County
import ke.co.coopbank.financialliteracy.model.Sacco
import ke.co.coopbank.financialliteracy.model.Token
import ke.co.coopbank.financialliteracy.model.User
import okhttp3.MultipartBody
import retrofit2.http.*

interface AuthService {

    @POST("api/user/resend-otp")
    fun resendOtp(@Body otpRequest: OtpRequest): LiveData<ApiResponse<OtpResponse>>

    @POST("api/user/verify-otp")
    fun verifyOtp(@Body verifyOtpRequest: VerifyOtpRequest): LiveData<ApiResponse<VerifyOtpResponse>>

    @POST("api/user/register")
    fun register(@Body registerRequest: RegisterRequest): LiveData<ApiResponse<Token>>

    @GET("api/user")
    fun getUser(): LiveData<ApiResponse<User>>

    @GET("api/counties")
    fun getCounties(): LiveData<ApiResponse<List<County>>>

    @GET("api/saccos")
    fun getSaccos(): LiveData<ApiResponse<List<Sacco>>>

    @POST("api/user/update-profile")
    fun updateProfile(@Body profileRequest: ProfileRequest): LiveData<ApiResponse<Token>>

    @Multipart
    @POST("api/user/upload-profile-picture")
    fun uploadPhoto(@Part file: MultipartBody.Part): LiveData<ApiResponse<PhotoUploadResponse>>

}
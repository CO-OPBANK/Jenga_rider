package ke.co.coopbank.financialliteracy.ui.activity.auth

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.co.coopbank.financialliteracy.api.Resource
import ke.co.coopbank.financialliteracy.api.request.RegisterRequest
import ke.co.coopbank.financialliteracy.api.response.OtpResponse
import ke.co.coopbank.financialliteracy.api.response.VerifyOtpResponse
import ke.co.coopbank.financialliteracy.model.County
import ke.co.coopbank.financialliteracy.model.Sacco
import ke.co.coopbank.financialliteracy.model.Token
import ke.co.coopbank.financialliteracy.model.User
import ke.co.coopbank.financialliteracy.repository.AuthRepository
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.KFunction1

@HiltViewModel
class AuthViewModel @Inject internal constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    var otpResource: LiveData<Resource<OtpResponse>>? = null
    var verifyOtpResource: LiveData<Resource<VerifyOtpResponse>>? = null
    var saccosResource: LiveData<Resource<List<Sacco>>>? = null
    var countiesResource: LiveData<Resource<List<County>>>? = null
    var registerResource: LiveData<Resource<Token>>? = null
    var userResource: LiveData<Resource<User>>? = null

    fun sendOtp(phoneNumber: String) {
        otpResource = authRepository.sendOtp(phoneNumber)
    }

    fun verifyOtp(phoneNumber: String, otp: String) {
        verifyOtpResource = authRepository.verifyOtp(phoneNumber, otp)
    }

    fun getSaccos() {
        saccosResource = authRepository.getSaccos();
    }

    fun getCounties() {
        countiesResource = authRepository.getCounties();
    }

    fun register(registerRequest: RegisterRequest) {
        registerResource = authRepository.register(registerRequest)
    }

    fun getUser(phoneNumber: String) {
        userResource = authRepository.getUser(phoneNumber)
    }

    fun setBotAccount(user: VerifyOtpResponse) {
        viewModelScope.launch {
            authRepository.setBotAccount(user)
        }
    }
}
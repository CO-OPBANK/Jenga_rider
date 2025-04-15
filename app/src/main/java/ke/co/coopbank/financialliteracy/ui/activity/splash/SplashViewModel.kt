package ke.co.coopbank.financialliteracy.ui.activity.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.co.coopbank.financialliteracy.model.User
import ke.co.coopbank.financialliteracy.repository.AuthRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
@ExperimentalCoroutinesApi
@HiltViewModel
class SplashViewModel @Inject internal constructor(
    authRepository: AuthRepository,
) : ViewModel() {
    val user: LiveData<User>? = authRepository.getLoggedInUser()
}
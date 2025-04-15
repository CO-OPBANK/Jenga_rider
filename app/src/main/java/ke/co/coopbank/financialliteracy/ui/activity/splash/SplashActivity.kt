package ke.co.coopbank.financialliteracy.ui.activity.splash

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import dagger.hilt.android.AndroidEntryPoint
import ke.co.coopbank.financialliteracy.R
import ke.co.coopbank.financialliteracy.ui.activity.auth.AuthActivity
import ke.co.coopbank.financialliteracy.ui.activity.main.MainActivity
import ke.co.coopbank.financialliteracy.ui.activity.onboarding.OnBoardingActivity
import ke.co.coopbank.financialliteracy.utilities.ACCESS_TOKEN
import ke.co.coopbank.financialliteracy.utilities.USER_ID
import ke.co.coopbank.financialliteracy.utilities.USER_ONBOARDED
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (sharedPreferences.getString(ACCESS_TOKEN, null) != null) {
            viewModel.user?.observe(this) {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (it != null) {

                        sharedPreferences.edit {
                            putString(USER_ID, it.id.toString())
                            apply()
                        }

                        startActivity(Intent(this, MainActivity::class.java))
                        finish()

                    } else {
                        if (!sharedPreferences.getBoolean(USER_ONBOARDED, false)) {
                            startActivity(Intent(this, OnBoardingActivity::class.java))
                        } else {
                            startActivity(Intent(this, AuthActivity::class.java))
                        }
                        finish()
                    }
                }, 1500)
            }
        } else {
            if (!sharedPreferences.getBoolean(USER_ONBOARDED, false)) {
                startActivity(Intent(this, OnBoardingActivity::class.java))
            } else {
                startActivity(Intent(this, AuthActivity::class.java))
            }
            finish()
        }
    }
}
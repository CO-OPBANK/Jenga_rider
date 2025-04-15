package ke.co.coopbank.financialliteracy.ui.activity.course

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import dagger.hilt.android.AndroidEntryPoint
import ke.co.coopbank.financialliteracy.databinding.ActivityCompleteCourseBinding
import ke.co.coopbank.financialliteracy.ui.activity.main.MainActivity
import ke.co.coopbank.financialliteracy.ui.activity.main.MainViewModel
import ke.co.coopbank.financialliteracy.utilities.COURSE_ID
import ke.co.coopbank.financialliteracy.utilities.CURRENT_USER_PHONE
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class CompleteCourseActivity : AppCompatActivity() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCompleteCourseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val courseId = intent.getStringExtra(COURSE_ID)

        courseId?.let {
            viewModel.setCourseProgressComplete(courseId)
        }

        sharedPreferences.edit {
            putInt(COURSE_ID, 0)
            apply()
        }

        binding.buttonSubmit.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
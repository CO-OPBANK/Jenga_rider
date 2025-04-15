package ke.co.coopbank.financialliteracy.ui.activity.course

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.util.Util
import dagger.hilt.android.AndroidEntryPoint
import ke.co.coopbank.financialliteracy.databinding.ActivityCourseBinding
import ke.co.coopbank.financialliteracy.ui.activity.main.MainViewModel
import ke.co.coopbank.financialliteracy.utilities.COURSE_ID
import ke.co.coopbank.financialliteracy.utilities.CURRENT_USER_PHONE
import ke.co.coopbank.financialliteracy.utilities.QUIZ_ID
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class CourseActivity : AppCompatActivity() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityCourseBinding
    private var hasQuiz: Boolean = false
    private var player: SimpleExoPlayer? = null
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0
    private var playUrl: String? = null
    private var phoneNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        player = SimpleExoPlayer.Builder(this).build()
        binding.videoView.player = player

        val courseId = intent.getStringExtra(COURSE_ID)
        val quizId = intent.getStringExtra(QUIZ_ID)
        phoneNumber = sharedPreferences.getString(CURRENT_USER_PHONE, "")

        with(viewModel) {
            courseId?.let {
                setCourseId(it)
                viewModel.queryCourse(it)
            }

            quizId?.let {
                setQuizId(it)
                courseId?.let { it1 -> viewModel.queryQuizzes(it1, it) }
            }
        }

        observeLiveData()

        binding.buttonNextCourse.setOnClickListener {
            if (hasQuiz) {
                startActivity(Intent(this, QuizActivity::class.java).apply {
                    putExtra(COURSE_ID, courseId)
                    putExtra(QUIZ_ID, quizId)
                })
            } else {
                startActivity(Intent(this, CompleteCourseActivity::class.java).apply {
                    putExtra(COURSE_ID, courseId)
                })
                finish()
            }
        }

        val screenHeight = binding.scrollView.scrollY
        val savedHeight = sharedPreferences.getInt(COURSE_ID, 0)

        if (savedHeight > screenHeight) {
            binding.scrollView.postDelayed({
                binding.scrollView.scrollY = savedHeight
            }, 200)
        }

        binding.scrollView.viewTreeObserver.addOnScrollChangedListener {
            val scrollY = binding.scrollView.scrollY
            if (scrollY > screenHeight) {
                sharedPreferences.edit {
                    putInt(COURSE_ID, scrollY)
                    apply()
                }
            }

            if (!binding.scrollView.canScrollVertically(1)) {
                binding.bottomLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun observeLiveData() {
        viewModel.localCourse.observe(this) { course ->
            supportActionBar!!.title = course.title
            binding.course = course
            if (course.progress != null) {
                if (course.progress == 0) {
                    viewModel.addOngoingProgress(course.id, 20)
                }
            }
        }

        viewModel.countQuiz.observe(this) {
            if (it != null) {
                if (it > 0) {
                    hasQuiz = true
                }
            }
        }

        viewModel.media.observe(this) {
            binding.media = it
            if (it != null) {
                if (it.url != null) {
                    binding.videoView.visibility = View.VISIBLE
                    playUrl = it.url
                    initializePlayer()
                } else {
                    binding.videoView.visibility = View.GONE
                }
            }
        }

        viewModel.course.observe(this) {}
        viewModel.quizzes.observe(this) {}
    }

    private fun initializePlayer() {
        if (playUrl != null) {
            val mediaItem = MediaItem.fromUri(playUrl!!)
            player?.setMediaItem(mediaItem)
            player?.playWhenReady = playWhenReady
            player?.seekTo(currentWindow, playbackPosition)
            player?.prepare()
        }
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initializePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT < 24 || player == null) {
            initializePlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }

    private fun releasePlayer() {
        if (player != null) {
            playWhenReady = player!!.playWhenReady
            playbackPosition = player!!.currentPosition
            currentWindow = player!!.currentWindowIndex
            player!!.release()
            player = null
        }
    }
}
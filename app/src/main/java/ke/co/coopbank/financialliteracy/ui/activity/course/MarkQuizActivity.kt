package ke.co.coopbank.financialliteracy.ui.activity.course

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import dagger.hilt.android.AndroidEntryPoint
import ke.co.coopbank.financialliteracy.api.Status
import ke.co.coopbank.financialliteracy.api.request.CourseProgressRequest
import ke.co.coopbank.financialliteracy.api.request.Result
import ke.co.coopbank.financialliteracy.databinding.ActivityMarkQuizBinding
import ke.co.coopbank.financialliteracy.model.Question
import ke.co.coopbank.financialliteracy.ui.activity.main.MainActivity
import ke.co.coopbank.financialliteracy.ui.activity.main.MainViewModel
import ke.co.coopbank.financialliteracy.utilities.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MarkQuizActivity : AppCompatActivity() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMarkQuizBinding
    private lateinit var adapter: MarkQuizAdapter
    private var progressDialog: ProgressDialog? = null
    private lateinit var courseId: String
    private lateinit var quizId: String
    private lateinit var userId: String
    private var scores: Int = 0
    private lateinit var questions: List<Question>
    private var results: MutableList<Result> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarkQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        adapter = MarkQuizAdapter(this)
        binding.recyclerView.adapter = adapter

        courseId = intent.getStringExtra(COURSE_ID).toString()
        quizId = intent.getStringExtra(QUIZ_ID).toString()
        userId = sharedPreferences.getString(USER_ID, null).toString()

        viewModel.getQuestions(quizId).observeOnce(this) { questions ->
            this.questions = questions

            adapter.submitList(questions)

            for (question in questions) {
                val userAnswers = question.userAnswers!!.split(",").map { it.trim() }
                if (isEqual(question.answers, userAnswers)) {
                    scores += 1
                }

                val answers = mutableListOf<Int>()
                userAnswers.forEach {
                    answers.add(it.toInt())
                }

                results.add(
                    Result(
                        id = question.id,
                        answers = answers
                    )
                )
            }

            binding.scores = scores
            binding.total = questions.size
            binding.executePendingBindings()
        }

        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Submitting quizzes. Please wait...")
        progressDialog!!.setCanceledOnTouchOutside(false)

        binding.buttonContinue.setOnClickListener {
            if (scores == questions.size) {
                submitAnswers()
            } else {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Failed")
                builder.setMessage("Unable to proceed to next course. Please ensure you have passed all the questions.")
                builder.setPositiveButton("Retake") { dialogInterface, _ ->
                    sharedPreferences.edit {
                        putInt(COURSE_ID, 0)
                        apply()
                    }

                    dialogInterface.dismiss()

                    val intent = Intent(this, QuizActivity::class.java).apply {
                        putExtra(COURSE_ID, courseId)
                        putExtra(QUIZ_ID, quizId)
                        putExtra(ACTION_RETAKE, true)
                    }

                    startActivity(intent)
                    finish()
                }
                builder.setNegativeButton("Cancel") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    startActivity(Intent(this, MainActivity::class.java))
                    finishAffinity()
                }
                val alertDialog: AlertDialog = builder.create()
                alertDialog.setCancelable(false)
                alertDialog.show()
            }
        }
    }

    private fun fetchTrophy() {
        viewModel.getTrophy(userId)
        viewModel.trophyResource?.observe(this, {
            if (it?.status == Status.SUCCESS) {
                if (it.data?.totalCourses!! == it.data.completedCourses!!) {
                    showCompletedCoursesDialog()
                } else {
                    startActivity(Intent(
                        this,
                        CompleteCourseActivity::class.java
                    ).apply {
                        putExtra(COURSE_ID, courseId)
                    })
                    finishAffinity()
                }
            }
        })
    }

    private fun showCompletedCoursesDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Congratulations!")
        builder.setMessage("We are happy to inform you that you have completed all the courses.")
        builder.setNegativeButton("Go Home") { dialogInterface, _ ->

            sharedPreferences.edit {
                putInt(COURSE_ID, 0)
                apply()
            }

            dialogInterface.dismiss()
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun submitAnswers() {
        viewModel.submitCourseProgress(
            CourseProgressRequest(
                userId = userId,
                courseId = courseId.toInt(),
                quizId = quizId.toInt(),
                courseProgress = 100,
                quizStatus = 1,
                results = results
            )
        )

        viewModel.courseProgressResource?.observe(this, {
            when (it?.status) {
                Status.LOADING -> {
                    progressDialog!!.show()
                }
                Status.SUCCESS -> {
                    Toast.makeText(this, it.data?.message, Toast.LENGTH_SHORT).show()
                    progressDialog!!.dismiss()

                    if (it.data?.code == "100") {
                        viewModel.setCourseProgressComplete(courseId)
                        fetchTrophy()
                    }
                }
                Status.ERROR -> {
                    progressDialog!!.dismiss()
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}
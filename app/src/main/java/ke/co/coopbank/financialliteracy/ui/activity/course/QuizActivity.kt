package ke.co.coopbank.financialliteracy.ui.activity.course

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import ke.co.coopbank.financialliteracy.databinding.ActivityQuizBinding
import ke.co.coopbank.financialliteracy.model.Question
import ke.co.coopbank.financialliteracy.model.Quiz
import ke.co.coopbank.financialliteracy.model.User
import ke.co.coopbank.financialliteracy.ui.activity.main.MainViewModel
import ke.co.coopbank.financialliteracy.utilities.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class QuizActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityQuizBinding
    private lateinit var adapter: QuestionAdapter
    private lateinit var user: User
    private lateinit var quiz: Quiz
    private lateinit var courseId: String
    private lateinit var quizId: String
    private var retake: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        courseId = intent.getStringExtra(COURSE_ID).toString()
        quizId = intent.getStringExtra(QUIZ_ID).toString()
        retake = intent.getBooleanExtra(ACTION_RETAKE, false)

        adapter = QuestionAdapter(this, listener = { result ->
            viewModel.setUserAnswer(result)
        })

        binding.recyclerView.adapter = adapter
        adapter.clearResults()

        with(viewModel) {
            setCourseId(courseId)
            viewModel.setCourseProgress(courseId, 50)
        }

        viewModel.user?.observeOnce(this) {
            user = it
        }

        observeLiveData()

        binding.buttonSubmit.setOnClickListener {
            checkAnswers()
        }
    }

    private fun observeLiveData() {
        viewModel.quiz.observeOnce(this) { quiz ->
            this.quiz = quiz
            supportActionBar!!.title = quiz.title
            binding.quiz = quiz
            viewModel.setQuizId(quiz.id)

            if (!retake) {
                viewModel.removeUserAnswers(quiz.id)
            }
        }

        viewModel.getQuestions(quizId).observeOnce(this) { questions ->
            Timber.e("QUESTIONS: %s", questions)
            if (retake) {
                val _questions = mutableListOf<Question>()
                questions.forEach { question ->
                    val userAnswers = question.userAnswers!!.split(",").map { it.trim() }
                    if (!isEqual(question.answers, userAnswers)) {
                        _questions.add(question)
                    }
                }
                adapter.submitList(_questions)
            } else {
                adapter.submitList(questions)
            }
        }
    }

    private fun checkAnswers() {
        viewModel.getUserAnswers(quiz.id).observeOnce(this) { userAnswers ->
            run breaker@{
                userAnswers.forEach { answer ->

                    if (answer == null) {
                        alertQuestionsNotCompleted()
                        return@breaker
                    }

                    if (answer.isNullOrBlank() || answer.isNullOrEmpty()) {
                        alertQuestionsNotCompleted()
                        return@breaker
                    }
                }

                startActivity(Intent(this, MarkQuizActivity::class.java).apply {
                    putExtra(COURSE_ID, courseId)
                    putExtra(QUIZ_ID, quiz.id)
                })

                finish()
            }
        }
    }

    private fun alertQuestionsNotCompleted() {
        Toast.makeText(
            this,
            "Failed. You must attempt all the questions to proceed.", Toast.LENGTH_SHORT
        ).show()
        return
    }
}
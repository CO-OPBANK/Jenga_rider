package ke.co.coopbank.financialliteracy.ui.activity.main

import androidx.lifecycle.*
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import dagger.hilt.android.lifecycle.HiltViewModel
import ke.co.coopbank.financialliteracy.CourseQuery
import ke.co.coopbank.financialliteracy.CoursesQuery
import ke.co.coopbank.financialliteracy.QuizzesQuery
import ke.co.coopbank.financialliteracy.api.Resource
import ke.co.coopbank.financialliteracy.api.ViewState
import ke.co.coopbank.financialliteracy.api.request.CourseProgressRequest
import ke.co.coopbank.financialliteracy.api.request.Result
import ke.co.coopbank.financialliteracy.api.response.CourseProgress
import ke.co.coopbank.financialliteracy.api.response.CourseProgressResponse
import ke.co.coopbank.financialliteracy.db.dao.CourseDao
import ke.co.coopbank.financialliteracy.db.dao.CourseMediaDao
import ke.co.coopbank.financialliteracy.db.dao.QuestionDao
import ke.co.coopbank.financialliteracy.db.dao.QuizDao
import ke.co.coopbank.financialliteracy.model.*
import ke.co.coopbank.financialliteracy.repository.AuthRepository
import ke.co.coopbank.financialliteracy.repository.CourseRepository
import ke.co.coopbank.financialliteracy.repository.ReportRepository
import ke.co.coopbank.financialliteracy.utilities.CMS_UPLOAD
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
@ExperimentalCoroutinesApi
@HiltViewModel
class MainViewModel @Inject internal constructor(
    private val courseRepository: CourseRepository,
    private val courseDao: CourseDao,
    private val courseMediaDao: CourseMediaDao,
    private val quizDao: QuizDao,
    private val questionDao: QuestionDao,
    authRepository: AuthRepository,
    private val reportRepository: ReportRepository
) : ViewModel() {

    val user: LiveData<User>? = authRepository.getLoggedInUser()
    val courses: LiveData<List<Course>> = courseDao.getCourses().asLiveData()
    val onGoingCourses: LiveData<List<Course>> = courseDao.getOngoingCourses().asLiveData()
    var trophyResource: LiveData<Resource<User>>? = null
    var coursesProgressResource: LiveData<Resource<List<CourseProgress>>>? = null
    var courseProgressResource: LiveData<Resource<CourseProgressResponse>>? = null

    private val courseId: MutableStateFlow<String> = MutableStateFlow("")

    val localCourse: LiveData<Course> = courseId.flatMapLatest { courseId ->
        courseDao.getCourse(courseId)
    }.asLiveData()

    val media: LiveData<CourseMedia> = courseId.flatMapLatest { courseId ->
        courseMediaDao.getMedia(courseId)
    }.asLiveData()

    val quiz: LiveData<Quiz> = courseId.flatMapLatest { courseId ->
        quizDao.getQuiz(courseId)
    }.asLiveData()

    val countQuiz: LiveData<Int> = courseId.flatMapLatest { courseId ->
        quizDao.countQuiz(courseId)
    }.asLiveData()

    val quizId: MutableStateFlow<String> = MutableStateFlow("")
    /*val questions: LiveData<List<Question>> = quizId.flatMapLatest { quizId ->
        questionDao.getQuestions(quizId)
    }.asLiveData()*/

    fun getQuestions(quizId: String): LiveData<List<Question>> {
      return  questionDao.getQuestions(quizId).asLiveData()
    }

    fun setQuizId(id: String) {
        quizId.value = id
    }

    fun setCourseId(id: String) {
        courseId.value = id
    }

    private val _coursesList by lazy { MutableLiveData<ViewState<Response<CoursesQuery.Data>>>() }
    val coursesList: LiveData<ViewState<Response<CoursesQuery.Data>>>
        get() = _coursesList

    fun queryCourses() = viewModelScope.launch {
        _coursesList.postValue(ViewState.Loading())
        try {
            val response = courseRepository.queryCourses()
            response.data?.courses?.forEach {

                var bannerUrl = ""
                if (it?.banner?.isNotEmpty()!!) {
                    bannerUrl = "$CMS_UPLOAD${it.banner[0]?.url}"
                }

                if (courseDao.exists(it.id)) {
                    courseDao.update(it.id, it.title, bannerUrl, it.duration)
                } else {
                    val course = Course(
                        id = it.id,
                        title = it.title,
                        banner = bannerUrl,
                        duration = it.duration,
                        quizId = it.quiz?.id,
                        excerpt = null,
                        body = null,
                        progress = 0,
                        startedAt = null,
                        completedAt = null
                    )
                    courseDao.insert(course)
                }
            }
            _coursesList.postValue(ViewState.Success(response))
        } catch (e: ApolloException) {
            Timber.d(e, "Failure")
            _coursesList.postValue(ViewState.Error("Error fetching courses"))
        }
    }

    private val _course by lazy { MutableLiveData<ViewState<Response<CourseQuery.Data>>>() }
    val course: LiveData<ViewState<Response<CourseQuery.Data>>>
        get() = _course

    fun queryCourse(courseId: String) = viewModelScope.launch {
        _course.postValue(ViewState.Loading())
        try {
            val response = courseRepository.queryCourse(courseId)
            response.data?.courses?.forEach { course ->

                if (course != null) {
                    courseDao.updateDetails(
                        id = course.id,
                        excerpt = course.excerpt,
                        body = course.body,
                        quizId = course.quiz?.id,
                    )

                    if (course.media?.isNotEmpty()!!) {
                        courseMediaDao.deleteAll(courseId = course.id)
                        course.media.forEach { media ->
                            courseMediaDao.save(
                                CourseMedia(
                                    id = 0,
                                    name = media?.name,
                                    url = "$CMS_UPLOAD${media?.url}",
                                    mime = media?.mime,
                                    courseId = course.id
                                )
                            )
                        }
                    }
                }
            }
            _course.postValue(ViewState.Success(response))
        } catch (ae: ApolloException) {
            Timber.d(ae, "Failure")
            _course.postValue(ViewState.Error("Error fetching course"))
        }
    }

    private val _quizzes by lazy { MutableLiveData<ViewState<Response<QuizzesQuery.Data>>>() }
    val quizzes: LiveData<ViewState<Response<QuizzesQuery.Data>>>
        get() = _quizzes

    fun queryQuizzes(courseId: String, quizId: String) = viewModelScope.launch {
        _quizzes.postValue(ViewState.Loading())
        try {
            val response = courseRepository.queryQuizzes(quizId)

            response.data?.quizzes?.forEach { quiz ->
                quizDao.save(
                    Quiz(
                        id = quiz!!.id,
                        title = quiz.title,
                        courseId = courseId,
                    )
                )

                setQuizId(quiz.id)

                quiz.questions?.forEach { question ->
                    questionDao.save(
                        Question(
                            id = question!!.id,
                            quizId = quiz.id,
                            title = question.title,
                            type = question.type.toString(),
                            choices = question.answer as Map<String, String>?,
                            answers = question.correct_answer as List<String?>,
                            userAnswers = null,
                        )
                    )
                }
            }
            _quizzes.postValue(ViewState.Success(response))
        } catch (ae: ApolloException) {
            Timber.d(ae, "Failure")
            _quizzes.postValue(ViewState.Error("Error fetching quizzes"))
        }
    }

    fun addOngoingProgress(courseId: String, progress: Int) {
        viewModelScope.launch {
            courseDao.updateProgress(courseId, progress)
        }
    }

    fun setCourseProgress(courseId: String, progress: Int) {
        viewModelScope.launch {
            courseDao.updateProgress(courseId, progress)
        }
    }

    fun setCourseProgressComplete(courseId: String) {
        viewModelScope.launch {
            courseDao.updateProgressComplete(courseId, 100, Calendar.getInstance().time)
        }
    }

    fun getTrophy(userId: String) {
        trophyResource = reportRepository.getTrophies(userId)
    }

    fun getCoursesProgress(userId: String) {
        coursesProgressResource = reportRepository.getCoursesProgress(userId)
    }

    fun submitCourseProgress(courseProgressRequest: CourseProgressRequest) {
        courseProgressResource = reportRepository.submitCourseProgress(courseProgressRequest)
    }

    fun setUserAnswer(result: Result) {
        viewModelScope.launch {
            questionDao.setUserAnswer(result.id, result.answers.joinToString(","))
        }
    }

    fun getUserAnswers(quizId: String): LiveData<List<String?>> {
        return questionDao.getUserAnswers(quizId).asLiveData()
    }

    fun removeUserAnswers(quizId: String) {
        viewModelScope.launch {
            questionDao.removeUserAnswers(quizId)
        }
    }
}
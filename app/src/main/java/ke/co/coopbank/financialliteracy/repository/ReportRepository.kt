package ke.co.coopbank.financialliteracy.repository

import androidx.lifecycle.LiveData
import ke.co.coopbank.financialliteracy.AppExecutors
import ke.co.coopbank.financialliteracy.api.ApiResponse
import ke.co.coopbank.financialliteracy.api.NetworkBoundResource
import ke.co.coopbank.financialliteracy.api.Resource
import ke.co.coopbank.financialliteracy.api.request.CourseProgressRequest
import ke.co.coopbank.financialliteracy.api.response.CourseProgress
import ke.co.coopbank.financialliteracy.api.response.CourseProgressResponse
import ke.co.coopbank.financialliteracy.api.response.TrophyResponse
import ke.co.coopbank.financialliteracy.api.service.ReportService
import ke.co.coopbank.financialliteracy.db.dao.CourseDao
import ke.co.coopbank.financialliteracy.db.dao.UserDao
import ke.co.coopbank.financialliteracy.model.User
import ke.co.coopbank.financialliteracy.utilities.AbsentLiveData
import ke.co.coopbank.financialliteracy.utilities.RateLimiter
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val userDao: UserDao,
    private val courseDao: CourseDao,
    private val reportService: ReportService
) {

    private val rateLimit = RateLimiter<String>(10, TimeUnit.MINUTES)

    fun getTrophies(userId: String): LiveData<Resource<User>> {
        val key = "trophies"

        return object : NetworkBoundResource<User, TrophyResponse>(appExecutors) {
            override fun saveCallResult(item: TrophyResponse) {
                userDao.updateCourses(
                    totalCourses = item.totalCourses.toInt(),
                    completedCourses = item.completeCourses.toInt(),
                    userId = userId
                )
            }

            override fun shouldFetch(data: User?): Boolean {
                return data == null || rateLimit.shouldFetch(key)
            }

            override fun loadFromDb(): LiveData<User> {
                return userDao.findById(userId)
            }

            override fun createCall(): LiveData<ApiResponse<TrophyResponse>> {
                return reportService.getTrophies(userId)
            }

        }.asLiveData()
    }

    fun getCoursesProgress(userId: String): LiveData<Resource<List<CourseProgress>>> {
        val key = "courses_progress"

        return object :
            NetworkBoundResource<List<CourseProgress>, List<CourseProgress>>(appExecutors) {
            override fun saveCallResult(item: List<CourseProgress>) {
                item.filter {
                    it.courseProgress == "100"
                }.forEach {
                    courseDao.updateCourseProgress(it.courseId, it.courseProgress.toInt())
                }
            }

            override fun shouldFetch(data: List<CourseProgress>?): Boolean {
                return rateLimit.shouldFetch(key)
            }

            override fun loadFromDb(): LiveData<List<CourseProgress>> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<ApiResponse<List<CourseProgress>>> {
                return reportService.getCourseProgress(userId)
            }

        }.asLiveData()
    }

    fun submitCourseProgress(
        courseProgressRequest: CourseProgressRequest
    ): LiveData<Resource<CourseProgressResponse>> {
        val key = "course_progress"

        return object :
            NetworkBoundResource<CourseProgressResponse, CourseProgressResponse>(appExecutors) {

            private var resultsDb: CourseProgressResponse? = null

            override fun saveCallResult(item: CourseProgressResponse) {
                resultsDb = item
            }

            override fun shouldFetch(data: CourseProgressResponse?): Boolean {
                return data == null || rateLimit.shouldFetch(key)
            }

            override fun loadFromDb(): LiveData<CourseProgressResponse> {
                return if (resultsDb == null) {
                    AbsentLiveData.create()
                } else {
                    object : LiveData<CourseProgressResponse>() {
                        override fun onActive() {
                            super.onActive()
                            value = resultsDb
                        }
                    }
                }
            }

            override fun createCall(): LiveData<ApiResponse<CourseProgressResponse>> {
                return reportService.submitCourseProgress(courseProgressRequest)
            }

        }.asLiveData()
    }
}
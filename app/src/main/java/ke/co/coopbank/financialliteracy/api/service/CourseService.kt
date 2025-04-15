package ke.co.coopbank.financialliteracy.api.service

import com.apollographql.apollo.api.Response
import ke.co.coopbank.financialliteracy.CourseQuery
import ke.co.coopbank.financialliteracy.CoursesQuery
import ke.co.coopbank.financialliteracy.NotificationsQuery
import ke.co.coopbank.financialliteracy.QuizzesQuery

interface CourseService {

    suspend fun queryCourses(): Response<CoursesQuery.Data>

    suspend fun queryCourse(courseId: String): Response<CourseQuery.Data>

    suspend fun queryQuizzes(courseId: String): Response<QuizzesQuery.Data>

    suspend fun queryNotifications(): Response<NotificationsQuery.Data>
}
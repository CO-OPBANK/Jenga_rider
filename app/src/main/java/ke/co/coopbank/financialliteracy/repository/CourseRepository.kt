package ke.co.coopbank.financialliteracy.repository

import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import ke.co.coopbank.financialliteracy.CourseQuery
import ke.co.coopbank.financialliteracy.CoursesQuery
import ke.co.coopbank.financialliteracy.NotificationsQuery
import ke.co.coopbank.financialliteracy.QuizzesQuery
import ke.co.coopbank.financialliteracy.api.service.CourseApi
import ke.co.coopbank.financialliteracy.api.service.CourseService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CourseRepository @Inject constructor(
    private val courseService: CourseApi
) : CourseService {
    override suspend fun queryCourses(): Response<CoursesQuery.Data> {
        return courseService.getApolloClient().query(CoursesQuery()).await()
    }

    override suspend fun queryCourse(courseId: String): Response<CourseQuery.Data> {
        return courseService.getApolloClient().query(CourseQuery(courseId)).await()
    }

    override suspend fun queryQuizzes(courseId: String): Response<QuizzesQuery.Data> {
        return courseService.getApolloClient().query(QuizzesQuery(courseId)).await()
    }

    override suspend fun queryNotifications(): Response<NotificationsQuery.Data> {
        return courseService.getApolloClient().query(NotificationsQuery()).await()
    }
}
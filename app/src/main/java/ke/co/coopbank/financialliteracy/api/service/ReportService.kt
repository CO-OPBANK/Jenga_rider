package ke.co.coopbank.financialliteracy.api.service

import androidx.lifecycle.LiveData
import ke.co.coopbank.financialliteracy.api.ApiResponse
import ke.co.coopbank.financialliteracy.api.request.CourseProgressRequest
import ke.co.coopbank.financialliteracy.api.response.CourseProgress
import ke.co.coopbank.financialliteracy.api.response.CourseProgressResponse
import ke.co.coopbank.financialliteracy.api.response.TrophyResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ReportService {

    @GET("/V1/trophies/{userId}")
    fun getTrophies(@Path("userId") userId: String): LiveData<ApiResponse<TrophyResponse>>

    @GET("/V1/course_progress/{userId}")
    fun getCourseProgress(@Path("userId") userId: String): LiveData<ApiResponse<List<CourseProgress>>>

    @POST("/V1/course_progress")
    fun submitCourseProgress(@Body courseProgressRequest: CourseProgressRequest): LiveData<ApiResponse<CourseProgressResponse>>
}
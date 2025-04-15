package ke.co.coopbank.financialliteracy.api.response

import com.google.gson.annotations.SerializedName


data class CourseProgress(
    @field:SerializedName("course_id")
    val courseId: String,
    @field:SerializedName("course_progress")
    val courseProgress: String,
    @field:SerializedName("id_number")
    val idNumber: String,
    @field:SerializedName("quiz_id")
    val quizId: String,
    @field:SerializedName("quiz_status")
    val quizStatus: String
)
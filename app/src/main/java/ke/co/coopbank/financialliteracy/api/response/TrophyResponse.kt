package ke.co.coopbank.financialliteracy.api.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TrophyResponse(

    @SerializedName("total_courses")
    @Expose
    val totalCourses: String,

    @SerializedName("completed_courses")
    @Expose
    val completeCourses: String
)
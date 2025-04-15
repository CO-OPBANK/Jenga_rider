package ke.co.coopbank.financialliteracy.api.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CourseProgressRequest(

    @SerializedName("course_id")
    @Expose
    val courseId: Int,

    @SerializedName("course_progress")
    @Expose
    val courseProgress: Int,

    @SerializedName("quiz_id")
    @Expose
    val quizId: Int,

    @SerializedName("quiz_status")
    @Expose
    val quizStatus: Int,

    @SerializedName("results")
    @Expose
    val results: MutableList<Result> = mutableListOf(),

    @SerializedName("user_id")
    @Expose
    val userId: String
)

data class Result(
    var answers: MutableList<Int> = mutableListOf(),
    val id: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Result) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "Result(answers=$answers, id='$id')"
    }
}
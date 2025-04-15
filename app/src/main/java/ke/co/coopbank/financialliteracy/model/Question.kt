package ke.co.coopbank.financialliteracy.model

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class Question(
    @NonNull
    @PrimaryKey val id: String,
    val quizId: String,
    val title: String?,
    val type: String?,
    val choices: Map<String, String>?,
    val answers: List<String?>,
    val userAnswers: String?,
)
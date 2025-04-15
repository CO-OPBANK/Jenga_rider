package ke.co.coopbank.financialliteracy.model

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quizzes")
data class Quiz(
    @NonNull
    @PrimaryKey val id: String,
    val title: String?,
    val courseId: String,
)
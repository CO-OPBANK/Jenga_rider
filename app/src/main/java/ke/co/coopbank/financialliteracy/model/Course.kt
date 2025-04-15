package ke.co.coopbank.financialliteracy.model

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "courses")
data class Course(
    @NonNull
    @PrimaryKey val id: String,
    val title: String?,
    val banner: String?,
    val duration: String?,
    val excerpt: String?,
    val body: String?,
    val quizId: String?,
    val progress: Int?,
    var startedAt: Date?,
    var completedAt: Date?,
)
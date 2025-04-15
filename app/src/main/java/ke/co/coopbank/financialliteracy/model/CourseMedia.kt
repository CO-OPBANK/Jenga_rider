package ke.co.coopbank.financialliteracy.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media")
data class CourseMedia(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String?,
    val url: String?,
    val mime: String?,
    val courseId: String,
)
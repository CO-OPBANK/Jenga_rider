package ke.co.coopbank.financialliteracy.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String?,
    val content: String?,
    val createdAt: String?,
)
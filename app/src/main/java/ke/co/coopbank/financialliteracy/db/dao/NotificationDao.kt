package ke.co.coopbank.financialliteracy.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ke.co.coopbank.financialliteracy.model.Notification
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Query("SELECT * FROM notifications ORDER BY id")
    fun getNotifications(): Flow<List<Notification>>

    @Query("DELETE  FROM notifications")
    suspend fun delete()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notification: Notification)
}
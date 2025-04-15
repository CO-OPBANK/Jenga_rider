package ke.co.coopbank.financialliteracy.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ke.co.coopbank.financialliteracy.model.CourseMedia
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseMediaDao {

    @Query("SELECT * FROM media WHERE courseId=:courseId LIMIT 1")
    fun getMedia(courseId: String): Flow<CourseMedia>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(media: CourseMedia)

    @Query("DELETE FROM media WHERE courseId=:courseId")
    suspend fun deleteAll(courseId: String)
}
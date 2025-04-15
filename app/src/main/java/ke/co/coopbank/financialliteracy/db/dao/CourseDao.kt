package ke.co.coopbank.financialliteracy.db.dao

import androidx.room.*
import ke.co.coopbank.financialliteracy.model.Course
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface CourseDao {

    @Query("SELECT * FROM courses ORDER BY id")
    fun getCourses(): Flow<List<Course>>

    @Query("SELECT * FROM courses WHERE id = :id")
    fun getCourse(id: String): Flow<Course>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(courses: List<Course>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(course: Course?)

    @Query("SELECT EXISTS(SELECT * FROM courses WHERE id = :id)")
    suspend fun exists(id: String): Boolean

    @Query("UPDATE courses SET title =:title, banner=:banner, duration=:duration WHERE id =:id")
    suspend fun update(id: String, title: String?, banner: String?, duration: String?)

    @Query("UPDATE courses SET excerpt =:excerpt, body=:body, quizId=:quizId WHERE id =:id")
    suspend fun updateDetails(id: String, excerpt: String?, body: String?, quizId: String?)

    @Query("UPDATE courses SET progress =:progress WHERE id =:courseId")
    suspend fun updateProgress(courseId: String, progress: Int)

    @Query("UPDATE courses SET progress =:progress WHERE id =:courseId")
    fun updateCourseProgress(courseId: String, progress: Int)

    @Query("UPDATE courses SET progress =:progress, completedAt =:completedAt WHERE id =:courseId")
    suspend fun updateProgressComplete(courseId: String, progress: Int, completedAt: Date)

    @Query("SELECT * FROM courses WHERE progress > 0 AND progress < 100  ORDER BY progress ASC, startedAt DESC LIMIT 6")
    fun getOngoingCourses(): Flow<List<Course>>
}
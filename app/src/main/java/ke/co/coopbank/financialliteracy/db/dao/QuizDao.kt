package ke.co.coopbank.financialliteracy.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ke.co.coopbank.financialliteracy.model.Quiz
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizDao {

    @Query("SELECT * FROM quizzes WHERE courseId=:courseId LIMIT 1")
    fun getQuiz(courseId: String): Flow<Quiz>

    @Query("SELECT count(*) FROM quizzes WHERE courseId=:courseId")
    fun countQuiz(courseId: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(quiz: Quiz): Long

    @Query("DELETE FROM quizzes WHERE courseId=:courseId")
    suspend fun deleteAll(courseId: String)
}
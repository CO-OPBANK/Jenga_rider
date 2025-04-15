package ke.co.coopbank.financialliteracy.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ke.co.coopbank.financialliteracy.model.Question
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {

    @Query("SELECT * FROM questions WHERE quizId=:quizId")
    fun getQuestions(quizId: String): Flow<List<Question>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun save(question: Question)

    @Query("DELETE FROM questions WHERE quizId=:quizId")
    suspend fun deleteAll(quizId: String)

    @Query("UPDATE questions SET userAnswers =:userAnswers WHERE id =:id")
    suspend fun setUserAnswer(id: String, userAnswers: String)

    @Query("SELECT userAnswers FROM questions WHERE quizId=:quizId")
    fun getUserAnswers(quizId: String): Flow<List<String?>>

    @Query("UPDATE questions SET userAnswers = NULL WHERE quizId=:quizId ")
    suspend fun removeUserAnswers(quizId: String)
}
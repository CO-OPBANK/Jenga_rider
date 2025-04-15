package ke.co.coopbank.financialliteracy.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ke.co.coopbank.financialliteracy.model.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(user: User)

    @Query("SELECT * FROM users WHERE phoneNumber = :phoneNumber")
    fun findByPhone(phoneNumber: String): LiveData<User>

    @Query("SELECT * FROM users WHERE id = :userId")
    fun findById(userId: String): LiveData<User>

    @Query("UPDATE users SET profilePicture =:profilePicture WHERE phoneNumber = :phoneNumber")
    fun updatePhotoUrl(phoneNumber: String, profilePicture: String)

    @Query("UPDATE users SET totalCourses =:totalCourses, completedCourses=:completedCourses WHERE id=:userId")
    fun updateCourses(totalCourses: Int, completedCourses: Int, userId: String)
}
package ke.co.coopbank.financialliteracy.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ke.co.coopbank.financialliteracy.model.County

@Dao
interface CountyDao {

    @Query("SELECT * FROM counties ORDER BY name")
    fun getCounties(): LiveData<List<County>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(counties: List<County>)

    @Query("SELECT * FROM counties WHERE id =:id LIMIT 1")
    fun find(id: Int): LiveData<County>

    @Query("SELECT count(*) FROM counties")
    fun isTableEmpty(): LiveData<Int>
}
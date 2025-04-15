package ke.co.coopbank.financialliteracy.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ke.co.coopbank.financialliteracy.model.Sacco

@Dao
interface SaccoDao {

    @Query("SELECT * FROM saccos ORDER BY name")
    fun all(): LiveData<List<Sacco>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(saccos: List<Sacco>)

    @Query("SELECT * FROM saccos WHERE id =:id LIMIT 1")
    fun find(id: Int): LiveData<Sacco>

    @Query("SELECT count(*) FROM saccos")
    fun isTableEmpty():LiveData<Int>
}
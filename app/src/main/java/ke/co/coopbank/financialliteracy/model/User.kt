package ke.co.coopbank.financialliteracy.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: Int,
    val name: String?,
    val phoneNumber: String?,
    val dob: String?,
    val gender: String?,
    val idNumber: String?,
    val profilePicture: String?,
    val saccoId: Int?,
    val countyId: Int?,
    val createdAt: String?,
    val modifiedAt: String?,
    val totalCourses: Int?,
    val completedCourses: Int?
) {
    @Ignore
    @Embedded
    val sacco: Sacco? = null

    @Ignore
    @Embedded
    val county: County? = null
    override fun toString(): String {
        return "User(id=$id, name=$name, phoneNumber=$phoneNumber, dob=$dob, gender=$gender," +
                " idNumber=$idNumber, profilePicture=$profilePicture, saccoId=$saccoId," +
                " countyId=$countyId, createdAt=$createdAt, modifiedAt=$modifiedAt, " +
                "totalCourses=$totalCourses, completedCourses=$completedCourses, sacco=$sacco, " +
                "county=$county)"
    }
}
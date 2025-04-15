package ke.co.coopbank.financialliteracy.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "counties")
data class County(
    @PrimaryKey val id: Int,
    val name: String?,
    val createdAt: String?,
    val modifiedAt: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(createdAt)
        parcel.writeString(modifiedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "County(id=$id, name=$name, createdAt=$createdAt, modifiedAt=$modifiedAt)"
    }

    companion object CREATOR : Parcelable.Creator<County> {
        override fun createFromParcel(parcel: Parcel): County {
            return County(parcel)
        }

        override fun newArray(size: Int): Array<County?> {
            return arrayOfNulls(size)
        }
    }
}
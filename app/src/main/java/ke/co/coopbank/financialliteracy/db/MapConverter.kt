package ke.co.coopbank.financialliteracy.db

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@ProvidedTypeConverter
class MapConverter {

    @TypeConverter
    fun fromString(value: String): Map<String, String> {
        val mapType = object : TypeToken<Map<String, String>>() {}.type
        return Gson().fromJson(value, mapType)
    }

    @TypeConverter
    fun toJson(map: Map<String, String>): String {
        val gson = Gson()
        return gson.toJson(map)
    }
}
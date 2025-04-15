package ke.co.coopbank.financialliteracy.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ke.co.coopbank.financialliteracy.db.dao.*
import ke.co.coopbank.financialliteracy.model.*
import ke.co.coopbank.financialliteracy.utilities.DATABASE_NAME

@Database(
    entities = [
        User::class,
        County::class,
        Sacco::class,
        Course::class,
        CourseMedia::class,
        Quiz::class,
        Question::class,
        Notification::class,
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    Converters::class,
    MapConverter::class,
)
abstract class FinancialLiteracyDb : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun courseDao(): CourseDao
    abstract fun courseMediaDao(): CourseMediaDao
    abstract fun countyDao(): CountyDao
    abstract fun saccoDao(): SaccoDao
    abstract fun quizDao(): QuizDao
    abstract fun questionDao(): QuestionDao
    abstract fun notificationDao(): NotificationDao

    companion object {

        @Volatile
        private var instance: FinancialLiteracyDb? = null

        fun getInstance(context: Context): FinancialLiteracyDb {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): FinancialLiteracyDb {
            return Room.databaseBuilder(context, FinancialLiteracyDb::class.java, DATABASE_NAME)
                .addTypeConverter(MapConverter())
                .addTypeConverter(Converters())
                .build()
        }
    }
}
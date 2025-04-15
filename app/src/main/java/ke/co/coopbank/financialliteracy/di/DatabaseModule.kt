package ke.co.coopbank.financialliteracy.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ke.co.coopbank.financialliteracy.db.FinancialLiteracyDb
import ke.co.coopbank.financialliteracy.db.dao.*
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideFinancialLiteracyDb(@ApplicationContext context: Context): FinancialLiteracyDb {
        return FinancialLiteracyDb.getInstance(context)
    }

    @Provides
    fun provideUserDao(database: FinancialLiteracyDb): UserDao {
        return database.userDao()
    }

    @Provides
    fun provideCountyDao(database: FinancialLiteracyDb): CountyDao {
        return database.countyDao()
    }

    @Provides
    fun provideSaccoDao(database: FinancialLiteracyDb): SaccoDao {
        return database.saccoDao();
    }

    @Provides
    fun provideCourseDao(database: FinancialLiteracyDb): CourseDao {
        return database.courseDao()
    }

    @Provides
    fun provideCourseMediaDao(database: FinancialLiteracyDb): CourseMediaDao {
        return database.courseMediaDao()
    }

    @Provides
    fun provideQuizDao(database: FinancialLiteracyDb): QuizDao {
        return database.quizDao()
    }

    @Provides
    fun provideQuestionDao(database: FinancialLiteracyDb): QuestionDao {
        return database.questionDao()
    }

    @Provides
    fun provideNotificationDao(database: FinancialLiteracyDb): NotificationDao {
        return database.notificationDao()
    }
}
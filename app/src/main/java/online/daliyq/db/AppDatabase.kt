package online.daliyq.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import online.daliyq.db.dao.QuestionDao
import online.daliyq.db.dao.UserDao
import online.daliyq.db.entity.QuestionEntity
import online.daliyq.db.entity.UserEntity

@Database(entities = [UserEntity::class, QuestionEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase(){
    abstract fun getUserDao(): UserDao
    abstract fun getQuestionDao(): QuestionDao

    companion object{
        const val FILENAME = "dailyq.db"
        @Volatile var INSTANCE: AppDatabase? = null

        private fun create(context: Context): AppDatabase{
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                FILENAME
            ).fallbackToDestructiveMigration().build()
        }

        fun getInstance(context: Context): AppDatabase = INSTANCE ?:
        synchronized(this){
            INSTANCE ?: create(context).also {
                INSTANCE = it
            }
        }
    }
}
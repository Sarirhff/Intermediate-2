package com.example.mystoryapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mystoryapp.data.response.ListStoryItem


@Database(
    entities = [ListStoryItem::class, RemoteKeys::class],
    version = 1, exportSchema = false
)
abstract class DatabaseUserStory : RoomDatabase() {

    abstract fun storyUserDao(): UserStoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        private var instance: DatabaseUserStory? = null

        @JvmStatic
        fun getDatabase(context: Context): DatabaseUserStory {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context,
                    DatabaseUserStory::class.java,
                    "stories.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
        }
    }
}
package com.example.mystoryapp.data.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mystoryapp.data.response.ListStoryItem

@Dao
interface UserStoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllStories(stories: List<ListStoryItem>)

    @Query("SELECT * FROM table_story")
    fun getAllStories(): PagingSource<Int, ListStoryItem>


    @Query("DELETE FROM table_story")
    fun deleteAllStories()
}
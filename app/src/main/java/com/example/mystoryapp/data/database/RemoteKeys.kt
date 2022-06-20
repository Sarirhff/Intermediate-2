package com.example.mystoryapp.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey(autoGenerate = false)
    @field:ColumnInfo(name = "id")
    val id: String,

    @field:ColumnInfo(name = "prevKey")
    val prevKey: Int?,

    @field:ColumnInfo(name = "nextKey")
    val nextKey: Int?
)
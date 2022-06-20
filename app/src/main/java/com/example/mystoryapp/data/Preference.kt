package com.example.mystoryapp.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class Preference private constructor(private val dataStore: DataStore<Preferences>) {
    private val token = stringPreferencesKey("token")
    private val user = booleanPreferencesKey("user")

    fun getToken(): Flow<String> {
        return dataStore.data.map {
            it[token] ?: "null"
        }
    }

    fun newUser(): Flow<Boolean> {
        return dataStore.data.map {
            it[user] ?: true
        }
    }

    suspend fun saveToken(token: String) {
        dataStore.edit {
            it[this.token] = token
        }
    }

    suspend fun saveNewUser(firstTime: Boolean) {
        dataStore.edit {
            it[this.user] = firstTime
        }
    }

    suspend fun deleteToken() {
        dataStore.edit {
            it[token] = "null"
        }
    }

    companion object {
        @Volatile
        private var instance: Preference? = null

        fun getInstance(dataStore: DataStore<Preferences>): Preference =
            instance ?: synchronized(this) {
                instance ?: Preference(dataStore)
            }.also { instance = it }
    }
}
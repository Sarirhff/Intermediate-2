package com.example.mystoryapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.paging.*
import com.example.mystoryapp.data.Result
import com.example.mystoryapp.data.Preference
import com.example.mystoryapp.data.database.DatabaseUserStory
import com.example.mystoryapp.data.response.AddNewStoryResponse
import com.example.mystoryapp.data.response.ListStoryItem
import com.example.mystoryapp.data.response.LoginResponse
import com.example.mystoryapp.data.retrofit.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import kotlin.coroutines.CoroutineContext

class StoryRepository (
    private val storyUserDatabase: DatabaseUserStory,
    private val apiService: ApiService,
    private val userPreference: Preference
) : CoroutineScope {
    @OptIn(ExperimentalPagingApi::class)
    fun getPaging(token: String): LiveData<PagingData<ListStoryItem>> {

        return Pager(
            config = PagingConfig(
                pageSize = 8
            ),
            remoteMediator = RemoteMediator(storyUserDatabase, apiService, token),
            pagingSourceFactory = {
                PagingSource(apiService, token)
                storyUserDatabase.storyUserDao().getAllStories()
            }
        ).liveData
    }
    fun getToken(): LiveData<String> = userPreference.getToken().asLiveData()

    fun deleteToken() {
        launch(Dispatchers.IO) {
            userPreference.deleteToken()
        }
    }

    fun newUser(): LiveData<Boolean> = userPreference.newUser().asLiveData()

    fun saveToken(token: String) {
        launch(Dispatchers.IO) {
            userPreference.saveToken(token)
        }
    }

    fun loginUser(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(
                email,
                password
            )
            if (response.error) {
                emit(Result.Error(response.message))
            } else {
                emit(Result.Success(response))
            }
        } catch (exception: Exception) {
            emit(Result.Error(exception.message.toString()))
        }
    }

    fun regisUser(name: String, email: String, password: String) = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.register(
                name,
                email,
                password
            )
            if (response.error) {
                emit(Result.Error(response.message))
            } else {
                emit(Result.Success(response))
            }
        } catch (exception: Exception) {
            emit(Result.Error(exception.message.toString()))
        }
    }

    fun addNewStory(
        token: String,
        imageFile: File,
        description: String,
        lat: Float?,
        lon: Float?
    ): LiveData<Result<AddNewStoryResponse>> = liveData {
        emit(Result.Loading)

        val textPlainMediaType = "text/plain".toMediaType()
        val imageMediaType = "image/jpeg".toMediaTypeOrNull()

        val imageMultiPart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            imageFile.asRequestBody(imageMediaType)
        )
        val descriptionRequestBody = description.toRequestBody(textPlainMediaType)
        val latRequestBody = lat.toString().toRequestBody(textPlainMediaType)
        val lonRequestBody = lon.toString().toRequestBody(textPlainMediaType)

        try {
            val response = apiService.postStory(
                token,
                imageMultiPart,
                descriptionRequestBody,
                latRequestBody,
                lonRequestBody
            )

            if (response.error) {
                emit(Result.Error(response.message))
            } else {
                emit(Result.Success(response))
            }
        } catch (exception: Exception) {
            emit(Result.Error(exception.message.toString()))
        }
    }
    fun getStoriesLocation(token: String): LiveData<Result<List<ListStoryItem>>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = apiService.getStoriesLocation(token)
                if (response.error) {
                    emit(Result.Error(response.message))
                } else {
                    val stories = response.listStory
                    emit(Result.Success(stories))
                }
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }

    companion object {

        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(
            storyUserDatabase: DatabaseUserStory,
            apiService: ApiService,
            userPreference: Preference
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(storyUserDatabase, apiService, userPreference)
            }.also { instance = it }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}
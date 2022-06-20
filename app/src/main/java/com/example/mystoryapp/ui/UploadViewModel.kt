package com.example.mystoryapp.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mystoryapp.data.Injection
import com.example.mystoryapp.data.repository.StoryRepository
import java.io.File

class UploadViewModel (private val storyUserRepository: StoryRepository) : ViewModel() {

    fun addNewStory(
        token: String,
        imageFile: File,
        description: String,
        lat: Float? = null,
        lon: Float? = null
    )= storyUserRepository.addNewStory(token, imageFile, description, lat, lon)

    fun getToken(): LiveData<String> {
        return storyUserRepository.getToken()
    }
}
class UploadViewModelFactory(
    private val storyUserRepository: StoryRepository
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UploadViewModel::class.java)) {
            return UploadViewModel(storyUserRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var instance: UploadViewModelFactory? = null

        fun getInstance(
            context: Context
        ): UploadViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: UploadViewModelFactory(
                    Injection.provideStoryRepository(context))
            }.also { instance = it }
    }
}
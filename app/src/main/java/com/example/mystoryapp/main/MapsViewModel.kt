package com.example.mystoryapp.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mystoryapp.data.Injection
import com.example.mystoryapp.data.repository.StoryRepository

class MapsViewModel (
    private val storyUserRepository: StoryRepository
) : ViewModel() {
    fun getStories(token: String) = storyUserRepository.getStoriesLocation(token)

    fun getToken(): LiveData<String> {
        return storyUserRepository.getToken()
    }

    class MapsViewModelFactory private constructor(
        private val storyUserRepository: StoryRepository
    ) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
                return MapsViewModel(storyUserRepository) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }

        companion object {
            @Volatile
            private var instance: MapsViewModelFactory? = null
            fun getInstance(context: Context): MapsViewModelFactory =
                instance ?: synchronized(this) {
                    instance ?: MapsViewModelFactory(Injection.provideStoryRepository(context))
                }.also { instance = it }
        }
    }
}

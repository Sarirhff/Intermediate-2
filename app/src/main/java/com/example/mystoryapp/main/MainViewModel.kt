package com.example.mystoryapp.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import com.example.mystoryapp.data.Injection
import com.example.mystoryapp.data.repository.StoryRepository
import com.example.mystoryapp.data.response.ListStoryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel (
    private val storyUserRepository: StoryRepository) : ViewModel() {
    @ExperimentalPagingApi
    fun getStories(token: String): LiveData<PagingData<ListStoryItem>> {
        return storyUserRepository.getPaging(token)
    }
    fun getToken(): LiveData<String> {
        return storyUserRepository.getToken()
    }
    fun checkToken(): LiveData<String> {
        return storyUserRepository.getToken()
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            storyUserRepository.deleteToken()
        }
    }

    class MainViewModelFactory(
        private val storyUserRepository: StoryRepository
    ) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(storyUserRepository) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }

        companion object {
            @Volatile
            private var instance: MainViewModelFactory? = null

            fun getInstance(
                context: Context
            ): MainViewModelFactory = instance ?: synchronized(this) {
                instance ?: MainViewModelFactory(
                    Injection.provideStoryRepository(context))
            }.also { instance = it }
        }
    }
}
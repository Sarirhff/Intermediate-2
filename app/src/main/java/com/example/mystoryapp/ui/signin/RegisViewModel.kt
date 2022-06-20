package com.example.mystoryapp.ui.signin

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mystoryapp.data.Injection
import com.example.mystoryapp.data.repository.StoryRepository

class RegisViewModel(private val storyUserRepository: StoryRepository) : ViewModel() {
    fun regisUser(
        name: String,
        email: String,
        password: String
    ) = storyUserRepository.regisUser(name, email, password)

    class RegisViewModelFactory private constructor(private val storyUserRepository: StoryRepository) :
        ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RegisViewModel::class.java)) {
                return RegisViewModel(storyUserRepository) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }

        companion object {
            @Volatile
            private var instance: RegisViewModelFactory? = null

            fun getInstance(context: Context): RegisViewModelFactory =
                instance ?: synchronized(this) {
                    instance ?: RegisViewModelFactory(Injection.provideStoryRepository(context))
                }
        }
    }
}
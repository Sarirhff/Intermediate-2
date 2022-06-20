package com.example.mystoryapp.ui.signin

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mystoryapp.data.Injection
import com.example.mystoryapp.data.repository.StoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(
    private val storyUserRepository: StoryRepository
) : ViewModel() {
    fun loginUser(email: String, password: String) =
        storyUserRepository.loginUser(email, password)

    fun saveToken(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            storyUserRepository.saveToken(token)
        }
    }

    fun checkIfNewUser(): LiveData<Boolean> {
        return storyUserRepository.newUser()
    }

    class LoginViewModelFactory private constructor(
        private val storyUserRepository: StoryRepository
    ) :
        ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                return LoginViewModel(storyUserRepository) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }

        companion object {
            @Volatile
            private var instance: LoginViewModelFactory? = null
            fun getInstance(
                context: Context
            ): LoginViewModelFactory =
                instance ?: synchronized(this) {
                    instance ?: LoginViewModelFactory(
                        Injection.provideStoryRepository(context)
                    )
                }
        }
    }
}
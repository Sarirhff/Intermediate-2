package com.example.mystoryapp.ui.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mystoryapp.data.Preference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BoardingViewModel  (private val pref: Preference) : ViewModel() {

    fun saveNewUser(firstTime: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            pref.saveNewUser(firstTime)
        }
    }

    class BoardingViewModelFactory private constructor(private val pref: Preference) :
        ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BoardingViewModel::class.java)) {
                return BoardingViewModel(pref) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }

        companion object {
            @Volatile
            private var instance: BoardingViewModelFactory? = null

            fun getInstance(userPreference: Preference): BoardingViewModelFactory =
                instance ?: synchronized(this) {
                    instance ?: BoardingViewModelFactory(userPreference)
                }.also { instance = it }
        }
    }
}
package com.example.unsagrades.ui

import androidx.activity.result.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unsagrades.data.local.dao.UserDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userDao: UserDao
) : ViewModel() {

    // Helper to keep state
    var userName by mutableStateOf("Agustino")
        private set

    init {
        loadUserName()
    }

    private fun loadUserName() {
        viewModelScope.launch {
            // This runs your query safely
            val name = userDao.getUserName()
            if (name != null) userName = name
        }
    }
}

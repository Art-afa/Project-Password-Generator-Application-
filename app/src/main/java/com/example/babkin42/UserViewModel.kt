package com.example.babkin42

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val userRepository = UserRepository(database.userDao())
    private val passwordRepository = PasswordRepository(database.passwordDao())

    // Состояние для входа/регистрации
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _currentUsername = MutableStateFlow<String?>(null)
    val currentUsername: StateFlow<String?> = _currentUsername

    // Состояния для UI
    var errorMessage by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)

    // Данные пользователя
    var username by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")

    fun login() {
        if (username.isBlank() || password.isBlank()) {
            errorMessage = "Заполните все поля"
            return
        }

        isLoading = true
        viewModelScope.launch {
            try {
                val user = userRepository.login(username, password)
                isLoading = false
                if (user != null) {
                    _currentUsername.value = username
                    _isLoggedIn.value = true
                    errorMessage = null
                } else {
                    errorMessage = "Неверное имя пользователя или пароль"
                }
            } catch (e: Exception) {
                isLoading = false
                errorMessage = "Ошибка: ${e.message}"
            }
        }
    }

    fun register() {
        errorMessage = when {
            username.isBlank() -> "Введите имя пользователя"
            email.isBlank() -> "Введите Email"
            !email.contains("@") -> "Email должен содержать @"
            password.isBlank() -> "Введите пароль"
            password.length < 6 -> "Пароль должен быть минимум 6 символов"
            password != confirmPassword -> "Пароли не совпадают"
            else -> null
        }

        if (errorMessage != null) return

        isLoading = true
        viewModelScope.launch {
            try {
                val success = userRepository.register(username, email, password)
                isLoading = false
                if (success) {
                    _currentUsername.value = username
                    _isLoggedIn.value = true
                    errorMessage = null
                } else {
                    errorMessage = "Пользователь уже существует"
                }
            } catch (e: Exception) {
                isLoading = false
                errorMessage = "Ошибка: ${e.message}"
            }
        }
    }

    fun logout() {
        _isLoggedIn.value = false
        _currentUsername.value = null
        username = ""
        password = ""
        errorMessage = null
    }

    fun savePassword(passwordName: String, password: String) {
        val currentUsername = _currentUsername.value ?: return
        if (passwordName.isBlank()) return

        val date = java.text.SimpleDateFormat("dd.MM.yyyy HH:mm", java.util.Locale.getDefault())
            .format(java.util.Date())

        passwordRepository.savePassword(currentUsername, passwordName, password, date)
    }

    fun deletePassword(password: PasswordEntity) {
        passwordRepository.deletePassword(password)
    }

    fun getPasswords(): Flow<List<PasswordEntity>> {
        val currentUsername = _currentUsername.value ?: return emptyFlow()
        return passwordRepository.getPasswordsForUser(currentUsername)
    }
}

// Вспомогательная функция для пустого Flow
private fun emptyFlow(): Flow<List<PasswordEntity>> = kotlinx.coroutines.flow.flow { }
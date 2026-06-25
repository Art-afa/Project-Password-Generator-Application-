package com.example.babkin42

import kotlinx.coroutines.flow.Flow

class PasswordRepository(private val passwordDao: PasswordDao) {
    fun getPasswordsForUser(username: String): Flow<List<PasswordEntity>> {
        return passwordDao.getPasswordsForUser(username)
    }

    suspend fun savePassword(password: PasswordEntity) {
        passwordDao.insertPassword(password)
    }

    suspend fun deletePassword(password: PasswordEntity) {
        passwordDao.deletePassword(password)
    }
}
package com.example.babkin42

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PasswordRepository(private val passwordDao: PasswordDao) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun getPasswordsForUser(username: String): Flow<List<PasswordEntity>> {
        return passwordDao.getPasswordsForUser(username)
    }

    fun savePassword(username: String, passwordName: String, password: String, date: String) {
        coroutineScope.launch(Dispatchers.IO) {
            val passwordEntity = PasswordEntity(
                username = username,
                passwordName = passwordName,
                password = password,
                date = date
            )
            passwordDao.insertPassword(passwordEntity)
        }
    }

    fun deletePassword(password: PasswordEntity) {
        coroutineScope.launch(Dispatchers.IO) {
            passwordDao.deletePassword(password)
        }
    }
}
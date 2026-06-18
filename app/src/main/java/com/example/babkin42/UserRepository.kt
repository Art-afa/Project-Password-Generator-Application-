package com.example.babkin42

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserRepository(private val userDao: UserDao) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    suspend fun login(username: String, password: String): UserEntity? {
        return userDao.login(username, password)
    }

    suspend fun register(username: String, email: String, password: String): Boolean {
        val existingUser = userDao.getUserByUsername(username)
        return if (existingUser == null) {
            userDao.register(UserEntity(username = username, email = email, password = password))
            true
        } else {
            false
        }
    }
}